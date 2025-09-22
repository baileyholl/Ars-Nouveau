package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.DecorBlossomTile;
import com.hollingsworth.arsnouveau.common.util.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DecorBlossom extends ModBlock implements ITickableBlock {
    public static VoxelShape UP_SHAPE = makeShape();
    public static VoxelShape DOWN_SHAPE = VoxelShapeUtils.rotate(UP_SHAPE, Direction.UP);
    public static VoxelShape NORTH_SHAPE = VoxelShapeUtils.rotate(UP_SHAPE, Direction.SOUTH);
    public static VoxelShape SOUTH_SHAPE = VoxelShapeUtils.rotate(UP_SHAPE, Direction.NORTH);
    public static VoxelShape EAST_SHAPE = VoxelShapeUtils.rotate(UP_SHAPE, Direction.WEST);
    public static VoxelShape WEST_SHAPE = VoxelShapeUtils.rotate(UP_SHAPE, Direction.EAST);

    private static VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.01874999999999999, 0.4375, 0.5625, 0.1875, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0.1875, 0.1875, 0.8125, 0.5625, 0.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.5625, 0.375, 0.625, 0.5625, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.1875, 0.5, 0.5625, 0.5625, 0.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.015625, 0.1875, 0.8125, 0.015625, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0.1875, 0.1875, 0.8125, 0.5625, 0.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0.1875, 0.1875, 0.8125, 0.5625, 0.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0.1875, 0.1875, 0.8125, 0.5625, 0.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.1875, 0.5, 0.5625, 0.5625, 0.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.1875, 0.1875, 0.8125, 0.5625, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.01874999999999999, 0.4375, 0.5625, 0.1875, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.015625, 0.1875, 0.8125, 0.015625, 0.8125), BooleanOp.OR);

        return shape;
    }


    public DecorBlossom() {
        super(Properties.of().mapColor(MapColor.PLANT)
                .instabreak()
                .noCollission()
                .sound(SoundType.SPORE_BLOSSOM)
                .pushReaction(PushReaction.DESTROY));
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getClickedFace());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(BlockStateProperties.FACING);
        switch (direction) {
            case UP -> {
                return UP_SHAPE;
            }
            case DOWN -> {
                return DOWN_SHAPE;
            }
            case NORTH -> {
                return NORTH_SHAPE;
            }
            case SOUTH -> {
                return SOUTH_SHAPE;
            }
            case EAST -> {
                return EAST_SHAPE;
            }
            case WEST -> {
                return WEST_SHAPE;
            }
            default -> {
                return super.getShape(state, level, pos, context);
            }
        }
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(BlockStateProperties.FACING, rot.rotate(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof DecorBlossomTile tile) {
            tile.onRandomTick();
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DecorBlossomTile(pos, state);
    }
}
