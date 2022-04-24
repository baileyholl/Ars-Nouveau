package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ScryerCrystalTile extends ModdedTile{
    public ScryerCrystalTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public ScryerCrystalTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SCRYER_CRYSTAL_TILE, pos, state);
    }
}
