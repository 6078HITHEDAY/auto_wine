package cn.myflycat.auto_wine.decision;

import cn.myflycat.auto_wine.scan.ContainerScanResult;

/**
 * Snapshot of all information the planner and goals need to make decisions.
 *
 * <p>Instances are created fresh on every tick by {@code DecisionRuntime} and
 * are therefore safe to pass around without defensive copying.
 */
public class DecisionContext {

    private final ContainerScanResult latestScan;
    private final DecisionState runtimeState;
    private final String currentGoalId;

    private DecisionContext(ContainerScanResult latestScan, DecisionState runtimeState, String currentGoalId) {
        this.latestScan = latestScan;
        this.runtimeState = runtimeState != null ? runtimeState : DecisionState.DEGRADED;
        this.currentGoalId = currentGoalId;
    }

    public static DecisionContext of(ContainerScanResult latestScan, DecisionState runtimeState, String currentGoalId) {
        return new DecisionContext(latestScan, runtimeState, currentGoalId);
    }

    /** Returns the most recent container scan, or {@code null} if none is available. */
    public ContainerScanResult getLatestScan() {
        return latestScan;
    }

    /** Returns {@code true} when a scan result is present. */
    public boolean hasScan() {
        return latestScan != null;
    }

    /** Returns the current execution mode of the decision runtime. */
    public DecisionState getRuntimeState() {
        return runtimeState;
    }

    /** Returns {@code true} when the full execution layer (Baritone) is active. */
    public boolean isActive() {
        return runtimeState == DecisionState.ACTIVE;
    }

    /** Returns {@code true} when the runtime is running in degraded / local-only mode. */
    public boolean isDegraded() {
        return runtimeState == DecisionState.DEGRADED;
    }

    /** Returns {@code true} when a resume is scheduled for the next task boundary. */
    public boolean isPendingResume() {
        return runtimeState == DecisionState.PENDING_RESUME;
    }

    /** Returns the goal ID of the currently active goal, or {@code null} if none. */
    public String getCurrentGoalId() {
        return currentGoalId;
    }
}
