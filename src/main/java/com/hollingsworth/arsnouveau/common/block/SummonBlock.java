package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;

import javax.annotation.Nullable;

import static com.hollingsworth.arsnouveau.common.block.tile.SummoningTile.CONVERTED;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class SummonBlock extends TickableModBlock {

    public SummonBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        CompoundTag tag = context.getItemInHand().getTag();
        if (tag != null && tag.contains("BlockEntityTag")) {
            tag = tag.getCompound("BlockEntityTag");
            if (tag.contains("converted") && tag.getBoolean("converted")) {
                state = state.setValue(CONVERTED, true);
            }
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONVERTED);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
        return PushReaction.BLOCK;
    }


}
