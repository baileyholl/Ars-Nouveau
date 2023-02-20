package com.hollingsworth.arsnouveau.common.tss.platform.gui;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class CompoundSlot extends FakeSlot{

    CompoundTag tag;
    Consumer<CompoundTag> tagChange;
    public CompoundSlot(Consumer<CompoundTag> tagChange) {
        this.tagChange = tagChange;
    }

    @Override
    public void set(ItemStack p_40240_) {
        super.set(p_40240_);
        setTag(p_40240_.getOrCreateTag());
        tagChange.accept(tag);
    }

    public void setTag(CompoundTag tag) {
        this.tag = tag;
        setChanged();
    }
}
