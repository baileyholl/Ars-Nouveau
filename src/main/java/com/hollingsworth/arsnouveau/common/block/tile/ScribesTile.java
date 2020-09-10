package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class ScribesTile extends TileEntity implements ITickableTileEntity {

    public ItemEntity entity; // For rendering
    public ItemStack stack;
    public int frames;


    public ScribesTile() {
        super(BlockRegistry.SCRIBES_TABLE_TILE);
    }

    @Override
    public void tick() {

    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        stack = ItemStack.read((CompoundNBT)compound.get("itemStack"));
        super.read(state,compound);
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
        handleUpdateTag(world.getBlockState(pos),pkt.getNbtCompound());
    }
}
