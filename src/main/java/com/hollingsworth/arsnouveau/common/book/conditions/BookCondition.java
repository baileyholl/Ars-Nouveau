/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.conditions;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.common.book.LoaderRegistry;
import com.hollingsworth.arsnouveau.common.book.conditions.context.BookConditionContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public abstract class BookCondition {

    protected Component tooltip;

    public BookCondition(Component tooltip) {
        this.tooltip = tooltip;
    }

    public static MutableComponent tooltipFromJson(JsonObject json) {
        if (json.has("tooltip")) {
            var tooltipElement = json.get("tooltip");
            if (tooltipElement.isJsonPrimitive())
                return Component.translatable(tooltipElement.getAsString());

            return Component.Serializer.fromJson(tooltipElement);
        }
        return null;
    }

    public static BookCondition fromJson(JsonObject json) {
        var type = new ResourceLocation(GsonHelper.getAsString(json, "type"));
        var loader = LoaderRegistry.getConditionJsonLoader(type);
        return loader.fromJson(json);
    }

    public static BookCondition fromNetwork(FriendlyByteBuf buf) {
        var type = buf.readResourceLocation();
        var loader = LoaderRegistry.getConditionNetworkLoader(type);
        return loader.fromNetwork(buf);
    }

    public static void toNetwork(BookCondition condition, FriendlyByteBuf buf) {
        buf.writeResourceLocation(condition.getType());
        condition.toNetwork(buf);
    }

    public abstract ResourceLocation getType();

    /**
     * Always write type before calling, ideally call {@link #toNetwork(BookCondition, FriendlyByteBuf)}
     */
    public abstract void toNetwork(FriendlyByteBuf buffer);

    public abstract boolean test(BookConditionContext context, Player player);

    public List<Component> getTooltip(BookConditionContext context) {
        return this.tooltip != null ? List.of(this.tooltip) : List.of();
    }
}
