/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client.page;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

public abstract class BookRecipePageRenderer<R extends Recipe<?>, T extends BookRecipePage<R>> extends BookPageRenderer<T> implements PageWithTextRenderer {

    public static int Y = 4;
    public static int X = BookContentScreen.PAGE_WIDTH / 2 - 49;

    public BookRecipePageRenderer(T page) {
        super(page);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks) {
        int recipeX = X;
        int recipeY = Y;

        if (this.page.getRecipe1() != null) {

            //Title 1 is always rendered (falls back to recipe name)
            this.drawRecipe(guiGraphics, this.page.getRecipe1(), recipeX, recipeY, mouseX, mouseY, false);


            if (this.page.getRecipe2() != null) {
                //Title 2 might be skipped if identical to Title 2, so respect that here
                this.drawRecipe(guiGraphics, this.page.getRecipe2(), recipeX,
                        recipeY + this.getRecipeHeight() - (this.page.getTitle2().getString().isEmpty() ? 10 : 0),
                        mouseX, mouseY, true);
            }
        } else {
            this.drawWrappedStringNoShadow(guiGraphics,
                    Component.translatable(ModonomiconConstants.I18n.Gui.RECIPE_PAGE_RECIPE_MISSING, this.page.getRecipeId1()),
                    recipeX - 13, recipeY - 15, 0xFF0000, BookContentScreen.PAGE_WIDTH);
        }

        if (this.page.getRecipe2() == null) //only render if no second recipe availble
            this.renderBookTextHolder(guiGraphics, this.getPage().getText(),0, this.getTextY(), BookContentScreen.PAGE_WIDTH);

        var style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null)
            this.parentScreen.renderComponentHoverEffect(guiGraphics, style, mouseX, mouseY);
    }

    @Nullable
    @Override
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        if (pMouseX > 0 && pMouseY > 0) {

            //titles are not markdown enabled here, so no links

            var textStyle = this.getClickedComponentStyleAtForTextHolder(this.page.getText(), 0, this.getTextY(), BookContentScreen.PAGE_WIDTH, pMouseX, pMouseY);
            if (textStyle != null) {
                return textStyle;
            }
        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY);
    }

    @Override
    public int getTextY() {
        return Y + this.getRecipeHeight() * (this.page.getRecipe2() == null ? 1 : 2) - (this.page.getTitle2().isEmpty() ? 10 : 0);
    }

    protected abstract int getRecipeHeight();

    protected abstract void drawRecipe(GuiGraphics guiGraphics, R recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second);
}
