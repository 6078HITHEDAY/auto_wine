package cn.myflycat.auto_wine.decision;

import java.util.Objects;

/**
 * A ranked candidate goal produced during the planning phase.
 *
 * <p>The planner may produce multiple candidates; the one with the highest
 * priority value should be selected for execution.
 */
public class DecisionCandidate {

    private final DecisionGoal goal;
    private final int priority;
    private final String reason;

    private DecisionCandidate(DecisionGoal goal, int priority, String reason) {
        this.goal = Objects.requireNonNull(goal, "goal must not be null");
        this.priority = priority;
        this.reason = reason == null ? "" : reason;
    }

    public static DecisionCandidate of(DecisionGoal goal, int priority, String reason) {
        return new DecisionCandidate(goal, priority, reason);
    }

    public static DecisionCandidate of(DecisionGoal goal, int priority) {
        return new DecisionCandidate(goal, priority, "");
    }

    public DecisionGoal getGoal() {
        return goal;
    }

    public int getPriority() {
        return priority;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "DecisionCandidate{goalId=" + goal.goalId() + ", priority=" + priority + ", reason='" + reason + "'}";
    }
}
