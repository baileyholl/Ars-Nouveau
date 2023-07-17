/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.page;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.common.book.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class BookTextPage extends BookPage {
    protected BookTextHolder title;
    protected boolean useMarkdownInTitle;
    protected boolean showTitleSeparator;
    protected BookTextHolder text;

    public BookTextPage(BookTextHolder title, BookTextHolder text, boolean useMarkdownInTitle, boolean showTitleSeparator, String anchor) {
        super(anchor);
        this.title = title;
        this.text = text;
        this.useMarkdownInTitle = useMarkdownInTitle;
        this.showTitleSeparator = showTitleSeparator;
    }

    public static BookTextPage fromJson(JsonObject json) {
        var title = BookGsonHelper.getAsBookTextHolder(json, "title", BookTextHolder.EMPTY);
        var useMarkdownInTitle = GsonHelper.getAsBoolean(json, "use_markdown_title", false);
        var showTitleSeparator = GsonHelper.getAsBoolean(json, "show_title_separator", true);
        var text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY);
        var anchor = GsonHelper.getAsString(json, "anchor", "");
        return new BookTextPage(title, text, useMarkdownInTitle, showTitleSeparator, anchor);
    }

    public static BookTextPage fromNetwork(FriendlyByteBuf buffer) {
        var title = BookTextHolder.fromNetwork(buffer);
        var useMarkdownInTitle = buffer.readBoolean();
        var showTitleSeparator = buffer.readBoolean();
        var text = BookTextHolder.fromNetwork(buffer);
        var anchor = buffer.readUtf();
        return new BookTextPage(title, text, useMarkdownInTitle, showTitleSeparator, anchor);
    }

    public boolean useMarkdownInTitle() {
        return this.useMarkdownInTitle;
    }

    public boolean showTitleSeparator() {
        return this.showTitleSeparator;
    }

    public BookTextHolder getTitle() {
        return this.title;
    }

    public BookTextHolder getText() {
        return this.text;
    }

    public boolean hasTitle() {
        return !this.title.isEmpty();
    }

    @Override
    public ResourceLocation getType() {
        return ModonomiconConstants.Data.Page.TEXT;
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);

        if (!this.title.hasComponent()) {
            if (this.useMarkdownInTitle) {
                this.title = new RenderedBookTextHolder(this.title, textRenderer.render(this.title.getString()));
            } else {
                this.title = new BookTextHolder(Component.translatable(this.title.getKey())
                        .withStyle(Style.EMPTY
                                .withBold(true)
                                .withColor(this.getParentEntry().getCategory().getBook().getDefaultTitleColor())));
            }
        }
        if (!this.text.hasComponent()) {
            this.text = new RenderedBookTextHolder(this.text, textRenderer.render(this.text.getString()));
        }
    }


    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        this.title.toNetwork(buffer);
        buffer.writeBoolean(this.useMarkdownInTitle);
        buffer.writeBoolean(this.showTitleSeparator);
        this.text.toNetwork(buffer);
        buffer.writeUtf(this.anchor);
    }

    @Override
    public boolean matchesQuery(String query) {
        return this.title.getString().toLowerCase().contains(query)
                || this.text.getString().toLowerCase().contains(query);
    }
}
