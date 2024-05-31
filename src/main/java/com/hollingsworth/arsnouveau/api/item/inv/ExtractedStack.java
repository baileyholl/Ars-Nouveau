package com.hollingsworth.arsnouveau.api.item.inv;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class ExtractedStack extends SlotReference{

    public ItemStack stack;

    protected ExtractedStack(ItemStack stack, IItemHandler handler, int slot) {
        super(handler, slot);
        this.stack = stack;
    }

    public static ExtractedStack from(IItemHandler handler, int slot, int amount){
        return new ExtractedStack(handler.extractItem(slot, amount, false), handler, slot);
    }

    public static ExtractedStack from(SlotReference slotReference, int amount){
        return from(slotReference.getHandler(), slotReference.getSlot(), amount);
    }

    public static ExtractedStack empty(){
        return new ExtractedStack(ItemStack.EMPTY, null, -1);
    }

    public void replaceAndReturnOrDrop(ItemStack stack, Level level, BlockPos pos){
        this.stack = stack;
        this.stack = this.returnStack();
        if(!this.stack.isEmpty()){
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, this.stack.copy()));
            this.stack.setCount(0);
        }
    }

    /**
     * Returns the itemstack back to the handler and slot it came from, if possible.
     * @return The remainder
     */
    public ItemStack returnStack(){
        if(isEmpty())
            return ItemStack.EMPTY;
        ItemStack remainder = this.handler.insertItem(slot, stack, false);
        remainder = ItemHandlerHelper.insertItemStacked(this.handler, remainder, false);
        return remainder;
    }

    /**
     * Returns the itemstack back to the handler and slot it came from, if possible.
     * Drops the remainder on the ground.
     */
    public void returnOrDrop(Level level, BlockPos pos){
        if(isEmpty())
            return;
        ItemStack remainder = returnStack();
        if(!remainder.isEmpty()){
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, remainder.copy()));
            this.stack = ItemStack.EMPTY;
        }
    }

    public boolean isEmpty() {
        return super.isEmpty() || stack.isEmpty();
    }

    public ItemStack getStack() {
        return stack;
    }
}
