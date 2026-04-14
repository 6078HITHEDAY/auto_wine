package cn.myflycat.auto_wine.decision;

/** Types of atomic action that a {@link DecisionPlan} can contain. */
public enum DecisionActionType {

    /** No-op; the runtime stays idle for one tick. */
    IDLE,

    /** Wait for a specified number of ticks before the next action. */
    WAIT,

    /** Navigate to a target position. Requires Baritone in ACTIVE mode. */
    NAVIGATE,

    /** Open a container at the current position. */
    OPEN_CONTAINER,

    /** Close the currently open container screen. */
    CLOSE_CONTAINER,

    /** Take items from a container slot into the player inventory. */
    TAKE_ITEM,

    /** Place items from the player inventory into a container slot. */
    PLACE_ITEM,

    /** Halt execution and mark the current plan as stopped. */
    STOP
}
