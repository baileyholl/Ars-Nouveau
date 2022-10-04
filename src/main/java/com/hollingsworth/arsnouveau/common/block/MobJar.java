package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehaviorRegistry;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class MobJar extends TickableModBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final Property<Integer> LIGHT_LEVEL = IntegerProperty.create("level", 0, 15);

    public MobJar() {
        super(defaultProperties().noOcclusion().lightLevel((state) -> state.getValue(LIGHT_LEVEL)));
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false).setValue(FACING, Direction.NORTH).setValue(LIGHT_LEVEL, 0));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        MobJarTile tile = (MobJarTile) pLevel.getBlockEntity(pPos);
        if (tile == null) {
            return InteractionResult.PASS;
        }
        if(tile.getEntity() == null && !pLevel.isClientSide){
            ItemStack stack = pPlayer.getItemInHand(pHand);
            if(stack.getItem() instanceof SpawnEggItem spawnEggItem){
                EntityType type = spawnEggItem.getType(null);
                Entity entity = type.create(pLevel);
                if(entity != null) {
                    tile.setEntityData(entity);
                    stack.shrink(1);
                    return InteractionResult.CONSUME;
                }
            }
        }
        if(tile.getEntity() != null && !JarBehaviorRegistry.containsEntity(tile.getEntity())){
            pPlayer.interactOn(tile.getEntity(), pHand);
            tile.updateBlock();
        }
        tile.dispatchBehavior((behavior) -> {
            behavior.use(pState, pLevel, pPos, pPlayer, pHand, pHit, tile);
        });
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        context.getLevel().scheduleTick(context.getClickedPos(), this, 1);
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
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if(pLevel.isClientSide)
            return;
        if(pLevel.getBlockEntity(pPos) instanceof MobJarTile tile){
            int light = tile.calculateLight();
            if(pState.getValue(LIGHT_LEVEL) != light){
                pLevel.setBlock(pPos, pState.setValue(LIGHT_LEVEL, light), 2);
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
        if (tile == null || tile.getEntity() == null) return 0;
        return 15;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, LIGHT_LEVEL);
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

}
