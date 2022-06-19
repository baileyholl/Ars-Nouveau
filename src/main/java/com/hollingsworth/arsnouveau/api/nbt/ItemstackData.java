package com.hollingsworth.arsnouveau.api.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public abstract class ItemstackData extends AbstractData{
    ItemStack stack;

    public ItemstackData(ItemStack stack) {
        super(stack.getOrCreateTag());
        this.stack = stack;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        writeItem();
    }

    /**
     * This must be called to save the tag to the itemstack. Manipulating this object
     * only will not save the tag to the itemstack.
     */
    public void writeItem(){
        CompoundTag tag = new CompoundTag();
        writeToNBT(tag);
        stack.getOrCreateTag().put(getTagString(), tag);
    }

    public CompoundTag getItemTag(){
        return stack.getOrCreateTag().getCompound(getTagString());
    }

    public abstract String getTagString();

}
