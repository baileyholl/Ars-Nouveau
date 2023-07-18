/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client.page;

import com.klikli_dev.modonomicon.book.page.BookTextPage;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

public class BookTextPageRenderer extends BookPageRenderer<BookTextPage> implements PageWithTextRenderer {
    public BookTextPageRenderer(BookTextPage page) {
        super(page);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks) {
        if (this.page.hasTitle()) {
            this.renderTitle(guiGraphics, this.page.getTitle(), this.page.showTitleSeparator(), BookContentScreen.PAGE_WIDTH / 2, 0);
        }

        this.renderBookTextHolder(guiGraphics, this.getPage().getText(), 0, this.getTextY(), BookContentScreen.PAGE_WIDTH);

        var style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null)
            this.parentScreen.renderComponentHoverEffect(guiGraphics, style, mouseX, mouseY);
    }

    @Nullable
    @Override
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        if (pMouseX > 0 && pMouseY > 0) {
            if (this.page.hasTitle()) {
                var titleStyle = this.getClickedComponentStyleAtForTitle(this.page.getTitle(), BookContentScreen.PAGE_WIDTH / 2, 0, pMouseX, pMouseY);
                if (titleStyle != null) {
                    return titleStyle;
                }
            }

            var textStyle = this.getClickedComponentStyleAtForTextHolder(this.page.getText(), 0, this.getTextY(), BookContentScreen.PAGE_WIDTH, pMouseX, pMouseY);
            if (textStyle != null) {
                return textStyle;
            }
        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY);
    }

    @Override
    public int getTextY() {
        if (this.page.hasTitle()) {
            return this.page.showTitleSeparator() ? 17 : 7;
        }

        return -4;
    }
}
