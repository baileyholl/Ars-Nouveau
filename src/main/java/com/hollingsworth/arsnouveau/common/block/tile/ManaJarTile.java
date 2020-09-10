package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.common.block.ManaJar;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;

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

}
