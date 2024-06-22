package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class RitualBrazierBlock extends TickableModBlock {
    public static VoxelShape shape = Stream.of(
            Block.box(3, 12, 3, 13, 15, 13),
            Block.box(6, 0, 6, 10, 12, 10),
            Stream.of(
                    Block.box(2, 14, 2, 11, 16, 5),
                    Block.box(7, 11, 1, 9, 15, 6),
                    Block.box(7, 0, 0, 9, 5, 4),
                    Block.box(7, 0, 4, 9, 11, 6)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(2, 14, 5, 5, 16, 14),
                    Block.box(1, 11, 7, 6, 15, 9),
                    Block.box(0, 0, 7, 4, 5, 9),
                    Block.box(4, 0, 7, 6, 11, 9)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(5, 14, 11, 14, 16, 14),
                    Block.box(7, 11, 10, 9, 15, 15),
                    Block.box(7, 0, 12, 9, 5, 16),
                    Block.box(7, 0, 10, 9, 11, 12)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(11, 14, 2, 14, 16, 11),
                    Block.box(10, 11, 7, 15, 15, 9),
                    Block.box(12, 0, 7, 16, 5, 9),
                    Block.box(10, 0, 7, 12, 11, 9)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public RitualBrazierBlock() {
        super(defaultProperties().noOcclusion().pushReaction(PushReaction.BLOCK).lightLevel((b) -> b.getValue(LIT) ? 15 : 0));
        registerDefaultState(defaultBlockState().setValue(LIT, false));
    }

    public static final Property<Boolean> LIT = BooleanProperty.create("lit");

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!(worldIn.getBlockEntity(pos) instanceof RitualBrazierTile tile) || handIn != InteractionHand.MAIN_HAND)
            return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);
        ItemStack heldStack = player.getMainHandItem();
        if (heldStack.isEmpty() && tile.ritual != null && !tile.isRitualDone()) {
            tile.startRitual(player);
        }
        if(!heldStack.isEmpty()){
            tile.tryBurnStack(heldStack);
        }
        return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
        if (!world.isClientSide() && world.getBlockEntity(pos) instanceof RitualBrazierTile tile) {
            boolean wasOff = tile.isOff;
            tile.isOff = world.hasNeighborSignal(pos);
            if (wasOff != world.hasNeighborSignal(pos) && tile.ritual != null) {
                tile.ritual.onStatusChanged(tile.isOff);
            }
            if (world.hasNeighborSignal(pos) && tile.ritual != null && tile.ritual.canStart(null)) {
                tile.startRitual(null);
            }
            BlockUtil.safelyUpdateState(world, pos);
        }
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if (worldIn.getBlockEntity(pos) instanceof RitualBrazierTile tile) {
            if (tile.ritual != null) {
                tile.ritual.onDestroy();
                if (!tile.ritual.isRunning() && !tile.ritual.isDone()) {
                    worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(RitualRegistry.getRitualItemMap().get(tile.ritual.getRegistryName()))));
                }
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RitualBrazierTile(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }

    @Override
    public boolean isPathfindable(BlockState pState, PathComputationType pType) {
        return false;
    }
}
