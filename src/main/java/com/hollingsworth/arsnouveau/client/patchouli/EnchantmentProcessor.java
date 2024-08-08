package com.hollingsworth.arsnouveau.client.patchouli;


import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantmentRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EnchantmentProcessor implements IComponentProcessor {
    RecipeHolder<? extends EnchantmentRecipe> recipe;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        String recipeID = variables.get("recipe", level.registryAccess()).asString();
        recipe = (RecipeHolder<? extends EnchantmentRecipe>) manager.byKey(ResourceLocation.tryParse(recipeID)).orElse(null);
    }

    @Override
    public IVariable process(Level level, String key) {
        if (recipe == null)
            return null;
        var enchant = recipe.value();
        if (key.equals("enchantment")) {
            var enchantment = level.holderOrThrow(enchant.enchantmentKey);
            return IVariable.wrap(enchantment.value().description().getString(), level.registryAccess());
        }
        if (key.equals("level"))
            return IVariable.wrap(enchant.enchantLevel);

        if (key.startsWith("item")) {
            int index = Integer.parseInt(key.substring(4)) - 1;
            if (enchant.pedestalItems().size() <= index)
                return IVariable.from(ItemStack.EMPTY, level.registryAccess());
            Ingredient ingredient = enchant.pedestalItems().get(Integer.parseInt(key.substring(4)) - 1);
            return IVariable.wrapList(Arrays.stream(ingredient.getItems()).map(i -> IVariable.from(i, level.registryAccess())).collect(Collectors.toList()), level.registryAccess());
        }

        return null;
    }
}
