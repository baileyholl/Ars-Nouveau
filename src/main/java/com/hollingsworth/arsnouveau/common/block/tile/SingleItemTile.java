package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.util.registry.RegistryWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;


public class SingleItemTile extends ModdedTile implements Container{
    protected ItemStack stack = ItemStack.EMPTY;
    public ItemEntity renderEntity;

    public SingleItemTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }


    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return stack;
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        ItemStack copyStack = stack.copy().split(pAmount);
        stack.shrink(pAmount);
        updateBlock();
        return copyStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        ItemStack stack = this.stack.copy();
        this.stack = ItemStack.EMPTY;
        updateBlock();
        return stack;
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        this.stack = pStack;
        updateBlock();
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return stack.isEmpty();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void clearContent() {
        this.stack = ItemStack.EMPTY;
        updateBlock();
    }

    public ItemStack getStack(){
        return this.stack;
    }

    public void setStack(ItemStack otherStack){
        this.stack = otherStack;
        updateBlock();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        stack = ItemStack.of((CompoundTag) compound.get("itemStack"));
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        if (stack != null) {
            CompoundTag stackTag = new CompoundTag();
            stack.save(stackTag);
            tag.put("itemStack", stackTag);
        }
    }
}
