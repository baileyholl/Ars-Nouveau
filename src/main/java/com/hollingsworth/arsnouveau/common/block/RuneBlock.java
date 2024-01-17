package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.IFilter;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
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

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getClickedFace());
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(handIn);

        if (worldIn.getBlockEntity(pos) instanceof RuneTile runeTile) {

            if (!worldIn.isClientSide && stack.getItem() instanceof RunicChalk) {
                if (runeTile.isTemporary) {
                    runeTile.isTemporary = false;
                    PortUtil.sendMessage(player, Component.translatable("ars_nouveau.rune.setperm"));
                    return InteractionResult.SUCCESS;
                }
            }
            if (!(stack.getItem() instanceof SpellParchment) || worldIn.isClientSide)
                return InteractionResult.SUCCESS;
            Spell spell = CasterUtil.getCaster(stack).getSpell();
            if (spell.isEmpty())
                return InteractionResult.SUCCESS;

            if (!(spell.recipe.get(0) instanceof MethodTouch)) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.rune.touch"));
                return InteractionResult.SUCCESS;
            }
            runeTile.setSpell(spell);
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.spell_set"));
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }


    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(BlockStateProperties.FACING, rot.rotate(state.getValue(BlockStateProperties.FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(BlockStateProperties.FACING)));
    }
    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
        super.tick(state, worldIn, pos, rand);

        if (worldIn.getBlockEntity(pos) instanceof RuneTile rune && rune.touchedEntity != null) {
            rune.castSpell(rune.touchedEntity);
            rune.touchedEntity = null;
        }
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        super.entityInside(state, worldIn, pos, entityIn);
        List<Entity> entities = worldIn.getEntitiesOfClass(Entity.class, getShape(state, worldIn, pos, CollisionContext.empty()).bounds().move(pos), EntitySelector.NO_SPECTATORS.and((p_289691_) -> {
            return !p_289691_.isIgnoringBlockTriggers();
        }));
        if (!entities.isEmpty() && worldIn.getBlockEntity(pos) instanceof RuneTile rune) {
            if (rune.spell != null) {
                for (AbstractSpellPart part : rune.spell.recipe) {
                    if ( part instanceof IFilter filter) {
                        if (!filter.shouldResolveOnEntity(entityIn,worldIn)) {
                            return;
                        }
                    } else break;
                }
            }
            rune.touchedEntity = entities.get(0);
            worldIn.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
        if (!world.isClientSide() && world.getBlockEntity(pos) instanceof RuneTile runeTile) {
            runeTile.disabled = world.hasNeighborSignal(pos);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        switch(facing){
            case EAST:
                return VoxelShapeUtils.rotate(shape, Direction.WEST);
            case NORTH:
                return VoxelShapeUtils.rotateX(shape, 270);
            case DOWN:
                return VoxelShapeUtils.rotate(shape, Direction.UP);
            case WEST:
                return VoxelShapeUtils.rotate(shape, Direction.EAST);
            case SOUTH:
                return VoxelShapeUtils.rotateX(shape, 90);
        }
        return shape;
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RuneTile(pos, state);
    }

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty FLOOR = BooleanProperty.create("floor");
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED).add(FACING).add(FLOOR);
    }
}
