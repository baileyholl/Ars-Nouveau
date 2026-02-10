package com.hollingsworth.arsnouveau.common.light;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface ISkyLightSource {
    default boolean emitsDirectSkyLight(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }
}
