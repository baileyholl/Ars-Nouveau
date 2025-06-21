package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.IFilter;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.common.items.RunicChalk;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.common.util.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class RuneBlock extends TickableModBlock {

    public static VoxelShape shape = Block.box(0.0D, 0.0D, 0.0D, 16D, 0.5D, 16D);

    public RuneBlock() {
        this(defaultProperties().noCollission().noOcclusion().strength(0f, 0f));
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(FLOOR, true));
    }

    public RuneBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getClickedFace());
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {

        if (worldIn.getBlockEntity(pos) instanceof RuneTile runeTile) {

            if (!worldIn.isClientSide && stack.getItem() instanceof RunicChalk) {
                if (runeTile.isTemporary) {
                    runeTile.isTemporary = false;
                    PortUtil.sendMessage(player, Component.translatable("ars_nouveau.rune.setperm"));
                    return ItemInteractionResult.SUCCESS;
                }
            }
            if (!(stack.getItem() instanceof SpellParchment) || worldIn.isClientSide)
                return ItemInteractionResult.SUCCESS;
            Spell spell = SpellCasterRegistry.from(stack).getSpell();
            if (spell.isEmpty())
                return ItemInteractionResult.SUCCESS;

            if (!(spell.get(0) instanceof MethodTouch)) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.rune.touch"));
                return ItemInteractionResult.SUCCESS;
            }
            runeTile.setSpell(spell);
            runeTile.setPlayer(player.getUUID());
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.spell_set"));
        }
        return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);
    }


    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(BlockStateProperties.FACING, rot.rotate(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel worldIn, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        super.tick(state, worldIn, pos, rand);

        if (worldIn.getBlockEntity(pos) instanceof RuneTile rune && rune.touchedEntity != null) {
            rune.castSpell(rune.touchedEntity);
            rune.touchedEntity = null;
        }
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Entity entityIn) {
        super.entityInside(state, worldIn, pos, entityIn);
        List<Entity> entities = worldIn.getEntitiesOfClass(Entity.class, getShape(state, worldIn, pos, CollisionContext.empty()).bounds().move(pos), EntitySelector.NO_SPECTATORS.and((p_289691_) -> {
            return !p_289691_.isIgnoringBlockTriggers();
        }));
        if (!entities.isEmpty() && worldIn.getBlockEntity(pos) instanceof RuneTile rune) {
            if (rune.spell != null) {
                for (AbstractSpellPart part : rune.spell.recipe()) {
                    if (part instanceof IFilter filter) {
                        if (!filter.shouldResolveOnEntity(entityIn, worldIn)) {
                            return;
                        }
                    } else break;
                }
            }
            rune.touchedEntity = entities.getFirst();
            worldIn.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
        if (!world.isClientSide() && world.getBlockEntity(pos) instanceof RuneTile runeTile) {
            runeTile.disabled = world.hasNeighborSignal(pos);
        }
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return switch (facing) {
            case EAST -> VoxelShapeUtils.rotate(shape, Direction.WEST);
            case NORTH -> VoxelShapeUtils.rotateX(shape, 270);
            case DOWN -> VoxelShapeUtils.rotate(shape, Direction.UP);
            case WEST -> VoxelShapeUtils.rotate(shape, Direction.EAST);
            case SOUTH -> VoxelShapeUtils.rotateX(shape, 90);
            default -> shape;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED).add(FACING).add(FLOOR);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState p_149645_1_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RuneTile(pos, state);
    }

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty FLOOR = BooleanProperty.create("floor");
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
}
