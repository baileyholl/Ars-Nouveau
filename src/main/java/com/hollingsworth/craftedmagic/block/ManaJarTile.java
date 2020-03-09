package com.hollingsworth.craftedmagic.block;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class ManaJarTile extends AbstractManaTile implements ITickableTileEntity {
    public ManaJarTile() {
        super(ModBlocks.MANA_JAR_TILE);
        this.setMaxMana(10000);
    }

    @Override
    public void tick() {
        if(world.isRemote) {
            // world.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
            return;
        }
        if(world.getGameTime() % 20 != 0)
            return;

        BlockState state = world.getBlockState(pos);
        world.setBlockState(pos, state.with(ManaJar.fill, this.getCurrentMana() / 1000),3);
    }


    @Override
    public int getTransferRate() {
        return 100;
    }
}
