/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client.page;

import com.hollingsworth.arsnouveau.common.book.BookTextHolder;
import com.hollingsworth.arsnouveau.common.book.RenderedBookTextHolder;
import com.hollingsworth.arsnouveau.common.book.client.BookContentScreen;
import com.hollingsworth.arsnouveau.common.book.markdown.MarkdownComponentRenderUtils;
import com.hollingsworth.arsnouveau.common.book.page.BookPage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BookPageRenderer<T extends BookPage> {
    public int left;
    public int top;
    protected T page;
    protected BookContentScreen parentScreen;
    protected Minecraft mc;
    protected Font font;

    private List<Button> buttons = new ArrayList<>();


    public BookPageRenderer(T page) {
        this.page = page;
    }

    /**
     * Will render the given BookTextHolder as (left-aligned) content text. Will automatically handle markdown.
     */
    public static void renderBookTextHolder(GuiGraphics guiGraphics, BookTextHolder text, Font font, int x, int y, int width) {
        if (text.hasComponent()) {
            //if it is a component, we draw it directly
            for (FormattedCharSequence formattedcharsequence : font.split(text.getComponent(), width)) {
                guiGraphics.drawString(font, formattedcharsequence, x, y, 0, false);
                y += font.lineHeight;
            }
        } else if (text instanceof RenderedBookTextHolder renderedText) {
            //if it is not a component it was sent through the markdown renderer
            var components = renderedText.getRenderedText();

            for (var component : components) {
                var wrapped = MarkdownComponentRenderUtils.wrapComponents(component, width, width - 10, font);
                for (FormattedCharSequence formattedcharsequence : wrapped) {
                    guiGraphics.drawString(font, formattedcharsequence, x, y, 0, false);
                    y += font.lineHeight;
                }
            }
        } else {
//            Modonomicon.LOGGER.warn("BookTextHolder with String {} has no component, but is not rendered to markdown either.", text.getString());
        }
    }

    /**
     * Call when the page is being set up to be displayed (when book content screen opens, or pages are changed)
     */
    public void onBeginDisplayPage(BookContentScreen parentScreen, int left, int top) {
        this.parentScreen = parentScreen;

        this.mc = parentScreen.getMinecraft();
        this.font = this.mc.font;
        this.left = left;
        this.top = top;

        this.buttons = new ArrayList<>();
    }

    public T getPage() {
        return this.page;
    }

    /**
     * Call when the page is will no longer be displayed (when book content screen opens, or pages are changed)
     */
    public void onEndDisplayPage(BookContentScreen parentScreen) {
        parentScreen.removeRenderableWidgets(this.buttons);
    }

    /**
     * @param pMouseX localized to page x (mouseX - bookLeft - page.left)
     * @param pMouseY localized to page y (mouseY - bookTop - page.top)
     */
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return false;
    }

    /**
     * Will render the given BookTextHolder as (left-aligned) content text. Will automatically handle markdown.
     */
    public void renderBookTextHolder(GuiGraphics guiGraphics, BookTextHolder text, int x, int y, int width) {
        x += this.parentScreen.getBook().getBookTextOffsetX();
        y += this.parentScreen.getBook().getBookTextOffsetY();
        width += this.parentScreen.getBook().getBookTextOffsetWidth();
        width -= this.parentScreen.getBook().getBookTextOffsetX(); //always remove the offset x from the width to avoid overflow

        renderBookTextHolder(guiGraphics, text, this.font, x, y, width);
    }

    /**
     * Will render the given BookTextHolder as (centered) title.
     */
    public void renderTitle(GuiGraphics guiGraphics, BookTextHolder title, boolean showTitleSeparator, int x, int y) {

        guiGraphics.pose().pushPose();

        if (title instanceof RenderedBookTextHolder renderedTitle) {
            //if user decided to use markdown title, we need to use the  rendered version
            var formattedCharSequence = FormattedCharSequence.fromList(
                    renderedTitle.getRenderedText().stream().map(Component::getVisualOrderText).toList());

            //if title is larger than allowed, scaled to fit
            var scale = Math.min(1.0f, (float) BookContentScreen.MAX_TITLE_WIDTH / (float) this.font.width(formattedCharSequence));
            if (scale < 1) {
                guiGraphics.pose().translate(0, y - y * scale, 0);
                guiGraphics.pose().scale(scale, scale, scale);
            }

            this.drawCenteredStringNoShadow(guiGraphics, formattedCharSequence, x, y, 0, scale);
        } else {

            //if title is larger than allowed, scaled to fit
            var scale = Math.min(1.0f, (float) BookContentScreen.MAX_TITLE_WIDTH / (float) this.font.width(title.getComponent().getVisualOrderText()));
            if (scale < 1) {
                guiGraphics.pose().translate(0, y - y * scale, 0);
                guiGraphics.pose().scale(scale, scale, scale);
            }

            //otherwise we use the component - that is either provided by the user, or created from the default title style.
            this.drawCenteredStringNoShadow(guiGraphics, title.getComponent().getVisualOrderText(), x, y, 0, scale);
        }

        guiGraphics.pose().popPose();

        if (showTitleSeparator)
            BookContentScreen.drawTitleSeparator(guiGraphics, this.page.getBook(), x, y + 12);
    }

    public abstract void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks);

    public void drawCenteredStringNoShadow(GuiGraphics guiGraphics, FormattedCharSequence s, int x, int y, int color, float scale) {
        guiGraphics.drawString(this.font, s, x - this.font.width(s) * scale / 2.0F, y + (this.font.lineHeight * (1 - scale)), color, false);
    }

    public void drawCenteredStringNoShadow(GuiGraphics guiGraphics, String s, int x, int y, int color, float scale) {
        guiGraphics.drawString(this.font, s, x - this.font.width(s) * scale / 2.0F, y + (this.font.lineHeight * (1 - scale)), color, false);
    }

    public void drawWrappedStringNoShadow(GuiGraphics guiGraphics, Component s, int x, int y, int color, int width) {
        for (FormattedCharSequence formattedcharsequence : this.font.split(s, width)) {
            guiGraphics.drawString(this.font, formattedcharsequence, x, y + (this.font.lineHeight), color, false);
            y += this.font.lineHeight;
        }
    }

    /**
     * @param pMouseX localized to page x (mouseX - bookLeft - page.left)
     * @param pMouseY localized to page y (mouseY - bookTop - page.top)
     */
    @Nullable
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        return null;
    }

    protected void addButton(Button button) {
        button.setX(button.getX() + this.parentScreen.getBookLeft() + this.left);
        button.setY(button.getY() + this.parentScreen.getBookTop() + this.top);
        this.buttons.add(button);
        this.parentScreen.addRenderableWidget(button);
    }

    @Nullable
    protected Style getClickedComponentStyleAtForTitle(BookTextHolder title, int x, int y, double pMouseX, double pMouseY) {
        //they say good code comments itself. Well, this is not good code.
        if (title instanceof RenderedBookTextHolder renderedTitle) {
            //markdown title
            var formattedCharSequence = FormattedCharSequence.fromList(
                    renderedTitle.getRenderedText().stream().map(Component::getVisualOrderText).toList());
            if (pMouseY > y && pMouseY < y + this.font.lineHeight) {
                //check if we are vertically over the title line

                x = x - this.font.width(formattedCharSequence) / 2;
                if (pMouseX < x)
                    return null;
                //if we are horizontally left of the title, exit

                //horizontally over and right of the title is handled by font splitter
                return this.font.getSplitter().componentStyleAtWidth(formattedCharSequence, (int) pMouseX - x);
            }
        } else {
            if (pMouseY > y && pMouseY < y + this.font.lineHeight) {
                //check if we are vertically over the title line

                var formattedCharSequence = title.getComponent().getVisualOrderText();
                x = x - this.font.width(formattedCharSequence) / 2;
                if (pMouseX < x)
                    return null;
                //if we are horizontally left of the title, exit

                //horizontally over and right of the title is handled by font splitter
                return this.font.getSplitter().componentStyleAtWidth(formattedCharSequence, (int) pMouseX - x);
            }
        }
        return null;
    }

    @Nullable
    protected Style getClickedComponentStyleAtForTextHolder(BookTextHolder text, int x, int y, int width, double pMouseX, double pMouseY) {
        if (text.hasComponent()) {
            //we don't do math to geht the current line, we just split and iterate.
            //why? Because performance should not matter (significantly enough to bother)
            for (FormattedCharSequence formattedcharsequence : this.font.split(text.getComponent(), width)) {
                if (pMouseY > y && pMouseY < y + this.font.lineHeight) {
                    //check if we are vertically over the title line
                    //horizontally over and right of the title is handled by font splitter
                    return this.font.getSplitter().componentStyleAtWidth(formattedcharsequence, (int) pMouseX - x);
                }
                y += this.font.lineHeight;
            }
        } else if (text instanceof RenderedBookTextHolder renderedText) {
            var components = renderedText.getRenderedText();
            for (var component : components) {
                var wrapped = MarkdownComponentRenderUtils.wrapComponents(component, width, width - 10, this.font);
                for (FormattedCharSequence formattedcharsequence : wrapped) {
                    if (pMouseY > y && pMouseY < y + this.font.lineHeight) {
                        //check if we are vertically over the title line
                        //horizontally over and right of the title is handled by font splitter
                        return this.font.getSplitter().componentStyleAtWidth(formattedcharsequence, (int) pMouseX - x);
                    }
                    y += this.font.lineHeight;
                }
            }
        }

        return null;
    }
}
