package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.VitalicSourcelinkTile;
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

public class VitalicSourcelinkBlock extends SourcelinkBlock {
    public static final VoxelShape shape = Stream.of(
            Block.box(6, 12, 6, 10, 16, 10),
            Block.box(6, 0, 6, 10, 8, 10),
            Stream.of(
                    Block.box(7, 7, 0, 9, 14, 6),
                    Block.box(7, 0, 4, 9, 7, 6),
                    Block.box(7, 0, 1, 9, 3, 4)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(0, 7, 7, 6, 14, 9),
                    Block.box(4, 0, 7, 6, 7, 9),
                    Block.box(1, 0, 7, 4, 3, 9)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(7, 7, 10, 9, 14, 16),
                    Block.box(7, 0, 10, 9, 7, 12),
                    Block.box(7, 0, 12, 9, 3, 15)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(10, 7, 7, 16, 14, 9),
                    Block.box(10, 0, 7, 12, 7, 9),
                    Block.box(12, 0, 7, 15, 3, 9)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public VitalicSourcelinkBlock(Properties properties) {
        super(properties);
    }


    public VitalicSourcelinkBlock() {
        this(TickableModBlock.defaultProperties().noOcclusion());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VitalicSourcelinkTile(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }
}
