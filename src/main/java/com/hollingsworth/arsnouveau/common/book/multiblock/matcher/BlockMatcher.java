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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

/**
 * Matches a block, ignoring the BlockState properties.
 */
public class BlockMatcher implements StateMatcher {
    public static final ResourceLocation TYPE = ArsNouveau.loc("block");

    private final BlockState displayState;
    private final Block block;
    private final TriPredicate<BlockGetter, BlockPos, BlockState> predicate;

    protected BlockMatcher(BlockState displayState, Block block) {
        this.displayState = displayState;
        this.block = block;
        this.predicate = (blockGetter, blockPos, blockState) ->
                blockState.getBlock() == block;
    }

    public static BlockMatcher from(Block block) {
        return new BlockMatcher(null, block);
    }

    public static BlockMatcher from(BlockState displayState, Block block) {
        return new BlockMatcher(displayState, block);
    }

    public static BlockMatcher fromJson(JsonObject json) {
        BlockState displayState = null;
        if (json.has("display")) {
            try {
                displayState = BlockStateParser.parseForBlock(Registry.BLOCK, new StringReader(GsonHelper.getAsString(json, "display")), false).blockState();
            } catch (CommandSyntaxException e) {
                throw new IllegalArgumentException("Failed to parse BlockState from json member \"display\" for BlockMatcher.", e);
            }
        }

        try {
            var blockRL = ResourceLocation.tryParse(GsonHelper.getAsString(json, "block"));
            var block = Registry.BLOCK.getOptional(blockRL).orElseThrow();

            return new BlockMatcher(displayState, block);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse Block from json member \"block\" for BlockMatcher", e);
        }
    }

    public static BlockMatcher fromNetwork(FriendlyByteBuf buffer) {
        try {
            BlockState displayState = null;
            if (buffer.readBoolean())
                displayState = BlockStateParser.parseForBlock(Registry.BLOCK, new StringReader(buffer.readUtf()), false).blockState();

            var block = Registry.BLOCK.getOptional(buffer.readResourceLocation()).orElseThrow();
            return new BlockMatcher(displayState, block);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse BlockMatcher from network.", e);
        }
    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }

    @Override
    public BlockState getDisplayedState(long ticks) {
        return this.displayState == null ? this.block.defaultBlockState() : this.displayState;
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
        buffer.writeResourceLocation(Registry.BLOCK.getKey(this.block));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.block, this.displayState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        var that = (BlockMatcher) o;
        return this.block.equals(that.block) && this.displayState.equals(that.displayState);
    }
}
