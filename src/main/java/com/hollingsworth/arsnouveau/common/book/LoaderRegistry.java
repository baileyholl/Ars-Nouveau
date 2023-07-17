/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book;

import com.hollingsworth.arsnouveau.common.book.conditions.*;
import com.hollingsworth.arsnouveau.common.book.multiblock.DenseMultiblock;
import com.hollingsworth.arsnouveau.common.book.multiblock.SparseMultiblock;
import com.hollingsworth.arsnouveau.common.book.multiblock.StateMatcher;
import com.hollingsworth.arsnouveau.common.book.multiblock.TriPredicate;
import com.hollingsworth.arsnouveau.common.book.multiblock.matcher.*;
import com.hollingsworth.arsnouveau.common.book.page.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

import static com.hollingsworth.arsnouveau.common.book.ModonomiconConstants.Data.Condition;
import static com.hollingsworth.arsnouveau.common.book.ModonomiconConstants.Data.Page;
public class LoaderRegistry {

    private static final Map<ResourceLocation, JsonLoader<? extends BookPage>> pageJsonLoaders = new HashMap<>();
    private static final Map<ResourceLocation, NetworkLoader<? extends BookPage>> pageNetworkLoaders = new HashMap<>();

    private static final Map<ResourceLocation, JsonLoader<? extends BookCondition>> conditionJsonLoaders = new HashMap<>();

    private static final Map<ResourceLocation, NetworkLoader<? extends BookCondition>> conditionNetworkLoaders = new HashMap<>();
    private static final Map<ResourceLocation, JsonLoader<? extends Multiblock>> multiblockJsonLoaders = new HashMap<>();
    private static final Map<ResourceLocation, NetworkLoader<? extends Multiblock>> multiblockNetworkLoaders = new HashMap<>();

    private static final Map<ResourceLocation, JsonLoader<? extends StateMatcher>> stateMatcherJsonLoaders = new HashMap<>();
    private static final Map<ResourceLocation, NetworkLoader<? extends StateMatcher>> stateMatcherNetworkLoaders = new HashMap<>();


    private static final Map<ResourceLocation, TriPredicate<BlockGetter, BlockPos, BlockState>> predicates = new HashMap<>();

    /**
     * Call from common setup
     */
    public static void registerLoaders() {
        registerDefaultPageLoaders();
        registerDefaultConditionLoaders();
        registerDefaultPredicates();
        registerDefaultStateMatcherLoaders();
        registerDefaultMultiblockLoaders();
    }

    private static void registerDefaultPageLoaders() {
        registerPageLoader(Page.TEXT, BookTextPage::fromJson, BookTextPage::fromNetwork);
        registerPageLoader(Page.MULTIBLOCK, BookMultiblockPage::fromJson, BookMultiblockPage::fromNetwork);
        registerPageLoader(Page.CRAFTING_RECIPE, BookCraftingRecipePage::fromJson, BookCraftingRecipePage::fromNetwork);
        registerPageLoader(Page.SMELTING_RECIPE, BookSmeltingRecipePage::fromJson, BookSmeltingRecipePage::fromNetwork);
        registerPageLoader(Page.SMOKING_RECIPE, BookSmokingRecipePage::fromJson, BookSmokingRecipePage::fromNetwork);
        registerPageLoader(Page.CAMPFIRE_COOKING_RECIPE, BookCampfireCookingRecipePage::fromJson, BookCampfireCookingRecipePage::fromNetwork);
        registerPageLoader(Page.BLASTING_RECIPE, BookBlastingRecipePage::fromJson, BookBlastingRecipePage::fromNetwork);
        registerPageLoader(Page.STONECUTTING_RECIPE, BookStonecuttingRecipePage::fromJson, BookStonecuttingRecipePage::fromNetwork);
        registerPageLoader(Page.SMITHING_RECIPE, BookSmithingRecipePage::fromJson, BookSmithingRecipePage::fromNetwork);
        registerPageLoader(Page.SPOTLIGHT, BookSpotlightPage::fromJson, BookSpotlightPage::fromNetwork);
        registerPageLoader(Page.EMPTY, BookEmptyPage::fromJson, BookEmptyPage::fromNetwork);
        registerPageLoader(Page.ENTITY, BookEntityPage::fromJson, BookEntityPage::fromNetwork);
        registerPageLoader(Page.IMAGE, BookImagePage::fromJson, BookImagePage::fromNetwork);
    }

    private static void registerDefaultConditionLoaders() {
        registerConditionLoader(Condition.NONE, BookNoneCondition::fromJson, BookNoneCondition::fromNetwork);
        registerConditionLoader(Condition.ADVANCEMENT, BookAdvancementCondition::fromJson, BookAdvancementCondition::fromNetwork);
        registerConditionLoader(Condition.ENTRY_UNLOCKED, BookEntryUnlockedCondition::fromJson, BookEntryUnlockedCondition::fromNetwork);
        registerConditionLoader(Condition.ENTRY_READ, BookEntryReadCondition::fromJson, BookEntryReadCondition::fromNetwork);
        registerConditionLoader(Condition.OR, BookOrCondition::fromJson, BookOrCondition::fromNetwork);
        registerConditionLoader(Condition.AND, BookAndCondition::fromJson, BookAndCondition::fromNetwork);
        registerConditionLoader(Condition.TRUE, BookTrueCondition::fromJson, BookTrueCondition::fromNetwork);
        registerConditionLoader(Condition.FALSE, BookFalseCondition::fromJson, BookFalseCondition::fromNetwork);
        registerConditionLoader(Condition.MOD_LOADED, BookModLoadedCondition::fromJson, BookModLoadedCondition::fromNetwork);
    }

    private static void registerDefaultMultiblockLoaders() {
        registerMultiblockLoader(DenseMultiblock.TYPE, DenseMultiblock::fromJson, DenseMultiblock::fromNetwork);
        registerMultiblockLoader(SparseMultiblock.TYPE, SparseMultiblock::fromJson, SparseMultiblock::fromNetwork);
    }

    private static void registerDefaultStateMatcherLoaders() {
        registerStateMatcherLoader(AnyMatcher.TYPE, AnyMatcher::fromJson, AnyMatcher::fromNetwork);
        registerStateMatcherLoader(BlockMatcher.TYPE, BlockMatcher::fromJson, BlockMatcher::fromNetwork);
        registerStateMatcherLoader(BlockStateMatcher.TYPE, BlockStateMatcher::fromJson, BlockStateMatcher::fromNetwork);
        registerStateMatcherLoader(BlockStatePropertyMatcher.TYPE, BlockStatePropertyMatcher::fromJson, BlockStatePropertyMatcher::fromNetwork);
        registerStateMatcherLoader(DisplayOnlyMatcher.TYPE, DisplayOnlyMatcher::fromJson, DisplayOnlyMatcher::fromNetwork);
        registerStateMatcherLoader(PredicateMatcher.TYPE, PredicateMatcher::fromJson, PredicateMatcher::fromNetwork);
        registerStateMatcherLoader(TagMatcher.TYPE, TagMatcher::fromJson, TagMatcher::fromNetwork);
    }

    private static void registerDefaultPredicates() {
        registerPredicate(Matchers.AIR.getPredicateId(), (getter, pos, state) -> state.isAir());
    }


    /**
     * Call from common setup
     */
    public static void registerPageLoader(ResourceLocation id, JsonLoader<? extends BookPage> jsonLoader,
                                          NetworkLoader<? extends BookPage> networkLoader) {
        pageJsonLoaders.put(id, jsonLoader);
        pageNetworkLoaders.put(id, networkLoader);
    }

    /**
     * Call from common setup
     */
    public static void registerConditionLoader(ResourceLocation id, JsonLoader<? extends BookCondition> jsonLoader,
                                          NetworkLoader<? extends BookCondition> networkLoader) {
        conditionJsonLoaders.put(id, jsonLoader);
        conditionNetworkLoaders.put(id, networkLoader);
    }

    /**
     * Call from common setup
     */
    public static void registerMultiblockLoader(ResourceLocation id, JsonLoader<? extends Multiblock> jsonLoader,
                                                NetworkLoader<? extends Multiblock> networkLoader) {
        multiblockJsonLoaders.put(id, jsonLoader);
        multiblockNetworkLoaders.put(id, networkLoader);
    }

    /**
     * Call from common setup
     */
    public static void registerStateMatcherLoader(ResourceLocation id, JsonLoader<? extends StateMatcher> jsonLoader,
                                                  NetworkLoader<? extends StateMatcher> networkLoader) {
        stateMatcherJsonLoaders.put(id, jsonLoader);
        stateMatcherNetworkLoaders.put(id, networkLoader);
    }

    /**
     * Call from common setup, so predicates are available on both sides.
     */
    public static void registerPredicate(ResourceLocation id, TriPredicate<BlockGetter, BlockPos, BlockState> predicate) {
        predicates.put(id, predicate);
    }

    public static JsonLoader<? extends StateMatcher> getStateMatcherJsonLoader(ResourceLocation id) {
        var loader = stateMatcherJsonLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No json loader registered for state matcher type " + id);
        }
        return loader;
    }

    public static NetworkLoader<? extends StateMatcher> getStateMatcherNetworkLoader(ResourceLocation id) {
        var loader = stateMatcherNetworkLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No network loader registered for state matcher type " + id);
        }
        return loader;
    }

    public static TriPredicate<BlockGetter, BlockPos, BlockState> getPredicate(ResourceLocation id) {
        var predicate = predicates.get(id);
        if (predicate == null) {
            throw new IllegalArgumentException("No predicated registered for id " + id);
        }
        return predicate;
    }

    public static JsonLoader<? extends BookPage> getPageJsonLoader(ResourceLocation id) {
        var loader = pageJsonLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No json loader registered for page type " + id);
        }
        return loader;
    }

    public static NetworkLoader<? extends BookPage> getPageNetworkLoader(ResourceLocation id) {
        var loader = pageNetworkLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No network loader registered for page type " + id);
        }
        return loader;
    }

    public static JsonLoader<? extends BookCondition> getConditionJsonLoader(ResourceLocation id) {
        var loader = conditionJsonLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No json loader registered for condition type " + id);
        }
        return loader;
    }

    public static NetworkLoader<? extends BookCondition> getConditionNetworkLoader(ResourceLocation id) {
        var loader = conditionNetworkLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No network loader registered for condition type " + id);
        }
        return loader;
    }

    public static JsonLoader<? extends Multiblock> getMultiblockJsonLoader(ResourceLocation id) {
        var loader = multiblockJsonLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No json loader registered for multiblock type " + id);
        }
        return loader;
    }

    public static NetworkLoader<? extends Multiblock> getMultiblockNetworkLoader(ResourceLocation id) {
        var loader = multiblockNetworkLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No network loader registered for multiblock type " + id);
        }
        return loader;
    }
}
