package com.hollingsworth.arsnouveau.client.gui.RadialMenu;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.IntConsumer;

public class RadialMenu {

    private final IntConsumer setSelectedSlot;
    private final List<RadialMenuSlot> radialMenuSlots;
    private final ItemStack itemStack;
    private final boolean showMoreSecondaryItems;

    public RadialMenu(IntConsumer setSelectedSlot, List<RadialMenuSlot> radialMenuSlots, ItemStack itemStack, boolean showMoreSecondaryItems) {
        this.setSelectedSlot = setSelectedSlot;
        this.radialMenuSlots = radialMenuSlots;
        this.itemStack = itemStack;
        this.showMoreSecondaryItems = showMoreSecondaryItems;
    }

    public List<RadialMenuSlot> getRadialMenuSlots() {
        return radialMenuSlots;
    }

    public void setCurrentSlot(int slot) {
        setSelectedSlot.accept(slot);
    }

    public CompoundTag getTag() {
        return itemStack.getTag();
    }

    public boolean isShowMoreSecondaryItems() {
        return showMoreSecondaryItems;
    }

}
