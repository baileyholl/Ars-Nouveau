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
        if(world.isRemote)
            return;
        duration++;
        if(duration > maxLength){
            world.setBlockState(pos, Block.getStateById(stateID));

        }
        world.notifyBlockUpdate(this.pos, world.getBlockState(pos),  world.getBlockState(pos), 2);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        stateID = nbt.getInt("state_id");
        duration = nbt.getInt("duration");
        maxLength = nbt.getInt("max_length");
        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("state_id", stateID);
        compound.putInt("duration", duration);
        compound.putInt("max_length", maxLength);
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
