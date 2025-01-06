package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ScryerCrystalTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ScryerCrystal extends TickableModBlock {
    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    public static final BooleanProperty BEING_VIEWED = BooleanProperty.create("being_viewed");

    public static VoxelShape SOUTH = box(5, 5, 0, 11, 11, 1);
    public static VoxelShape NORTH = box(5, 5, 15, 11, 11, 16);
    public static VoxelShape EAST = box(0, 5, 5, 1, 11, 11);
    public static VoxelShape WEST = box(15, 5, 5, 16, 11, 11);
    public static VoxelShape UP = box(5, 0, 5, 11, 1, 11);
    public static VoxelShape DOWN = box(5, 15, 5, 11, 16, 11);

    public ScryerCrystal(Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any().setValue(FACING, Direction.NORTH)).setValue(BEING_VIEWED, false));
    }

    public ScryerCrystal() {
        this(defaultProperties().noOcclusion());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BEING_VIEWED);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pState.getValue(FACING) == Direction.SOUTH) {
            return SOUTH;
        }
        if (pState.getValue(FACING) == Direction.NORTH) {
            return NORTH;
        }
        if (pState.getValue(FACING) == Direction.EAST) {
            return EAST;
        }
        if (pState.getValue(FACING) == Direction.WEST) {
            return WEST;
        }
        if (pState.getValue(FACING) == Direction.UP) {
            return UP;
        }
        if (pState.getValue(FACING) == Direction.DOWN) {
            return DOWN;
        }

        return EAST;
    }


    /**
     * Get the position where the dispenser at the given Coordinates should dispense to.
     */
    public static Position getDispensePosition(BlockPos pos, Direction direction) {
        // Offset to get closer to the eye.
        double negOffset = -0.49;
        double d0 = pos.getX() + 0.5 + negOffset * (double) direction.getStepX();
        double d1 = pos.getY() + 0.5 + negOffset * (double) direction.getStepY();
        double d2 = pos.getZ() + 0.5 + negOffset * (double) direction.getStepZ();
        return new Vec3(d0, d1, d2);
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ScryerCrystalTile(pPos, pState);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.getBlockEntity(pPos) instanceof ScryerCrystalTile scryerCrystalTile && pPlayer.getItemInHand(pHand).isEmpty() & pHand == InteractionHand.MAIN_HAND) {
            scryerCrystalTile.mountCamera(pLevel, pPos, pPlayer);
        }
        return super.useItemOn(stack, pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        if (worldIn.getBlockEntity(pos) instanceof ScryerCrystalTile scryerCrystalTile) {
            return scryerCrystalTile.playersViewing;
        }
        return 0;
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getClickedFace());
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }
}
