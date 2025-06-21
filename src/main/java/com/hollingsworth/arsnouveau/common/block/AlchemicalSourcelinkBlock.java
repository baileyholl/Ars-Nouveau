package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.AlchemicalSourcelinkTile;
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

public class AlchemicalSourcelinkBlock extends SourcelinkBlock {
    public static final VoxelShape shape = Stream.of(
            Block.box(3, 3, 3, 13, 9, 13),
            Block.box(6, 12, 6, 10, 16, 10),
            Stream.of(
                    Block.box(7, 9, 2, 9, 14, 6),
                    Block.box(7, 0, 2, 9, 3, 5),
                    Block.box(7, 3, 2, 9, 9, 3)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(2, 9, 7, 6, 14, 9),
                    Block.box(2, 0, 7, 5, 3, 9),
                    Block.box(2, 3, 7, 3, 9, 9)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(7, 9, 10, 9, 14, 14),
                    Block.box(7, 0, 11, 9, 3, 14),
                    Block.box(7, 3, 13, 9, 9, 14)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(10, 9, 7, 14, 14, 9),
                    Block.box(11, 0, 7, 14, 3, 9),
                    Block.box(13, 3, 7, 14, 9, 9)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public AlchemicalSourcelinkBlock() {
        super(TickableModBlock.defaultProperties().noOcclusion());
    }

    public AlchemicalSourcelinkBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemicalSourcelinkTile(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }
}
