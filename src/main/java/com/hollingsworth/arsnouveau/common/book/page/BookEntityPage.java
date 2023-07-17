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
import vazkii.patchouli.common.util.EntityUtil;

public class BookEntityPage extends BookPage {

    protected BookTextHolder entityName;
    protected BookTextHolder text;

    //is string, because we allow appending nbt 
    protected String entityId;
    protected float scale = 1.0f;
    protected float offset = 0f;
    protected boolean rotate = true;
    protected float defaultRotation = -45f;


    public BookEntityPage(BookTextHolder entityName, BookTextHolder text, String entityId, float scale, float offset, boolean rotate, float defaultRotation, String anchor) {
        super(anchor);
        this.entityName = entityName;
        this.text = text;
        this.entityId = entityId;
        this.scale = scale;
        this.offset = offset;
        this.rotate = rotate;
        this.defaultRotation = defaultRotation;
    }

    public static BookEntityPage fromJson(JsonObject json) {
        var entityName = BookGsonHelper.getAsBookTextHolder(json, "name", BookTextHolder.EMPTY);
        var text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY);
        var entityId = GsonHelper.getAsString(json, "entity_id");
        var scale = GsonHelper.getAsFloat(json, "scale", 1.0f);
        var offset = GsonHelper.getAsFloat(json, "offset", 0.0f);
        var rotate = GsonHelper.getAsBoolean(json, "rotate", true);
        var defaultRotation = GsonHelper.getAsFloat(json, "default_rotation", -45.0f);

        var anchor = GsonHelper.getAsString(json, "anchor", "");
        return new BookEntityPage(entityName, text, entityId, scale, offset, rotate, defaultRotation, anchor);
    }

    public static BookEntityPage fromNetwork(FriendlyByteBuf buffer) {
        var entityName = BookTextHolder.fromNetwork(buffer);
        var text = BookTextHolder.fromNetwork(buffer);
        var entityId = buffer.readUtf();
        var scale = buffer.readFloat();
        var offset = buffer.readFloat();
        var rotate = buffer.readBoolean();
        var defaultRotation = buffer.readFloat();
        var anchor = buffer.readUtf();
        return new BookEntityPage(entityName, text, entityId, scale, offset, rotate, defaultRotation, anchor);
    }

    public String getEntityId() {
        return this.entityId;
    }

    public float getScale() {
        return this.scale;
    }

    public float getOffset() {
        return this.offset;
    }

    public boolean doesRotate() {
        return this.rotate;
    }

    public float getDefaultRotation() {
        return this.defaultRotation;
    }

    public BookTextHolder getEntityName() {
        return this.entityName;
    }

    public BookTextHolder getText() {
        return this.text;
    }

    @Override
    public ResourceLocation getType() {
        return ModonomiconConstants.Data.Page.ENTITY;
    }

    @Override
    public void build(BookEntry parentEntry, int pageNum) {
        super.build(parentEntry, pageNum);

        if (this.entityName.isEmpty()) {
            //use entity name if we don't have a custom title
            this.entityName = new BookTextHolder(Component.translatable(EntityUtil.getEntityName(this.entityId))
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getBook().getDefaultTitleColor())
                    ));
        }
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);

        if (!this.entityName.hasComponent()) {
            this.entityName = new BookTextHolder(Component.translatable(this.entityName.getKey())
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
        this.entityName.toNetwork(buffer);
        this.text.toNetwork(buffer);
        buffer.writeUtf(this.entityId);
        buffer.writeFloat(this.scale);
        buffer.writeFloat(this.offset);
        buffer.writeBoolean(this.rotate);
        buffer.writeFloat(this.defaultRotation);
        buffer.writeUtf(this.anchor);
    }

    @Override
    public boolean matchesQuery(String query) {
        return this.entityName.getString().toLowerCase().contains(query)
                || this.text.getString().toLowerCase().contains(query);
    }
}
