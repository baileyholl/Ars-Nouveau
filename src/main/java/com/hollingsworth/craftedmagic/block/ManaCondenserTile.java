package com.hollingsworth.craftedmagic.block;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class ManaCondenserTile extends TileEntity implements ITickableTileEntity {
    public ManaCondenserTile() {
        super(ModBlocks.MANA_CONDENSER_TILE);
    }
    private int counter = 1;

    @Override
    public void tick() {
        if(world.isRemote) {
           // world.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
            return;
        }
        if(world.getGameTime() % 1 != 0)
            return;

        counter += 1;

        if(counter > 8){
            counter = 1;
        }

        BlockState state = world.getBlockState(pos);
        world.setBlockState(pos, state.with(ManaCondenserBlock.stage, counter),3);
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
