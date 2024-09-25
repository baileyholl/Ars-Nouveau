package com.hollingsworth.arsnouveau.client.container;

import com.hollingsworth.arsnouveau.common.block.tile.StorageLecternTile;

public class SlotStorage {

    /** display position of the inventory slot on the screen x axis */
    public int xDisplayPosition;
    /** display position of the inventory slot on the screen y axis */
    public int yDisplayPosition;
    /** The index of the slot in the inventory. */
    public final int slotIndex;
    /** The inventory we want to extract a slot from. */
    public final StorageLecternTile inventory;
    public StoredItemStack stack;

    public SlotStorage(StorageLecternTile inventory, int slotIndex, int xPosition, int yPosition) {
        this.xDisplayPosition = xPosition;
        this.yDisplayPosition = yPosition;
        this.slotIndex = slotIndex;
        this.inventory = inventory;
    }
}
