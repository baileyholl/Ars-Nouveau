package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ArcanePedestalTile extends SingleItemTile implements Container {
    public float frames;
    public boolean hasSignal;

    public ArcanePedestalTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }


    public ArcanePedestalTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARCANE_PEDESTAL_TILE.get(), pos, state);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("hasSignal", hasSignal);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.hasSignal = compound.getBoolean("hasSignal");
    }
}
