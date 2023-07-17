/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.common.book.conditions.BookCondition;
import com.hollingsworth.arsnouveau.common.book.conditions.BookNoneCondition;
import com.hollingsworth.arsnouveau.common.book.error.BookErrorManager;
import com.hollingsworth.arsnouveau.common.book.page.BookPage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.List;

public class BookEntry {
    protected ResourceLocation id;
    protected ResourceLocation categoryId;
    protected BookCategory category;
    protected Book book;
    protected List<BookEntryParent> parents;
    protected String name;
    protected String description;
    protected BookIcon icon;
    protected int x;
    protected int y;

    //The first two rows in "entry_texures.png" are reserved for the entry icons.
    //the entry background is selected by querying the texture at entryBackgroundUIndex * 26 (= Y Axis / Up-Down), entryBackgroundUIndex * 26 (= X Axis / Left-Right)

    /**
     * = Y Axis / Up-Down
     */
    protected int entryBackgroundUIndex;
    /**
     * = X Axis / Left-Right
     */
    protected int entryBackgroundVIndex;

    protected boolean hideWhileLocked;
    protected List<BookPage> pages;
    protected BookCondition condition;

    /**
     * if this is not null pages will be ignored and the entry instead will open a new category
     */
    protected ResourceLocation categoryToOpenId;
    protected BookCategory categoryToOpen;

    /**
     * if this is not null, the command will be run when the entry is first read.
     */
    protected ResourceLocation commandToRunOnFirstReadId;
    protected BookCommand commandToRunOnFirstRead;

    public BookEntry(ResourceLocation id, ResourceLocation categoryId, String name, String description, BookIcon icon, int x, int y, int entryBackgroundUIndex, int entryBackgroundVIndex, boolean hideWhileLocked, BookCondition condition, List<BookEntryParent> parents, List<BookPage> pages, ResourceLocation categoryToOpenId, ResourceLocation commandToRunOnFirstReadId) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.x = x;
        this.y = y;
        this.entryBackgroundUIndex = entryBackgroundUIndex;
        this.entryBackgroundVIndex = entryBackgroundVIndex;
        this.parents = parents;
        this.pages = pages;
        this.condition = condition;
        this.hideWhileLocked = hideWhileLocked;

        this.categoryToOpenId = categoryToOpenId;
        this.commandToRunOnFirstReadId = commandToRunOnFirstReadId;
    }

    public static BookEntry fromJson(ResourceLocation id, JsonObject json) {
        var categoryId = new ResourceLocation(GsonHelper.getAsString(json, "category"));
        var name = GsonHelper.getAsString(json, "name");
        var description = GsonHelper.getAsString(json, "description", "");
        var icon = BookIcon.fromString(new ResourceLocation(GsonHelper.getAsString(json, "icon")));
        var x = GsonHelper.getAsInt(json, "x");
        var y = GsonHelper.getAsInt(json, "y");
        var entryBackgroundUIndex = GsonHelper.getAsInt(json, "background_u_index", 0);
        var entryBackgroundVIndex = GsonHelper.getAsInt(json, "background_v_index", 0);
        var hideWhileLocked = GsonHelper.getAsBoolean(json, "hide_while_locked", false);

        var parentEntries = new ArrayList<BookEntryParent>();

        if (json.has("parents")) {
            JsonArray parents = GsonHelper.getAsJsonArray(json, "parents");
            for (var parent : parents) {
                parentEntries.add(BookEntryParent.fromJson(parent.getAsJsonObject()));
            }
        }

        var pages = new ArrayList<BookPage>();
        if (json.has("pages")) {
            var jsonPages = GsonHelper.getAsJsonArray(json, "pages");
            for (var pageElem : jsonPages) {
                BookErrorManager.get().setContext("Page Index: {}", pages.size());
                var pageJson = GsonHelper.convertToJsonObject(pageElem, "page");
                var type = new ResourceLocation(GsonHelper.getAsString(pageJson, "type"));
                var loader = LoaderRegistry.getPageJsonLoader(type);
                var page = loader.fromJson(pageJson);
                pages.add(page);
            }
        }

        BookCondition condition = new BookNoneCondition(); //default to unlocked
        if (json.has("condition")) {
            condition = BookCondition.fromJson(json.getAsJsonObject("condition"));
        }

        ResourceLocation categoryToOpen = null;
        if (json.has("category_to_open")) {
            categoryToOpen = new ResourceLocation(GsonHelper.getAsString(json, "category_to_open"));
        }

        ResourceLocation commandToRunOnFirstRead = null;
        if (json.has("command_to_run_on_first_read")) {
            commandToRunOnFirstRead = new ResourceLocation(GsonHelper.getAsString(json, "command_to_run_on_first_read"));
        }

        return new BookEntry(id, categoryId, name, description, icon, x, y, entryBackgroundUIndex,
                entryBackgroundVIndex, hideWhileLocked, condition, parentEntries, pages, categoryToOpen, commandToRunOnFirstRead);
    }

    public static BookEntry fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        var categoryId = buffer.readResourceLocation();
        var name = buffer.readUtf();
        var description = buffer.readUtf();
        var icon = BookIcon.fromNetwork(buffer);
        var x = buffer.readVarInt();
        var y = buffer.readVarInt();
        var entryBackgroundUIndex = buffer.readVarInt();
        var entryBackgroundVIndex = buffer.readVarInt();
        var hideWhileLocked = buffer.readBoolean();

        var parentEntries = new ArrayList<BookEntryParent>();

        var parentCount = buffer.readVarInt();
        for (var i = 0; i < parentCount; i++) {
            parentEntries.add(BookEntryParent.fromNetwork(buffer));
        }

        var pages = new ArrayList<BookPage>();
        var pageCount = buffer.readVarInt();
        for (var i = 0; i < pageCount; i++) {
            var type = buffer.readResourceLocation();
            var loader = LoaderRegistry.getPageNetworkLoader(type);
            var page = loader.fromNetwork(buffer);
            pages.add(page);
        }

        var condition = BookCondition.fromNetwork(buffer);

        ResourceLocation categoryToOpen = buffer.readNullable(FriendlyByteBuf::readResourceLocation);
        ResourceLocation commandToRunOnFirstRead = buffer.readNullable(FriendlyByteBuf::readResourceLocation);

        return new BookEntry(id, categoryId, name, description, icon, x, y, entryBackgroundUIndex,
                entryBackgroundVIndex, hideWhileLocked, condition, parentEntries, pages, categoryToOpen, commandToRunOnFirstRead);
    }

    public BookCategory getCategoryToOpen() {
        return this.categoryToOpen;
    }

    public BookCommand getCommandToRunOnFirstRead() {
        return this.commandToRunOnFirstRead;
    }

    /**
     * call after loading the book jsons to finalize.
     */
    public void build(BookCategory category) {
        this.category = category;
        this.book = category.getBook();

        //resolve parents
        var newParents = new ArrayList<BookEntryParent>();
        for (var parent : this.getParents()) {
            var parentEntry = this.book.getEntry(parent.getEntryId());
            newParents.add(new ResolvedBookEntryParent(parentEntry));
        }
        this.parents = newParents;

        if (this.categoryToOpenId != null) {
            this.categoryToOpen = this.book.getCategory(this.categoryToOpenId);

            if(this.categoryToOpen == null){
                BookErrorManager.get().error("Category to open \"" + this.categoryToOpenId + "\" does not exist in this book. Set to null.");
                this.categoryToOpenId = null;
            }
        }

        if (this.commandToRunOnFirstReadId != null) {
            this.commandToRunOnFirstRead = this.book.getCommand(this.commandToRunOnFirstReadId);

            if(this.commandToRunOnFirstRead == null){
                BookErrorManager.get().error("Command to run on first read \"" + this.commandToRunOnFirstReadId + "\" does not exist in this book. Set to null.");
                this.commandToRunOnFirstReadId = null;
            }
        }

        //build pages
        int pageNum = 0;
        for (var page : this.pages) {
            BookErrorManager.get().getContextHelper().pageNumber = pageNum;
            page.build(this, pageNum);
            BookErrorManager.get().getContextHelper().pageNumber = -1;
            pageNum++;
        }
    }

    /**
     * Called after build() (after loading the book jsons) to render markdown and store any errors
     */
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        for (var page : this.pages) {
            BookErrorManager.get().getContextHelper().pageNumber = page.getPageNumber();
            page.prerenderMarkdown(textRenderer);
            BookErrorManager.get().getContextHelper().pageNumber = -1;
        }
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.categoryId);
        buffer.writeUtf(this.name);
        buffer.writeUtf(this.description);
        this.icon.toNetwork(buffer);
        buffer.writeVarInt(this.x);
        buffer.writeVarInt(this.y);
        buffer.writeVarInt(this.entryBackgroundUIndex);
        buffer.writeVarInt(this.entryBackgroundVIndex);
        buffer.writeBoolean(this.hideWhileLocked);

        buffer.writeVarInt(this.parents.size());
        for (var parent : this.parents) {
            parent.toNetwork(buffer);
        }

        buffer.writeVarInt(this.pages.size());
        for (var page : this.pages) {
            buffer.writeResourceLocation(page.getType());
            page.toNetwork(buffer);
        }

        BookCondition.toNetwork(this.condition, buffer);

        buffer.writeNullable(this.categoryToOpenId, FriendlyByteBuf::writeResourceLocation);
        buffer.writeNullable(this.commandToRunOnFirstReadId, FriendlyByteBuf::writeResourceLocation);
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }

    public boolean hideWhileLocked() {
        return this.hideWhileLocked;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public ResourceLocation getCategoryId() {
        return this.categoryId;
    }

    public BookCategory getCategory() {
        return this.category;
    }

    public List<BookEntryParent> getParents() {
        return this.parents;
    }

    public String getName() {
        return this.name;
    }

    public BookIcon getIcon() {
        return this.icon;
    }

    public String getDescription() {
        return this.description;
    }

    public List<BookPage> getPages() {
        return this.pages;
    }

    public Book getBook() {
        return this.book;
    }

    public int getPageNumberForAnchor(String anchor) {
        var pages = this.getPages();
        for (int i = 0; i < pages.size(); i++) {
            var page = pages.get(i);
            if (anchor.equals(page.getAnchor())) {
                return i;
            }
        }

        return -1;
    }

    public BookCondition getCondition() {
        return this.condition;
    }

    public void setCondition(BookCondition condition) {
        this.condition = condition;
    }

    /**
     * = Y Axis / Up-Down
     */
    public int getEntryBackgroundUIndex() {
        return this.entryBackgroundUIndex;
    }

    /**
     * = X Axis / Left-Right
     */
    public int getEntryBackgroundVIndex() {
        return this.entryBackgroundVIndex;
    }

    /**
     * Returns true if this entry should show up in search for the given query.
     */
    public boolean matchesQuery(String query) {
        if (this.getName().toLowerCase().contains(query)) {
            return true;
        }

        for (var page : this.getPages()) {
            if (page.matchesQuery(query)) {
                return true;
            }
        }

        return false;
    }
}
