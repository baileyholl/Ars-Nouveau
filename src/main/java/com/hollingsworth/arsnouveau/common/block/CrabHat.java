package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.BlockGetter;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.block.Block;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.block.SoundType;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.block.state.BlockState;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.pathfinder.PathComputationType;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.phys.shapes.BooleanOp;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.phys.shapes.CollisionContext;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.phys.shapes.Shapes;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class CrabHat extends ModBlock {
    public CrabHat() {
        super(BlockRegistry.newBlockProperties().strength(0.8F).sound(SoundType.WOOL).noOcclusion().noLootTable());
    }

    public static VoxelShape shape = Stream.of(
            Block.box(0, 13, 0, 16, 17, 16),
            Block.box(2, 0, 2, 14, 13, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }

    @Override
    protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
        return false;
    }
}
