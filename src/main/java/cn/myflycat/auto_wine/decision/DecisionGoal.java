package cn.myflycat.auto_wine.decision;

/**
 * A single executable goal managed by the decision runtime.
 *
 * <p>Implementations are ticked once per game-tick until one of
 * {@link #isCompleted()}, {@link #isFailed()}, or {@link #isStopped()} returns
 * {@code true}. Any terminal condition constitutes a <em>task boundary</em>:
 * the runtime may switch the execution layer at that point.
 */
public interface DecisionGoal {

    /** Unique identifier for this goal type. */
    String goalId();

    /**
     * Called once per game-tick while this is the active goal.
     *
     * @param context current decision context (scan result, runtime state, …)
     */
    void tick(DecisionContext context);

    /** Returns {@code true} when the goal has finished successfully. */
    boolean isCompleted();

    /** Returns {@code true} when the goal has failed unrecoverably. */
    boolean isFailed();

    /** Returns {@code true} when the goal was explicitly stopped before completion. */
    boolean isStopped();

    /**
     * Convenience check: returns {@code true} when the goal has reached any
     * terminal state (completed, failed, or stopped) so the runtime may safely
     * switch execution layers.
     */
    default boolean isAtBoundary() {
        return isCompleted() || isFailed() || isStopped();
    }
}
