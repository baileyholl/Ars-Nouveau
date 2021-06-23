package com.hollingsworth.arsnouveau.common.block.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;

public class SummoningTile extends TileEntity implements ITickableTileEntity {
    public int tickCounter; // just for animation, not saved
    public boolean converted;

    public static final BooleanProperty CONVERTED = BooleanProperty.create("converted");

    public SummoningTile(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }

    @Override
    public void tick() {
        if(level.isClientSide)
            return;

        if (!converted) {
            convertedEffect();
            return;
        }
    }

    public void convertedEffect() {
        tickCounter++;
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        this.converted = compound.getBoolean("converted");
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putBoolean("converted", converted);
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
