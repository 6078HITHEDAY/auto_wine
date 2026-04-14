package cn.myflycat.auto_wine.client.decision;

import cn.myflycat.auto_wine.AutoWineFramework;
import cn.myflycat.auto_wine.client.feature.ContainerScanFeature;
import cn.myflycat.auto_wine.decision.DecisionContext;
import cn.myflycat.auto_wine.decision.DecisionGoal;
import cn.myflycat.auto_wine.decision.DecisionPlan;
import cn.myflycat.auto_wine.decision.DecisionPlanner;
import cn.myflycat.auto_wine.decision.DecisionState;
import cn.myflycat.auto_wine.decision.ExecutionLayer;
import cn.myflycat.auto_wine.scan.ContainerScanResult;
import net.minecraft.client.MinecraftClient;

/**
 * Tick-driven decision runtime that manages the three-state execution model:
 * {@link DecisionState#ACTIVE} → {@link DecisionState#DEGRADED} →
 * {@link DecisionState#PENDING_RESUME} → {@link DecisionState#ACTIVE}.
 *
 * <h2>Degradation policy</h2>
 * <ol>
 *   <li>First exception thrown by {@link ExecutionLayer#execute} triggers immediate
 *       degradation to {@link DecisionState#DEGRADED}.</li>
 *   <li>In DEGRADED mode, a probe runs every {@value #PROBE_INTERVAL_TICKS} ticks.
 *       If the execution layer becomes available again, the runtime transitions to
 *       {@link DecisionState#PENDING_RESUME} (or directly to ACTIVE if at a task
 *       boundary).</li>
 *   <li>The switch back to ACTIVE only happens at a task boundary: when the current
 *       goal is {@code null} or has reached a terminal state (completed / failed /
 *       stopped), preventing mid-task disruption.</li>
 * </ol>
 */
public class DecisionRuntime {

    /** Tick interval between availability probes when in DEGRADED or PENDING_RESUME mode. */
    public static final int PROBE_INTERVAL_TICKS = 200;

    private volatile DecisionState state;
    private final ExecutionLayer executionLayer;
    private final DecisionPlanner planner;
    private DecisionGoal currentGoal;
    private DecisionPlan currentPlan;
    private int probeTickCounter;

    /**
     * Creates a new runtime.
     *
     * @param planner        the planner used in all modes (must not be null)
     * @param executionLayer the execution backend, or {@code null} to start in
     *                       {@link DecisionState#DEGRADED} immediately
     */
    public DecisionRuntime(DecisionPlanner planner, ExecutionLayer executionLayer) {
        if (planner == null) {
            throw new IllegalArgumentException("planner must not be null");
        }
        this.planner = planner;
        this.executionLayer = executionLayer;
        this.state = (executionLayer != null && executionLayer.isAvailable())
                ? DecisionState.ACTIVE
                : DecisionState.DEGRADED;
    }

    /**
     * Advances the runtime by one game-tick.
     * Should be called from a client-tick event handler.
     *
     * @param client the current Minecraft client instance
     */
    public void tick(MinecraftClient client) {
        ContainerScanResult latestScan = ContainerScanFeature.getLatestScan().orElse(null);
        DecisionContext context = DecisionContext.of(
                latestScan,
                state,
                currentGoal != null ? currentGoal.goalId() : null);

        switch (state) {
            case ACTIVE -> tickActive(context);
            case DEGRADED -> tickDegraded(context);
            case PENDING_RESUME -> tickPendingResume(context);
        }
    }

    // -------------------------------------------------------------------------
    // Per-state tick logic
    // -------------------------------------------------------------------------

    private void tickActive(DecisionContext context) {
        if (isAtTaskBoundary()) {
            currentPlan = planner.plan(context);
            currentGoal = null;
        }

        if (currentPlan != null && executionLayer != null) {
            try {
                executionLayer.execute(currentPlan, context);
            } catch (Exception e) {
                handleFault(e);
            }
        }
    }

    private void tickDegraded(DecisionContext context) {
        currentPlan = planner.plan(context);

        probeTickCounter++;
        if (probeTickCounter >= PROBE_INTERVAL_TICKS) {
            probeTickCounter = 0;
            attemptProbe();
        }
    }

    private void tickPendingResume(DecisionContext context) {
        currentPlan = planner.plan(context);

        if (isAtTaskBoundary()) {
            resumeActiveLayer();
        }
    }

    // -------------------------------------------------------------------------
    // State transition helpers
    // -------------------------------------------------------------------------

    /**
     * Called when the execution layer throws an exception.
     * Immediately transitions to {@link DecisionState#DEGRADED}.
     */
    private void handleFault(Exception cause) {
        AutoWineFramework.LOGGER.warn(
                "[Decision] Execution layer faulted – degrading to local-decision mode: {}",
                cause.getMessage());
        if (executionLayer != null) {
            try {
                executionLayer.interrupt();
            } catch (Exception ignored) {
                // interrupt must not throw; suppress defensively
            }
        }
        state = DecisionState.DEGRADED;
        probeTickCounter = 0;
        currentGoal = null;
    }

    /**
     * Called every {@value #PROBE_INTERVAL_TICKS} ticks from DEGRADED mode.
     * If the execution layer is available, transitions to PENDING_RESUME or
     * directly to ACTIVE if already at a task boundary.
     */
    private void attemptProbe() {
        if (executionLayer == null || !executionLayer.isAvailable()) {
            AutoWineFramework.LOGGER.debug("[Decision] Probe: execution layer still unavailable");
            return;
        }

        AutoWineFramework.LOGGER.info(
                "[Decision] Probe succeeded – execution layer available; scheduling resume at next task boundary");

        if (isAtTaskBoundary()) {
            resumeActiveLayer();
        } else {
            state = DecisionState.PENDING_RESUME;
        }
    }

    /**
     * Switches from PENDING_RESUME (or directly from DEGRADED at a boundary)
     * back to {@link DecisionState#ACTIVE}.
     */
    private void resumeActiveLayer() {
        AutoWineFramework.LOGGER.info("[Decision] Resuming active execution layer at task boundary");
        if (executionLayer != null) {
            try {
                executionLayer.reset();
            } catch (Exception ignored) {
                // reset must not throw; suppress defensively
            }
        }
        state = DecisionState.ACTIVE;
        currentGoal = null;
        currentPlan = null;
    }

    /**
     * Returns {@code true} when there is no active goal or the current goal has
     * reached a terminal state – the point at which execution layers may be switched.
     */
    private boolean isAtTaskBoundary() {
        return currentGoal == null || currentGoal.isAtBoundary();
    }

    // -------------------------------------------------------------------------
    // Public accessors
    // -------------------------------------------------------------------------

    /** Returns the current execution state. */
    public DecisionState getState() {
        return state;
    }

    /** Returns the currently active goal, or {@code null} if none. */
    public DecisionGoal getCurrentGoal() {
        return currentGoal;
    }

    /** Returns the most recently planned plan, or {@code null} if none. */
    public DecisionPlan getCurrentPlan() {
        return currentPlan;
    }

    /**
     * Stops the runtime: interrupts the execution layer and clears the active goal/plan.
     * The execution state is not changed; call this from {@link cn.myflycat.auto_wine.feature.AutoWineFeature#shutdown()}.
     */
    public void stop() {
        if (executionLayer != null) {
            try {
                executionLayer.interrupt();
            } catch (Exception ignored) {
                // intentionally suppressed
            }
        }
        currentGoal = null;
        currentPlan = null;
    }
}
