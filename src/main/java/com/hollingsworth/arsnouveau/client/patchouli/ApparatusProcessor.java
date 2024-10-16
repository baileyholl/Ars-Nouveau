package com.hollingsworth.arsnouveau.client.patchouli;


import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ApparatusProcessor implements IComponentProcessor {
    RecipeHolder<? extends EnchantingApparatusRecipe> holder;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        String recipeID = variables.get("recipe", level.registryAccess()).asString();
        holder = (RecipeHolder<? extends EnchantingApparatusRecipe>) manager.byKey(ResourceLocation.tryParse(recipeID)).orElse(null);
    }

    @Override
    public IVariable process(Level level, String key) {
        if (holder == null)
            return null;
        var recipe = holder.value();
        if (key.equals("reagent"))
            return IVariable.wrapList(Arrays.stream(recipe.reagent().getItems()).map(i -> IVariable.from(i, level.registryAccess())).collect(Collectors.toList()), level.registryAccess());

        if (key.equals("recipe")) {
            return IVariable.wrap(holder.id().toString(), level.registryAccess());
        }
        if (key.equals("output")) {
            return IVariable.from(recipe.result(), level.registryAccess());
        }
        if (key.equals("footer")) {
            return IVariable.wrap(recipe.result().getItem().getDescriptionId(), level.registryAccess());
        }

        return null;
    }
}
