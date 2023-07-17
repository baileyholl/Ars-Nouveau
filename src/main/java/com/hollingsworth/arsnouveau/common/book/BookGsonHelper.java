/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */
package com.hollingsworth.arsnouveau.common.book;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;

public class BookGsonHelper {

    public static BookTextHolder getAsBookTextHolder(JsonObject pJson, String pMemberName, BookTextHolder pFallback) {
        return pJson.has(pMemberName) ? convertToBookTextHolder(pJson.get(pMemberName), pMemberName) : pFallback;
    }

    public static BookTextHolder convertToBookTextHolder(JsonElement pJson, String pMemberName) {
        if (pJson.isJsonPrimitive()) {
            return new BookTextHolder(pJson.getAsString());
        } else {
            return new BookTextHolder(Component.Serializer.fromJson(pJson));
        }
    }
}
