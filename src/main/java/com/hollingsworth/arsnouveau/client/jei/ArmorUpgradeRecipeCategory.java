package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.ArmorUpgradeRecipe;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArmorUpgradeRecipeCategory extends EnchantingApparatusRecipeCategory<ArmorUpgradeRecipe> {

    public ArmorUpgradeRecipeCategory(IGuiHelper helper) {
        super(helper);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ArmorUpgradeRecipe recipe, IFocusGroup focuses) {
        MultiProvider provider = multiProvider.apply(recipe);
        List<Ingredient> inputs = provider.input();
        double angleBetweenEach = 360.0 / inputs.size();

        for (Ingredient input : inputs) {
            builder.addSlot(RecipeIngredientRole.INPUT, (int) point.x, (int) point.y).addIngredients(input);
            point = rotatePointAbout(point, center, angleBetweenEach);
        }
    }

    @Override
    public Component getTitle() {
        return Component.translatable("ars_nouveau.armor_upgrade");
    }

    @Override
    public RecipeType<ArmorUpgradeRecipe> getRecipeType() {
        return JEIArsNouveauPlugin.ARMOR_RECIPE_TYPE;
    }

    @Override
    public void draw(ArmorUpgradeRecipe recipe, @NotNull IRecipeSlotsView slotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        Font renderer = Minecraft.getInstance().font;
        renderer.draw(matrixStack, Component.translatable("ars_nouveau.tier", 1 + recipe.tier), 0.0f, 0.0f, 10);

        if (recipe.consumesSource())
            renderer.draw(matrixStack, Component.translatable("ars_nouveau.source", recipe.sourceCost), 0.0f, 100f, 10);
    }
}
