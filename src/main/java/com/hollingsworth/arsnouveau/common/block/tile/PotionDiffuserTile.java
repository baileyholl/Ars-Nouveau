package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PotionDiffuserTile extends ModdedTile implements ITickable {
    public PotionDiffuserTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.POTION_DIFFUSER_TILE, pos, state);
    }
}
