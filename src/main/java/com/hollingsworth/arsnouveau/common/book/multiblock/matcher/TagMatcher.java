/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.multiblock.matcher;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
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
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Matches against the given tag, and optionally checks for the given BlockState properties.
 */
public class TagMatcher implements StateMatcher {
    public static final ResourceLocation TYPE = ArsNouveau.loc("tag");

    private final BlockState displayState;
    private final Supplier<TagKey<Block>> tag;
    private final Supplier<Map<String, String>> props;
    private final TriPredicate<BlockGetter, BlockPos, BlockState> predicate;

    protected TagMatcher( Supplier<TagKey<Block>> tag,  Supplier<Map<String, String>> props) {
        this(null, tag, props);
    }

    protected TagMatcher(BlockState displayState,  Supplier<TagKey<Block>> tag, Supplier<Map<String, String>> props) {
        this.displayState = displayState;
        this.tag = tag;
        this.props = props;
        this.predicate = (blockGetter, blockPos, blockState) -> blockState.is(this.tag.get()) && checkProps(blockState, this.props);
    }

    public static TagMatcher fromJson(JsonObject json) {
        BlockState displayState = null;
        if (json.has("display")) {
            try {
                displayState = BlockStateParser.parseForBlock(Registry.BLOCK, new StringReader(GsonHelper.getAsString(json, "display")), false).blockState();
            } catch (CommandSyntaxException e) {
                throw new IllegalArgumentException("Failed to parse BlockState from json member \"display\" for TagStateMatcher.", e);
            }
        }


            //testing=true enables tag parsing
            //last param = allowNBT
            var tagString = GsonHelper.getAsString(json, "tag");
            if(!tagString.startsWith("#")) {
                tagString = "#" + tagString;
            }


            String finalTagString = tagString;
            Supplier<TagKey<Block>> tagSupplier = Suppliers.memoize(() -> {
                try {
                    var parserResult = BlockStateParser.parseForTesting(Registry.BLOCK, new StringReader(finalTagString), true).right().orElseThrow();
                   return parserResult.tag().unwrap().left().orElseThrow();
                } catch (CommandSyntaxException e) {
                    throw new IllegalArgumentException("Failed to parse Tag and BlockState properties from json member \"tag\" for TagMatcher.", e);
                }
            });

            Supplier<Map<String, String>> propsSupplier = Suppliers.memoize(() -> {
                try {
                    var parserResult = BlockStateParser.parseForTesting(Registry.BLOCK, new StringReader(finalTagString), true).right().orElseThrow();
                    return parserResult.vagueProperties();
                } catch (CommandSyntaxException e) {
                    throw new IllegalArgumentException("Failed to parse Tag and BlockState properties from json member \"tag\" for TagMatcher.", e);
                }
            });

            return new TagMatcher(displayState, tagSupplier, propsSupplier);

    }

    public static TagMatcher fromNetwork(FriendlyByteBuf buffer) {
        try {
            BlockState displayState = null;
            if (buffer.readBoolean()) {
                displayState = BlockStateParser.parseForBlock(Registry.BLOCK, new StringReader(buffer.readUtf()), false).blockState();
            }

            var tag = TagKey.create(Registry.BLOCK_REGISTRY, buffer.readResourceLocation());
            var props = buffer.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readUtf);

            return new TagMatcher(displayState, () -> tag, () -> props);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse TagMatcher from network.", e);
        }
    }

    public static boolean checkProps(BlockState state, Supplier<Map<String, String>> props) {
        for (Entry<String, String> entry : props.get().entrySet()) {
            Property<?> prop = state.getBlock().getStateDefinition().getProperty(entry.getKey());
            if (prop == null) {
                return false;
            }

            Comparable<?> value = prop.getValue(entry.getValue()).orElse(null);
            if (value == null) {
                return false;
            }

            if (!state.getValue(prop).equals(value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }

    @Override
    public BlockState getDisplayedState(long ticks) {
        if (this.displayState != null) {
            return this.displayState;
        } else {
            var all = ImmutableList.copyOf(Registry.BLOCK.getTagOrEmpty(this.tag.get()));
            if (all.isEmpty()) {
                return Blocks.BEDROCK.defaultBlockState(); // show something impossible
            } else {
                int idx = (int) ((ticks / 20) % all.size());
                return all.get(idx).value().defaultBlockState();
            }
        }
    }

    @Override
    public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
        return this.predicate;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.displayState != null);
        if (this.displayState != null) {
            buffer.writeUtf(BlockStateParser.serialize(this.displayState));
        }
        buffer.writeResourceLocation(this.tag.get().location());
        buffer.writeMap(this.props.get(), FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.tag, this.props, this.displayState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        var that = (TagMatcher) o;
        return this.tag.equals(that.tag) && this.props.equals(that.props) && this.displayState.equals(that.displayState);
    }
}
