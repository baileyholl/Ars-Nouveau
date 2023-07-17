/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.common.book.conditions.BookCondition;
import com.hollingsworth.arsnouveau.common.book.conditions.BookNoneCondition;
import com.hollingsworth.arsnouveau.common.book.error.BookErrorManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BookCategory {

    protected ResourceLocation id;
    protected Book book;
    protected String name;
    protected BookIcon icon;
    protected int sortNumber;
    protected ResourceLocation background;
    protected int backgroundWidth;
    protected int backgroundHeight;
    protected List<BookCategoryBackgroundParallaxLayer> backgroundParallaxLayers;
    protected ResourceLocation entryTextures;
    protected ConcurrentMap<ResourceLocation, BookEntry> entries;

    protected BookCondition condition;
    protected boolean showCategoryButton;

    public BookCategory(ResourceLocation id, String name, int sortNumber, BookCondition condition, boolean showCategoryButton, BookIcon icon, ResourceLocation background, int backgroundWidth, int backgroundHeight, List<BookCategoryBackgroundParallaxLayer> backgroundParallaxLayers, ResourceLocation entryTextures) {
        this.id = id;
        this.name = name;
        this.sortNumber = sortNumber;
        this.condition = condition;
        this.showCategoryButton = showCategoryButton;
        this.icon = icon;
        this.background = background;
        this.backgroundWidth = backgroundWidth;
        this.backgroundHeight = backgroundHeight;
        this.backgroundParallaxLayers = backgroundParallaxLayers;
        this.entryTextures = entryTextures;
        this.entries = new ConcurrentHashMap<>();
    }

    public static BookCategory fromJson(ResourceLocation id, JsonObject json) {
        var name = GsonHelper.getAsString(json, "name");
        var sortNumber = GsonHelper.getAsInt(json, "sort_number", -1);
        var icon = BookIcon.fromString(new ResourceLocation(GsonHelper.getAsString(json, "icon")));
        var background = new ResourceLocation(GsonHelper.getAsString(json, "background", ModonomiconConstants.Data.Category.DEFAULT_BACKGROUND));
        var backgroundWidth = GsonHelper.getAsInt(json, "background_width", ModonomiconConstants.Data.Category.DEFAULT_BACKGROUND_WIDTH);
        var backgroundHeight = GsonHelper.getAsInt(json, "background_height", ModonomiconConstants.Data.Category.DEFAULT_BACKGROUND_HEIGHT);
        var entryTextures = new ResourceLocation(GsonHelper.getAsString(json, "entry_textures", ModonomiconConstants.Data.Category.DEFAULT_ENTRY_TEXTURES));
        var showCategoryButton = GsonHelper.getAsBoolean(json, "show_category_button", true);

        BookCondition condition = new BookNoneCondition(); //default to unlocked
        if (json.has("condition")) {
            condition = BookCondition.fromJson(json.getAsJsonObject("condition"));
        }

        List<BookCategoryBackgroundParallaxLayer> backgroundParallaxLayers = List.of();
        if (json.has("background_parallax_layers"))
            backgroundParallaxLayers = BookCategoryBackgroundParallaxLayer.fromJson(json.getAsJsonArray("background_parallax_layers"));

        return new BookCategory(id, name, sortNumber, condition, showCategoryButton, icon, background, backgroundWidth, backgroundHeight, backgroundParallaxLayers, entryTextures);
    }

    public static BookCategory fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        var name = buffer.readUtf();
        var sortNumber = buffer.readInt();
        var icon = BookIcon.fromNetwork(buffer);
        var background = buffer.readResourceLocation();
        var backgroundWidth = buffer.readVarInt();
        var backgroundHeight = buffer.readVarInt();
        var backgroundParallaxLayers = buffer.readList(BookCategoryBackgroundParallaxLayer::fromNetwork);
        var entryTextures = buffer.readResourceLocation();
        var condition = BookCondition.fromNetwork(buffer);
        var showCategoryButton = buffer.readBoolean();
        return new BookCategory(id, name, sortNumber, condition, showCategoryButton, icon, background, backgroundWidth, backgroundHeight, backgroundParallaxLayers, entryTextures);
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.name);
        buffer.writeInt(this.sortNumber);
        this.icon.toNetwork(buffer);
        buffer.writeResourceLocation(this.background);
        buffer.writeVarInt(this.backgroundWidth);
        buffer.writeVarInt(this.backgroundHeight);
        buffer.writeCollection(this.backgroundParallaxLayers, (buf, layer) -> layer.toNetwork(buf));
        buffer.writeResourceLocation(this.entryTextures);
        BookCondition.toNetwork(this.condition, buffer);
        buffer.writeBoolean(this.showCategoryButton);
    }

    public boolean showCategoryButton() {
        return this.showCategoryButton;
    }

    /**
     * call after loading the book jsons to finalize.
     */
    public void build(Book book) {
        this.book = book;

        for (var entry : this.entries.values()) {
            BookErrorManager.get().getContextHelper().entryId = entry.getId();
            entry.build(this);
            BookErrorManager.get().getContextHelper().entryId = null;
        }
    }

    /**
     * Called after build() (after loading the book jsons) to render markdown and store any errors
     */
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        for (var entry : this.entries.values()) {
            BookErrorManager.get().getContextHelper().entryId = entry.getId();
            entry.prerenderMarkdown(textRenderer);
            BookErrorManager.get().getContextHelper().entryId = null;
        }
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public Book getBook() {
        return this.book;
    }

    public String getName() {
        return this.name;
    }

    public int getSortNumber() {
        return this.sortNumber;
    }

    public BookIcon getIcon() {
        return this.icon;
    }

    public ResourceLocation getBackground() {
        return this.background;
    }

    public int getBackgroundWidth() {
        return this.backgroundWidth;
    }

    public int getBackgroundHeight() {
        return this.backgroundHeight;
    }

    public List<BookCategoryBackgroundParallaxLayer> getBackgroundParallaxLayers() {
        return this.backgroundParallaxLayers;
    }

    public ResourceLocation getEntryTextures() {
        return this.entryTextures;
    }

    public Map<ResourceLocation, BookEntry> getEntries() {
        return this.entries;
    }

    public void addEntry(BookEntry entry) {
        this.entries.putIfAbsent(entry.id, entry);
    }

    public BookEntry getEntry(ResourceLocation id) {
        return this.entries.get(id);
    }

    public BookCondition getCondition() {
        return this.condition;
    }
}
