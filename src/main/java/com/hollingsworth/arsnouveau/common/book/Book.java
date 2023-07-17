/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book;

import com.google.common.base.Suppliers;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.common.book.error.BookErrorManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class Book {
    protected ResourceLocation id;
    protected String name;
    protected String tooltip;
    protected String creativeTab;

    protected ResourceLocation model;
    protected ResourceLocation bookOverviewTexture;
    protected ResourceLocation frameTexture;
    protected BookFrameOverlay topFrameOverlay;
    protected BookFrameOverlay bottomFrameOverlay;
    protected BookFrameOverlay leftFrameOverlay;
    protected BookFrameOverlay rightFrameOverlay;
    protected ResourceLocation bookContentTexture;

    protected ResourceLocation craftingTexture;
    protected ResourceLocation turnPageSound;
    protected ConcurrentMap<ResourceLocation, BookCategory> categories;
    protected ConcurrentMap<ResourceLocation, BookEntry> entries;
    protected ConcurrentMap<ResourceLocation, BookCommand> commands;


    protected int defaultTitleColor;
    protected float categoryButtonIconScale;
    protected boolean autoAddReadConditions;
    protected boolean generateBookItem;
    @Nullable
    protected ResourceLocation customBookItem;

    /**
     * When rendering book text holders, add this offset to the x position (basically, create a left margin).
     * Will be automatically subtracted from the width to avoid overflow.
     */
    protected int bookTextOffsetX;

    /**
     * When rendering book text holders, add this offset to the y position (basically, create a top margin).
     */
    protected int bookTextOffsetY;

    /**
     * When rendering book text holders, add this offset to the width (allows to create a right margin)
     * To make the line end move to the left (as it would for a margin setting in eg css), use a negative value.
     */
    protected int bookTextOffsetWidth;

    protected int categoryButtonXOffset;
    protected int categoryButtonYOffset;
    protected int searchButtonXOffset;
    protected int searchButtonYOffset;
    protected int readAllButtonYOffset;

    protected Supplier<ItemStack> bookItem = Suppliers.memoize(() -> {
        if (this.customBookItem != null) {
            var parsed = ItemStackUtil.parseItemStackString(this.customBookItem.toString());
            return ItemStackUtil.loadFromParsed(parsed);
        }
        var stack = new ItemStack(Items.AXOLOTL_BUCKET);
        var tag = new CompoundTag();
        tag.putString("book_id", this.id.toString());
        stack.setTag(tag);
        return stack;
    });

    public Book(ResourceLocation id, String name, String tooltip, ResourceLocation model, boolean generateBookItem,
                ResourceLocation customBookItem, String creativeTab, ResourceLocation bookOverviewTexture, ResourceLocation frameTexture,
                BookFrameOverlay topFrameOverlay, BookFrameOverlay bottomFrameOverlay, BookFrameOverlay leftFrameOverlay, BookFrameOverlay rightFrameOverlay,
                ResourceLocation bookContentTexture, ResourceLocation craftingTexture, ResourceLocation turnPageSound,
                int defaultTitleColor, float categoryButtonIconScale, boolean autoAddReadConditions, int bookTextOffsetX, int bookTextOffsetY, int bookTextOffsetWidth,
                int categoryButtonXOffset, int categoryButtonYOffset, int searchButtonXOffset, int searchButtonYOffset, int readAllButtonYOffset
    ) {
        this.id = id;
        this.name = name;
        this.tooltip = tooltip;
        this.model = model;
        this.generateBookItem = generateBookItem;
        this.customBookItem = customBookItem;
        this.creativeTab = creativeTab;
        this.bookOverviewTexture = bookOverviewTexture;
        this.frameTexture = frameTexture;
        this.topFrameOverlay = topFrameOverlay;
        this.bottomFrameOverlay = bottomFrameOverlay;
        this.leftFrameOverlay = leftFrameOverlay;
        this.rightFrameOverlay = rightFrameOverlay;
        this.bookContentTexture = bookContentTexture;
        this.craftingTexture = craftingTexture;
        this.turnPageSound = turnPageSound;
        this.defaultTitleColor = defaultTitleColor;
        this.categoryButtonIconScale = categoryButtonIconScale;
        this.autoAddReadConditions = autoAddReadConditions;
        this.categories = new ConcurrentHashMap<>();
        this.entries = new ConcurrentHashMap<>();
        this.commands = new ConcurrentHashMap<>();
        this.bookTextOffsetX = bookTextOffsetX;
        this.bookTextOffsetY = bookTextOffsetY;
        this.bookTextOffsetWidth = bookTextOffsetWidth;

        this.categoryButtonXOffset = categoryButtonXOffset;
        this.categoryButtonYOffset = categoryButtonYOffset;
        this.searchButtonXOffset = searchButtonXOffset;
        this.searchButtonYOffset = searchButtonYOffset;
        this.readAllButtonYOffset = readAllButtonYOffset;
    }

    public static Book fromJson(ResourceLocation id, JsonObject json) {
        var name = GsonHelper.getAsString(json, "name");
        var tooltip = GsonHelper.getAsString(json, "tooltip", "");
        var model = new ResourceLocation(GsonHelper.getAsString(json, "model", ModonomiconConstants.Data.Book.DEFAULT_MODEL));
        var generateBookItem = GsonHelper.getAsBoolean(json, "generate_book_item", true);
        var customBookItem = json.has("custom_book_item") ?
                new ResourceLocation(GsonHelper.getAsString(json, "custom_book_item")) :
                null;
        var creativeTab = GsonHelper.getAsString(json, "creative_tab", "misc");
        var bookOverviewTexture = new ResourceLocation(GsonHelper.getAsString(json, "book_overview_texture", ModonomiconConstants.Data.Book.DEFAULT_OVERVIEW_TEXTURE));
        var frameTexture = new ResourceLocation(GsonHelper.getAsString(json, "frame_texture", ModonomiconConstants.Data.Book.DEFAULT_FRAME_TEXTURE));

        var topFrameOverlay = json.has("top_frame_overlay") ?
                BookFrameOverlay.fromJson(json.get("top_frame_overlay").getAsJsonObject()) :
                ModonomiconConstants.Data.Book.DEFAULT_TOP_FRAME_OVERLAY;

        var bottomFrameOverlay = json.has("bottom_frame_overlay") ?
                BookFrameOverlay.fromJson(json.get("bottom_frame_overlay").getAsJsonObject()) :
                ModonomiconConstants.Data.Book.DEFAULT_BOTTOM_FRAME_OVERLAY;

        var leftFrameOverlay = json.has("left_frame_overlay") ?
                BookFrameOverlay.fromJson(json.get("left_frame_overlay").getAsJsonObject()) :
                ModonomiconConstants.Data.Book.DEFAULT_LEFT_FRAME_OVERLAY;

        var rightFrameOverlay = json.has("right_frame_overlay") ?
                BookFrameOverlay.fromJson(json.get("right_frame_overlay").getAsJsonObject()) :
                ModonomiconConstants.Data.Book.DEFAULT_RIGHT_FRAME_OVERLAY;

        var bookContentTexture = new ResourceLocation(GsonHelper.getAsString(json, "book_content_texture", ModonomiconConstants.Data.Book.DEFAULT_CONTENT_TEXTURE));
        var craftingTexture = new ResourceLocation(GsonHelper.getAsString(json, "crafting_texture", ModonomiconConstants.Data.Book.DEFAULT_CRAFTING_TEXTURE));
        var turnPageSound = new ResourceLocation(GsonHelper.getAsString(json, "turn_page_sound", ModonomiconConstants.Data.Book.DEFAULT_PAGE_TURN_SOUND));
        var defaultTitleColor = GsonHelper.getAsInt(json, "default_title_color", 0x00000);
        var categoryButtonIconScale = GsonHelper.getAsFloat(json, "category_button_icon_scale", 1.0f);
        var autoAddReadConditions = GsonHelper.getAsBoolean(json, "auto_add_read_conditions", false);

        var bookTextOffsetX = GsonHelper.getAsInt(json, "book_text_offset_x", 0);
        var bookTextOffsetY = GsonHelper.getAsInt(json, "book_text_offset_y", 0);
        var bookTextOffsetWidth = GsonHelper.getAsInt(json, "book_text_offset_width", 0);

        var categoryButtonXOffset = GsonHelper.getAsInt(json, "category_button_x_offset", 0);
        var categoryButtonYOffset = GsonHelper.getAsInt(json, "category_button_y_offset", 0);
        var searchButtonXOffset = GsonHelper.getAsInt(json, "search_button_x_offset", 0);
        var searchButtonYOffset = GsonHelper.getAsInt(json, "search_button_y_offset", 0);
        var readAllButtonYOffset = GsonHelper.getAsInt(json, "read_all_button_y_offset", 0);

        return new Book(id, name, tooltip, model, generateBookItem, customBookItem, creativeTab, bookOverviewTexture,
                frameTexture, topFrameOverlay, bottomFrameOverlay, leftFrameOverlay, rightFrameOverlay,
                bookContentTexture, craftingTexture, turnPageSound, defaultTitleColor, categoryButtonIconScale, autoAddReadConditions, bookTextOffsetX, bookTextOffsetY, bookTextOffsetWidth, categoryButtonXOffset, categoryButtonYOffset,
                searchButtonXOffset, searchButtonYOffset, readAllButtonYOffset);
    }


    @SuppressWarnings("deprecation")
    public static Book fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        var name = buffer.readUtf();
        var tooltip = buffer.readUtf();
        var model = buffer.readResourceLocation();
        var generateBookItem = buffer.readBoolean();
        var customBookItem = buffer.readBoolean() ? buffer.readResourceLocation() : null;
        var creativeTab = buffer.readUtf();
        var bookOverviewTexture = buffer.readResourceLocation();

        var frameTexture = buffer.readResourceLocation();

        var topFrameOverlay = BookFrameOverlay.fromNetwork(buffer);
        var bottomFrameOverlay = BookFrameOverlay.fromNetwork(buffer);
        var leftFrameOverlay = BookFrameOverlay.fromNetwork(buffer);
        var rightFrameOverlay = BookFrameOverlay.fromNetwork(buffer);

        var bookContentTexture = buffer.readResourceLocation();
        var craftingTexture = buffer.readResourceLocation();
        var turnPageSound = buffer.readResourceLocation();
        var defaultTitleColor = buffer.readInt();
        var categoryButtonIconScale = buffer.readFloat();
        var autoAddReadConditions = buffer.readBoolean();
        var bookTextOffsetX = (int) buffer.readShort();
        var bookTextOffsetY = (int) buffer.readShort();
        var bookTextOffsetWidth = (int) buffer.readShort();

        var categoryButtonXOffset = (int) buffer.readShort();
        var categoryButtonYOffset = (int) buffer.readShort();
        var searchButtonXOffset = (int) buffer.readShort();
        var searchButtonYOffset = (int) buffer.readShort();
        var readAllButtonYOffset = (int) buffer.readShort();

        return new Book(id, name, tooltip, model, generateBookItem, customBookItem, creativeTab, bookOverviewTexture,
                frameTexture, topFrameOverlay, bottomFrameOverlay, leftFrameOverlay, rightFrameOverlay,
                bookContentTexture, craftingTexture, turnPageSound, defaultTitleColor, categoryButtonIconScale, autoAddReadConditions, bookTextOffsetX, bookTextOffsetY, bookTextOffsetWidth, categoryButtonXOffset, categoryButtonYOffset,
                searchButtonXOffset, searchButtonYOffset, readAllButtonYOffset);
    }

    /**
     * call after loading the book jsons to finalize.
     */
    public void build() {
        //first "backlink" all our entries directly into the book
        for (var category : this.categories.values()) {
            for (var entry : category.getEntries().values()) {
                this.addEntry(entry);
            }
        }

        //then build categories, which will in turn build entries (which need the above backlinks to resolve parents)
        for (var category : this.categories.values()) {
            BookErrorManager.get().getContextHelper().categoryId = category.getId();
            category.build(this);
            BookErrorManager.get().getContextHelper().categoryId = null;
        }

        for (var command : this.commands.values()) {
            command.build(this);
        }
    }

    /**
     * Called after build() (after loading the book jsons) to render markdown and store any errors
     */
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        for (var category : this.categories.values()) {
            BookErrorManager.get().getContextHelper().categoryId = category.getId();
            category.prerenderMarkdown(textRenderer);
            BookErrorManager.get().getContextHelper().categoryId = null;
        }
    }

    @SuppressWarnings("deprecation")
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.name);
        buffer.writeUtf(this.tooltip);
        buffer.writeResourceLocation(this.model);
        buffer.writeBoolean(this.generateBookItem);
        buffer.writeBoolean(this.customBookItem != null);
        if (this.customBookItem != null) {
            buffer.writeResourceLocation(this.customBookItem);
        }
        buffer.writeUtf(this.creativeTab);
        buffer.writeResourceLocation(this.bookOverviewTexture);
        buffer.writeResourceLocation(this.frameTexture);

        this.topFrameOverlay.toNetwork(buffer);
        this.bottomFrameOverlay.toNetwork(buffer);
        this.leftFrameOverlay.toNetwork(buffer);
        this.rightFrameOverlay.toNetwork(buffer);

        buffer.writeResourceLocation(this.bookContentTexture);
        buffer.writeResourceLocation(this.craftingTexture);
        buffer.writeResourceLocation(this.turnPageSound);
        buffer.writeInt(this.defaultTitleColor);
        buffer.writeFloat(this.categoryButtonIconScale);
        buffer.writeBoolean(this.autoAddReadConditions);

        buffer.writeShort(this.bookTextOffsetX);
        buffer.writeShort(this.bookTextOffsetY);
        buffer.writeShort(this.bookTextOffsetWidth);

        buffer.writeShort(this.categoryButtonXOffset);
        buffer.writeShort(this.categoryButtonYOffset);
        buffer.writeShort(this.searchButtonXOffset);
        buffer.writeShort(this.searchButtonYOffset);
        buffer.writeShort(this.readAllButtonYOffset);
    }

    public ItemStack getBookItem() {
        return this.bookItem.get();
    }

    public boolean autoAddReadConditions() {
        return this.autoAddReadConditions;
    }

    public ResourceLocation getTurnPageSound() {
        return this.turnPageSound;
    }

    public int getDefaultTitleColor() {
        return this.defaultTitleColor;
    }

    public float getCategoryButtonIconScale() {
        return this.categoryButtonIconScale;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public void addCategory(BookCategory category) {
        this.categories.putIfAbsent(category.id, category);
    }

    public BookCategory getCategory(ResourceLocation id) {
        return this.categories.get(id);
    }

    public Map<ResourceLocation, BookCategory> getCategories() {
        return this.categories;
    }

    public List<BookCategory> getCategoriesSorted() {
        return this.categories.values().stream().sorted(Comparator.comparingInt(BookCategory::getSortNumber)).toList();
    }

    public void addEntry(BookEntry entry) {
        this.entries.putIfAbsent(entry.id, entry);
    }

    public BookEntry getEntry(ResourceLocation id) {
        return this.entries.get(id);
    }

    public Map<ResourceLocation, BookEntry> getEntries() {
        return this.entries;
    }

    public void addCommand(BookCommand command) {
        this.commands.putIfAbsent(command.id, command);
    }

    public ConcurrentMap<ResourceLocation, BookCommand> getCommands() {
        return this.commands;
    }

    public BookCommand getCommand(ResourceLocation id) {
        return this.commands.get(id);
    }

    public String getName() {
        return this.name;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public String getCreativeTab() {
        return this.creativeTab;
    }

    public ResourceLocation getBookOverviewTexture() {
        return this.bookOverviewTexture;
    }

    public ResourceLocation getFrameTexture() {
        return this.frameTexture;
    }

    public BookFrameOverlay getTopFrameOverlay() {
        return this.topFrameOverlay;
    }

    public BookFrameOverlay getBottomFrameOverlay() {
        return this.bottomFrameOverlay;
    }

    public BookFrameOverlay getLeftFrameOverlay() {
        return this.leftFrameOverlay;
    }

    public BookFrameOverlay getRightFrameOverlay() {
        return this.rightFrameOverlay;
    }

    @Nullable
    public ResourceLocation getCustomBookItem() {
        return this.customBookItem;
    }

    public ResourceLocation getCraftingTexture() {
        return this.craftingTexture;
    }

    public ResourceLocation getBookContentTexture() {
        return this.bookContentTexture;
    }

    public ResourceLocation getModel() {
        return this.model;
    }

    public boolean generateBookItem() {
        return this.generateBookItem;
    }

    public int getBookTextOffsetX() {
        return this.bookTextOffsetX;
    }

    public int getBookTextOffsetY() {
        return this.bookTextOffsetY;
    }

    public int getBookTextOffsetWidth() {
        return this.bookTextOffsetWidth;
    }

    public int getCategoryButtonXOffset() {
        return this.categoryButtonXOffset;
    }

    public int getCategoryButtonYOffset() {
        return this.categoryButtonYOffset;
    }

    public int getSearchButtonXOffset() {
        return this.searchButtonXOffset;
    }

    public int getSearchButtonYOffset() {
        return this.searchButtonYOffset;
    }

    public int getReadAllButtonYOffset() {
        return this.readAllButtonYOffset;
    }
}
