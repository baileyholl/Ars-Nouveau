package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FalseWeaveTile extends MirrorWeaveTile{
    public FalseWeaveTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.FALSE_WEAVE_TILE.get(), pos, state);
    }

    @Override
    public BlockState getDefaultBlockState() {
        return BlockRegistry.FALSE_WEAVE.defaultBlockState();
    }
}
