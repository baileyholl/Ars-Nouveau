package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.SkyBlockTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SkyWeave extends MirrorWeave implements ITickableBlock{

    public SkyWeave(Properties properties) {
        super(properties);
    }

    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new SkyBlockTile(p_153215_, p_153216_);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof SkyBlockTile sbt && sbt.showFacade()) {
            return sbt.mimicState.canOcclude() ? sbt.mimicState.getOcclusionShape(level, pos) : Shapes.empty();
        }
        return super.getOcclusionShape(state, level, pos);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        var newState = super.updateShape(state, direction, neighborState, level, pos, neighborPos);

        if (level.isClientSide() && level.getBlockEntity(pos) instanceof SkyBlockTile tile) {
            tile.recalculateFaceVisibility(direction);
        }

        return newState;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof SkyBlockTile tile && !tile.showFacade();
    }
}
