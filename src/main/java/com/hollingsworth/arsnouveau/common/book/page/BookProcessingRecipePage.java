/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.page;

import com.hollingsworth.arsnouveau.common.book.BookTextHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public abstract class BookProcessingRecipePage<T extends Recipe<?>> extends BookRecipePage<T> {
    public BookProcessingRecipePage(RecipeType<T> recipeType, BookTextHolder title1, ResourceLocation recipeId1, BookTextHolder title2, ResourceLocation recipeId2, BookTextHolder text, String anchor) {
        super(recipeType, title1, recipeId1, title2, recipeId2, text, anchor);
    }

    @Override
    protected ItemStack getRecipeOutput(T recipe) {
        if (recipe == null) {
            return ItemStack.EMPTY;
        }

        return recipe.getResultItem();
    }
}
