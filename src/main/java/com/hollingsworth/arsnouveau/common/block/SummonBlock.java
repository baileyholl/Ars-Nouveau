package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;

import javax.annotation.Nullable;

import static com.hollingsworth.arsnouveau.common.block.tile.SummoningTile.CONVERTED;

public abstract class SummonBlock extends TickableModBlock {

    public SummonBlock(Properties properties) {
        super(properties.pushReaction(PushReaction.BLOCK));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        //todo: check if needed?
//        CompoundTag tag = context.getItemInHand().getTag();
//        if (tag != null && tag.contains("BlockEntityTag")) {
//            tag = tag.getCompoundOrEmpty("BlockEntityTag");
//            if (tag.contains("converted") && tag.getBooleanOr("converted", false)) {
//                state = state.setValue(CONVERTED, true);
//            }
//        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONVERTED);
    }
}
