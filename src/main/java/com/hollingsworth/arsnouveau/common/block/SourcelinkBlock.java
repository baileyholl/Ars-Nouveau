package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.SourcelinkTile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public abstract class SourcelinkBlock extends TickableModBlock {
    public SourcelinkBlock(Properties properties, String registry) {
        super(properties, registry);
    }

    public SourcelinkBlock(String registryName) {
        super(registryName);
    }

    @Override
    public boolean isRandomlyTicking(BlockState p_149653_1_) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
        super.randomTick(state, worldIn, pos, random);
        SourcelinkTile tile = (SourcelinkTile) worldIn.getBlockEntity(pos);
        if(tile == null)
            return;
        tile.doRandomAction();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

}
