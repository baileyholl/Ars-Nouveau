package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class ArcanePedestalTile extends TileEntity implements ITickableTileEntity {
    public int frames;
    public ItemEntity entity;
    public ItemStack stack;
    public ArcanePedestalTile() {
        super(BlockRegistry.ARCANE_PEDESTAL_TILE);
    }

    @Override
    public void read(CompoundNBT compound) {
        stack = ItemStack.read((CompoundNBT)compound.get("itemStack"));
        super.read(compound);
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
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public void tick() {


    }
}
