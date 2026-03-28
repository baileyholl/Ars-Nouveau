package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.RepositoryCatalogTile;
import com.hollingsworth.arsnouveau.common.items.data.ItemScrollData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class RepositoryCatalog extends TickableModBlock {
    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;

    public RepositoryCatalog() {
        super(ModBlock.defaultProperties().noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemScrollData scrollData = stack.get(DataComponentRegistry.ITEM_SCROLL_DATA);
        if (hand != InteractionHand.MAIN_HAND || !(level.getBlockEntity(pos) instanceof RepositoryCatalogTile controllerTile)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if (scrollData != null) {
            ItemStack resultStack = controllerTile.setNewScroll(stack);
            player.getItemInHand(hand).shrink(1);
            if (resultStack.isEmpty()) {
                player.setItemInHand(hand, ItemStack.EMPTY);
            } else {
                player.setItemInHand(hand, resultStack);
            }
            return InteractionResult.SUCCESS;
        }

        if (player.getItemInHand(hand).isEmpty()) {
            ItemStack resultStack = controllerTile.setNewScroll(ItemStack.EMPTY);
            player.setItemInHand(hand, resultStack);

            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState stateIn, LevelReader pLevel, ScheduledTickAccess pScheduledTickAccess, @NotNull BlockPos currentPos, @NotNull Direction side, @NotNull BlockPos facingPos, @NotNull BlockState facingState, RandomSource pRandom) {
        if (stateIn.getValue(WATERLOGGED)) {
            pScheduledTickAccess.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return stateIn;
    }

    // Called when block is removed — drops stored scroll. No longer an @Override in 1.21.11 (onRemove removed).
    // Invoked via playerWillDestroy to handle player-driven removal.
    protected void dropContentsOnRemove(Level level, BlockPos pos) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof RepositoryCatalogTile tile) {
            if (!tile.scrollStack.isEmpty()) {
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, tile.scrollStack));
            }
        }
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull net.minecraft.world.entity.player.Player player) {
        dropContentsOnRemove(level, pos);
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RepositoryCatalogTile(pPos, pState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public @NotNull BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

}
