package cn.myflycat.auto_wine.scan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ContainerScanResult {

    private long scannedAtMillis;
    private String screenClassName = "";
    private String screenTitle = "";
    private String screenHandlerClassName = "";
    private ItemStackSnapshot cursorStack = ItemStackSnapshot.empty();
    private int slotCount;
    private List<ContainerSlotSnapshot> slots = new ArrayList<>();

    public ContainerScanResult() {
    }

    public long getScannedAtMillis() {
        return scannedAtMillis;
    }

    public void setScannedAtMillis(long scannedAtMillis) {
        this.scannedAtMillis = scannedAtMillis;
    }

    public String getScreenClassName() {
        return screenClassName;
    }

    public void setScreenClassName(String screenClassName) {
        this.screenClassName = screenClassName == null ? "" : screenClassName;
    }

    public String getScreenTitle() {
        return screenTitle;
    }

    public void setScreenTitle(String screenTitle) {
        this.screenTitle = screenTitle == null ? "" : screenTitle;
    }

    public String getScreenHandlerClassName() {
        return screenHandlerClassName;
    }

    public void setScreenHandlerClassName(String screenHandlerClassName) {
        this.screenHandlerClassName = screenHandlerClassName == null ? "" : screenHandlerClassName;
    }

    public ItemStackSnapshot getCursorStack() {
        return cursorStack;
    }

    public void setCursorStack(ItemStackSnapshot cursorStack) {
        this.cursorStack = cursorStack == null ? ItemStackSnapshot.empty() : cursorStack;
    }

    public int getSlotCount() {
        return slotCount;
    }

    public void setSlotCount(int slotCount) {
        this.slotCount = Math.max(slotCount, 0);
    }

    public List<ContainerSlotSnapshot> getSlots() {
        return Collections.unmodifiableList(slots);
    }

    public void setSlots(List<ContainerSlotSnapshot> slots) {
        this.slots = slots == null ? new ArrayList<>() : new ArrayList<>(slots);
        this.slotCount = this.slots.size();
    }

    public int getFilledSlotCount() {
        int count = 0;
        for (ContainerSlotSnapshot slot : slots) {
            if (slot != null && !slot.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public List<ContainerSlotSnapshot> getSlotsByRole(ContainerSlotRole role) {
        List<ContainerSlotSnapshot> filtered = new ArrayList<>();
        if (role == null) {
            return filtered;
        }

        for (ContainerSlotSnapshot slot : slots) {
            if (slot != null && role == slot.getRole()) {
                filtered.add(slot);
            }
        }
        return filtered;
    }

    public Map<ItemKey, Integer> aggregateItemCounts() {
        Map<ItemKey, Integer> counts = new LinkedHashMap<>();
        for (ContainerSlotSnapshot slot : slots) {
            if (slot == null || slot.isEmpty()) {
                continue;
            }

            ItemKey key = ItemKey.from(slot.getStack());
            counts.merge(key, slot.getStack().getCount(), Integer::sum);
        }
        return counts;
    }
}

