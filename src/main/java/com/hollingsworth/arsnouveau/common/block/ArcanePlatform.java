package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class ArcanePlatform extends ArcanePedestal{

    public ArcanePlatform() {
        super();
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false).setValue(BlockStateProperties.FACING, Direction.NORTH));
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
            Block.box(11, 7, 13, 15, 9, 16),
            Block.box(7, 1, 13, 9, 5, 16),
            Block.box(1, 7, 13, 5, 9, 16),
            Block.box(7, 11, 13, 9, 15, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape SOUTH = Stream.of(
            Block.box(2, 2, 3, 14, 14, 5),
            Block.box(5, 5, 0, 11, 11, 3),
            Block.box(1, 7, 0, 5, 9, 3),
            Block.box(7, 1, 0, 9, 5, 3),
            Block.box(11, 7, 0, 15, 9, 3),
            Block.box(7, 11, 0, 9, 15, 3)
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


    public float getOffsetScalar(){
        return 0.1f;
    }
}
