package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.common.crafting.recipes.DyeRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IDyeable;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmiDyeRecipeCategory implements EmiRecipe {
    private final ResourceLocation id;
    private final DyeRecipe recipe;

    public EmiDyeRecipeCategory(ResourceLocation id, DyeRecipe recipe) {
        this.id = id;
        this.recipe = recipe;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return VanillaEmiRecipeCategories.CRAFTING;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return recipe.getIngredients().stream()
                .map(EmiIngredient::of)
                .toList();
    }

    @Override
    public List<EmiStack> getOutputs() {
        ItemStack resultItem = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
        List<EmiStack> results = new ArrayList<>();
        if (resultItem.getItem() instanceof IDyeable toDye) {
            List<DyeColor> colors = Arrays.stream(recipe.getIngredients().get(0).getItems()).map(DyeColor::getColor).toList();

            for (DyeColor color : colors) {
                if (color == null) continue;
                ItemStack copy = resultItem.copy();
                toDye.onDye(copy, color);
                results.add(EmiStack.of(copy));
            }
        }

        return results;
    }

    @Override
    public int getDisplayWidth() {
        return 118;
    }

    @Override
    public int getDisplayHeight() {
        return 54;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 18);
        widgets.addTexture(EmiTexture.SHAPELESS, 97, 0);

        var input = this.getInputs();
        for (int i = 0; i < 9; i++) {
            if (i < input.size()) {
                widgets.addSlot(input.get(i), i % 3 * 18, i / 3 * 18);
            } else {
                widgets.addSlot(EmiStack.of(ItemStack.EMPTY), i % 3 * 18, i / 3 * 18);
            }
        }
        widgets.addSlot(EmiIngredient.of(this.getOutputs()), 92, 14).large(true).recipeContext(this);
    }
}