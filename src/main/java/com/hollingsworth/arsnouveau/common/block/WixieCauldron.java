package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.recipe.MultiRecipeWrapper;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

import static com.hollingsworth.arsnouveau.common.block.tile.SummoningTile.CONVERTED;

public class WixieCauldron extends SummonBlock {

    public static final VoxelShape shape = Stream.of(
            Block.box(2, 0, 4, 4, 13, 14),
            Block.box(4, 0, 12, 14, 13, 14),
            Block.box(12, 0, 2, 14, 13, 12),
            Block.box(2, 0, 2, 12, 13, 4),
            Block.box(4, 0, 4, 12, 5, 12),
            Block.box(4, 0, 4, 12, 5, 12),
            Block.box(14, 0, 5, 16, 4, 16),
            Block.box(14, 4, 9, 16, 8, 16),
            Block.box(9, 0, 14, 14, 4, 16),
            Block.box(2, 0, 14, 7, 4, 16),
            Block.box(0, 0, 11, 2, 4, 16),
            Block.box(0, 0, 2, 2, 9, 7),
            Block.box(0, 0, 0, 2, 9, 2),
            Block.box(2, 0, 0, 7, 9, 2),
            Block.box(7, 0, 0, 11, 5, 2),
            Block.box(9, 4, 14, 14, 8, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final BooleanProperty FILLED = BooleanProperty.create("filled");

    public WixieCauldron() {
        super(defaultProperties().noOcclusion());
        registerDefaultState(defaultBlockState().setValue(CONVERTED, false).setValue(FILLED, false));
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.isClientSide || handIn != InteractionHand.MAIN_HAND || !(worldIn.getBlockEntity(pos) instanceof WixieCauldronTile) || player.getMainHandItem().getItem() == ItemsRegistry.DOMINION_ROD.get()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (player.getMainHandItem().getItem() != ItemsRegistry.WIXIE_CHARM.get()
                && !player.getMainHandItem().isEmpty()
                && worldIn.getBlockEntity(pos) instanceof WixieCauldronTile cauldronTile) {
            MultiRecipeWrapper wrapper = cauldronTile.getRecipesForStack(player.getMainHandItem());
            if (wrapper.isEmpty()) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.wixie.no_recipe"));
            } else {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.wixie.recipe_set"));
            }
            return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FILLED);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
        if (!world.isClientSide() && world.getBlockEntity(pos) instanceof WixieCauldronTile cauldronTile) {
            cauldronTile.isOff = world.hasNeighborSignal(pos);
            cauldronTile.updateBlock();
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WixieCauldronTile(pos, state);
    }
}
