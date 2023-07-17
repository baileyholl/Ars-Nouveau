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

public class BookImagePage extends BookPage {
    protected BookTextHolder title;
    protected BookTextHolder text;
    protected ResourceLocation[] images;
    protected boolean border;

    public BookImagePage(BookTextHolder title, BookTextHolder text, ResourceLocation[] images, boolean border, String anchor) {
        super(anchor);
        this.title = title;
        this.text = text;
        this.images = images;
        this.border = border;
    }

    public static BookImagePage fromJson(JsonObject json) {
        var title = BookGsonHelper.getAsBookTextHolder(json, "title", BookTextHolder.EMPTY);
        var text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY);

        var imagesArray = GsonHelper.getAsJsonArray(json, "images");
        var images = new ResourceLocation[imagesArray.size()];
        for (int i = 0; i < imagesArray.size(); i++) {
            images[i] = new ResourceLocation(GsonHelper.convertToString(imagesArray.get(i), "images[" + i + "]"));
        }

        var border = GsonHelper.getAsBoolean(json, "border", true);

        var anchor = GsonHelper.getAsString(json, "anchor", "");
        return new BookImagePage(title, text, images, border, anchor);
    }

    public static BookImagePage fromNetwork(FriendlyByteBuf buffer) {
        var title = BookTextHolder.fromNetwork(buffer);
        var text = BookTextHolder.fromNetwork(buffer);

        var count = buffer.readVarInt();
        var images = new ResourceLocation[count];
        for (int i = 0; i < count; i++) {
            images[i] = new ResourceLocation(buffer.readUtf());
        }

        var border = buffer.readBoolean();

        var anchor = buffer.readUtf();
        return new BookImagePage(title, text, images, border, anchor);
    }

    public ResourceLocation[] getImages() {
        return this.images;
    }

    public boolean hasBorder() {
        return this.border;
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
        return ModonomiconConstants.Data.Page.IMAGE;
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);

        if (!this.title.hasComponent()) {
            this.title = new BookTextHolder(Component.translatable(this.title.getKey())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getBook().getDefaultTitleColor())));
        }
        if (!this.text.hasComponent()) {
            this.text = new RenderedBookTextHolder(this.text, textRenderer.render(this.text.getString()));
        }
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        this.title.toNetwork(buffer);
        this.text.toNetwork(buffer);

        buffer.writeVarInt(this.images.length);
        for (var image : this.images) {
            buffer.writeUtf(image.toString());
        }

        buffer.writeBoolean(this.border);
        buffer.writeUtf(this.anchor);
    }

    @Override
    public boolean matchesQuery(String query) {
        return this.title.getString().toLowerCase().contains(query)
                || this.text.getString().toLowerCase().contains(query);
    }
}
