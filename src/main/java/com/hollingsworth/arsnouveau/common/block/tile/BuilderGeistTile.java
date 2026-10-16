package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BuilderGeistTile extends ModdedTile {
    public BuilderGeistTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.BUILDER_GEIST_TILE, pos, state);
    }
}
