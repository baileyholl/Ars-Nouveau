package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.util.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class StarbunclePlush extends DirectionalModBlock {

    private static final VoxelShape SHAPE_NORTH = Stream.of(
            Block.box(6.5, 0, 5, 9.5, 3, 8),
            Block.box(5.5, 0, 8, 10.5, 5, 13),
            Block.box(5.5, 3, 4, 10.5, 7, 8),
            Block.box(8.5, 7, 5, 9.5, 12, 6),
            Block.box(6.5, 7, 5, 7.5, 12, 6),
            Block.box(9.5, 8, 5, 10.5, 13, 6),
            Block.box(5.5, 8, 5, 6.5, 13, 6),
            Block.box(4.5, 9, 5, 5.5, 12, 6),
            Block.box(10.5, 9, 5, 11.5, 12, 6),
            Block.box(7.5, 4, 3.5, 8.5, 5, 4),
            Block.box(6.5, 0, 4, 7.5, 1, 5),
            Block.box(8.5, 0, 4, 9.5, 1, 5),
            Block.box(6.5, 1.75, 4, 7.5, 2.75, 5),
            Block.box(8.5, 1.75, 4, 9.5, 2.75, 5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final Map<Direction, VoxelShape> SHAPES = computeShapes();

    private static Map<Direction, VoxelShape> computeShapes() {
        Map<Direction, VoxelShape> map = new EnumMap<>(Direction.class);
        map.put(Direction.NORTH, SHAPE_NORTH);
        map.put(Direction.SOUTH, VoxelShapeUtils.rotateHorizontal(SHAPE_NORTH, Direction.SOUTH));
        map.put(Direction.EAST, VoxelShapeUtils.rotateHorizontal(SHAPE_NORTH, Direction.EAST));
        map.put(Direction.WEST, VoxelShapeUtils.rotateHorizontal(SHAPE_NORTH, Direction.WEST));
        return map;
    }


    public StarbunclePlush() {
        super(Properties.of().strength(0.8F).sound(SoundType.WOOL).noOcclusion());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPES.getOrDefault(pState.getValue(FACING), SHAPE_NORTH);
    }

    @Override
    protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
        return false;
    }
}
