package com.hollingsworth.arsnouveau.client.patchouli;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ApparatusProcessor implements IComponentProcessor {
    EnchantingApparatusRecipe recipe;
    @Override
    public void setup(IVariableProvider variables) {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        String recipeID = variables.get("recipe").asString();
        recipe = (EnchantingApparatusRecipe) manager.byKey(new ResourceLocation(recipeID)).orElse(null);
    }

    @Override
    public IVariable process(String key) {
        if(recipe == null)
            return null;
        if(key.equals("reagent"))
            return IVariable.wrapList(Arrays.asList(recipe.reagent.getItems()).stream().map(IVariable::from).collect(Collectors.toList()));

        if(key.startsWith("item")) {
            int index = Integer.parseInt(key.substring(4)) - 1;
            if(recipe.pedestalItems.size() <= index)
                return IVariable.from(ItemStack.EMPTY);
            Ingredient ingredient = recipe.pedestalItems.get(Integer.parseInt(key.substring(4)) - 1);
            return IVariable.wrapList(Arrays.asList(ingredient.getItems()).stream().map(IVariable::from).collect(Collectors.toList()));
        }

        return null;
    }
}
