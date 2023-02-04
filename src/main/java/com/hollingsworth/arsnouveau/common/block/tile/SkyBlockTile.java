package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;


public class SkyBlockTile extends ModdedTile {

    public SkyBlockTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SKY_BLOCK_TILE.get(), pos, state);
    }

}
