package com.hollingsworth.arsnouveau.client.container;

import com.hollingsworth.arsnouveau.common.block.tile.StorageLecternTile;

public class SlotStorage {

    /**
     * display position of the inventory slot on the screen x axis
     */
    public int xDisplayPosition;
    /**
     * display position of the inventory slot on the screen y axis
     */
    public int yDisplayPosition;
    /**
     * The index of the slot in the inventory.
     */
    public final int slotIndex;
    /**
     * The inventory we want to extract a slot from.
     */
    public final StorageLecternTile inventory;
    public StoredItemStack stack;
    public boolean show;

    public SlotStorage(StorageLecternTile inventory, int slotIndex, int xPosition, int yPosition, boolean show) {
        this.xDisplayPosition = xPosition;
        this.yDisplayPosition = yPosition;
        this.slotIndex = slotIndex;
        this.inventory = inventory;
        this.show = show;
    }

    public int xPosition() {
        return xDisplayPosition;
    }

    public int yPosition() {
        return yDisplayPosition;
    }

    public int slot() {
        return slotIndex;
    }

    public StorageLecternTile inventory() {
        return inventory;
    }

    public StoredItemStack stack() {
        return stack;
    }

    public void setStack(StoredItemStack stack) {
        this.stack = stack;
    }
}
