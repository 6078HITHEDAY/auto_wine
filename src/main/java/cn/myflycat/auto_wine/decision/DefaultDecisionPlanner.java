package cn.myflycat.auto_wine.decision;

/**
 * Minimal fallback {@link DecisionPlanner} that always returns an idle plan.
 *
 * <p>This planner is used in {@link DecisionState#DEGRADED} mode and as the
 * default until richer recipe-matching logic is wired in (Phase D / E).
 */
public class DefaultDecisionPlanner implements DecisionPlanner {

    @Override
    public DecisionPlan plan(DecisionContext context) {
        return DecisionPlan.idle();
    }
}
