package cn.myflycat.auto_wine.decision;

/**
 * Produces a {@link DecisionPlan} for the given context.
 *
 * <p>Implementations must never return {@code null}; use {@link DecisionPlan#idle()}
 * as a safe fallback when no meaningful work is available.
 */
@FunctionalInterface
public interface DecisionPlanner {

    /**
     * Plans the next action sequence based on the current decision context.
     *
     * @param context current runtime state, scan result, and goal information
     * @return the plan to execute; never {@code null}
     */
    DecisionPlan plan(DecisionContext context);
}
