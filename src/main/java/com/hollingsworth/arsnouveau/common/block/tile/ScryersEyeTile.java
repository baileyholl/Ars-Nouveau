package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ScryersEyeTile extends ModdedTile{
    public ScryersEyeTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public ScryersEyeTile(BlockPos pos, BlockState state) {
        this(BlockRegistry.SCRYERS_EYE_TILE, pos, state);
    }

}
