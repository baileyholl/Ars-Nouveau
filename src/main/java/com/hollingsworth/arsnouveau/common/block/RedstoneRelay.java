package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.RedstoneRelayTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jetbrains.annotations.Nullable;

public class RedstoneRelay extends TickableModBlock implements EntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public RedstoneRelay() {
        this(defaultProperties().noOcclusion());
    }

    public RedstoneRelay(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWER, 0));
    }


    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        Direction facing = pBlockState.getValue(FACING);
        if (pSide == facing.getOpposite() || !(pBlockAccess.getBlockEntity(pPos) instanceof RedstoneRelayTile redstoneRelayTile))
            return 0;
        return redstoneRelayTile.getOutputPower();
    }

    @Override
    public int getDirectSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
        return getSignal(pState, pLevel, pPos, pDirection);
    }

    @Override
    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, Orientation pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        updatePower(pLevel, pPos, pState);
    }

    public void updatePower(Level pLevel, BlockPos pPos, BlockState pState) {
        int power = 0;
        Direction direction = pState.getValue(FACING);
        power = pLevel.getSignal(pPos.relative(pState.getValue(FACING)), direction);


        if (pLevel.getBlockEntity(pPos) instanceof RedstoneRelayTile redstoneRelayTile) {
            redstoneRelayTile.setLocalPower(power);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState pState, ServerLevel pLevel, BlockPos pPos, boolean pMovedByPiston) {
        if (!(pLevel.getBlockEntity(pPos) instanceof RedstoneRelayTile thisTile))
            return;
        BlockPos worldPosition = pPos.immutable();
        for (BlockPos pos : thisTile.powering) {
            if (pLevel.getBlockEntity(pos) instanceof RedstoneRelayTile redstoneRelayTile) {
                redstoneRelayTile.onParentRemoved(worldPosition);
            }
        }
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
        updatePower(pLevel, pPos, pState);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RedstoneRelayTile(pPos, pState);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWER);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(HorizontalDirectionalBlock.FACING, rot.rotate(state.getValue(HorizontalDirectionalBlock.FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    public boolean isPathfindable(BlockState pState, PathComputationType pType) {
        return false;
    }
}
