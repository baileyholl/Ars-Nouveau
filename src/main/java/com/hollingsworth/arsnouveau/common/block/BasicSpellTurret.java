package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class BasicSpellTurret extends TickableModBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public static HashMap<AbstractCastMethod, ITurretBehavior> TURRET_BEHAVIOR_MAP = new HashMap<>();

    public BasicSpellTurret(Properties properties) {
        super(properties.forceSolidOn());
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false).setValue(FACING, Direction.NORTH).setValue(TRIGGERED, Boolean.FALSE));
    }


    public BasicSpellTurret() {
        this(defaultProperties().noOcclusion());
    }

    @Override
    public void tick(@NotNull BlockState state, ServerLevel worldIn, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        if (worldIn.getBlockEntity(pos) instanceof BasicSpellTurretTile tile) {
            tile.shootSpell();
        }
    }

    static {
        TURRET_BEHAVIOR_MAP.put(MethodProjectile.INSTANCE, new ITurretBehavior() {

            @Override
            public void onCast(SpellResolver resolver, ServerLevel world, BlockPos pos, Player fakePlayer, Position iposition, Direction direction) {
                EntityProjectileSpell spell = new EntityProjectileSpell(world, resolver);
                spell.setOwner(fakePlayer);
                spell.setPos(iposition.x(), iposition.y() - 0.25, iposition.z());
                SpellStats stats = resolver.getCastStats();
                float velocity = Math.max(0.1f, 0.75f + stats.getAccMultiplier() / 2);
                if (world.getBlockEntity(pos) instanceof RotatingTurretTile rotatingTurretTile) {
                    Vec3 vec3d = rotatingTurretTile.getShootAngle().normalize();
                    spell.shoot(vec3d.x(), vec3d.y(), vec3d.z(), velocity, 0);
                } else {
                    spell.shoot(direction.getStepX(), ((float) direction.getStepY()), direction.getStepZ(), velocity, 0);
                }
                world.addFreshEntity(spell);
            }
        });

        TURRET_BEHAVIOR_MAP.put(MethodTouch.INSTANCE, new ITurretBehavior() {
            @Override
            public void onCast(SpellResolver resolver, ServerLevel serverLevel, BlockPos pos, Player fakePlayer, Position dispensePosition, Direction facingDir) {
                BlockPos touchPos = pos.relative(facingDir);
                List<LivingEntity> entityList = serverLevel.getEntitiesOfClass(LivingEntity.class, new AABB(touchPos));
                if (!entityList.isEmpty()) {
                    LivingEntity entity = entityList.get(serverLevel.random.nextInt(entityList.size()));
                    resolver.onCastOnEntity(ItemStack.EMPTY, entity, InteractionHand.MAIN_HAND);
                } else {
                    Vec3 hitVec = new Vec3(touchPos.getX() + facingDir.getStepX() * 0.5, touchPos.getY() + facingDir.getStepY() * 0.5, touchPos.getZ() + facingDir.getStepZ() * 0.5);
                    resolver.onCastOnBlock(new BlockHitResult(hitVec, facingDir.getOpposite(), new BlockPos(touchPos.getX(), touchPos.getY(), touchPos.getZ()), true));
                }
            }
        });
    }

    public void neighborChanged(BlockState state, Level worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos, boolean isMoving) {
        boolean neighborSignal = worldIn.hasNeighborSignal(pos) || worldIn.hasNeighborSignal(pos.above());
        boolean isTriggered = state.getValue(TRIGGERED);
        if (neighborSignal && !isTriggered) {
            worldIn.scheduleTick(pos, this, 4);
            worldIn.setBlock(pos, state.setValue(TRIGGERED, Boolean.TRUE), 4);

        } else if (!neighborSignal && isTriggered) {
            worldIn.setBlock(pos, state.setValue(TRIGGERED, Boolean.FALSE), 4);
        }
    }

    /**
     * Get the position where the dispenser at the given Coordinates should dispense to.
     */
    public static Position getDispensePosition(BlockPos pos, Direction direction) {
        double d0 = pos.getX() + 0.5 + 0.5D * (double) direction.getStepX();
        double d1 = pos.getY() + 0.5 + 0.5D * (double) direction.getStepY();
        double d2 = pos.getZ() + 0.5 + 0.5D * (double) direction.getStepZ();
        return new Vec3(d0, d1, d2);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState p_149645_1_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState stateIn, @NotNull Direction side, @NotNull BlockState facingState, @NotNull LevelAccessor worldIn, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        return stateIn;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack pStack, @NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult pHitResult) {
        if (handIn != InteractionHand.MAIN_HAND) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (worldIn.isClientSide)
            return ItemInteractionResult.SUCCESS;
        ItemStack stack = player.getItemInHand(handIn);
        if (SpellCasterRegistry.from(stack) != null) {
            Spell spell = SpellCasterRegistry.from(stack).getSpell();
            if (!spell.isEmpty()) {
                if (spell.getCastMethod() == null) {
                    PortUtil.sendMessage(player, Component.translatable("ars_nouveau.alert.turret_needs_form"));
                    return ItemInteractionResult.SUCCESS;
                }
                if (!(TURRET_BEHAVIOR_MAP.containsKey(spell.getCastMethod()))) {
                    PortUtil.sendMessage(player, Component.translatable("ars_nouveau.alert.turret_type"));
                    return ItemInteractionResult.SUCCESS;
                }
                if (worldIn.getBlockEntity(pos) instanceof BasicSpellTurretTile tile) {
                    tile.setSpell(spell);
                    tile.setPlayer(player.getUUID());
                    tile.updateBlock();
                    PortUtil.sendMessage(player, Component.translatable("ars_nouveau.alert.spell_set"));
                    worldIn.sendBlockUpdated(pos, state, state, 2);
                }
            }
        }
        return super.useItemOn(pStack, state, worldIn, pos, player, handIn, pHitResult);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED, WATERLOGGED);
    }

    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public @NotNull BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new BasicSpellTurretTile(pos, state);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return shape;
    }

    //kept is as simple as possible for compat. with other turrets, needed for waterlogged. Does not keep track of turret direction
    static final VoxelShape shape = Block.box(4.6, 4.6, 4.6, 11.6, 11.6, 11.6);

    @Override
    protected boolean isPathfindable(@NotNull BlockState pState, @NotNull PathComputationType pPathComputationType) {
        return false;
    }
}
