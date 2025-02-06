package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.api.registry.AlakarkinosConversionRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.AlakarkinosRecipe;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.text.WordUtils;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Locale;

public class AlakarkinosRecipeCategory implements IRecipeCategory<AlakarkinosRecipe> {
    public static float ITEMS_PER_ROW = 7f;

    public IDrawable background;
    public IDrawable icon;

    public AlakarkinosRecipeCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(126, 140);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ItemsRegistry.ALAKARKINOS_CHARM.asItem().getDefaultInstance());
    }

    @Override
    public RecipeType<AlakarkinosRecipe> getRecipeType() {
        return JEIArsNouveauPlugin.ALAKARKINOS_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("ars_nouveau.alakarkinos_recipe");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(AlakarkinosRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        String prepared = recipe.table().location().getPath().replace("archaeology/", "").replaceAll("_[0-9]", "").replaceAll("_", " ").toLowerCase(Locale.ROOT);
        String name = WordUtils.capitalizeFully(prepared);
        guiGraphics.drawString(minecraft.font, Component.literal(name), 22, 4, 0xFF000000, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlakarkinosRecipe recipe, IFocusGroup focuses) {
        DecimalFormat df = new DecimalFormat("##.##%");
        String recipeChance = df.format((float) recipe.weight() / AlakarkinosConversionRegistry.getTotalWeight(recipe.input()));
        builder.addSlot(RecipeIngredientRole.INPUT, 0, 0).addIngredient(VanillaTypes.ITEM_STACK, recipe.input().asItem().getDefaultInstance()).addRichTooltipCallback(
                (view, tooltip) -> tooltip.add(Component.translatable("ars_nouveau.alakarkinos_recipe.chance", recipeChance))
        );
        recipe.drops().ifPresent(drops -> {
            int yOffset = 9;
            int i = (int) ITEMS_PER_ROW;

            for (AlakarkinosRecipe.LootDrop drop : drops) {
                int row = (int) Math.floor(i / ITEMS_PER_ROW);
                int x = (int) ((i - (row * ITEMS_PER_ROW)) * 18);
                int y = row * 18 + yOffset;

                String chance = df.format(drop.chance());
                builder.addSlot(RecipeIngredientRole.OUTPUT, x, y).addItemStack(drop.item()).addRichTooltipCallback(
                        (view, tooltip) -> tooltip.add(Component.translatable("ars_nouveau.alakarkinos_recipe.chance", chance))
                );
                i += 1;
            }
        });
    }
}
