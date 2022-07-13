package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class PotionJar extends TickableModBlock implements SimpleWaterloggedBlock {
    public PotionJar(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        PotionJarTile tile = (PotionJarTile) worldIn.getBlockEntity(pos);
        if (tile == null || tile.getCurrentFill() <= 0) return 0;
        int step = (tile.getMaxFill() - 1) / 14;
        return (tile.getCurrentFill() - 1) / step + 1;
    }


    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.isClientSide)
            return InteractionResult.SUCCESS;

        PotionJarTile tile = (PotionJarTile) worldIn.getBlockEntity(pos);
        if (tile == null)
            return InteractionResult.SUCCESS;
        ItemStack stack = player.getItemInHand(handIn);
        Potion potion = PotionUtils.getPotion(stack);

        if (stack.getItem() == Items.POTION && potion != Potions.EMPTY) {
            if (tile.getAmount() == 0) {

                tile.setPotion(stack);
                tile.addAmount(100);
                if (!player.isCreative()) {
                    player.addItem(new ItemStack(Items.GLASS_BOTTLE));
                    stack.shrink(1);
                }

            } else if (tile.isMixEqual(stack) && tile.getCurrentFill() < tile.getMaxFill()) {

                tile.addAmount(100);
                if (!player.isCreative()) {
                    player.addItem(new ItemStack(Items.GLASS_BOTTLE));
                    stack.shrink(1);
                }

            }
            worldIn.sendBlockUpdated(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 3);
        }

        if (stack.getItem() == Items.GLASS_BOTTLE && tile.getCurrentFill() >= 100) {
            ItemStack potionStack = new ItemStack(Items.POTION);
            PotionUtils.setPotion(potionStack, tile.getPotion());
            PotionUtils.setCustomEffects(potionStack, tile.getCustomEffects());
            player.addItem(potionStack);
            player.getItemInHand(handIn).shrink(1);
            tile.addAmount(-100);
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }


    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(SourceJar.fill);
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PotionJarTile(pos, state);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        if (stack.getTag() == null)
            return;
        int mana = stack.getTag().getCompound("BlockEntityTag").getInt("amount");
        tooltip.add(Component.literal((mana * 100) / 10000 + "% full"));
        ItemStack stack1 = new ItemStack(Items.POTION);
        stack1.setTag(stack.getTag().getCompound("BlockEntityTag"));
        PotionUtils.addPotionTooltip(stack1, tooltip, 1.0F);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction side, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        return stateIn;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }

    VoxelShape shape = Stream.of(
            Block.box(2, 0, 2, 14, 2, 14),
            Block.box(3, 2, 3, 13, 9, 13),
            Block.box(5, 9, 5, 11, 14, 11),
            Block.box(6, 13, 6, 10, 16, 10)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

}
