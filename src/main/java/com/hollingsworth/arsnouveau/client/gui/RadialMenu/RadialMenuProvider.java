package com.hollingsworth.arsnouveau.client.gui.RadialMenu;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.IntConsumer;

public class RadialMenuProvider {

    private final IntConsumer setSelectedSlot;
    private final List<RadialMenuSlot> radialMenuSlots;
    private final ItemStack itemStack;

    public RadialMenuProvider(IntConsumer setSelectedSlot, List<RadialMenuSlot> radialMenuSlots, ItemStack itemStack) {
        this.setSelectedSlot = setSelectedSlot;
        this.radialMenuSlots = radialMenuSlots;
        this.itemStack = itemStack;
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


}
