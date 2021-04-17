package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ArcanePedestalTile extends AnimatedTile implements IInventory {
    public int frames;
    public ItemEntity entity;
    public ItemStack stack;
    public ArcanePedestalTile() {
        super(BlockRegistry.ARCANE_PEDESTAL_TILE);
    }



    @Override
    public void load(BlockState state, CompoundNBT compound) {
        stack = ItemStack.of((CompoundNBT)compound.get("itemStack"));
        super.load(state, compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        if(stack != null) {
            CompoundNBT reagentTag = new CompoundNBT();
            stack.save(reagentTag);
            compound.put("itemStack", reagentTag);
        }

        return super.save(compound);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return stack == null || stack.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return stack == null ? ItemStack.EMPTY : stack;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack toReturn = getItem(0).copy();
        stack.shrink(1);
        updateBlock();
        return toReturn;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack toReturn = getItem(0).copy();
        stack.shrink(1);
        updateBlock();
        return toReturn;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack s) {
        return stack == null || stack.isEmpty();
    }

    @Override
    public void setItem(int index, ItemStack s) {

        if(stack == null || stack.isEmpty()) {
            stack = s;
            updateBlock();
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }


    @Override
    public void clearContent() {
        this.stack = ItemStack.EMPTY;
    }

    @Override
    public void tick() {

    }
}
