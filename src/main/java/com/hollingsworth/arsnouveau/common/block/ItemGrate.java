package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.common.mixin.BlockBehaviourAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Optional;

public class ItemGrate extends ModBlock implements BucketPickup {
    protected static final VoxelShape BOTTOM_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
    protected static final VoxelShape TOP_AABB = Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EAST_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
    protected static final VoxelShape WEST_OPEN_AABB = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
    protected static final VoxelShape NORTH_OPEN_AABB = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);

    public ItemGrate() {
        super(defaultProperties().noOcclusion());
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.FACING, Direction.NORTH));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        BlockState below = pLevel.getBlockState(pPos.below());
        BlockHitResult belowRes = new BlockHitResult(pHitResult.getLocation().add(0, -1, 0), pHitResult.getDirection(), pPos.below(), pHitResult.isInside());
        return below.useItemOn(pStack, pLevel, pPlayer, pHand, belowRes);
    }


    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        BlockState below = pLevel.getBlockState(pPos.below());
        return below.useWithoutItem(pLevel, pPlayer, new BlockHitResult(pHitResult.getLocation().add(0, -1, 0), pHitResult.getDirection(), pPos.below(), pHitResult.isInside()));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.tick(pState, pLevel, pPos, pRandom);
        BlockPos below = pPos.below();
        BlockState stateAbove = pLevel.getBlockState(pPos.above());
        BlockState stateBelow = pLevel.getBlockState(below);
        if (!stateAbove.getFluidState().isSource()) {
            return;
        }

        if (stateBelow.getBlock() instanceof LiquidBlockContainer blockContainer
                && blockContainer.canPlaceLiquid(null, pLevel, pPos.below(), stateBelow, stateAbove.getFluidState().getType())) {
            if (blockContainer.placeLiquid(pLevel, pPos.below(), stateBelow, stateAbove.getFluidState())) {
                if (stateAbove.getBlock() instanceof BucketPickup bucketPickup) {
                    bucketPickup.pickupBlock(null, pLevel, pPos.above(), stateAbove);
                }
                if (pLevel.getBlockState(pPos.above()).getFluidState().isSource()) {
                    pLevel.setBlockAndUpdate(pPos.above(), Blocks.AIR.defaultBlockState());
                }
            }
        } else if (stateBelow.canBeReplaced()) {
            if (stateAbove.getBlock() instanceof BucketPickup bucketPickup) {
                ItemStack bucket = bucketPickup.pickupBlock(null, pLevel, pPos.above(), stateAbove);
                if (bucket.getItem() instanceof BucketItem bucketItem) {
                    pLevel.setBlockAndUpdate(below, stateAbove.getFluidState().createLegacyBlock());
                }
            }
        } else if (stateBelow.getBlock() instanceof CauldronBlock cauldronBlock
                && !cauldronBlock.isFull(stateBelow)) {
            if (stateAbove.getBlock() instanceof BucketPickup bucketPickup) {

                ItemStack stack = bucketPickup.pickupBlock(null, pLevel, pPos.above(), stateAbove);
                if (!stack.isEmpty()) {
                    var accessor = (BlockBehaviourAccessor) cauldronBlock;
                    ANFakePlayer fakePlayer = ANFakePlayer.getPlayer(pLevel);
                    fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
                    ItemInteractionResult result = accessor.callUseItemOn(stack, stateBelow, pLevel, pPos.below(), fakePlayer, InteractionHand.MAIN_HAND, new BlockHitResult(new Vec3(below.getX(), below.getY(), below.getZ()), Direction.UP, pPos.below(), false));
                    if (!ItemStack.isSameItem(fakePlayer.getItemInHand(InteractionHand.MAIN_HAND), stack)) {
                        if (pLevel.getBlockState(pPos.above()).getFluidState().isSource()) {
                            pLevel.setBlockAndUpdate(pPos.above(), Blocks.AIR.defaultBlockState());
                        }
                    }
                }
            }
        }

    }

    @Override
    protected void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
        if (!pLevel.isClientSide && pLevel.getBlockState(pPos.above()).getFluidState().isSource()) {
            pLevel.scheduleTick(pPos, this, 10);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction direction = pState.getValue(BlockStateProperties.FACING);
        if (direction == Direction.DOWN) {
            return TOP_AABB;
        } else if (direction == Direction.WEST) {
            return WEST_OPEN_AABB;
        } else if (direction == Direction.SOUTH) {
            return SOUTH_OPEN_AABB;
        } else if (direction == Direction.NORTH) {
            return NORTH_OPEN_AABB;
        } else if (direction == Direction.EAST) {
            return EAST_OPEN_AABB;
        }
        return BOTTOM_AABB;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pContext instanceof EntityCollisionContext entityCollisionContext) {
            var entity = entityCollisionContext.getEntity();
            if (entity == null || entity.getType().is(EntityTags.ITEM_GRATE_COLLIDE)) {
                return super.getCollisionShape(pState, pLevel, pPos, pContext);
            }
            if (entity instanceof ItemEntity || entity instanceof Projectile || entity.getType().is(EntityTags.ITEM_GRATE_PASSABLE)) {
                return Shapes.empty();
            }
        }
        return super.getCollisionShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(BlockStateProperties.FACING, rot.rotate(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player pPlayer, LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        BlockState belowState = pLevel.getBlockState(pPos.below());
        if (belowState.getBlock() instanceof BucketPickup bucketPickup) {
            return bucketPickup.pickupBlock(pPlayer, pLevel, pPos.below(), belowState);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.empty();
    }
}
