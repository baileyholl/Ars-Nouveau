/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.page;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.common.book.BookTextHolder;
import com.hollingsworth.arsnouveau.common.book.ModonomiconConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class BookCraftingRecipePage extends BookRecipePage<Recipe<?>> {
    public BookCraftingRecipePage(BookTextHolder title1, ResourceLocation recipeId1, BookTextHolder title2, ResourceLocation recipeId2, BookTextHolder text, String anchor) {
        super(RecipeType.CRAFTING, title1, recipeId1, title2, recipeId2, text, anchor);
    }

    public static BookCraftingRecipePage fromJson(JsonObject json) {
        var common = BookRecipePage.commonFromJson(json);
        var anchor = GsonHelper.getAsString(json, "anchor", "");
        return new BookCraftingRecipePage(common.title1(), common.recipeId1(), common.title2(), common.recipeId2(), common.text(), anchor);
    }

    public static BookCraftingRecipePage fromNetwork(FriendlyByteBuf buffer) {
        var common = BookRecipePage.commonFromNetwork(buffer);
        var anchor = buffer.readUtf();
        return new BookCraftingRecipePage(common.title1(), common.recipeId1(), common.title2(), common.recipeId2(), common.text(), anchor);
    }

    @Override
    protected ItemStack getRecipeOutput(Recipe<?> recipe) {
        if (recipe == null) {
            return ItemStack.EMPTY;
        }

        return recipe.getResultItem();
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        super.toNetwork(buffer);
        buffer.writeUtf(this.anchor);
    }

    @Override
    public ResourceLocation getType() {
        return ModonomiconConstants.Data.Page.CRAFTING_RECIPE;
    }
}
