package cn.myflycat.auto_wine.scan;

public class ContainerSlotSnapshot {

    private int slotIndex;
    private int inventoryIndex;
    private int x;
    private int y;
    private ContainerSlotRole role = ContainerSlotRole.UNKNOWN;
    private ItemStackSnapshot stack = ItemStackSnapshot.empty();

    public ContainerSlotSnapshot() {
    }

    public static ContainerSlotSnapshot of(int slotIndex, int inventoryIndex, int x, int y, ContainerSlotRole role, ItemStackSnapshot stack) {
        ContainerSlotSnapshot snapshot = new ContainerSlotSnapshot();
        snapshot.setSlotIndex(slotIndex);
        snapshot.setInventoryIndex(inventoryIndex);
        snapshot.setX(x);
        snapshot.setY(y);
        snapshot.setRole(role);
        snapshot.setStack(stack);
        return snapshot;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public void setSlotIndex(int slotIndex) {
        this.slotIndex = Math.max(slotIndex, 0);
    }

    public int getInventoryIndex() {
        return inventoryIndex;
    }

    public void setInventoryIndex(int inventoryIndex) {
        this.inventoryIndex = Math.max(inventoryIndex, 0);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ContainerSlotRole getRole() {
        return role;
    }

    public void setRole(ContainerSlotRole role) {
        this.role = role == null ? ContainerSlotRole.UNKNOWN : role;
    }

    public ItemStackSnapshot getStack() {
        return stack;
    }

    public void setStack(ItemStackSnapshot stack) {
        this.stack = stack == null ? ItemStackSnapshot.empty() : stack;
    }

    public boolean isEmpty() {
        return stack == null || stack.isEmpty();
    }
}

