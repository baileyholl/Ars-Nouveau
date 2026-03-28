package com.hollingsworth.arsnouveau.client.jei;


import com.hollingsworth.arsnouveau.common.crafting.recipes.DyeRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IDyeable;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// JEI 27.4 (1.21.11): ICraftingCategoryExtension now requires getIngredients(RecipeHolder<R>) returning List<SlotDisplay>.
// The old setRecipe helper methods (createAndSetInputs/Outputs with List<List<ItemStack>>) were removed.
public class DyeRecipeCategory implements ICraftingCategoryExtension<DyeRecipe> {

    public DyeRecipeCategory() {
    }

    @Override
    public List<SlotDisplay> getIngredients(RecipeHolder<DyeRecipe> recipeHolder) {
        // Return slot displays from placementInfo - used by JEI internally for ingredient matching
        return recipeHolder.value().placementInfo().ingredients().stream()
                .map(ingredient -> (SlotDisplay) new SlotDisplay.Composite(
                        ingredient.items()
                                .map(holder -> (SlotDisplay) new SlotDisplay.ItemSlotDisplay(holder))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void setRecipe(RecipeHolder<DyeRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        var recipe = recipeHolder.value();
        List<List<ItemStack>> inputs = recipe.placementInfo().ingredients().stream()
                .map(ingredient -> ingredient.items()
                        .map(h -> new ItemStack(h.value()))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        // Get the result item from the ShapelessRecipe via the non-empty input assembly
        // ShapelessRecipe stores result but doesn't expose it directly; use a matching input stack
        List<ItemStack> inputStacks = inputs.stream()
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .collect(Collectors.toList());

        // Use the first non-dye stack as the base item for result display
        ItemStack baseItem = inputStacks.stream()
                .filter(s -> !(s.getItem() instanceof DyeItem))
                .findFirst().orElse(ItemStack.EMPTY);

        List<ItemStack> results = new ArrayList<>();
        if (!baseItem.isEmpty() && baseItem.getItem() instanceof IDyeable toDye) {
            var focus = focuses.getItemStackFocuses(RecipeIngredientRole.INPUT)
                    .map(f -> f.getTypedValue().getIngredient())
                    .filter(f -> f.getItem() instanceof DyeItem)
                    .toList();

            List<DyeColor> colors = focus.isEmpty()
                    ? inputs.stream()
                        .flatMap(list -> list.stream())
                        .filter(s -> s.getItem() instanceof DyeItem)
                        .map(DyeColor::getColor)
                        .filter(c -> c != null)
                        .toList()
                    : focus.stream().map(DyeColor::getColor).toList();

            for (DyeColor color : colors) {
                if (color == null) continue;
                ItemStack copy = baseItem.copy();
                toDye.onDye(copy, color);
                results.add(copy);
            }
        }

        craftingGridHelper.createAndSetOutputs(builder, results);
        craftingGridHelper.createAndSetInputs(builder, inputs, 0, 0);
    }
}
