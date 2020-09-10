package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;

public class ArcanePedestalTile extends AnimatedTile implements ITickableTileEntity {
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
    public void tick() { }
}
