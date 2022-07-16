package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.VitalicSourcelinkTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class VitalicSourcelinkBlock extends SourcelinkBlock {
    public VitalicSourcelinkBlock(Properties properties) {
        super(properties);
    }


    public VitalicSourcelinkBlock() {
        this(TickableModBlock.defaultProperties().noOcclusion());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VitalicSourcelinkTile(pos, state);
    }

}
