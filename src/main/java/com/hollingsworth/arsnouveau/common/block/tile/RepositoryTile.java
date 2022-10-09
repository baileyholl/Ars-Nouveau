package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class RepositoryTile extends ModdedTile{
    public RepositoryTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.REPOSITORY_TILE, pos, state);
    }
}
