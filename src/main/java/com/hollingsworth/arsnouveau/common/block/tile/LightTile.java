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

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        this.red = nbt.getInt("red");
        this.red = red > 0 ? red : 255;
        this.green = nbt.getInt("green");
        green = this.green > 0 ? green : 125;
        this.blue = nbt.getInt("blue");
        blue = this.blue > 0 ? blue : 255;
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putInt("red", red);
        compound.putInt("green", green);
        compound.putInt("blue", blue);
        return super.save(compound);
    }
}
