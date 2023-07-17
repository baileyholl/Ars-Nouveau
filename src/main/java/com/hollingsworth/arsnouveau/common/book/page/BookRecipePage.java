/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.page;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.common.book.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.server.ServerLifecycleHooks;

public abstract class BookRecipePage<T extends Recipe<?>> extends BookPage {

    protected final RecipeType<? extends T> recipeType;

    protected BookTextHolder title1;
    protected ResourceLocation recipeId1;
    protected T recipe1;

    protected BookTextHolder title2;
    protected ResourceLocation recipeId2;
    protected T recipe2;

    protected BookTextHolder text;

    public BookRecipePage(RecipeType<? extends T> recipeType, BookTextHolder title1, ResourceLocation recipeId1, BookTextHolder title2, ResourceLocation recipeId2, BookTextHolder text, String anchor) {
        super(anchor);
        this.recipeType = recipeType;
        this.title1 = title1;
        this.recipeId1 = recipeId1;
        this.title2 = title2;
        this.recipeId2 = recipeId2;
        this.text = text;
    }

    public static DataHolder commonFromJson(JsonObject json) {
        var title1 = BookGsonHelper.getAsBookTextHolder(json, "title1", BookTextHolder.EMPTY);
        ResourceLocation recipeId1 = json.has("recipe_id_1") ? ResourceLocation.tryParse(GsonHelper.getAsString(json, "recipe_id_1")) : null;

        var title2 = BookGsonHelper.getAsBookTextHolder(json, "title2", BookTextHolder.EMPTY);
        ResourceLocation recipeId2 = json.has("recipe_id_2") ? ResourceLocation.tryParse(GsonHelper.getAsString(json, "recipe_id_2")) : null;

        var text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY);

        return new DataHolder(title1, recipeId1, title2, recipeId2, text);
    }

    public static DataHolder commonFromNetwork(FriendlyByteBuf buffer) {
        var title1 = BookTextHolder.fromNetwork(buffer);
        var recipeId1 = buffer.readBoolean() ? buffer.readResourceLocation() : null;

        var title2 = BookTextHolder.fromNetwork(buffer);
        var recipeId2 = buffer.readBoolean() ? buffer.readResourceLocation() : null;

        var text = BookTextHolder.fromNetwork(buffer);

        return new DataHolder(title1, recipeId1, title2, recipeId2, text);
    }

    public RecipeType<? extends T> getRecipeType() {
        return this.recipeType;
    }

    public BookTextHolder getTitle1() {
        return this.title1;
    }

    public ResourceLocation getRecipeId1() {
        return this.recipeId1;
    }

    public T getRecipe1() {
        return this.recipe1;
    }

    public BookTextHolder getTitle2() {
        return this.title2;
    }

    public ResourceLocation getRecipeId2() {
        return this.recipeId2;
    }

    public T getRecipe2() {
        return this.recipe2;
    }

    public BookTextHolder getText() {
        return this.text;
    }

    protected T loadRecipe(BookEntry entry, ResourceLocation recipeId) {
        if (recipeId == null) {
            return null;
        }

        var tempRecipe = this.getRecipe(recipeId);


        if (tempRecipe == null) {
//            Modonomicon.LOGGER.warn("Recipe {} (of type {}) not found.", recipeId, Registry.RECIPE_TYPE.getKey(this.recipeType));
        }

        return tempRecipe;
    }

    protected abstract ItemStack getRecipeOutput(T recipe);

    private T getRecipe(ResourceLocation id) {

        var server = ServerLifecycleHooks.getCurrentServer();
        RecipeManager manager = server != null ? server.getRecipeManager() : Minecraft.getInstance().level.getRecipeManager();

        return (T) manager.byKey(id).filter(recipe -> recipe.getType() == this.recipeType).orElse(null);
    }

    @Override
    public void build(BookEntry parentEntry, int pageNum) {
        super.build(parentEntry, pageNum);

        this.recipe1 = this.loadRecipe(parentEntry, this.recipeId1);
        this.recipe2 = this.loadRecipe(parentEntry, this.recipeId2);

        if (this.recipe1 == null && this.recipe2 != null) {
            this.recipe1 = this.recipe2;
            this.recipe2 = null;
        }

        if (this.title1.isEmpty()) {
            //use recipe title if we don't have a custom one
            this.title1 = new BookTextHolder(((MutableComponent) this.getRecipeOutput(this.recipe1).getHoverName())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getBook().getDefaultTitleColor())
                    ));
        }

        if (this.recipe2 != null && this.title2.isEmpty()) {
            //use recipe title if we don't have a custom one
            this.title2 = new BookTextHolder(((MutableComponent) this.getRecipeOutput(this.recipe2).getHoverName())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getBook().getDefaultTitleColor())
                    ));
        }

        if (this.title1.equals(this.title2)) {
            this.title2 = BookTextHolder.EMPTY;
        }
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);

        if (!this.title1.hasComponent()) {
            this.title1 = new BookTextHolder(Component.translatable(this.title1.getKey())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getCategory().getBook().getDefaultTitleColor())));
        }
        if (!this.title2.hasComponent()) {
            this.title2 = new BookTextHolder(Component.translatable(this.title2.getKey())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getCategory().getBook().getDefaultTitleColor())));
        }

        if (!this.text.hasComponent()) {
            this.text = new RenderedBookTextHolder(this.text, textRenderer.render(this.text.getString()));
        }
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        this.title1.toNetwork(buffer);
        buffer.writeBoolean(this.recipeId1 != null);
        if (this.recipeId1 != null) {
            buffer.writeResourceLocation(this.recipeId1);
        }

        this.title2.toNetwork(buffer);
        buffer.writeBoolean(this.recipeId2 != null);
        if (this.recipeId2 != null) {
            buffer.writeResourceLocation(this.recipeId2);
        }

        this.text.toNetwork(buffer);
    }

    @Override
    public boolean matchesQuery(String query) {
        return this.title1.getString().toLowerCase().contains(query)
                || this.title2.getString().toLowerCase().contains(query)
                || this.text.getString().toLowerCase().contains(query);
    }

    public record DataHolder(BookTextHolder title1, ResourceLocation recipeId1, BookTextHolder title2,
                             ResourceLocation recipeId2, BookTextHolder text) {
    }
}
