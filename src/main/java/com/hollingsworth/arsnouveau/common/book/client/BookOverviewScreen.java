/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Arcana
 *
 * SPDX-License-Identifier: MIT
 */
package com.hollingsworth.arsnouveau.common.book.client;


import com.hollingsworth.arsnouveau.common.book.*;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BookOverviewScreen extends Screen {

    private final Book book;
    private final List<BookCategory> categories;
    private final List<BookCategoryScreen> categoryScreens;

    //TODO: make the frame thickness configurable in the book?
    private final int frameThicknessW = 14;
    private final int frameThicknessH = 14;
    private int currentCategory = 0;

    private boolean hasUnreadEntries;
    private boolean hasUnreadUnlockedEntries;

    public BookOverviewScreen(Book book) {
        super(Component.literal(""));

        //somehow there are render calls before init(), leaving minecraft null
        this.minecraft = Minecraft.getInstance();

        this.book = book;

        this.categories = book.getCategoriesSorted(); //we no longer handle category locking here, is done on init to be able to refresh on unlock
        this.categoryScreens = this.categories.stream().map(c -> new BookCategoryScreen(this, c)).toList();
    }

    public void onDisplay() {
        this.loadBookState();

        this.updateUnreadEntriesState();

        var currentScreen = this.categoryScreens.get(this.currentCategory);
        currentScreen.onDisplay();
    }

    protected void updateUnreadEntriesState() {
        //check if ANY entry is unread
        this.hasUnreadEntries = this.book.getEntries().values().stream().anyMatch(e -> !BookUnlockCapability.isReadFor(this.minecraft.player, e));

        //check if any currently unlocked entry is unread
        this.hasUnreadUnlockedEntries = this.book.getEntries().values().stream().anyMatch(e ->
                BookUnlockCapability.isUnlockedFor(this.minecraft.player, e) &&
                        !BookUnlockCapability.isReadFor(this.minecraft.player, e));
    }

    public BookCategoryScreen getCurrentCategoryScreen() {
        return this.categoryScreens.get(this.currentCategory);
    }

    public int getCurrentCategory() {
        return this.currentCategory;
    }

    public Book getBook() {
        return this.book;
    }

    public ResourceLocation getBookOverviewTexture() {
        return this.book.getBookOverviewTexture();
    }

    /**
     * gets the x coordinate of the inner area of the book frame
     */
    public int getInnerX() {
        return (this.width - this.getFrameWidth()) / 2 + this.frameThicknessW / 2;
    }

    /**
     * gets the y coordinate of the inner area of the book frame
     */
    public int getInnerY() {
        return (this.height - this.getFrameHeight()) / 2 + this.frameThicknessH / 2;
    }

    /**
     * gets the width of the inner area of the book frame
     */
    public int getInnerWidth() {
        return this.getFrameWidth() - this.frameThicknessW;
    }

    /**
     * gets the height of the inner area of the book frame
     */
    public int getInnerHeight() {
        return this.getFrameHeight() - this.frameThicknessH;
    }

    public int getFrameThicknessW() {
        return this.frameThicknessW;
    }

    public int getFrameThicknessH() {
        return this.frameThicknessH;
    }

    public void changeCategory(BookCategory category) {
        int index = this.categories.indexOf(category);
        if (index != -1) {
            this.changeCategory(index);
        } else {
//            Modonomicon.LOGGER.warn("Tried to change to a category ({}) that does not exist in this book ({}).", this.book.getId(), category.getId());
        }
    }

    public void changeCategory(int categoryIndex) {
        var oldIndex = this.currentCategory;
        this.currentCategory = categoryIndex;
        this.onCategoryChanged(oldIndex, this.currentCategory);
    }

    public void onCategoryChanged(int oldIndex, int newIndex) {
        var oldScreen = this.categoryScreens.get(oldIndex);
        oldScreen.onClose();

        var newScreen = this.categoryScreens.get(newIndex);
        newScreen.onDisplay();

        //TODO: SFX for category change?
    }

    /**
     * Gets the outer width of the book frame
     */
    protected int getFrameWidth() {
        //TODO: enable config frame width
        return this.width - 60;
    }

    /**
     * Gets the outer height of the book frame
     */
    protected int getFrameHeight() {
        //TODO: enable config frame height
        return this.height - 20;
    }

    protected void renderFrame(GuiGraphics guiGraphics) {
        int width = this.getFrameWidth();
        int height = this.getFrameHeight();
        int x = (this.width - width) / 2;
        int y = (this.height - height) / 2;

        //draw a resizeable border. Center parts of each side will be stretched
        //the exact border size mostly does not matter because the center is empty anyway, but 50 gives a lot of flexiblity
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        guiGraphics.blitWithBorder(this.book.getFrameTexture(), x, y, 0, 0, width, height,140, 140, 50, 50, 50, 50);

        //now render overlays on top of that border to cover repeating elements
        this.renderFrameOverlay(guiGraphics, this.book.getTopFrameOverlay(), (x + (width / 2)), y);
        this.renderFrameOverlay(guiGraphics, this.book.getBottomFrameOverlay(), (x + (width / 2)), (y + height));
        this.renderFrameOverlay(guiGraphics, this.book.getLeftFrameOverlay(), x, y + (height / 2));
        this.renderFrameOverlay(guiGraphics, this.book.getRightFrameOverlay(), x + width, y + (height / 2));
    }

    protected void renderFrameOverlay(GuiGraphics guiGraphics, BookFrameOverlay overlay, int x, int y) {
        if (overlay.getFrameWidth() > 0 && overlay.getFrameHeight() > 0) {
            guiGraphics.blit(overlay.getTexture(), overlay.getFrameX(x), overlay.getFrameY(y), overlay.getFrameU(), overlay.getFrameV(), overlay.getFrameWidth(), overlay.getFrameHeight());
        }
    }

    protected void onBookCategoryButtonClick(CategoryButton button) {
        this.changeCategory(button.getCategoryIndex());
    }


    protected void onReadAllButtonClick(ReadAllButton button) {
        if (this.hasUnreadUnlockedEntries && !Screen.hasShiftDown()) {
            Networking.sendToServer(new ClickReadAllButtonMessage(this.book.getId(), false));
            this.hasUnreadUnlockedEntries = false;
        } else if (this.hasUnreadEntries && Screen.hasShiftDown()) {
            Networking.sendToServer(new ClickReadAllButtonMessage(this.book.getId(), true));
            this.hasUnreadEntries = false;
        }
    }

    protected boolean canSeeReadAllButton() {
        return this.hasUnreadEntries || this.hasUnreadUnlockedEntries;
    }

    private void loadBookState() {
        var state = BookStateCapability.getBookStateFor(this.minecraft.player, this.book);
        if (state != null) {
            if (state.openCategory != null) {
                var openCategory = this.book.getCategory(state.openCategory);
                if (openCategory != null) {
                    this.currentCategory = this.categories.indexOf(openCategory);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        //ignore return value, because we need our base class to handle dragging and such
        this.getCurrentCategoryScreen().mouseClicked(pMouseX, pMouseY, pButton);
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return this.getCurrentCategoryScreen().mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        this.getCurrentCategoryScreen().zoom(pDelta);
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.disableDepthTest(); //guard against depth test being enabled by other rendering code, that would cause ui elements to vanish

        this.renderBackground(guiGraphics);

        this.getCurrentCategoryScreen().renderBackground(guiGraphics);

        this.getCurrentCategoryScreen().render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        this.renderFrame(guiGraphics);

        this.getCurrentCategoryScreen().renderEntryTooltips(guiGraphics, pMouseX, pMouseY, pPartialTick);

        //do super render last -> it does the widgets including especially the tooltips and we want those to go over the frame
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        this.getCurrentCategoryScreen().onClose();
        Networking.sendToServer(new SaveBookStateMessage(this.book, this.getCurrentCategoryScreen().getCategory().getId()));

        BookGuiManager.get().resetHistory();

        BookGuiManager.get().openOverviewScreen = null;

        super.onClose();
    }

    @Override
    public boolean handleComponentClicked(@Nullable Style pStyle) {
        return super.handleComponentClicked(pStyle);
    }

    public void onSyncBookUnlockCapabilityMessage(SyncBookUnlockCapabilityMessage message) {
        //this leads to re-init of the category buttons after a potential unlock
        this.rebuildWidgets();
        this.updateUnreadEntriesState();
    }

    @Override
    protected void init() {
        super.init();

        BookGuiManager.get().openOverviewScreen = this;

        int buttonXOffset = -11;


        int buttonYOffset = 30 + this.getBook().getCategoryButtonYOffset();

        int buttonX = (this.width - this.getFrameWidth()) / 2 - this.getFrameThicknessW() + buttonXOffset;
        int buttonY = (this.height - this.getFrameHeight()) / 2 - this.getFrameThicknessH() + buttonYOffset;
        //calculate button width so it aligns with the outer edge of the frame
        int buttonWidth = (this.width - this.getFrameWidth()) / 2 + buttonXOffset + 6;
        int buttonHeight = 20;
        int buttonSpacing = 2;

        int buttonCount = 0;
        for (int i = 0, size = this.categories.size(); i < size; i++) {
            if (this.categories.get(i).showCategoryButton() && BookUnlockCapability.isUnlockedFor(this.minecraft.player, this.categories.get(i))) {
                var button = new CategoryButton(this, this.categories.get(i), i,
                        buttonX, buttonY + (buttonHeight + buttonSpacing) * buttonCount, buttonWidth, buttonHeight,
                        (b) -> this.onBookCategoryButtonClick((CategoryButton) b),
                        Tooltip.create(Component.translatable(this.categories.get(i).getName())));

                this.addRenderableWidget(button);
                buttonCount++;
            }
        }

        int readAllButtonX = this.getFrameWidth() + this.getFrameThicknessW() + ReadAllButton.WIDTH / 2 - 3; //(this.width - this.getFrameWidth()); // / 2 - this.getFrameThicknessW() + buttonXOffset;
        int readAllButtonYOffset = 30 + this.getBook().getReadAllButtonYOffset();

        int readAllButtonY = (this.height - this.getFrameHeight()) / 2 + ReadAllButton.HEIGHT / 2 + readAllButtonYOffset;

        var readAllButton = new ReadAllButton(this, readAllButtonX, readAllButtonY,
                () -> this.hasUnreadUnlockedEntries, //if we have unlocked entries that are not read -> blue
                this::canSeeReadAllButton, //display condition -> if we have any unlocked entries -> grey
                (b) -> this.onReadAllButtonClick((ReadAllButton) b));

        this.addRenderableWidget(readAllButton);


        int searchButtonXOffset = 7;
        int searchButtonYOffset = -30 + this.getBook().getSearchButtonYOffset();
        int searchButtonX = this.getFrameWidth() + this.getFrameThicknessW() + ReadAllButton.WIDTH / 2 + searchButtonXOffset;
        int searchButtonY = this.getFrameHeight() + this.getFrameThicknessH() - ReadAllButton.HEIGHT / 2 + searchButtonYOffset;
        int searchButtonWidth = 44; //width in png
        int scissorX = this.getFrameWidth() + this.getFrameThicknessW() * 2 + 2; //this is the render location of our frame so our search button never overlaps

        var searchButton = new SearchButton(this, searchButtonX, searchButtonY,
                scissorX,
                searchButtonWidth, buttonHeight,
                (b) -> this.onSearchButtonClick((SearchButton) b),
                Tooltip.create(Component.translatable(ModonomiconConstants.I18n.Gui.OPEN_SEARCH)));

        this.addRenderableWidget(searchButton);
    }

    protected void onSearchButtonClick(SearchButton button) {
        ForgeHooksClient.pushGuiLayer(this.getMinecraft(), new BookSearchScreen(this));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
