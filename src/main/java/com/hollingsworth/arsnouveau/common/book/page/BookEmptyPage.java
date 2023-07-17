/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.page;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.common.book.ModonomiconConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class BookEmptyPage extends BookPage {

    public BookEmptyPage(String anchor) {
        super(anchor);
    }

    public static BookEmptyPage fromJson(JsonObject json) {
        var anchor = GsonHelper.getAsString(json, "anchor", "");
        return new BookEmptyPage(anchor);
    }

    public static BookEmptyPage fromNetwork(FriendlyByteBuf buffer) {
        var anchor = buffer.readUtf();
        return new BookEmptyPage(anchor);
    }

    @Override
    public ResourceLocation getType() {
        return ModonomiconConstants.Data.Page.EMPTY;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.anchor);
    }

    @Override
    public boolean matchesQuery(String query) {
        return false;
    }
}
