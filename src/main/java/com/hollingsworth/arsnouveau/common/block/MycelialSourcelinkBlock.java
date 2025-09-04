package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.MycelialSourcelinkTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class MycelialSourcelinkBlock extends SourcelinkBlock {
    public static final VoxelShape shape = Stream.of(
            Block.box(1, 10, 1, 15, 13, 15),
            Block.box(4, 13, 4, 12, 16, 12),
            Block.box(6, 0, 6, 10, 10, 10),
            Stream.of(
                    Block.box(7, 5, 4, 9, 10, 6),
                    Block.box(7, 9, 0, 9, 10, 4),
                    Block.box(7, 10, 0, 9, 13, 1),
                    Block.box(7, 13, 0, 9, 16, 4),
                    Block.box(7, 0, 0, 9, 5, 6)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(4, 5, 7, 6, 10, 9),
                    Block.box(0, 9, 7, 4, 10, 9),
                    Block.box(0, 10, 7, 1, 13, 9),
                    Block.box(0, 13, 7, 4, 16, 9),
                    Block.box(0, 0, 7, 6, 5, 9)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(7, 5, 10, 9, 10, 12),
                    Block.box(7, 9, 12, 9, 10, 16),
                    Block.box(7, 10, 15, 9, 13, 16),
                    Block.box(7, 13, 12, 9, 16, 16),
                    Block.box(7, 0, 10, 9, 5, 16)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(10, 5, 7, 12, 10, 9),
                    Block.box(12, 9, 7, 16, 10, 9),
                    Block.box(15, 10, 7, 16, 13, 9),
                    Block.box(12, 13, 7, 16, 16, 9),
                    Block.box(10, 0, 7, 16, 5, 9)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public MycelialSourcelinkBlock() {
        this(TickableModBlock.defaultProperties().noOcclusion());
    }

    public MycelialSourcelinkBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MycelialSourcelinkTile(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }
}
