package com.hollingsworth.arsnouveau.api.item.inv;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class ExtractedStack extends SlotReference{

    public ItemStack stack;
    public boolean simulate;

    protected ExtractedStack(ItemStack stack, IItemHandler handler, int slot) {
        this(stack, handler, slot, false);
    }

    protected ExtractedStack(ItemStack stack, IItemHandler handler, int slot, boolean simulate) {
        super(handler, slot);
        this.stack = stack;
        this.simulate = simulate;
    }

    public static ExtractedStack from(IItemHandler handler, int slot, int amount, boolean simulate) {
        return new ExtractedStack(handler.extractItem(slot, amount, simulate), handler, slot, simulate);
    }

    @Deprecated(forRemoval = true)
    public static ExtractedStack from(IItemHandler handler, int slot, int amount){
        return new ExtractedStack(handler.extractItem(slot, amount, false), handler, slot);
    }

    @Deprecated(forRemoval = true)
    public static ExtractedStack from(SlotReference slotReference, int amount){
        return from(slotReference.getHandler(), slotReference.getSlot(), amount);
    }

    public static ExtractedStack from(SlotReference slotReference, int amount, boolean simulate){
        return from(slotReference.getHandler(), slotReference.getSlot(), amount, simulate);
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
        IItemHandler handlerVal = this.handler.get();
        if(handlerVal == null)
            return stack;
        ItemStack remainder = handlerVal.insertItem(slot, stack, simulate);
        remainder = ItemHandlerHelper.insertItemStacked(handlerVal, remainder, simulate);
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
            if(!simulate) {
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, remainder.copy()));
            }
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
