/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client.page;

import com.klikli_dev.modonomicon.book.page.BookSmithingRecipePage;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

public class BookSmithingRecipePageRenderer extends BookRecipePageRenderer<SmithingRecipe, BookSmithingRecipePage> {
    public BookSmithingRecipePageRenderer(BookSmithingRecipePage page) {
        super(page);
    }

    @Override
    protected int getRecipeHeight() {
        return 76;
    }

    @Override
    protected void drawRecipe(GuiGraphics guiGraphics, SmithingRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {

        recipeY += 10;


        if (!second) {
            if (!this.page.getTitle1().isEmpty()) {
                this.renderTitle(guiGraphics, this.page.getTitle1(), false, BookContentScreen.PAGE_WIDTH / 2, 0);
            }
        } else {
            if (!this.page.getTitle2().isEmpty()) {
                this.renderTitle(guiGraphics, this.page.getTitle2(), false, BookContentScreen.PAGE_WIDTH / 2,
                        recipeY - (this.page.getTitle2().getString().isEmpty() ? 10 : 0) - 10);
            }
        }

        RenderSystem.enableBlend();
        guiGraphics.blit(this.page.getBook().getCraftingTexture(), recipeX, recipeY, 11, 178, 96, 62, 128, 256);

        Ingredient base = Ingredient.EMPTY;
        Ingredient addition = Ingredient.EMPTY;
        Ingredient template = Ingredient.EMPTY;
        if (recipe instanceof SmithingTransformRecipe transformRecipe) {
            base = transformRecipe.base;
            addition = transformRecipe.addition;
            template = transformRecipe.template;
        }
        if (recipe instanceof SmithingTrimRecipe trimRecipe) {
            base = trimRecipe.base;
            addition = trimRecipe.addition;
            template = trimRecipe.template;
        }

        this.parentScreen.renderIngredient(guiGraphics, recipeX + 4, recipeY + 4, mouseX, mouseY, template);
        this.parentScreen.renderIngredient(guiGraphics, recipeX + 4, recipeY + 23, mouseX, mouseY, base);
        this.parentScreen.renderIngredient(guiGraphics, recipeX + 4, recipeY + 42, mouseX, mouseY, addition);
        this.parentScreen.renderItemStack(guiGraphics, recipeX + 40, recipeY + 23, mouseX, mouseY, recipe.getToastSymbol());
        this.parentScreen.renderItemStack(guiGraphics, recipeX + 76, recipeY + 23, mouseX, mouseY, recipe.getResultItem(this.parentScreen.getMinecraft().level.registryAccess()));
    }
}
