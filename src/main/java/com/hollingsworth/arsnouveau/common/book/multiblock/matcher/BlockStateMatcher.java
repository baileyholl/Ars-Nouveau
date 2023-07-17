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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

/**
 * Matches a BlockState, respecting all BlockState properties.
 */
public class BlockStateMatcher implements StateMatcher {
    public static final ResourceLocation TYPE = ArsNouveau.loc("blockstate");

    private final BlockState displayState;
    private final BlockState blockState;
    private final TriPredicate<BlockGetter, BlockPos, BlockState> predicate;

    protected BlockStateMatcher(BlockState displayState, BlockState blockState) {
        this.displayState = displayState;
        this.blockState = blockState;
        this.predicate = (blockGetter, blockPos, state) -> state == blockState;
    }

    public static BlockStateMatcher from(BlockState blockState) {
        return new BlockStateMatcher(null, blockState);
    }

    public static BlockStateMatcher from(BlockState displayState, BlockState blockState) {
        return new BlockStateMatcher(displayState, blockState);
    }

    public static BlockStateMatcher fromJson(JsonObject json) {
        BlockState displayState = null;
        if (json.has("display")) {
            try {
                displayState = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), new StringReader(GsonHelper.getAsString(json, "display")), false).blockState();
            } catch (CommandSyntaxException e) {
                throw new IllegalArgumentException("Failed to parse BlockState from json member \"display\" for BlockStateMatcher.", e);
            }
        }

        try {
            var blockState = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), new StringReader(GsonHelper.getAsString(json, "block")), false).blockState();
            return new BlockStateMatcher(displayState, blockState);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse BlockState from json member \"block\" for BlockStateMatcher.", e);
        }

    }

    public static BlockStateMatcher fromNetwork(FriendlyByteBuf buffer) {
        try {
            BlockState displayState = null;
            if (buffer.readBoolean())
                displayState = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), new StringReader(buffer.readUtf()), false).blockState();

            var blockState = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), new StringReader(buffer.readUtf()), false).blockState();

            return new BlockStateMatcher(displayState, blockState);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse BlockStateMatcher from network.", e);
        }
    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }

    @Override
    public BlockState getDisplayedState(long ticks) {
        return this.displayState == null ? this.blockState : this.displayState;
    }

    @Override
    public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
        return this.predicate;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.displayState != null);
        if (this.displayState != null)
            buffer.writeUtf(BlockStateParser.serialize(this.displayState));
        buffer.writeUtf(BlockStateParser.serialize(this.blockState));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.blockState, this.displayState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        var that = (BlockStateMatcher) o;
        return this.blockState.equals(that.blockState) && this.displayState.equals(that.displayState);
    }
}
