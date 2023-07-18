/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client.page;

import com.klikli_dev.modonomicon.book.page.BookSpotlightPage;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class BookSpotlightPageRenderer extends BookPageRenderer<BookSpotlightPage> implements PageWithTextRenderer {

    public static final int ITEM_X = BookContentScreen.PAGE_WIDTH / 2 - 8;
    public static final int ITEM_Y = 15;


    public BookSpotlightPageRenderer(BookSpotlightPage page) {
        super(page);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks) {
        if (this.page.hasTitle()) {
            this.renderTitle(guiGraphics, this.page.getTitle(), false, BookContentScreen.PAGE_WIDTH / 2, 0);
        }

        this.renderBookTextHolder(guiGraphics, this.getPage().getText(), 0, this.getTextY(), BookContentScreen.PAGE_WIDTH);

        int w = 66;
        int h = 26;

        RenderSystem.enableBlend();
        guiGraphics.blit(this.page.getBook().getCraftingTexture(), BookContentScreen.PAGE_WIDTH / 2 - w / 2, 10, 0, 128 - h, w, h, 128, 256);

        this.parentScreen.renderIngredient(guiGraphics, ITEM_X, ITEM_Y, mouseX, mouseY, this.page.getItem());


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

            //BookContentScreen.PAGE_WIDTH / 2 - 8, 15
            if (pMouseX >= ITEM_X && pMouseX <= ITEM_X + 16 && pMouseY >= ITEM_Y && pMouseY <= ITEM_Y + 16) {

                var stacks = this.page.getItem().getItems();
                if (stacks.length > 0) {
                    var stack = stacks[(this.parentScreen.ticksInBook / 20) % stacks.length];
                    var itemLink = "item://" + ForgeRegistries.ITEMS.getKey(stack.getItem());
                    if (stack.hasTag()) {
                        itemLink += stack.getTag().toString();
                    }
                    var spotlightItemStyle = Style.EMPTY
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(stack)))
                            .withClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, itemLink));

                    return spotlightItemStyle;
                }

            }
        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY);
    }

    @Override
    public int getTextY() {
        return 40;
    }
}
