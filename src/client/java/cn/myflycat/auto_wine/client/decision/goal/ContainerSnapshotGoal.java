package cn.myflycat.auto_wine.client.decision.goal;

import cn.myflycat.auto_wine.decision.DecisionContext;
import cn.myflycat.auto_wine.decision.DecisionGoal;
import cn.myflycat.auto_wine.scan.ContainerScanResult;

/**
 * A one-shot goal that captures the current container scan result and then completes.
 *
 * <p>This goal completes on the first tick that a non-null scan is available;
 * the captured result can be retrieved via {@link #getCapturedScan()}.
 */
public class ContainerSnapshotGoal implements DecisionGoal {

    public static final String GOAL_ID = "container_snapshot";

    private boolean completed;
    private boolean failed;
    private ContainerScanResult capturedScan;

    @Override
    public String goalId() {
        return GOAL_ID;
    }

    @Override
    public void tick(DecisionContext context) {
        if (completed || failed) {
            return;
        }
        ContainerScanResult scan = context.getLatestScan();
        if (scan != null) {
            capturedScan = scan;
            completed = true;
        }
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public boolean isFailed() {
        return failed;
    }

    @Override
    public boolean isStopped() {
        return false;
    }

    /** Returns the captured scan result, or {@code null} if the goal has not completed yet. */
    public ContainerScanResult getCapturedScan() {
        return capturedScan;
    }
}
