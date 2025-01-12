package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.util.ItemUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class PotionJar extends ModBlock implements SimpleWaterloggedBlock, EntityBlock {
    public PotionJar(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    public PotionJar(){
        this(ModBlock.defaultProperties().noOcclusion());
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        PotionJarTile tile = (PotionJarTile) worldIn.getBlockEntity(pos);
        if (tile == null || tile.getAmount() <= 0) return 0;
        int step = (tile.getMaxFill() - 1) / 14;
        return (tile.getAmount() - 1) / step + 1;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.isClientSide)
            return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);

        PotionJarTile tile = (PotionJarTile) worldIn.getBlockEntity(pos);
        if (tile == null)
            return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);
        PotionContents potion = stack.get(DataComponents.POTION_CONTENTS);

        if (stack.getItem() == Items.POTION && potion != null && potion != PotionContents.EMPTY) {
            if (tile.canAccept(potion,100)) {
                tile.add(potion, 100);
                if (!player.isCreative()) {
                    stack.shrink(1);
                    player.addItem(new ItemStack(Items.GLASS_BOTTLE));
                }
            }
            return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);
        }else if (stack.getItem() == Items.GLASS_BOTTLE && tile.getAmount() >= 100) {
            ItemStack potionStack = new ItemStack(Items.POTION);
            PotionContents contents = tile.getData();
            potionStack.set(DataComponents.POTION_CONTENTS, new PotionContents(contents.potion(), contents.customColor(), contents.customEffects()));
            if(ItemUtil.shrinkHandAndAddStack(potionStack, handIn, player)){
                tile.remove(100);
            }
        }else if(stack.getItem() == Items.ARROW && tile.getAmount() >= 10){
            ItemStack potionStack = new ItemStack(Items.TIPPED_ARROW);
            PotionContents contents = tile.getData();
            potionStack.set(DataComponents.POTION_CONTENTS, new PotionContents(contents.potion(), contents.customColor(), contents.customEffects()));
            if(ItemUtil.shrinkHandAndAddStack(potionStack, handIn, player)) {
                tile.remove(10);
            }
        }
        return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext pContext, List<Component> tooltip, TooltipFlag pTooltipFlag) {
        var potion = stack.get(DataComponentRegistry.POTION_JAR);
        if(potion == null)
            return;
        var fill = potion.fill();
        var data = potion.contents();
        if(!data.equals(PotionContents.EMPTY)) {
            ItemStack potionItem = new ItemStack(Items.POTION);
            potionItem.set(DataComponents.POTION_CONTENTS, data);
            tooltip.add(Component.translatable(potionItem.getDescriptionId()));
        }
        PotionContents.addPotionTooltip(data.getAllEffects(), tooltip::add, 1.0F, 20.0f);
        tooltip.add(Component.translatable("ars_nouveau.source_jar.fullness", (fill * 100) / 10000));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

   @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        context.getLevel().scheduleTick(context.getClickedPos(), BlockRegistry.POTION_JAR.get(), 1);
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public void tick(BlockState p_222945_, ServerLevel level, BlockPos pos, RandomSource p_222948_) {
        super.tick(p_222945_, level, pos, p_222948_);
        if(level.getBlockEntity(pos) instanceof PotionJarTile jarTile){
            jarTile.updateBlock();
        }
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

    @Override
    public boolean isPathfindable(BlockState pState, PathComputationType pType) {
        return false;
    }
}
