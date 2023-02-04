package com.hollingsworth.arsnouveau.common.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import static com.hollingsworth.arsnouveau.setup.BlockRegistry.SKY_BLOCK_TILE_TYPE;

public class SkyBlockTile extends ModdedTile {

    public SkyBlockTile(BlockPos pos, BlockState state) {
        super(SKY_BLOCK_TILE_TYPE, pos, state);
    }

}
