/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */
package com.hollingsworth.arsnouveau.common.book.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A composite element of a rendering block state, and a predicate to validate if the real state in the world is valid
 * or not. Used as the core building block for multiblocks.
 */
public interface StateMatcher {

    /**
     * The state matcher type Id for serialization.
     */
    ResourceLocation getType();

    /**
     * Gets the state displayed by this state matcher for rendering the multiblock page type and the in-world preview.
     *
     * @param ticks World ticks, to allow cycling the state shown.
     */
    BlockState getDisplayedState(long ticks);

    /**
     * Returns a predicate that validates whether the given state is acceptable. This should check the passed in
     * blockstate instead of requerying it from the world, for both performance and correctness reasons -- the state may
     * be rotated for multiblock matching.
     */
    TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate();

    /**
     * Serializes the state matcher to the given buffer.
     */
    void toNetwork(FriendlyByteBuf buffer);

}
