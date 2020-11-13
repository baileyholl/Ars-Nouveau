package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class VolcanicTile extends AbstractManaTile {
    public VolcanicTile() {
        super(BlockRegistry.VOLCANIC_TILE);
    }

    int progress;
    @Override
    public int getTransferRate() {
        return 1000;
    }

    @Override
    public void tick() {
        if(world.isRemote)
            return;
        if(world.getGameTime() % 20 == 0){
            int numSource = (int) BlockPos.getAllInBox(this.getPos().down().add(1, 0, 1), this.getPos().down().add(-1, 0, -1))
                    .filter(b -> world.getFluidState(b).getFluid() instanceof LavaFluid).map(b -> world.getFluidState(b))
                    .filter(FluidState::isSource).count();
            this.addMana(numSource);
        }
    }

    public void doRandomAction(){

    }

    @Override
    public int getMaxMana() {
        return 1000;
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        progress = tag.getInt("progress");
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.putInt("progress", progress);
        return super.write(tag);
    }
}
