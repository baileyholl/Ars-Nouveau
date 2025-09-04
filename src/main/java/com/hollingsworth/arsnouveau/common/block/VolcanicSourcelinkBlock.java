package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.VolcanicSourcelinkTile;
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

public class VolcanicSourcelinkBlock extends SourcelinkBlock {

    public static VoxelShape shape = Stream.of(
            Block.box(4, 0, 4, 12, 6, 12),
            Block.box(6, 6, 6, 10, 16, 10),
            Stream.of(
                    Block.box(7, 12, 3, 9, 14, 6),
                    Block.box(7, 6, 4, 9, 12, 6),
                    Block.box(7, 3, 2, 9, 9, 4),
                    Block.box(7, 0, 0, 9, 3, 4)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(3, 12, 7, 6, 14, 9),
                    Block.box(4, 6, 7, 6, 12, 9),
                    Block.box(2, 3, 7, 4, 9, 9),
                    Block.box(0, 0, 7, 4, 3, 9)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(7, 12, 10, 9, 14, 13),
                    Block.box(7, 6, 10, 9, 12, 12),
                    Block.box(7, 3, 12, 9, 9, 14),
                    Block.box(7, 0, 12, 9, 3, 16)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(10, 12, 7, 13, 14, 9),
                    Block.box(10, 6, 7, 12, 12, 9),
                    Block.box(12, 3, 7, 14, 9, 9),
                    Block.box(12, 0, 7, 16, 3, 9)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public VolcanicSourcelinkBlock() {
        super(defaultProperties().noOcclusion().lightLevel(state -> 15));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VolcanicSourcelinkTile(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }
}
