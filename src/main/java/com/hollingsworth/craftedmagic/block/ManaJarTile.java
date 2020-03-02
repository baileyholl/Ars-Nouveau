package com.hollingsworth.craftedmagic.block;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class ManaJarTile extends TileEntity implements ITickableTileEntity {
    public ManaJarTile() {
        super(ModBlocks.MANA_JAR_TILE);
    }
    private int counter = 0;

    @Override
    public void tick() {
        if(world.isRemote) {
            // world.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
            return;
        }
        if(world.getGameTime() % 20 != 0)
            return;

        counter += 1;

        if(counter > 10){
            counter = 0;
        }
        System.out.println("Counted");

        BlockState state = world.getBlockState(pos);
        world.setBlockState(pos, state.with(ManaJar.fill, counter),3);
    }

    @Override
    public void read(CompoundNBT tag) {
        counter = tag.getInt("counter");
        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.putInt("counter", counter);
        return super.write(tag);
    }
}
