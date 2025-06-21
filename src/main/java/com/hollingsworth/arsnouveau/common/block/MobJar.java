package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.registry.JarBehaviorRegistry;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;
import com.hollingsworth.arsnouveau.common.items.MobJarItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class MobJar extends TickableModBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final Property<Integer> LIGHT_LEVEL = IntegerProperty.create("level", 0, 15);
    public static final Property<Boolean> POWERED = BlockStateProperties.POWERED;

    private static Properties props = defaultProperties().noOcclusion().lightLevel(state -> state.getValue(LIGHT_LEVEL));

    public MobJar() {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false).setValue(FACING, Direction.NORTH).setValue(LIGHT_LEVEL, 0).setValue(POWERED, false));
    }

    private static final VoxelShape VISUAL_SHAPE = Block.box(3, 14, 3, 13, 16, 13);

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return VISUAL_SHAPE;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        MobJarTile tile = (MobJarTile) pLevel.getBlockEntity(pPos);
        if (tile == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!pLevel.isClientSide) {
            ItemStack held = pPlayer.getItemInHand(pHand);
            if (held.is(ItemTagProvider.JAR_ITEM_BLACKLIST)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        }

        var jarEntity = tile.getEntity();
        if (jarEntity == null && !pLevel.isClientSide) {
            if (stack.getItem() instanceof SpawnEggItem spawnEggItem) {
                EntityType<?> type = spawnEggItem.getType(stack);
                Entity entity = type.create(pLevel);
                if (entity != null) {
                    tile.setEntityData(entity);
                    if (!pPlayer.hasInfiniteMaterials()) {
                        stack.shrink(1);
                    }
                    return ItemInteractionResult.CONSUME;
                }
            } else if (!stack.isEmpty() && !(stack.getItem() instanceof MobJarItem) && !(pPlayer instanceof ANFakePlayer)) {
                ItemEntity entity = new ItemEntity(EntityType.ITEM, pLevel);
                entity.setItem(stack.copy());
                tile.setEntityData(entity);
                stack.setCount(0);
                return ItemInteractionResult.CONSUME;
            }
        }
        if (jarEntity != null
                && !(jarEntity instanceof PlayerRideable)
                && !JarBehaviorRegistry.containsEntity(jarEntity)
                && !(jarEntity instanceof ContainerEntity)) {
            pPlayer.interactOn(jarEntity, pHand);
            if (!jarEntity.isAlive() || jarEntity.isRemoved()) {
                tile.removeEntity();
            }
        }
        if (pPlayer.isSecondaryUseActive() && jarEntity instanceof ContainerEntity ce) {
            switch (ce.interactWithContainerVehicle(pPlayer)) {
                case SUCCESS, SUCCESS_NO_ITEM_USED -> {
                    return ItemInteractionResult.SUCCESS;
                }
                case CONSUME -> {
                    return ItemInteractionResult.CONSUME;
                }
                case CONSUME_PARTIAL -> {
                    return ItemInteractionResult.CONSUME_PARTIAL;
                }
                case FAIL -> {
                    return ItemInteractionResult.FAIL;
                }
            }
        }
        tile.dispatchBehavior((behavior) -> {
            behavior.use(pState, pLevel, pPos, pPlayer, pHand, pHit, tile);
        });
        tile.updateBlock();
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        context.getLevel().scheduleTick(context.getClickedPos(), this, 1);
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER).setValue(POWERED, Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos())));
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction side, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        return stateIn;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.isClientSide)
            return;
        if (pLevel.getBlockEntity(pPos) instanceof MobJarTile tile) {
            int light = tile.calculateLight();
            if (pState.getValue(LIGHT_LEVEL) != light) {
                pLevel.setBlock(pPos, pState.setValue(LIGHT_LEVEL, light), 2);
            }
        }
    }

    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if (!pLevel.isClientSide) {
            boolean flag = pState.getValue(POWERED);
            if (flag != pLevel.hasNeighborSignal(pPos)) {
                if (!flag) {
                    MobJarTile tile = (MobJarTile) pLevel.getBlockEntity(pPos);
                    tile.dispatchBehavior((behavior) -> {
                        behavior.onRedstonePower(tile);
                    });
                }
                pLevel.setBlock(pPos, pState.cycle(POWERED), 2);
            }

        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        MobJarTile tile = (MobJarTile) worldIn.getBlockEntity(pos);
        AtomicInteger power = new AtomicInteger();
        tile.dispatchBehavior((behavior) -> {
            power.set(Math.max(power.get(), behavior.getAnalogPower(tile)));
        });
        return Math.min(power.get(), 15);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, LIGHT_LEVEL, POWERED);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    public static final VoxelShape shape = Stream.of(
            Block.box(3, 14, 3, 13, 16, 13),
            Block.box(1, 0, 1, 15, 13, 15),
            Block.box(4, 13, 4, 12, 14, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MobJarTile(pPos, pState);
    }

    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        if (pBlockAccess.getBlockEntity(pPos) instanceof MobJarTile jarTile) {
            AtomicInteger power = new AtomicInteger();
            jarTile.dispatchBehavior((behavior) -> {
                power.set(Math.max(power.get(), behavior.getSignalPower(jarTile)));
            });
            return Math.min(power.get(), 15);
        }
        return 0;
    }

    @Override
    public boolean isPathfindable(BlockState pState, PathComputationType pType) {
        return false;
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }
}
