package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.items.data.PotionJarData;
import com.hollingsworth.arsnouveau.common.util.ItemUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.*;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class PotionJar extends ModBlock implements SimpleWaterloggedBlock, EntityBlock {
    public PotionJar(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    public PotionJar() {
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
            if (tile.canAccept(potion, 100)) {
                tile.add(potion, 100);
                if (!player.hasInfiniteMaterials()) {
                    stack.shrink(1);
                    player.addItem(new ItemStack(Items.GLASS_BOTTLE));
                }
            }
            return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);
        } else if (stack.getItem() == Items.GLASS_BOTTLE && tile.getAmount() >= 100) {
            ItemStack potionStack = new ItemStack(Items.POTION);
            PotionContents contents = tile.getData();
            potionStack.set(DataComponents.POTION_CONTENTS, new PotionContents(contents.potion(), contents.customColor(), contents.customEffects()));
            if (ItemUtil.shrinkHandAndAddStack(potionStack, handIn, player)) {
                tile.remove(100);
            }
        } else if (stack.getItem() == Items.ARROW && tile.getAmount() >= 10) {
            ItemStack potionStack = new ItemStack(Items.TIPPED_ARROW);
            PotionContents contents = tile.getData();
            potionStack.set(DataComponents.POTION_CONTENTS, new PotionContents(contents.potion(), contents.customColor(), contents.customEffects()));
            if (ItemUtil.shrinkHandAndAddStack(potionStack, handIn, player)) {
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
        if (potion == null)
            return;
        var fill = potion.fill();
        var data = potion.contents();
        if (!data.equals(PotionContents.EMPTY)) {
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
        if (level.getBlockEntity(pos) instanceof PotionJarTile jarTile) {
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

    enum StackingMode {
        FillJar,
        TransferJar,
        FillBottle,
        TipArrow,
    }

    @SubscribeEvent
    public static void onStack(ItemStackedOnOtherEvent event) {
        if (event.getClickAction() != ClickAction.SECONDARY) {
            return;
        }

        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack from;
        ItemStack to;
        StackingMode mode;

        // No idea if intentional, but the order of these seem swapped.
        // See https://github.com/neoforged/NeoForge/blob/1.21.1/patches/net/minecraft/world/inventory/AbstractContainerMenu.java.patch
        // and the patched net.minecraft.world.inventory.AbstractContainerMenu#tryItemClickBehaviourOverride
        var carried = event.getStackedOnItem();
        var stacked = event.getCarriedItem();
        if (carried.isEmpty() || stacked.isEmpty()) {
            return;
        }

        if (carried.getItem() instanceof PotionItem && stacked.is(BlockRegistry.POTION_JAR.asItem())) {
            from = carried;
            to = stacked;
            mode = StackingMode.FillJar;
        } else if (carried.is(BlockRegistry.POTION_JAR.asItem()) && stacked.getItem() instanceof PotionItem) {
            from = stacked;
            to = carried;
            mode = StackingMode.FillJar;
        } else if (carried.is(Items.GLASS_BOTTLE) && stacked.is(BlockRegistry.POTION_JAR.asItem())) {
            from = stacked;
            to = carried;
            mode = StackingMode.FillBottle;
        } else if (carried.is(BlockRegistry.POTION_JAR.asItem()) && stacked.is(Items.GLASS_BOTTLE)) {
            from = carried;
            to = stacked;
            mode = StackingMode.FillBottle;
        } else if (carried.is(Items.ARROW) && stacked.is(BlockRegistry.POTION_JAR.asItem())) {
            from = stacked;
            to = carried;
            mode = StackingMode.TipArrow;
        } else if (carried.is(BlockRegistry.POTION_JAR.asItem()) && stacked.is(Items.ARROW)) {
            from = carried;
            to = stacked;
            mode = StackingMode.TipArrow;
        } else if (carried.is(BlockRegistry.POTION_JAR.asItem()) && stacked.is(BlockRegistry.POTION_JAR.asItem())) {
            from = carried;
            to = stacked;
            mode = StackingMode.TransferJar;
        } else {
            return;
        }

        switch (mode) {
            case FillJar -> {
                if (to.getCount() != 1) {
                    return;
                }

                var potionData = from.get(DataComponents.POTION_CONTENTS);
                var jarData = to.getOrDefault(DataComponentRegistry.POTION_JAR, new PotionJarData(0, PotionContents.EMPTY, false));
                if (potionData == null || potionData.potion().isEmpty()) {
                    return;
                }

                var space = PotionJarTile.MAX_FILL - jarData.fill();
                if (space < 100) {
                    return;
                }

                var potions = Math.min(from.getCount(), space / 100);
                var fill = 100 * potions;

                if (!jarData.canAccept(potionData, fill, PotionJarTile.MAX_FILL)) {
                    return;
                }

                var result = jarData.add(potionData, fill, PotionJarTile.MAX_FILL).left();
                to.set(DataComponentRegistry.POTION_JAR, result);
                if (!player.hasInfiniteMaterials()) {
                    from.shrink(potions);
                    var emptied = new ItemStack(Items.GLASS_BOTTLE, potions);
                    if (player.containerMenu.getCarried().isEmpty()) {
                        player.containerMenu.setCarried(emptied);
                    } else {
                        player.addItem(emptied);
                    }
                }

                event.setCanceled(true);
            }
            case TransferJar -> {
                if (to.getCount() != 1 || ItemStack.isSameItemSameComponents(to, from)) {
                    return;
                }

                var fromData = from.getOrDefault(DataComponentRegistry.POTION_JAR, new PotionJarData(0, PotionContents.EMPTY, false));
                var toData = to.getOrDefault(DataComponentRegistry.POTION_JAR, new PotionJarData(0, PotionContents.EMPTY, false));
                if (fromData.contents().potion().isEmpty() || fromData.fill() <= 0) {
                    return;
                }

                var space = PotionJarTile.MAX_FILL - toData.fill();
                var fill = Math.min(fromData.fill() * from.getCount(), space);

                if (!toData.canAccept(fromData.contents(), fill, PotionJarTile.MAX_FILL)) {
                    return;
                }

                var result = toData.add(fromData.contents(), fill, PotionJarTile.MAX_FILL).left();
                if (!player.hasInfiniteMaterials()) {
                    to.set(DataComponentRegistry.POTION_JAR, result);
                    var fullJars = fill / fromData.fill();
                    from.shrink(fullJars);
                    var emptied = new ItemStack(BlockRegistry.POTION_JAR.asItem(), fullJars);
                    if (player.containerMenu.getCarried().isEmpty()) {
                        player.containerMenu.setCarried(emptied);
                    } else {
                        player.addItem(emptied);
                    }
                    var remaining = fill - fullJars * fromData.fill();
                    if (remaining > 0) {
                        var reduced = new ItemStack(BlockRegistry.POTION_JAR.asItem());
                        reduced.set(DataComponentRegistry.POTION_JAR, fromData.withFill(fromData.fill() - remaining));
                        from.shrink(1);
                        if (player.containerMenu.getCarried().isEmpty()) {
                            player.containerMenu.setCarried(reduced);
                        } else {
                            player.addItem(reduced);
                        }
                    }
                }

                event.setCanceled(true);
            }
            case FillBottle -> {
                if (to.getCount() != 1) {
                    return;
                }

                var fromData = from.getOrDefault(DataComponentRegistry.POTION_JAR, new PotionJarData(0, PotionContents.EMPTY, false));
                if (fromData.contents().potion().isEmpty() || fromData.fill() <= 100) {
                    return;
                }

                ItemStack potionStack = new ItemStack(Items.POTION);
                PotionContents contents = fromData.contents();
                potionStack.set(DataComponents.POTION_CONTENTS, new PotionContents(contents.potion(), contents.customColor(), contents.customEffects()));

                if (!player.hasInfiniteMaterials()) {
                    from.set(DataComponentRegistry.POTION_JAR, fromData.withFill(fromData.fill() - 100));
                    to.shrink(1);
                    if (player.containerMenu.getCarried().isEmpty()) {
                        player.containerMenu.setCarried(potionStack);
                    } else {
                        player.addItem(potionStack);
                    }
                }

                event.setCanceled(true);
            }
            case TipArrow -> {
                if (to.getCount() != 1) {
                    return;
                }

                var fromData = from.getOrDefault(DataComponentRegistry.POTION_JAR, new PotionJarData(0, PotionContents.EMPTY, false));
                if (fromData.contents().potion().isEmpty() || fromData.fill() <= 10) {
                    return;
                }

                ItemStack tippedArrow = new ItemStack(Items.TIPPED_ARROW);
                PotionContents contents = fromData.contents();
                tippedArrow.set(DataComponents.POTION_CONTENTS, new PotionContents(contents.potion(), contents.customColor(), contents.customEffects()));

                if (!player.hasInfiniteMaterials()) {
                    from.set(DataComponentRegistry.POTION_JAR, fromData.withFill(fromData.fill() - 10));
                    to.shrink(1);
                    if (player.containerMenu.getCarried().isEmpty()) {
                        player.containerMenu.setCarried(tippedArrow);
                    } else {
                        player.addItem(tippedArrow);
                    }
                }

                event.setCanceled(true);
            }
            case null, default -> {}
        }
    }
}
