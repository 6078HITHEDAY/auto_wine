package cn.myflycat.auto_wine.client.decision.goal;

import cn.myflycat.auto_wine.decision.DecisionContext;
import cn.myflycat.auto_wine.decision.DecisionGoal;

/**
 * A persistent goal that idles and observes the environment until explicitly stopped.
 *
 * <p>This goal never completes or fails on its own.  It is used as the default
 * goal when no other work is pending.  Calling {@link #stop()} marks it
 * stopped so that the runtime recognises a task boundary and may switch the
 * execution layer at that point.
 */
public class IdleWatchGoal implements DecisionGoal {

    public static final String GOAL_ID = "idle_watch";

    private volatile boolean stopped;

    @Override
    public String goalId() {
        return GOAL_ID;
    }

    @Override
    public void tick(DecisionContext context) {
        // Idle – no active work; just allows the runtime to observe the environment
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public boolean isFailed() {
        return false;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    /** Marks this goal as stopped so the runtime registers a task boundary. */
    public void stop() {
        stopped = true;
    }
}
