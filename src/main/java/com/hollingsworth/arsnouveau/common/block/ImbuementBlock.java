package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.hollingsworth.arsnouveau.common.network.HighlightAreaPacket;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;

public class ImbuementBlock extends TickableModBlock {
    public ImbuementBlock() {
        super(defaultProperties().noOcclusion());
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
                for(BlockPos pedPos : tile.getNearbyPedestals()){
                    if(worldIn.getBlockEntity(pedPos) instanceof ArcanePedestalTile pedestalTile){
                        colorPos.add(ColorPos.centeredAbove(pedPos));
                    }
                }
                Networking.sendToNearbyClient(worldIn, tile.getBlockPos(), new HighlightAreaPacket(colorPos, 60));
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.imbuement.norecipe"));
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
