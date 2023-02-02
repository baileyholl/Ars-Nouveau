package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class ArcanePlatform extends ArcanePedestal{

    public ArcanePlatform() {
        super();
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false).setValue(BlockStateProperties.FACING, Direction.NORTH));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArcanePedestalTile(BlockRegistry.ARCANE_PLATFORM_TILE.get(), pos, state);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(BlockStateProperties.FACING, rot.rotate(state.getValue(BlockStateProperties.FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(BlockStateProperties.FACING)));
    }

    public static final VoxelShape UP = Stream.of(
            Block.box(2, 3, 2, 14, 5, 14),
            Block.box(5, 0, 5, 11, 3, 11),
            Block.box(7, 0, 1, 9, 3, 5),
            Block.box(1, 0, 7, 5, 3, 9),
            Block.box(7, 0, 11, 9, 3, 15),
            Block.box(11, 0, 7, 15, 3, 9)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape DOWN = Stream.of(
            Block.box(2, 11, 2, 14, 13, 14),
            Block.box(5, 13, 5, 11, 16, 11),
            Block.box(7, 13, 1, 9, 16, 5),
            Block.box(11, 13, 7, 15, 16, 9),
            Block.box(7, 13, 11, 9, 16, 15),
            Block.box(1, 13, 7, 5, 16, 9)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape EAST = Stream.of(
            Block.box(3, 2, 2, 5, 14, 14),
            Block.box(0, 5, 5, 3, 11, 11),
            Block.box(0, 7, 11, 3, 9, 15),
            Block.box(0, 1, 7, 3, 5, 9),
            Block.box(0, 7, 1, 3, 9, 5),
            Block.box(0, 11, 7, 3, 15, 9)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape WEST = Stream.of(
            Block.box(11, 2, 2, 13, 14, 14),
            Block.box(13, 5, 5, 16, 11, 11),
            Block.box(13, 7, 1, 16, 9, 5),
            Block.box(13, 1, 7, 16, 5, 9),
            Block.box(13, 7, 11, 16, 9, 15),
            Block.box(13, 11, 7, 16, 15, 9)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape NORTH = Stream.of(
            Block.box(2, 2, 11, 14, 14, 13),
            Block.box(5, 5, 13, 11, 11, 16),
            Block.box(7, 7, 13, 9, 9, 16),
            Block.box(7, 1, 13, 9, 5, 16),
            Block.box(11, 7, 13, 15, 9, 16),
            Block.box(1, 11, 13, 5, 15, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape SOUTH = Stream.of(
            Block.box(2, 2, 3, 14, 14, 5),
            Block.box(5, 5, 0, 11, 11, 3),
            Block.box(7, 7, 0, 9, 9, 3),
            Block.box(7, 1, 0, 9, 5, 3),
            Block.box(1, 7, 0, 5, 9, 3),
            Block.box(11, 11, 0, 15, 15, 3)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        switch(facing){
            case UP:
                return UP;
            case DOWN:
                return DOWN;
            case EAST:
                return EAST;
            case WEST:
                return WEST;
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
        }
        return UP;
    }
}
