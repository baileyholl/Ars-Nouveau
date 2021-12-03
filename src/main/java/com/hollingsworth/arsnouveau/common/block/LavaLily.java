package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LavaLily extends BushBlock {
    protected static final VoxelShape LILY_PAD_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.5D, 16.0D);

    public LavaLily() {
        super(TickableModBlock.defaultProperties().noOcclusion());
        setRegistryName(LibBlockNames.LAVA_LILY);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return LILY_PAD_AABB;
    }

    public static final IntegerProperty LOC = IntegerProperty.create("loc", 0, 2);

    public BlockState getState(Level world, BlockPos pos){
        BlockState state = defaultBlockState();
        if(world.getBlockState(pos.below()).getBlock() == Blocks.STONE)
            state = state.setValue(LOC, 0);
        if(world.getBlockState(pos.below()).getBlock() == Blocks.MAGMA_BLOCK)
            state = state.setValue(LOC, 1);
        if(world.getBlockState(pos.below()).getBlock() == Blocks.LAVA)
            state = state.setValue(LOC, 2);
        return state;
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return getState(context.getLevel(), context.getClickedPos());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LOC);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return super.canSurvive(state, worldIn, pos);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
        FluidState fluidstate = worldIn.getFluidState(pos);
        FluidState fluidstate1 = worldIn.getFluidState(pos.above());
        return fluidstate1.getType() == Fluids.EMPTY;
    }
}
