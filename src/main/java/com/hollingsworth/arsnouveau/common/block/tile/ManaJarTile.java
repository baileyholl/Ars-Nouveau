package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import com.hollingsworth.arsnouveau.common.block.ManaJar;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;

import javax.annotation.Nullable;

public class ManaJarTile extends AbstractManaTile implements ITickableTileEntity {

    public ManaJarTile() {
        super(BlockRegistry.MANA_JAR_TILE);
        this.setMaxMana(10000);
    }

    @Override
    public void tick() {
        if(world.isRemote) {
            // world.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
            return;
        }
        BlockState state = world.getBlockState(pos);
        world.setBlockState(pos, state.with(ManaJar.fill, this.getCurrentMana() < 1000 ? 0 : this.getCurrentMana() / 1000),3);
    }


    @Override
    public int getTransferRate() {
        return 100;
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


}
