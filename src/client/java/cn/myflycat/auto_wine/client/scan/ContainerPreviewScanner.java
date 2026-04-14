package cn.myflycat.auto_wine.client.scan;

import cn.myflycat.auto_wine.mixin.client.HandledScreenAccessor;
import cn.myflycat.auto_wine.scan.ContainerScanResult;
import cn.myflycat.auto_wine.scan.ContainerSlotRole;
import cn.myflycat.auto_wine.scan.ContainerSlotSnapshot;
import cn.myflycat.auto_wine.scan.ItemStackSnapshot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ContainerPreviewScanner {

    private ContainerPreviewScanner() {
    }

    public static Optional<ContainerScanResult> scanCurrentScreen(MinecraftClient client) {
        if (client == null || client.currentScreen == null) {
            return Optional.empty();
        }

        Screen screen = client.currentScreen;
        if (!(screen instanceof HandledScreen<?> handledScreen)) {
            return Optional.empty();
        }

        ScreenHandler handler = ((HandledScreenAccessor) handledScreen).autoWine$getScreenHandler();
        if (handler == null) {
            return Optional.empty();
        }

        return Optional.of(scan(screen, handler));
    }

    public static ContainerScanResult scan(Screen screen, ScreenHandler handler) {
        List<ContainerSlotSnapshot> snapshots = new ArrayList<>();
        List<Slot> slots = handler.slots;
        int totalSlots = slots.size();

        for (int slotIndex = 0; slotIndex < totalSlots; slotIndex++) {
            Slot slot = slots.get(slotIndex);
            ContainerSlotRole role = resolveRole(screen, slotIndex, totalSlots);
            ItemStackSnapshot stackSnapshot = ItemStackSnapshot.from(slot.getStack());
            snapshots.add(ContainerSlotSnapshot.of(slotIndex, slot.getIndex(), slot.x, slot.y, role, stackSnapshot));
        }

        ContainerScanResult result = new ContainerScanResult();
        result.setScannedAtMillis(System.currentTimeMillis());
        result.setScreenClassName(screen.getClass().getName());
        result.setScreenTitle(screen.getTitle().getString());
        result.setScreenHandlerClassName(handler.getClass().getName());
        result.setCursorStack(ItemStackSnapshot.from(handler.getCursorStack()));
        result.setSlots(snapshots);
        return result;
    }

    private static ContainerSlotRole resolveRole(Screen screen, int slotIndex, int totalSlots) {
        if (screen instanceof InventoryScreen) {
            int hotbarStart = Math.max(totalSlots - 9, 0);
            int playerInventoryStart = Math.max(totalSlots - 36, 0);

            if (slotIndex >= hotbarStart) {
                return ContainerSlotRole.HOTBAR;
            }

            if (slotIndex >= playerInventoryStart) {
                return ContainerSlotRole.PLAYER_INVENTORY;
            }

            return ContainerSlotRole.PLAYER_EXTRA;
        }

        int playerInventoryStart = Math.max(totalSlots - 36, 0);
        int hotbarStart = Math.max(totalSlots - 9, playerInventoryStart);

        if (slotIndex >= hotbarStart) {
            return ContainerSlotRole.HOTBAR;
        }

        if (slotIndex >= playerInventoryStart) {
            return ContainerSlotRole.PLAYER_INVENTORY;
        }

        return ContainerSlotRole.CONTAINER;
    }
}

