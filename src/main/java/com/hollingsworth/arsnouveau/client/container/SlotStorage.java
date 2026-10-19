package com.hollingsworth.arsnouveau.client.container;

public class SlotStorage {

    /**
     * display position of the inventory slot on the screen x axis
     */
    public int xDisplayPosition;
    /**
     * display position of the inventory slot on the screen y axis
     */
    public int yDisplayPosition;
    public StoredItemStack stack;
    public boolean show;

    public SlotStorage(int xPosition, int yPosition, boolean show) {
        this.xDisplayPosition = xPosition;
        this.yDisplayPosition = yPosition;
        this.show = show;
    }

    public int xPosition() {
        return xDisplayPosition;
    }

    public int yPosition() {
        return yDisplayPosition;
    }

    public StoredItemStack stack() {
        return stack;
    }

    public void setStack(StoredItemStack stack) {
        this.stack = stack;
    }
}
