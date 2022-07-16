package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.MycelialSourcelinkTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class MycelialSourcelinkBlock extends SourcelinkBlock {

    public MycelialSourcelinkBlock() {
        this(TickableModBlock.defaultProperties().noOcclusion());
    }

    public MycelialSourcelinkBlock(Properties properties) {
        super(properties);
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MycelialSourcelinkTile(pos, state);
    }
}
