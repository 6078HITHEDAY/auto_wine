package cn.myflycat.auto_wine.scan;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemStackSnapshot {

    private String itemId = "minecraft:air";
    private String itemName = "";
    private int count;
    private int maxCount;
    private int damage;
    private int maxDamage;
    private boolean empty = true;
    private boolean damageable;
    private String nbt;

    public ItemStackSnapshot() {
    }

    public static ItemStackSnapshot empty() {
        return new ItemStackSnapshot();
    }

    public static ItemStackSnapshot from(ItemStack stack) {
        ItemStackSnapshot snapshot = new ItemStackSnapshot();
        if (stack == null || stack.isEmpty()) {
            return snapshot;
        }

        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        snapshot.setItemId(itemId == null ? "minecraft:air" : itemId.toString());
        snapshot.setItemName(stack.getName().getString());
        snapshot.setCount(stack.getCount());
        snapshot.setMaxCount(stack.getMaxCount());
        snapshot.setDamage(stack.getDamage());
        snapshot.setMaxDamage(stack.getMaxDamage());
        snapshot.setEmpty(false);
        snapshot.setDamageable(stack.isDamageable());

        if (stack.hasNbt()) {
            NbtCompound nbtCompound = stack.getNbt();
            snapshot.setNbt(nbtCompound == null ? null : nbtCompound.copy().toString());
        }

        return snapshot;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId == null || itemId.isBlank() ? "minecraft:air" : itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName == null ? "" : itemName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = Math.max(count, 0);
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = Math.max(maxCount, 0);
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = Math.max(damage, 0);
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = Math.max(maxDamage, 0);
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public boolean isDamageable() {
        return damageable;
    }

    public void setDamageable(boolean damageable) {
        this.damageable = damageable;
    }

    public String getNbt() {
        return nbt;
    }

    public void setNbt(String nbt) {
        this.nbt = nbt == null || nbt.isBlank() ? null : nbt;
    }
}

