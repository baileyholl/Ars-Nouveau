package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

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
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!(worldIn.getBlockEntity(pos) instanceof ImbuementTile tile))
            return InteractionResult.SUCCESS;
        if (worldIn.isClientSide || handIn != InteractionHand.MAIN_HAND)
            return InteractionResult.SUCCESS;

        if (tile.stack.isEmpty() && !player.getItemInHand(handIn).isEmpty()) {

            tile.stack = player.getItemInHand(handIn).copy();
            ImbuementRecipe recipe = worldIn.getRecipeManager().getAllRecipesFor(RecipeRegistry.IMBUEMENT_TYPE.get()).stream()
                    .filter(f -> f.matches(tile, worldIn)).findFirst().orElse(null);
            if (recipe == null) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.imbuement.norecipe"));
                tile.stack = ItemStack.EMPTY;
            } else {
                tile.stack = player.getInventory().removeItem(player.getInventory().selected, 1);
                tile.update();
            }
        } else {

            ItemEntity item = new ItemEntity(worldIn, player.getX(), player.getY(), player.getZ(), tile.stack.copy());
            worldIn.addFreshEntity(item);
            tile.stack = ItemStack.EMPTY;
            tile.stack = player.getInventory().getSelected().copy();
            ImbuementRecipe recipe = worldIn.getRecipeManager().getAllRecipesFor(RecipeRegistry.IMBUEMENT_TYPE.get()).stream()
                    .filter(f -> f.matches(tile, worldIn)).findFirst().orElse(null);
            if (recipe != null) {
                tile.stack = player.getInventory().removeItem(player.getInventory().selected, 1);
            } else {
                tile.stack = ItemStack.EMPTY;
            }
            tile.draining = false;
            tile.update();
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if (!(worldIn.getBlockEntity(pos) instanceof ImbuementTile))
            return;
        ItemStack stack = ((ImbuementTile) worldIn.getBlockEntity(pos)).stack;
        worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack.copy()));
        ((ImbuementTile) worldIn.getBlockEntity(pos)).stack = ItemStack.EMPTY;
    }
}
