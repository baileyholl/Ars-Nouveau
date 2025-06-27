package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ApparatusRecipeInput;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.common.network.HighlightAreaPacket;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ImbuementBlock extends TickableModBlock {
    public ImbuementBlock() {
        super(defaultProperties().noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(BlockStateProperties.FACING, rot.rotate(state.getValue(BlockStateProperties.FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ImbuementTile(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        ImbuementTile tile = (ImbuementTile) pLevel.getBlockEntity(pPos);

        if (tile == null) return 0;
        RecipeHolder<? extends IImbuementRecipe> holder = tile.getRecipeNow();
        if (holder == null && tile.stack.isEmpty()) return 0;
        if (holder == null) return 15;

        IImbuementRecipe recipe = holder.value();
        int cost = recipe.getSourceCost(tile);

        return Mth.lerpDiscrete((float) tile.getSource() / cost, 1, 15);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!(worldIn.getBlockEntity(pos) instanceof ImbuementTile tile))
            return ItemInteractionResult.SUCCESS;
        if (worldIn.isClientSide || handIn != InteractionHand.MAIN_HAND)
            return ItemInteractionResult.SUCCESS;

        if (tile.stack.isEmpty() && !player.getItemInHand(handIn).isEmpty()) {

            tile.stack = player.getItemInHand(handIn).copy();
            var recipe = tile.getRecipeNow();
            if (recipe == null) {
                List<ColorPos> colorPos = new ArrayList<>();
                for (BlockPos pedPos : tile.getNearbyPedestals()) {
                    if (worldIn.getBlockEntity(pedPos) instanceof ArcanePedestalTile pedestalTile) {
                        colorPos.add(ColorPos.centeredAbove(pedPos));
                    }
                }

                Networking.sendToNearbyClient(worldIn, tile.getBlockPos(), new HighlightAreaPacket(colorPos, 60));

                var apparatusRecipe = IEnchantingRecipe.getRecipe(worldIn, new ApparatusRecipeInput(tile.stack, tile.getPedestalItems(), player));
                if (apparatusRecipe == null) {
                    PortUtil.sendMessage(player, Component.translatable("ars_nouveau.imbuement.norecipe"));
                } else {
                    PortUtil.sendMessage(player, Component.translatable("ars_nouveau.imbuement.use_apparatus"));
                }

                tile.stack = ItemStack.EMPTY;
            } else {
                tile.stack = player.getInventory().removeItem(player.getInventory().selected, 1);
                PortUtil.sendMessageNoSpam(player, recipe.value().getCraftingStartedText(tile));
                tile.updateBlock();
            }
        } else {

            ItemEntity item = new ItemEntity(worldIn, player.getX(), player.getY(), player.getZ(), tile.stack.copy());
            worldIn.addFreshEntity(item);
            tile.stack = ItemStack.EMPTY;
            tile.stack = player.getInventory().getSelected().copy();

            var recipe = tile.getRecipeNow();
            if (recipe != null) {
                tile.stack = player.getInventory().removeItem(player.getInventory().selected, 1);
            } else {
                tile.stack = ItemStack.EMPTY;
            }
            tile.draining = false;
            tile.updateBlock();
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public BlockState playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if (!(worldIn.getBlockEntity(pos) instanceof ImbuementTile))
            return state;
        ItemStack stack = ((ImbuementTile) worldIn.getBlockEntity(pos)).stack;
        worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack.copy()));
        ((ImbuementTile) worldIn.getBlockEntity(pos)).stack = ItemStack.EMPTY;
        return state;
    }
}
