package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class GhostWeaveTile extends ModdedTile{
    public GhostWeaveTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.GHOST_WEAVE_TILE, pos, state);
    }
}
