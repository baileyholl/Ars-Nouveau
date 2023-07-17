/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.multiblock.matcher;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

/**
 * Matches any block, including air, and does not display anything.
 */
public class AnyMatcher extends DisplayOnlyMatcher {

    public static final ResourceLocation TYPE = ArsNouveau.loc("any");

    protected AnyMatcher() {
        super(Blocks.AIR.defaultBlockState());
    }

    public static AnyMatcher fromJson(JsonObject json) {
        return Matchers.ANY;
    }

    public static AnyMatcher fromNetwork(FriendlyByteBuf buffer) {
        return Matchers.ANY;
    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
    }
}
