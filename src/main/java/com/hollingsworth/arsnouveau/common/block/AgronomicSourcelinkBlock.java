package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.AgronomicSourcelinkTile;
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

public class AgronomicSourcelinkBlock extends SourcelinkBlock {

    public static final VoxelShape shape = Stream.of(
            Block.box(6, 0, 6, 10, 7, 10),
            Block.box(4, 3, 4, 12, 6, 12),
            Stream.of(
                    Block.box(7, 6, 1, 9, 10, 4),
                    Block.box(7, 3, 4, 9, 16, 6),
                    Block.box(7, 0, 2, 9, 3, 6),
                    Block.box(7, 11, 0, 9, 16, 4)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(1, 6, 7, 4, 10, 9),
                    Block.box(4, 3, 7, 6, 16, 9),
                    Block.box(2, 0, 7, 6, 3, 9),
                    Block.box(0, 11, 7, 4, 16, 9)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(7, 6, 12, 9, 10, 15),
                    Block.box(7, 3, 10, 9, 16, 12),
                    Block.box(7, 0, 10, 9, 3, 14),
                    Block.box(7, 11, 12, 9, 16, 16)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(12, 6, 7, 15, 10, 9),
                    Block.box(10, 3, 7, 12, 16, 9),
                    Block.box(10, 0, 7, 14, 3, 9),
                    Block.box(12, 11, 7, 16, 16, 9)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public AgronomicSourcelinkBlock() {
        super(TickableModBlock.defaultProperties().noOcclusion());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AgronomicSourcelinkTile(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }
}
