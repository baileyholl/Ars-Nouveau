package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.common.items.PotionFlask;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.*;

public class PotionFlaskRecipe extends ShapelessRecipe {
    public PotionFlaskRecipe(String groupIn, CraftingBookCategory category, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn) {
        super(groupIn, CraftingBookCategory.MISC, recipeOutputIn, recipeItemsIn);
    }


    @Override
    public ItemStack assemble(final CraftingInput inv, HolderLookup.Provider p_266797_) {
        final ItemStack output = super.assemble(inv, p_266797_); // Get the default output
        if (output.isEmpty())
            return ItemStack.EMPTY;
        ItemStack flaskPotionStack = ItemStack.EMPTY;
        ItemStack potionStack = ItemStack.EMPTY;
        for (int i = 0; i < inv.size(); i++) {
            final ItemStack stack = inv.getItem(i);
            if (stack.getItem() instanceof PotionFlask flask) {
                flaskPotionStack = stack.copy();
                if(flask.isMax(stack))
                    return ItemStack.EMPTY;
            }
            if(stack.getItem() instanceof PotionItem){
                potionStack = stack;
            }
        }
        if(flaskPotionStack.isEmpty() || potionStack.isEmpty())
            return ItemStack.EMPTY;
        PotionFlask.FlaskData flaskData = new PotionFlask.FlaskData(flaskPotionStack);
        PotionData potionData = new PotionData(potionStack);
        if(flaskData.getCount() <= 0){
            flaskData.setPotion(potionData);
            flaskData.setCount(1);
            return flaskPotionStack.copy();
        }
        if(flaskData.getPotion().areSameEffects(potionData)){
            flaskData.setCount(flaskData.getCount() + 1);
            return flaskPotionStack.copy();
        }
        return ItemStack.EMPTY; // Return the modified output
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.size(), ItemStack.EMPTY);
        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = inv.getItem(i);
            if (item.hasCraftingRemainingItem()) {
                nonnulllist.set(i, item.getCraftingRemainingItem());
            } else if (item.getItem() instanceof PotionItem) {
                nonnulllist.set(i, new ItemStack(Items.GLASS_BOTTLE));
            }
        }
        return nonnulllist;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.POTION_FLASK_RECIPE.get();
    }
}
