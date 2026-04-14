package cn.myflycat.auto_wine.decision;

/**
 * Runtime execution mode of the decision system.
 *
 * <p>State transitions:
 * <pre>
 *   ACTIVE ──(execution fault)──────────────────────────────> DEGRADED
 *   DEGRADED ──(probe success, goal at boundary)──────────> ACTIVE
 *   DEGRADED ──(probe success, goal not at boundary)──────> PENDING_RESUME
 *   PENDING_RESUME ──(task boundary reached)──────────────> ACTIVE
 * </pre>
 */
public enum DecisionState {

    /**
     * The full execution layer (e.g. Baritone) is active and healthy.
     */
    ACTIVE,

    /**
     * The execution layer has faulted; running on scan + local decision only.
     * A background probe (every 200 ticks by default)
     * will detect when the layer becomes available again.
     */
    DEGRADED,

    /**
     * The background probe confirmed the execution layer is available again.
     * Waiting for the current task to reach a boundary (goal completed / failed / stopped)
     * before switching back to {@link #ACTIVE} to avoid mid-task disruption.
     */
    PENDING_RESUME
}
