package cn.myflycat.auto_wine.decision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An ordered sequence of {@link DecisionAction}s associated with a goal.
 *
 * <p>Plans are immutable once created. Use {@link #idle()} as the safe
 * no-op fallback when no meaningful work is available.
 */
public class DecisionPlan {

    private final String goalId;
    private final List<DecisionAction> actions;

    private DecisionPlan(String goalId, List<DecisionAction> actions) {
        this.goalId = goalId == null ? "" : goalId;
        this.actions = Collections.unmodifiableList(
                new ArrayList<>(actions == null ? Collections.emptyList() : actions));
    }

    public static DecisionPlan of(String goalId, List<DecisionAction> actions) {
        return new DecisionPlan(goalId, actions);
    }

    /** Returns a plan that performs a single idle action – safe to return at any time. */
    public static DecisionPlan idle() {
        return new DecisionPlan("idle", List.of(DecisionAction.of(DecisionActionType.IDLE)));
    }

    public String getGoalId() {
        return goalId;
    }

    public List<DecisionAction> getActions() {
        return actions;
    }

    public boolean isEmpty() {
        return actions.isEmpty();
    }

    @Override
    public String toString() {
        return "DecisionPlan{goalId='" + goalId + "', actions=" + actions + '}';
    }
}
