package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.api.potion.IPotionProvider;
import com.hollingsworth.arsnouveau.api.registry.PotionProviderRegistry;
import com.hollingsworth.arsnouveau.common.items.PotionFlask;
import com.hollingsworth.arsnouveau.common.util.PotionUtil;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;

import java.util.List;

public class PotionFlaskRecipe extends ShapelessRecipe {

    public PotionFlaskRecipe(String groupIn, ItemStack result, ItemStack recipeOutputIn) {
        super(groupIn, CraftingBookCategory.MISC, recipeOutputIn, NonNullList.copyOf(List.of(Ingredient.of(result), Ingredient.of(Items.POTION))));
    }

    public PotionFlaskRecipe(String pGroup, CraftingBookCategory pCategory, ItemStack pResult, NonNullList<Ingredient> pIngredients) {
        super(pGroup, pCategory, pResult, pIngredients);
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
                IPotionProvider provider = PotionProviderRegistry.from(flaskPotionStack);
                if (provider == null || provider.roomLeft(flaskPotionStack) <= 0)
                    return ItemStack.EMPTY;
            }
            if (stack.getItem() instanceof PotionItem) {
                potionStack = stack;
            }
        }
        if (flaskPotionStack.isEmpty() || potionStack.isEmpty())
            return ItemStack.EMPTY;
        IPotionProvider provider = PotionProviderRegistry.from(flaskPotionStack);
        PotionContents potionData = potionStack.get(DataComponents.POTION_CONTENTS);
        if (provider == null)
            return ItemStack.EMPTY;
        int count = provider.usesRemaining(flaskPotionStack);
        ItemStack copyStack = flaskPotionStack.copy();
        if (count <= 0) {
            provider.setData(potionData, 1, provider.maxUses(flaskPotionStack), copyStack);
            return copyStack;
        } else if (PotionUtil.arePotionContentsEqual(potionData, provider.getPotionData(potionStack))) {
            provider.setData(potionData, count + 1, provider.maxUses(flaskPotionStack), copyStack);
            return copyStack;
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
