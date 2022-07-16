package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.AlchemicalSourcelinkTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class AlchemicalSourcelinkBlock extends SourcelinkBlock {

    public AlchemicalSourcelinkBlock() {
        super(TickableModBlock.defaultProperties().noOcclusion());
    }

    public AlchemicalSourcelinkBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemicalSourcelinkTile(pos, state);
    }
}
