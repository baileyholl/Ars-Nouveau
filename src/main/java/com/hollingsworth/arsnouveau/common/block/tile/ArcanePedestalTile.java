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
    public void read(BlockState state, CompoundNBT compound) {
        stack = ItemStack.read((CompoundNBT)compound.get("itemStack"));
        super.read(state, compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if(stack != null) {
            CompoundNBT reagentTag = new CompoundNBT();
            stack.write(reagentTag);
            compound.put("itemStack", reagentTag);
        }

        return super.write(compound);
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return stack == null || stack.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return stack == null ? ItemStack.EMPTY : stack;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack toReturn = getStackInSlot(0).copy();
        stack.shrink(1);
        updateBlock();
        return toReturn;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack toReturn = getStackInSlot(0).copy();
        stack.shrink(1);
        updateBlock();
        return toReturn;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack s) {
        return stack == null || stack.isEmpty();
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack s) {

        if(stack == null || stack.isEmpty()) {
            stack = s;
            updateBlock();
        }
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }


    @Override
    public void clear() {
        this.stack = ItemStack.EMPTY;
    }

    @Override
    public void tick() {

    }
}
