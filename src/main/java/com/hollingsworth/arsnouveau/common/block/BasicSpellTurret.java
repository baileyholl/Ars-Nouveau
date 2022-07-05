package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOneShotAnimation;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.*;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Random;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class BasicSpellTurret extends TickableModBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public static HashMap<AbstractCastMethod, ITurretBehavior> TURRET_BEHAVIOR_MAP = new HashMap<>();

    public BasicSpellTurret(Properties properties, String registry) {
        super(properties, registry);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false).setValue(FACING, Direction.NORTH).setValue(TRIGGERED, Boolean.FALSE));
    }

    public BasicSpellTurret(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false).setValue(FACING, Direction.NORTH).setValue(TRIGGERED, Boolean.FALSE));
    }

    public BasicSpellTurret() {
        this(defaultProperties().noOcclusion(), LibBlockNames.BASIC_SPELL_TURRET);
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
        this.shootSpell(worldIn, pos);
    }

    static {
        TURRET_BEHAVIOR_MAP.put(MethodProjectile.INSTANCE, new ITurretBehavior() {
            @Override
            public void onCast(SpellResolver resolver, BasicSpellTurretTile tile, ServerLevel world, BlockPos pos, FakePlayer fakePlayer, Position iposition, Direction direction) {
                EntityProjectileSpell spell = new EntityProjectileSpell(world,resolver);
                spell.setOwner(fakePlayer);
                spell.setPos(iposition.x(), iposition.y(), iposition.z());
                spell.shoot(direction.getStepX(), ((float)direction.getStepY()), direction.getStepZ(), 0.5f, 0);
                world.addFreshEntity(spell);
            }
        });

        TURRET_BEHAVIOR_MAP.put(MethodTouch.INSTANCE, new ITurretBehavior() {
            @Override
            public void onCast(SpellResolver resolver, BasicSpellTurretTile tile, ServerLevel serverLevel, BlockPos pos, FakePlayer fakePlayer, Position dispensePosition, Direction direction) {
                BlockPos touchPos = new BlockPos(dispensePosition.x(), dispensePosition.y(), dispensePosition.z());
                if(direction == Direction.WEST || direction == Direction.NORTH){
                    touchPos = touchPos.relative(direction);
                }
                if(direction == Direction.DOWN) // Why do I need to do this? Why does the vanilla dispenser code not offset correctly for DOWN?
                    touchPos = touchPos.below();
                resolver.onCastOnBlock(new BlockHitResult(new Vec3(touchPos.getX(), touchPos.getY(), touchPos.getZ()),
                                direction.getOpposite(), new BlockPos(touchPos.getX(), touchPos.getY(), touchPos.getZ()), false));
            }
        });
    }

    public void shootSpell(ServerLevel world, BlockPos pos ) {
        if (! (world.getBlockEntity(pos) instanceof BasicSpellTurretTile tile)) return;
        ISpellCaster caster = tile.getSpellCaster();
        if(caster.getSpell().isEmpty())
            return;
        int manaCost = tile.getManaCost();
        if(manaCost > 0 && SourceUtil.takeSourceNearbyWithParticles(pos, world, 10, manaCost) == null)
            return;
        Networking.sendToNearby(world, pos, new PacketOneShotAnimation(pos));
        Position iposition = getDispensePosition(new BlockSourceImpl(world, pos));
        Direction direction = world.getBlockState(pos).getValue(FACING);
        FakePlayer fakePlayer = ANFakePlayer.getPlayer(world);
        fakePlayer.setPos(pos.getX(), pos.getY(), pos.getZ());
        EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(caster, fakePlayer)
                .withCastingTile(world.getBlockEntity(pos)).withType(SpellContext.CasterType.TURRET));
        if(resolver.castType != null && TURRET_BEHAVIOR_MAP.containsKey(resolver.castType)) {
            TURRET_BEHAVIOR_MAP.get(resolver.castType).onCast(resolver, tile, world, pos, fakePlayer, iposition, direction);
            caster.playSound(pos, world, null, caster.getCurrentSound(), SoundSource.BLOCKS);
        }
    }


    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        boolean neighborSignal = worldIn.hasNeighborSignal(pos) || worldIn.hasNeighborSignal(pos.above());
        boolean isTriggered = state.getValue(TRIGGERED);
        if (neighborSignal && !isTriggered) {
            worldIn.scheduleTick(pos, this,4);
            worldIn.setBlock(pos, state.setValue(TRIGGERED, Boolean.TRUE), 4);

        } else if (!neighborSignal && isTriggered) {
            worldIn.setBlock(pos, state.setValue(TRIGGERED, Boolean.FALSE), 4);
        }
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return false;
    }


    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(worldIn.getBlockEntity(pos));
    }


    /**
     * Get the position where the dispenser at the given Coordinates should dispense to.
     */
    public static Position getDispensePosition(BlockSource coords) {
        Direction direction = coords.getBlockState().getValue(FACING);
        double d0 = coords.x() + 0.5D * (double)direction.getStepX();
        double d1 = coords.y() + 0.5D * (double)direction.getStepY();
        double d2 = coords.z() + 0.5D * (double)direction.getStepZ();
        return new PositionImpl(d0, d1, d2);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction side, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        return stateIn;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if(handIn == InteractionHand.MAIN_HAND){
            ItemStack stack = player.getItemInHand(handIn);
            if(!(stack.getItem() instanceof SpellParchment) || worldIn.isClientSide)
                return InteractionResult.SUCCESS;
            Spell spell =  CasterUtil.getCaster(stack).getSpell();
            if(spell.isEmpty())
                return InteractionResult.SUCCESS;
            if(!(TURRET_BEHAVIOR_MAP.containsKey(spell.getCastMethod()))){
                PortUtil.sendMessage(player, new TranslatableComponent("ars_nouveau.alert.turret_type"));
                return InteractionResult.SUCCESS;
            }
            BasicSpellTurretTile tile = (BasicSpellTurretTile) worldIn.getBlockEntity(pos);
            tile.spellCaster.copyFromCaster(CasterUtil.getCaster(stack));
            tile.setChanged();
            PortUtil.sendMessage(player, new TranslatableComponent("ars_nouveau.alert.spell_set"));
            worldIn.sendBlockUpdated(pos, state, state, 2);
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
        builder.add(WATERLOGGED);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BasicSpellTurretTile(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }

    //kept is as simple as possible for compat. with other turrets, needed for waterlogged. Does not keep track of turret direction
    static final VoxelShape shape = Block.box(4.6, 4.6, 4.6, 11.6, 11.6, 11.6);

}
