/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.conditions;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.common.book.ModonomiconConstants;
import com.hollingsworth.arsnouveau.common.book.conditions.context.BookConditionContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

public class BookAdvancementCondition extends BookCondition {

    protected ResourceLocation advancementId;

    public BookAdvancementCondition(Component component, ResourceLocation advancementId) {
        super(component);
        this.advancementId = advancementId;
    }

    public static BookAdvancementCondition fromJson(JsonObject json) {
        var advancementId = new ResourceLocation(GsonHelper.getAsString(json, "advancement_id"));

        //default tooltip
        var tooltip = Component.translatable(ModonomiconConstants.I18n.Tooltips.CONDITION_ADVANCEMENT,
                "advancements." + advancementId.toString().replace(":", ".") + ".title");

        if (json.has("tooltip")) {
            tooltip = tooltipFromJson(json);
        }

        return new BookAdvancementCondition(tooltip, advancementId);
    }

    public static BookAdvancementCondition fromNetwork(FriendlyByteBuf buffer) {
        var tooltip = buffer.readBoolean() ? buffer.readComponent() : null;
        var advancementId = buffer.readResourceLocation();
        return new BookAdvancementCondition(tooltip, advancementId);
    }

    @Override
    public ResourceLocation getType() {
        return ModonomiconConstants.Data.Condition.ADVANCEMENT;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.tooltip != null);
        if (this.tooltip != null) {
            buffer.writeComponent(this.tooltip);
        }
        buffer.writeResourceLocation(this.advancementId);
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            var advancement = serverPlayer.getServer().getAdvancements().getAdvancement(this.advancementId);
            return advancement != null && serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone();
        }
        return false;
    }
}
