package com.hollingsworth.arsnouveau.client.patchouli.component;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.api.registry.ImbuementRecipeRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import vazkii.patchouli.api.IVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class RotatingItemListComponent extends RotatingItemListComponentBase {
    @SerializedName("recipe_name")
    public String recipeName;

    @SerializedName("recipe_type")
    public String recipeType;

    @Override
    protected List<Ingredient> makeIngredients() {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) return new ArrayList<>();

        Map<ResourceLocation, ? extends Recipe<?>> map;
        if ("enchanting_apparatus".equals(recipeType)) {
            RecipeHolder<EnchantingApparatusRecipe> holder = world.getRecipeManager().getAllRecipesFor(RecipeRegistry.APPARATUS_TYPE.get()).stream().filter(f -> f.id().toString().equals(recipeName)).findFirst().orElse(null);
            var recipe = holder != null ? holder.value() : null;
            for (RecipeType<? extends IEnchantingRecipe> type : ArsNouveauAPI.getInstance().getEnchantingRecipeTypes()) {
                RecipeHolder<? extends IEnchantingRecipe> recipe1 = world.getRecipeManager().getAllRecipesFor(type).stream().filter(f -> f.id().toString().equals(recipeName)).findFirst().orElse(null);
                if (recipe1 != null && recipe1.value() instanceof EnchantingApparatusRecipe enchantingApparatusRecipe) {
                    recipe = enchantingApparatusRecipe;
                    break;
                }
            }
            return recipe == null ? ImmutableList.of() : recipe.pedestalItems();
        } else if ("imbuement_chamber".equals(recipeType)) {
            RecipeHolder<ImbuementRecipe> holder = world.getRecipeManager().getAllRecipesFor(RecipeRegistry.IMBUEMENT_TYPE.get()).stream().filter(f -> f.id().toString().equals(recipeName)).findFirst().orElse(null);
            var recipe = holder != null ? holder.value() : null;
            for (RecipeType<? extends IImbuementRecipe> type : ImbuementRecipeRegistry.INSTANCE.getRecipeTypes()) {
                RecipeType<IImbuementRecipe> imbuementRecipeType = (RecipeType<IImbuementRecipe>) type;
                RecipeHolder<IImbuementRecipe> recipe1 = world.getRecipeManager().getAllRecipesFor(imbuementRecipeType).stream().filter(f -> f.id().toString().equals(recipeName)).findFirst().orElse(null);
                if (recipe1 != null && recipe1.value() instanceof ImbuementRecipe imbuementRecipe) {
                    recipe = imbuementRecipe;
                    break;
                }
            }
            return recipe == null ? ImmutableList.of() : recipe.pedestalItems;
        } else if ("glyph_recipe".equals(recipeType)) {
            RecipeHolder<? extends GlyphRecipe> recipe = (RecipeHolder<? extends GlyphRecipe>) world.getRecipeManager().byKey(ResourceLocation.tryParse(recipeName)).orElse(null);
            return recipe == null ? ImmutableList.of() : recipe.value().inputs;
        } else {
            throw new IllegalArgumentException("Type must be 'enchanting_apparatus', 'glyph_recipe', or 'imbuement_chamber'!");
        }
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
        recipeName = lookup.apply(IVariable.wrap(recipeName)).asString();
        recipeType = lookup.apply(IVariable.wrap(recipeType)).asString();
    }
}
