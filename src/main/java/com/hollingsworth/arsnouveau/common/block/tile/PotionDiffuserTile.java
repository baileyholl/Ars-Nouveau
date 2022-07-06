package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PotionDiffuserTile extends ModdedTile{
    public PotionDiffuserTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.POTION_DIFFUSER_TILE, pos, state);
    }
}
