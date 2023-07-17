/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.multiblock.matcher;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.book.LoaderRegistry;
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
import net.minecraftforge.common.util.Lazy;

import java.util.Objects;

/**
 * Matches against the predicate with the given id. Predicates are stored in {@link
 * LoaderRegistry}
 */
public class PredicateMatcher implements StateMatcher {

    public static final ResourceLocation TYPE = ArsNouveau.loc("predicate");

    private final BlockState displayState;
    private final ResourceLocation predicateId;
    private final Lazy<TriPredicate<BlockGetter, BlockPos, BlockState>> predicate;

    protected PredicateMatcher(BlockState displayState, ResourceLocation predicateId) {
        this.displayState = displayState;
        this.predicateId = predicateId;
        this.predicate = Lazy.of(() -> LoaderRegistry.getPredicate(this.predicateId));
    }

    public ResourceLocation getPredicateId() {
        return this.predicateId;
    }

    public static PredicateMatcher fromJson(JsonObject json) {
        try {
            var displayState = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), new StringReader(GsonHelper.getAsString(json, "display")), false).blockState();
            var predicateId = new ResourceLocation(GsonHelper.getAsString(json, "predicate"));
            return new PredicateMatcher(displayState, predicateId);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse BlockState from json member \"display\" for PredicateMatcher.", e);
        }
    }

    public static PredicateMatcher fromNetwork(FriendlyByteBuf buffer) {
        try {
            var displayState = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), new StringReader(buffer.readUtf()), false).blockState();
            var predicateId = buffer.readResourceLocation();
            return new PredicateMatcher(displayState, predicateId);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse PredicateMatcher from network.", e);
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
        return this.predicate.get();
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(BlockStateParser.serialize(this.displayState));
        buffer.writeUtf(this.predicateId.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.predicateId, this.displayState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        var that = (PredicateMatcher) o;
        return this.predicateId.equals(that.predicateId) && this.displayState.equals(that.displayState);
    }
}
