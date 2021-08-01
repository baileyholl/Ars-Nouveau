package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;

import javax.annotation.Nullable;

import static com.hollingsworth.arsnouveau.common.block.tile.SummoningTile.CONVERTED;

public class SummonBlock extends ModBlock{
    public SummonBlock(Properties properties, String registry) {
        super(properties, registry);
    }

    public SummonBlock(String string){
        super(string);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);
        CompoundNBT tag = context.getItemInHand().getTag();
        if(tag != null && tag.contains("BlockEntityTag")){
            tag = tag.getCompound("BlockEntityTag");
            if(tag.contains("converted") && tag.getBoolean("converted")){
                state = state.setValue(CONVERTED, true);
            }
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(CONVERTED);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
        return PushReaction.BLOCK;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}
