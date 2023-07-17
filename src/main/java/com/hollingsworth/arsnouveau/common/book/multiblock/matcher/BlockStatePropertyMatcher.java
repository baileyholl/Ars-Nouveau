/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.multiblock.matcher;

import com.google.common.base.Suppliers;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Matches a BlockState, respecting only the provided BlockState properties.
 */
public class BlockStatePropertyMatcher implements StateMatcher {
    public static final ResourceLocation TYPE = ArsNouveau.loc("blockstateproperty");

    private final BlockState displayState;
    private final Block block;

    private final Supplier<Map<String, String>> props;
    private final TriPredicate<BlockGetter, BlockPos, BlockState> predicate;

    protected BlockStatePropertyMatcher(BlockState displayState, Block block, Supplier<Map<String, String>> props) {
        this.displayState = displayState;
        this.block = block;
        this.props = props;
        this.predicate = (blockGetter, blockPos, blockState) -> blockState.getBlock() == block && TagMatcher.checkProps(blockState, this.props);
    }

    public static BlockStatePropertyMatcher fromJson(JsonObject json) {
        BlockState displayState = null;
        if (json.has("display")) {
            try {
                displayState = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), new StringReader(GsonHelper.getAsString(json, "display")), false).blockState();
            } catch (CommandSyntaxException e) {
                throw new IllegalArgumentException("Failed to parse BlockState from json member \"display\" for BlockStatePropertyMatcher.", e);
            }
        }

        try {
            var result = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), new StringReader(GsonHelper.getAsString(json, "block")), false);

            var props = convertProps(result.properties());
            return new BlockStatePropertyMatcher(displayState, result.blockState().getBlock(), Suppliers.memoize(() -> props));
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse BlockState from json member \"block\" for BlockStatePropertyMatcher.", e);
        }

    }

    public static BlockStatePropertyMatcher fromNetwork(FriendlyByteBuf buffer) {
        try {
            BlockState displayState = null;
            if (buffer.readBoolean())
                displayState = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), new StringReader(buffer.readUtf()), false).blockState();

            var block = ForgeRegistries.BLOCKS.getValue(buffer.readResourceLocation());
            var props = buffer.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readUtf);

            return new BlockStatePropertyMatcher(displayState, block, Suppliers.memoize(() -> props));
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse BlockStatePropertyMatcher from network.", e);
        }
    }

    private static Map<String, String> convertProps(Map<Property<?>, Comparable<?>> props) {
        Map<String, String> newProps = new HashMap<>();
        for (var entry : props.entrySet()) {

            appendProperty(newProps, entry.getKey(), entry.getValue());
        }
        return newProps;
    }

    private static <T extends Comparable<T>> void appendProperty(Map<String, String> properties, Property<T> pProperty, Comparable<?> pValue) {
        properties.put(pProperty.getName(), pProperty.getName((T) pValue));
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
        buffer.writeResourceLocation(ForgeRegistries.BLOCKS.getKey(this.block));
        buffer.writeMap(this.props.get(), FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.block, this.displayState, this.props);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        var that = (BlockStatePropertyMatcher) o;
        return this.block.equals(that.block) && this.props.equals(that.props) && this.displayState.equals(that.displayState);
    }
}
