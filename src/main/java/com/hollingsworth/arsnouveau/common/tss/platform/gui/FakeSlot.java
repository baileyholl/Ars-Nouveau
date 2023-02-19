package com.hollingsworth.arsnouveau.common.tss.platform.gui;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

class FakeSlot extends Slot {
    private static final Container DUMMY = new SimpleContainer(1);

    public FakeSlot() {
        super(DUMMY, 0, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    @Override
    public boolean allowModification(Player p_150652_) {
        return false;
    }

    @Override
    public void set(ItemStack p_40240_) {
    }

    @Override
    public ItemStack remove(int p_40227_) {
        return ItemStack.EMPTY;
    }
}
