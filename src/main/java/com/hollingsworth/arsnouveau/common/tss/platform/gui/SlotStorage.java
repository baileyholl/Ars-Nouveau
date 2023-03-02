package com.hollingsworth.arsnouveau.common.tss.platform.gui;

import com.hollingsworth.arsnouveau.common.tss.platform.StorageTerminalBlockEntity;
import com.hollingsworth.arsnouveau.common.tss.platform.util.StoredItemStack;

public class SlotStorage {

    /** display position of the inventory slot on the screen x axis */
    public int xDisplayPosition;
    /** display position of the inventory slot on the screen y axis */
    public int yDisplayPosition;
    /** The index of the slot in the inventory. */
    private final int slotIndex;
    /** The inventory we want to extract a slot from. */
    public final StorageTerminalBlockEntity inventory;
    public StoredItemStack stack;

    public SlotStorage(StorageTerminalBlockEntity inventory, int slotIndex, int xPosition, int yPosition) {
        this.xDisplayPosition = xPosition;
        this.yDisplayPosition = yPosition;
        this.slotIndex = slotIndex;
        this.inventory = inventory;
    }
}
