package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class LightTile extends TileEntity {

    public int red = 255;
    public int green = 125;
    public int blue = 255;

    public LightTile() {
        super(BlockRegistry.LIGHT_TILE);
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

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.red = nbt.getInt("red");
        this.green = nbt.getInt("green");
        this.blue = nbt.getInt("blue");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("red", red);
        compound.putInt("green", green);
        compound.putInt("blue", blue);
        return super.write(compound);
    }
}
