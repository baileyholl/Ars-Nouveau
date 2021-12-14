package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.CrystallizerTile;
import com.hollingsworth.arsnouveau.common.crafting.recipes.InfuserRecipe;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CrystallizerBlock extends TickableModBlock {
    public CrystallizerBlock() {
        super(defaultProperties().noOcclusion(), LibBlockNames.CRYSTALLIZER);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrystallizerTile(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }


    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if(!(worldIn.getBlockEntity(pos) instanceof CrystallizerTile))
            return super.use(state, worldIn, pos, player, handIn, hit);
        if(worldIn.isClientSide || handIn != InteractionHand.MAIN_HAND)
            return super.use(state, worldIn, pos, player, handIn, hit);
        CrystallizerTile tile = (CrystallizerTile) worldIn.getBlockEntity(pos);
        if(tile.stack.isEmpty() && !player.getItemInHand(handIn).isEmpty()){
            InfuserRecipe recipe = worldIn.getRecipeManager().getAllRecipesFor(RecipeRegistry.INFUSER_TYPE).stream()
                    .filter(f -> f.matches(new SimpleContainer(player.getItemInHand(handIn)), worldIn)).findFirst().orElse(null);
            if(recipe == null){
                PortUtil.sendMessage(player, new TranslatableComponent("ars_nouveau.norecipe"));
            }else{
                tile.stack = player.getInventory().removeItem(player.getInventory().selected, 1);
                tile.update();
            }
        }else{

            ItemEntity item = new ItemEntity(worldIn, player.getX(), player.getY(), player.getZ(), tile.stack.copy());
            worldIn.addFreshEntity(item);
            tile.stack = ItemStack.EMPTY;
            InfuserRecipe recipe = worldIn.getRecipeManager().getAllRecipesFor(RecipeRegistry.INFUSER_TYPE).stream()
                    .filter(f -> f.matches(new SimpleContainer(player.getItemInHand(handIn)), worldIn)).findFirst().orElse(null);
            if(recipe != null){
                tile.stack = player.getInventory().removeItem(player.getInventory().selected, 1);
            }
            tile.draining = false;
            tile.update();
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if(!(worldIn.getBlockEntity(pos) instanceof CrystallizerTile))
            return;
        ItemStack stack = ((CrystallizerTile) worldIn.getBlockEntity(pos)).stack;
        worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack.copy()));
        ((CrystallizerTile) worldIn.getBlockEntity(pos)).stack = ItemStack.EMPTY;
    }
}
