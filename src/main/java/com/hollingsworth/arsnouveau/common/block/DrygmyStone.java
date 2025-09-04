package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.DrygmyTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

import static com.hollingsworth.arsnouveau.common.block.tile.SummoningTile.CONVERTED;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class DrygmyStone extends SummonBlock implements SimpleWaterloggedBlock {
    public static VoxelShape shape = Stream.of(
            Block.box(16, 3, 9.5, 16, 6, 11.5),
            Block.box(9, 0, 0.5, 12, 5, 2.5),
            Block.box(4, 0, 0.5, 7, 5, 2.5),
            Block.box(3.5, 5, 0, 12.5, 7, 3),
            Block.box(13.5, 0, 4, 15.5, 6, 7),
            Block.box(13.5, 0, 9, 15.5, 6, 12),
            Block.box(13, 6, 3.5, 16, 8, 12.5),
            Block.box(4, 0, 13.5, 7, 7, 15.5),
            Block.box(9, 0, 13.5, 12, 7, 15.5),
            Block.box(3.5, 7, 13, 12.5, 9, 16),
            Block.box(0.5, 0, 4, 2.5, 8, 7),
            Block.box(0.5, 0, 9, 2.5, 8, 12),
            Block.box(6, 9, 6, 10, 11, 10),
            Block.box(6, 0, 6, 10, 2, 10),
            Block.box(7, 2, 7, 9, 9, 9),
            Block.box(0, 8, 3.5, 3, 10, 12.5),
            Block.box(10, 5, 6, 10, 9, 10),
            Block.box(6, 5, 6, 6, 9, 10),
            Block.box(6, 5, 10, 10, 9, 10),
            Block.box(6, 5, 6, 10, 9, 6),
            Block.box(6.5, 1, 0, 10.5, 5, 0),
            Block.box(5.5, 2, 3, 8.5, 5, 3),
            Block.box(4.5, 5, 16, 8.5, 7, 16),
            Block.box(9.5, 3, 13, 11.5, 7, 13),
            Block.box(16, 4, 4.5, 16, 6, 8.5),
            Block.box(3, 4, 5.5, 3, 8, 9.5),
            Block.box(0, 4, 5.5, 0, 8, 7.5),
            Block.box(0, 3, 8.5, 0, 8, 11.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(Shapes.block());

    public DrygmyStone() {
        super(defaultProperties().noOcclusion().lightLevel((b) -> 8));
        registerDefaultState(defaultBlockState().setValue(CONVERTED, false).setValue(BlockStateProperties.WATERLOGGED, false));
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DrygmyTile(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return shape;
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction side, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        return stateIn;
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        if (!pLevel.isClientSide() && pLevel.getBlockEntity(pPos) instanceof DrygmyTile henge) {
            henge.isOff = pLevel.hasNeighborSignal(pPos);
            henge.updateBlock();
        }
    }

    @Override
    public boolean isPathfindable(BlockState pState, PathComputationType pType) {
        return false;
    }
}
