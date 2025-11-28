package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PlanariumProjectorTile extends ModdedTile {
    public PlanariumProjectorTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.PLANARIUM_PROJECTOR_TILE, pos, state);
    }
}
