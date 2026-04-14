package cn.myflycat.auto_wine.decision;

/**
 * Abstraction for the active execution backend (e.g. Baritone).
 *
 * <p>The {@link DecisionRuntime} communicates with this interface only.
 * Concrete implementations live in the client source-set and are never
 * referenced from shared code.
 *
 * <h2>Degradation contract</h2>
 * <ul>
 *   <li>If {@link #execute} throws any unchecked exception, the runtime
 *       immediately calls {@link #interrupt()} and transitions to
 *       {@link DecisionState#DEGRADED}.</li>
 *   <li>The runtime probes {@link #isAvailable()} on a fixed tick interval;
 *       when it returns {@code true} the runtime schedules a resume at the
 *       next task boundary.</li>
 *   <li>{@link #interrupt()} and {@link #reset()} must <em>never</em> throw.</li>
 * </ul>
 */
public interface ExecutionLayer {

    /**
     * Returns {@code true} when the execution layer is loaded, initialised,
     * and ready to accept new plans.
     */
    boolean isAvailable();

    /**
     * Submits a plan for execution.
     *
     * <p>The implementation may execute synchronously (in the same tick) or
     * begin an asynchronous sequence.  It must throw a {@link RuntimeException}
     * on any unrecoverable failure so the runtime can degrade immediately.
     *
     * @param plan    the plan to execute
     * @param context current decision context
     * @throws RuntimeException if execution fails in a way that requires degradation
     */
    void execute(DecisionPlan plan, DecisionContext context);

    /**
     * Interrupts any in-progress execution.
     * Must be safe to call at any time and must not throw.
     */
    void interrupt();

    /**
     * Resets internal state so the layer is ready for the next plan submission.
     * Called by the runtime after a successful re-probe before switching back
     * to {@link DecisionState#ACTIVE}.
     * Must be safe to call at any time and must not throw.
     */
    void reset();
}
