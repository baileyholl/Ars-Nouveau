package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class IntangibleAirTile extends TileEntity implements ITickableTileEntity {
    public int duration;
    public int maxLength;
    public int stateID;

    public IntangibleAirTile() {
        super(BlockRegistry.INTANGIBLE_AIR_TYPE);
    }

    @Override
    public void tick() {
        if(level.isClientSide)
            return;
        duration++;
        if(duration > maxLength){
            level.setBlockAndUpdate(worldPosition, Block.stateById(stateID));

        }
        level.sendBlockUpdated(this.worldPosition, level.getBlockState(worldPosition),  level.getBlockState(worldPosition), 2);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        stateID = nbt.getInt("state_id");
        duration = nbt.getInt("duration");
        maxLength = nbt.getInt("max_length");
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putInt("state_id", stateID);
        compound.putInt("duration", duration);
        compound.putInt("max_length", maxLength);
        return super.save(compound);
    }

    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 3, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(level.getBlockState(worldPosition),pkt.getTag());
    }
}
