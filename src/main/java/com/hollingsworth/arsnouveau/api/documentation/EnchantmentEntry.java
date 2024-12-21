package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantmentRecipe;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class EnchantmentEntry extends PedestalRecipeEntry{
    RecipeHolder<EnchantmentRecipe> enchantmentRecipe;
    public EnchantmentEntry(RecipeHolder<EnchantmentRecipe> enchantmentRecipe, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.enchantmentRecipe = enchantmentRecipe;
        if(enchantmentRecipe != null) {
            this.ingredients = enchantmentRecipe.value().pedestalItems();
        }
    }

    public static SinglePageCtor create(RecipeHolder<EnchantmentRecipe> enchantmentRecipe){
        return (parent, x, y, width, height) -> new EnchantmentEntry(enchantmentRecipe, parent, x, y, width, height);
    }

    public static SinglePageCtor create(ResourceLocation enchantmentRecipe){
        return (parent, x, y, width, height) -> new EnchantmentEntry(parent.recipeManager().byKeyTyped(RecipeRegistry.ENCHANTMENT_TYPE.get(), enchantmentRecipe), parent, x, y, width, height);
    }
}
