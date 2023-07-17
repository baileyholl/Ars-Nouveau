/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.multiblock.matcher;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.book.multiblock.StateMatcher;
import com.hollingsworth.arsnouveau.common.book.multiblock.TriPredicate;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;


/**
 * Matches any block, including air, but displays a block in the multiblock preview.
 */
public class DisplayOnlyMatcher implements StateMatcher {
    public static final ResourceLocation TYPE = ArsNouveau.loc("display");

    private final BlockState displayState;
    private final TriPredicate<BlockGetter, BlockPos, BlockState> predicate;

    protected DisplayOnlyMatcher(BlockState displayState) {
        this.displayState = displayState;
        this.predicate = (blockGetter, blockPos, blockState) -> true;
    }

    public static DisplayOnlyMatcher fromJson(JsonObject json) {
        try {
            var displayState = BlockStateParser.parseForBlock(Registry.BLOCK, new StringReader(GsonHelper.getAsString(json, "display")), false).blockState();
            return new DisplayOnlyMatcher(displayState);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse BlockState from json member \"display\" for DisplayOnlyMatcher.", e);
        }
    }

    public static DisplayOnlyMatcher fromNetwork(FriendlyByteBuf buffer) {
        try {
            var displayState = BlockStateParser.parseForBlock(Registry.BLOCK, new StringReader(buffer.readUtf()), false).blockState();
            return new DisplayOnlyMatcher(displayState);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse DisplayOnlyMatcher from network.", e);
        }
    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }

    @Override
    public BlockState getDisplayedState(long ticks) {
        return this.displayState;
    }

    @Override
    public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
        return this.predicate;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(BlockStateParser.serialize(this.displayState));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.displayState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        var that = (DisplayOnlyMatcher) o;
        return this.displayState.equals(that.displayState);
    }
}
