/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class BookEntryParent {
    protected ResourceLocation entryId;
    protected boolean drawArrow = true;
    protected boolean lineEnabled = true;
    protected boolean lineReversed = false;

    public BookEntryParent(ResourceLocation entry) {
        this.entryId = entry;
    }

    public static BookEntryParent fromJson(JsonObject json) {
        var entry = new ResourceLocation(GsonHelper.getAsString(json, "entry"));
        var parent = new BookEntryParent(entry);
        parent.drawArrow = GsonHelper.getAsBoolean(json, "draw_arrow", parent.drawArrow);
        parent.lineEnabled = GsonHelper.getAsBoolean(json, "line_enabled", parent.lineEnabled);
        parent.lineReversed = GsonHelper.getAsBoolean(json, "line_reversed", parent.lineReversed);
        return parent;
    }

    public static BookEntryParent fromNetwork(FriendlyByteBuf buffer) {
        var entry = buffer.readResourceLocation();
        var parent = new BookEntryParent(entry);
        parent.drawArrow = buffer.readBoolean();
        parent.lineEnabled = buffer.readBoolean();
        parent.lineReversed = buffer.readBoolean();
        return parent;
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.entryId);
        buffer.writeBoolean(this.drawArrow);
        buffer.writeBoolean(this.lineEnabled);
        buffer.writeBoolean(this.lineReversed);
    }

    public BookEntry getEntry() {
        throw new UnsupportedOperationException("BookEntryParent is not resolved yet.");
    }

    public ResourceLocation getEntryId() {
        return this.entryId;
    }

    public boolean drawArrow() {
        return this.drawArrow;
    }

    public boolean isLineEnabled() {
        return this.lineEnabled;
    }

    public boolean isLineReversed() {
        return this.lineReversed;
    }
}
