package cn.myflycat.auto_wine.scan;

import java.util.Objects;

public class ItemKey {

    private String itemId = "minecraft:air";
    private int damage;
    private String nbt;

    public ItemKey() {
    }

    public static ItemKey from(ItemStackSnapshot snapshot) {
        ItemKey key = new ItemKey();
        if (snapshot == null) {
            return key;
        }

        key.setItemId(snapshot.getItemId());
        key.setDamage(snapshot.getDamage());
        key.setNbt(snapshot.getNbt());
        return key;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId == null || itemId.isBlank() ? "minecraft:air" : itemId;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = Math.max(damage, 0);
    }

    public String getNbt() {
        return nbt;
    }

    public void setNbt(String nbt) {
        this.nbt = nbt == null || nbt.isBlank() ? null : nbt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ItemKey other)) {
            return false;
        }
        return damage == other.damage && Objects.equals(itemId, other.itemId) && Objects.equals(nbt, other.nbt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, damage, nbt);
    }

    @Override
    public String toString() {
        return "ItemKey{" +
            "itemId='" + itemId + '\'' +
            ", damage=" + damage +
            ", nbt='" + nbt + '\'' +
            '}';
    }
}

