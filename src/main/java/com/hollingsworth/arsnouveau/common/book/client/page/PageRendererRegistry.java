/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client.page;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;
import com.klikli_dev.modonomicon.book.page.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;

public class PageRendererRegistry {
    static final Set<Item> ITEMS_NOT_TO_RENDER = new HashSet<>();
    static final Set<Fluid> FLUIDS_NOT_TO_RENDER = new HashSet<>();
    private static final Map<ResourceLocation, PageRendererFactory> pageRenderers = new HashMap<>();

    /**
     * Call from client setup
     */
    public static void registerPageRenderers() {
        registerDefaultPageRenderers();
    }

    private static void registerDefaultPageRenderers() {
        registerPageRenderer(Page.TEXT, p -> new BookTextPageRenderer((BookTextPage) p));
        registerPageRenderer(Page.MULTIBLOCK, p -> new BookMultiblockPageRenderer((BookMultiblockPage) p));
        registerPageRenderer(Page.CRAFTING_RECIPE, p -> new BookCraftingRecipePageRenderer((BookCraftingRecipePage) p));
        registerPageRenderer(Page.SMELTING_RECIPE, p -> new BookProcessingRecipePageRenderer<>((BookSmeltingRecipePage) p) {
        });
        registerPageRenderer(Page.SMOKING_RECIPE, p -> new BookProcessingRecipePageRenderer<>((BookSmokingRecipePage) p) {
        });
        registerPageRenderer(Page.CAMPFIRE_COOKING_RECIPE, p -> new BookProcessingRecipePageRenderer<>((BookCampfireCookingRecipePage) p) {
        });
        registerPageRenderer(Page.BLASTING_RECIPE, p -> new BookProcessingRecipePageRenderer<>((BookBlastingRecipePage) p) {
        });
        registerPageRenderer(Page.STONECUTTING_RECIPE, p -> new BookProcessingRecipePageRenderer<>((BookStonecuttingRecipePage) p) {
        });
        registerPageRenderer(Page.SMITHING_RECIPE, p -> new BookSmithingRecipePageRenderer((BookSmithingRecipePage) p) {
        });
        registerPageRenderer(Page.SPOTLIGHT, p -> new BookSpotlightPageRenderer((BookSpotlightPage) p) {
        });
        registerPageRenderer(Page.EMPTY, p -> new BookEmptyPageRenderer((BookEmptyPage) p) {
        });
        registerPageRenderer(Page.ENTITY, p -> new BookEntityPageRenderer((BookEntityPage) p) {
        });
        registerPageRenderer(Page.IMAGE, p -> new BookImagePageRenderer((BookImagePage) p) {
        });
    }

    /**
     * Call from client setup
     */
    public static void registerPageRenderer(ResourceLocation id, PageRendererFactory factory) {
        pageRenderers.put(id, factory);
    }

    public static PageRendererFactory getPageRenderer(ResourceLocation id) {
        var renderer = pageRenderers.get(id);
        if (renderer == null) {
            throw new IllegalArgumentException("No page renderer registered for page type " + id);
        }
        return renderer;
    }

    /**
     * Any items registered here, will not be rendered by the renderIngredient / renderItemStacks / renderItemStack methods.
     * Can be called at any time.
     */
    public static void registerItemStackNotToRender(ItemStack stack) {
        ITEMS_NOT_TO_RENDER.add(stack.getItem());
    }

    /**
     * Any fluids registered here, will not be rendered by the renderFluidStacks / renderFluidStack methods.
     * Can be called at any time.
     */
    public static void registerFluidStackNotToRender(FluidStack stack) {
        FLUIDS_NOT_TO_RENDER.add(stack.getFluid());
    }

    /**
     * Returns false, if the given stack should not be rendered in the book, e.g. in recipes.
     */
    public static boolean isRenderable(ItemStack stack) {
        return !ITEMS_NOT_TO_RENDER.contains(stack.getItem());
    }

    /**
     * Returns false, if the given stack should not be rendered in the book, e.g. in recipes.
     */
    public static boolean isRenderable(FluidStack stack) {
        return !FLUIDS_NOT_TO_RENDER.contains(stack.getFluid());
    }

    /**
     * Returns only those stacks in the list, that should be rendered in the book, e.g. in recipes.
     */
    public static List<ItemStack> filterRenderableItemStacks(Collection<ItemStack> stacks) {
        return stacks.stream().filter(stack -> !ITEMS_NOT_TO_RENDER.contains(stack.getItem())).toList();
    }

    /**
     * Returns only those stacks in the list, that should be rendered in the book, e.g. in recipes.
     */
    public static List<FluidStack> filterRenderableFluidStacks(Collection<FluidStack> stacks) {
        return stacks.stream().filter(stack -> !FLUIDS_NOT_TO_RENDER.contains(stack.getFluid())).toList();
    }
}
