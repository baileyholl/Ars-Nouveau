/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.*;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.capability.BookStateCapability;
import com.klikli_dev.modonomicon.capability.BookUnlockCapability;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.gui.book.button.ArrowButton;
import com.klikli_dev.modonomicon.client.gui.book.button.BackButton;
import com.klikli_dev.modonomicon.client.gui.book.button.ExitButton;
import com.klikli_dev.modonomicon.client.gui.book.markdown.ItemLinkRenderer;
import com.klikli_dev.modonomicon.client.render.FluidRenderHelper;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry;
import com.klikli_dev.modonomicon.config.ClientConfig;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.integration.ModonomiconJeiIntegration;
import com.klikli_dev.modonomicon.integration.ModonomiconPatchouliIntegration;
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.network.messages.ClickCommandLinkMessage;
import com.klikli_dev.modonomicon.network.messages.SaveEntryStateMessage;
import com.klikli_dev.modonomicon.util.ItemStackUtil;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BookContentScreen extends Screen implements BookScreenWithButtons {

    public static final int BOOK_BACKGROUND_WIDTH = 272;
    public static final int BOOK_BACKGROUND_HEIGHT = 178;

    public static final int TOP_PADDING = 15;
    public static final int LEFT_PAGE_X = 12;
    public static final int RIGHT_PAGE_X = 141;
    public static final int PAGE_WIDTH = 124;
    public static final int PAGE_HEIGHT = 128; //TODO: Adjust to what is real
    public static final int FULL_WIDTH = 272;
    public static final int FULL_HEIGHT = 180;

    public static final int MAX_TITLE_WIDTH = PAGE_WIDTH - 4;

    public static final int CLICK_SAFETY_MARGIN = 20;

    private static long lastTurnPageSoundTime;
    private final BookOverviewScreen parentScreen;
    private final BookEntry entry;
    private final ResourceLocation bookContentTexture;
    public int ticksInBook;
    public boolean simulateEscClosing;
    private BookPage leftPage;
    private BookPage rightPage;
    private BookPageRenderer<?> leftPageRenderer;
    private BookPageRenderer<?> rightPageRenderer;
    private int bookLeft;
    private int bookTop;
    /**
     * The index of the two pages being displayed. 0 means Pages 0 and 1, 1 means Pages 2 and 3, etc.
     */
    private int openPagesIndex;
    private int maxOpenPagesIndex;
    private List<Component> tooltip;

    private ItemStack tooltipStack;
    private FluidStack tooltipFluidStack;
    private boolean isHoveringItemLink;

    public BookContentScreen(BookOverviewScreen parentScreen, BookEntry entry) {
        super(Component.literal(""));

        this.minecraft = Minecraft.getInstance();

        this.parentScreen = parentScreen;
        this.entry = entry;

        this.bookContentTexture = this.parentScreen.getBook().getBookContentTexture();

        this.loadEntryState();
    }

    public static void drawFromTexture(GuiGraphics guiGraphics, Book book, int x, int y, int u, int v, int w, int h) {
        guiGraphics.blit(book.getBookContentTexture(), x, y, u, v, w, h, 512, 256);
    }

    public static void drawTitleSeparator(GuiGraphics guiGraphics, Book book, int x, int y) {
        int w = 110;
        int h = 3;
        int rx = x - w / 2;

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1F, 1F, 1F, 0.8F);
        //u and v are the pixel coordinates in our book_content_texture
        drawFromTexture(guiGraphics, book, rx, y, 0, 253, w, h);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    public static void drawLock(GuiGraphics guiGraphics, Book book, int x, int y) {
        drawFromTexture(guiGraphics, book, x, y, 496, 0, 16, 16);
    }

    public static void playTurnPageSound(Book book) {
        if (ClientTicks.ticks - lastTurnPageSoundTime > 6) {
            //TODO: make mod loader agnostic
            var sound = ForgeRegistries.SOUND_EVENTS.getValue(book.getTurnPageSound());
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, (float) (0.7 + Math.random() * 0.3)));
            lastTurnPageSoundTime = ClientTicks.ticks;
        }
    }

    public static void renderBookBackground(GuiGraphics guiGraphics, ResourceLocation bookContentTexture) {
        int x = 0; // (this.width - BOOK_BACKGROUND_WIDTH) / 2;
        int y = 0; // (this.height - BOOK_BACKGROUND_HEIGHT) / 2;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(bookContentTexture, x, y, 0, 0, 272, 178, 512, 256);
    }

    public BookEntry getEntry() {
        return this.entry;
    }

    public Book getBook() {
        return this.entry.getBook();
    }

    public boolean canSeeArrowButton(boolean left) {
        return left ? this.openPagesIndex > 0 : (this.openPagesIndex + 1) < this.maxOpenPagesIndex;
    }

    public boolean canSeeBackButton() {
        return BookGuiManager.get().getHistorySize() > 0;
    }

    /**
     * Needs to use Button instead of ArrowButton to conform to Button.OnPress otherwise we can't use it as method
     * reference, which we need - lambda can't use this in super constructor call.
     */
    public void handleArrowButton(Button button) {
        this.flipPage(((ArrowButton) button).left, true);
    }

    public void handleBackButton(Button button) {
        if (BookGuiManager.get().getHistorySize() > 0) {
            var lastPage = BookGuiManager.get().popHistory();
            BookGuiManager.get().openEntry(lastPage.bookId, lastPage.categoryId, lastPage.entryId, lastPage.page);
        }

        //TODO: SFX?
    }

    public void handleExitButton(Button button) {
        this.onClose();
    }

    public void setTooltip(Component... strings) {
        this.setTooltip(List.of(strings));
    }

    public void setTooltip(List<Component> tooltip) {
        this.resetTooltip();
        this.tooltip = tooltip;
    }

    public void setTooltipStack(ItemStack stack) {
        this.resetTooltip();
        this.tooltipStack = stack;
    }

    public void setTooltipStack(FluidStack stack) {
        this.resetTooltip();
        this.tooltipFluidStack = stack;
    }

    /**
     * Doesn't actually translate, as it's not necessary, just checks if mouse is in given area
     */
    public boolean isMouseInRelativeRange(double absMx, double absMy, int x, int y, int w, int h) {
        double mx = absMx; //this.getRelativeX(absMx);
        double my = absMy; //this.getRelativeY(absMy);

        return mx > x && my > y && mx <= (x + w) && my <= (y + h);
    }

    /**
     * Convert the given argument from global screen coordinates to local coordinates
     */
    public double getRelativeX(double absX) {
        return absX - this.bookLeft;
    }

    /**
     * Convert the given argument from global screen coordinates to local coordinates
     */
    public double getRelativeY(double absY) {
        return absY - this.bookTop;
    }

    public void renderItemStack(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, ItemStack stack) {
        if (stack.isEmpty() || !PageRendererRegistry.isRenderable(stack)) {
            return;
        }

        guiGraphics.renderItem(stack, x, y);
        guiGraphics.renderItemDecorations(this.font, stack, x, y);

        if (this.isMouseInRelativeRange(mouseX, mouseY, x, y, 16, 16)) {
            this.setTooltipStack(stack);
        }
    }

    public void renderItemStacks(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, Collection<ItemStack> stacks) {
        this.renderItemStacks(guiGraphics, x, y, mouseX, mouseY, stacks, -1);
    }

    public void renderItemStacks(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, Collection<ItemStack> stacks, int countOverride) {
        var filteredStacks = PageRendererRegistry.filterRenderableItemStacks(stacks);
        if (filteredStacks.size() > 0) {
            var currentStack = filteredStacks.get((this.ticksInBook / 20) % filteredStacks.size());
            this.renderItemStack(guiGraphics, x, y, mouseX, mouseY, countOverride > 0 ? currentStack.copyWithCount(countOverride) : currentStack);
        }
    }

    public void renderIngredient(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, Ingredient ingr) {
        this.renderItemStacks(guiGraphics, x, y, mouseX, mouseY, Arrays.asList(ingr.getItems()), -1);
    }

    public void renderIngredient(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, Ingredient ingr, int countOverride) {
        this.renderItemStacks(guiGraphics, x, y, mouseX, mouseY, Arrays.asList(ingr.getItems()), countOverride);
    }

    public void renderFluidStack(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, FluidStack stack) {
        this.renderFluidStack(guiGraphics, x, y, mouseX, mouseY, stack, FluidType.BUCKET_VOLUME);
    }

    public void renderFluidStack(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, FluidStack stack, int capacity) {
        if (stack.isEmpty() || !PageRendererRegistry.isRenderable(stack)) {
            return;
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);
        FluidRenderHelper.drawFluid(guiGraphics, 18, 18, stack, capacity);
        guiGraphics.pose().popPose();

        if (this.isMouseInRelativeRange(mouseX, mouseY, x, y, 18, 18)) {
            this.setTooltipStack(stack);
        }
    }

    public void renderFluidStacks(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, Collection<FluidStack> stacks) {
        this.renderFluidStacks(guiGraphics, x, y, mouseX, mouseY, stacks, FluidType.BUCKET_VOLUME);
    }

    public void renderFluidStacks(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, Collection<FluidStack> stacks, int capacity) {
        var filteredStacks = PageRendererRegistry.filterRenderableFluidStacks(stacks);
        if (filteredStacks.size() > 0) {
            this.renderFluidStack(guiGraphics, x, y, mouseX, mouseY, filteredStacks.get((this.ticksInBook / 20) % filteredStacks.size()), capacity);
        }
    }

    /**
     * Will change to the specified page, if not open already
     */
    public void goToPage(int pageIndex, boolean playSound) {
        int openPagesIndex = pageIndex / 2; //will floor, which is what we want
        if (openPagesIndex >= 0 && openPagesIndex < this.maxOpenPagesIndex) {
            if (this.openPagesIndex != openPagesIndex) {
                this.openPagesIndex = openPagesIndex;

                this.onPageChanged();
                if (playSound) {
                    playTurnPageSound(this.getBook());
                }
            }
        } else {
            Modonomicon.LOGGER.warn("Tried to change to page index {} corresponding with " +
                    "openPagesIndex {} but max open pages index is {}.", pageIndex, openPagesIndex, this.maxOpenPagesIndex);
        }
    }

    public Style getClickedComponentStyleAtForPage(BookPageRenderer<?> page, double pMouseX, double pMouseY) {
        if (page != null) {
            return page.getClickedComponentStyleAt(pMouseX - this.bookLeft - page.left, pMouseY - this.bookTop - page.top);
        }

        return null;
    }

    @Nullable
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        var leftPageClickedStyle = this.getClickedComponentStyleAtForPage(this.leftPageRenderer, pMouseX, pMouseY);
        if (leftPageClickedStyle != null) {
            return leftPageClickedStyle;
        }
        var rightPageClickedStyle = this.getClickedComponentStyleAtForPage(this.rightPageRenderer, pMouseX, pMouseY);
        return rightPageClickedStyle;
    }

    public int getBookLeft() {
        return this.bookLeft;
    }

    public int getBookTop() {
        return this.bookTop;
    }

    @SuppressWarnings("unchecked")
    public void removeRenderableWidgets(Collection<? extends Renderable> renderables) {
        this.renderables.removeIf(renderables::contains);
        this.children().removeIf(c -> c instanceof Renderable && renderables.contains(c));
        this.narratables.removeIf(n -> n instanceof Renderable && renderables.contains(n));
    }

    protected void flipPage(boolean left, boolean playSound) {
        if (this.canSeeArrowButton(left)) {

            var oldOpenPagesIndex = this.openPagesIndex;
            if (left) {
                this.openPagesIndex--;
            } else {
                this.openPagesIndex++;
            }

            if (BookGuiManager.get().getHistorySize() > 0) {
                var lastPage = BookGuiManager.get().peekHistory();
                if (lastPage.bookId == this.entry.getBook().getId() && lastPage.entryId == this.entry.getId() && lastPage.page == this.openPagesIndex * 2) {
                    //if we're flipping back to the last page in the history, don't add a new history entry,
                    // and remove the old one to avoid weird back-and-forth jumps when using the back button
                    BookGuiManager.get().popHistory();
                } else {
                    //if we flip to a new page, add a new history entry for the page we were on before flipping
                    BookGuiManager.get().pushHistory(this.entry.getBook().getId(), this.entry.getCategory().getId(), this.entry.getId(), oldOpenPagesIndex * 2);
                }
            } else {
                //if we don't have any history, add a new history entry for the page we were on before flipping
                BookGuiManager.get().pushHistory(this.entry.getBook().getId(), this.entry.getCategory().getId(), this.entry.getId(), oldOpenPagesIndex * 2);
            }

            this.onPageChanged();
            if (playSound) {
                playTurnPageSound(this.getBook());
            }
        }
    }

    protected void drawTooltip(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        if (this.tooltipStack != null) {
            List<Component> tooltip = this.getTooltipFromItem(this.tooltipStack);
            guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, pMouseX, pMouseY);
        } else if (this.tooltipFluidStack != null) {
            List<Component> tooltip = this.getTooltipFromFluid(this.tooltipFluidStack);
            guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, pMouseX, pMouseY);
        } else if (this.tooltip != null && !this.tooltip.isEmpty()) {
            guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, this.tooltip, pMouseX, pMouseY);
        }
    }

    protected boolean clickPage(BookPageRenderer<?> page, double mouseX, double mouseY, int mouseButton) {
        if (page != null) {
            return page.mouseClicked(mouseX - this.bookLeft - page.left, mouseY - this.bookTop - page.top, mouseButton);
        }

        return false;
    }

    protected void renderPage(GuiGraphics guiGraphics, BookPageRenderer<?> page, int pMouseX, int pMouseY, float pPartialTick) {
        if (page == null) {
            return;
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(page.left, page.top, 0);
        page.render(guiGraphics, pMouseX - this.bookLeft - page.left, pMouseY - this.bookTop - page.top, pPartialTick);
        guiGraphics.pose().popPose();
    }

    protected void beginDisplayPages() {
        //allow pages to clean up
        if (this.leftPageRenderer != null) {
            this.leftPageRenderer.onEndDisplayPage(this);
        }
        if (this.rightPageRenderer != null) {
            this.rightPageRenderer.onEndDisplayPage(this);
        }

        //get new pages
        var pages = this.entry.getPages();
        int leftPageIndex = this.openPagesIndex * 2;
        int rightPageIndex = leftPageIndex + 1;

        this.leftPage = leftPageIndex < pages.size() ? pages.get(leftPageIndex) : null;
        this.rightPage = rightPageIndex < pages.size() ? pages.get(rightPageIndex) : null;

        //allow pages to prepare for being displayed
        if (this.leftPage != null) {
            this.leftPageRenderer = PageRendererRegistry.getPageRenderer(this.leftPage.getType()).create(this.leftPage);
            this.leftPageRenderer.onBeginDisplayPage(this, LEFT_PAGE_X, TOP_PADDING);
        } else {
            this.leftPageRenderer = null;
        }
        if (this.rightPage != null) {
            this.rightPageRenderer = PageRendererRegistry.getPageRenderer(this.rightPage.getType()).create(this.rightPage);
            this.rightPageRenderer.onBeginDisplayPage(this, RIGHT_PAGE_X, TOP_PADDING);
        } else {
            this.rightPageRenderer = null;
        }
    }

    protected void onPageChanged() {
        this.beginDisplayPages();
    }

    protected void resetTooltip() {
        this.tooltip = null;
        this.tooltipStack = null;
        this.tooltipFluidStack = null;
    }

    private boolean clickOutsideEntry(double pMouseX, double pMouseY) {
        return pMouseX < this.bookLeft - CLICK_SAFETY_MARGIN
                || pMouseX > this.bookLeft + FULL_WIDTH + CLICK_SAFETY_MARGIN
                || pMouseY < this.bookTop - CLICK_SAFETY_MARGIN
                || pMouseY > this.bookTop + FULL_HEIGHT + CLICK_SAFETY_MARGIN;
    }

    private void loadEntryState() {
        var state = BookStateCapability.getEntryStateFor(this.parentScreen.getMinecraft().player, this.entry);

        BookGuiManager.get().currentEntry = this.entry;
        BookGuiManager.get().currentContentScreen = this;

        if (state != null) {
            this.openPagesIndex = state.openPagesIndex;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.disableDepthTest(); //guard against depth test being enabled by other rendering code, that would cause ui elements to vanish

        this.resetTooltip();

        //we need to modify blit offset (now: z pose) to not draw over toasts
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, -1300);  //magic number arrived by testing until toasts show, but BookOverviewScreen does not
        this.renderBackground(guiGraphics);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.bookLeft, this.bookTop, 0);
        renderBookBackground(guiGraphics, this.bookContentTexture);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.bookLeft, this.bookTop, 0);
        this.renderPage(guiGraphics, this.leftPageRenderer, pMouseX, pMouseY, pPartialTick);
        this.renderPage(guiGraphics, this.rightPageRenderer, pMouseX, pMouseY, pPartialTick);
        guiGraphics.pose().popPose();

        //do not translate super (= widget rendering) -> otherwise our buttons are messed up
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        //do not translate tooltip, would mess up location
        this.drawTooltip(guiGraphics, pMouseX, pMouseY);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {

        if (this.simulateEscClosing || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_ESCAPE)) {
            Networking.sendToServer(new SaveEntryStateMessage(this.entry, this.openPagesIndex));

            super.onClose();
            this.parentScreen.onClose();

            this.simulateEscClosing = false;
        } else {
            Networking.sendToServer(new SaveEntryStateMessage(this.entry,
                    ClientConfig.get().qolCategory.storeLastOpenPageWhenClosingEntry.get() ? this.openPagesIndex : 0));

            this.parentScreen.getCurrentCategoryScreen().onCloseEntry(this);
            super.onClose();
        }
    }

    /**
     * Make public to access from pages
     */
    @Override
    public <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T pWidget) {
        return super.addRenderableWidget(pWidget);
    }

    /**
     * Our copy of guiGraphics.renderComponentHoverEffect(); to handle book links
     */
    public void renderComponentHoverEffect(GuiGraphics guiGraphics, @Nullable Style style, int mouseX, int mouseY) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 1000);
        var newStyle = style;
        if (style != null && style.getHoverEvent() != null) {
            if (style.getHoverEvent().getAction() == HoverEvent.Action.SHOW_TEXT) {
                var clickEvent = style.getClickEvent();
                if (clickEvent != null) {
                    if (clickEvent.getAction() == Action.CHANGE_PAGE) {

                        //handle book links -> check if locked
                        if (BookLink.isBookLink(clickEvent.getValue())) {
                            var link = BookLink.from(this.getBook(), clickEvent.getValue());
                            var book = BookDataManager.get().getBook(link.bookId);
                            if (link.entryId != null) {
                                var entry = book.getEntry(link.entryId);

                                if (!BookUnlockCapability.isUnlockedFor(this.minecraft.player, entry)) {
                                    //if locked, append lock warning
                                    //handleComponentClicked will prevent the actual click

                                    var oldComponent = style.getHoverEvent().getValue(HoverEvent.Action.SHOW_TEXT);

                                    var newComponent = Component.translatable(
                                            Gui.HOVER_BOOK_LINK_LOCKED,
                                            oldComponent,
                                            Component.translatable(Gui.HOVER_BOOK_LINK_LOCKED_INFO)
                                                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xff0015)).withBold(true)));

                                    newStyle = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, newComponent));
                                }
                            }
                        }
                    }

                    if (clickEvent.getAction() == Action.RUN_COMMAND) {
                        if (CommandLink.isCommandLink(clickEvent.getValue())) {
                            var link = CommandLink.from(this.getBook(), clickEvent.getValue());
                            var book = BookDataManager.get().getBook(link.bookId);
                            if (link.commandId != null) {
                                var command = book.getCommand(link.commandId);

                                var oldComponent = style.getHoverEvent().getValue(HoverEvent.Action.SHOW_TEXT);

                                if (!BookUnlockCapability.canRunFor(this.minecraft.player, command)) {
                                    var hoverComponent = Component.translatable(Gui.HOVER_COMMAND_LINK_UNAVAILABLE).withStyle(ChatFormatting.RED);
                                    newStyle = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent));
                                    oldComponent = hoverComponent;
                                }

                                if(hasShiftDown()){
                                    var newComponent = oldComponent.copy().append(Component.literal("\n")).append(
                                            Component.literal(command.getCommand()).withStyle(ChatFormatting.GRAY));
                                    newStyle = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, newComponent));
                                }
                            }
                        }
                    }
                }
            }
        }

        style = newStyle;

        //original GuiGraphics.renderComponentHoverEffect(pPoseStack, newStyle, mouseX, mouseY);
        // our own copy of the render code that limits width for the show_text action to not go out of screen
        if (style != null && style.getHoverEvent() != null) {
            HoverEvent hoverevent = style.getHoverEvent();
            HoverEvent.ItemStackInfo hoverevent$itemstackinfo = hoverevent.getValue(HoverEvent.Action.SHOW_ITEM);
            if (hoverevent$itemstackinfo != null) {
                //special handling for item link hovers -> we append another line in this.getTooltipFromItem
                if (style.getClickEvent() != null)// && ItemLinkRenderer.isItemLink(style.getClickEvent().getValue()))
                    this.isHoveringItemLink = true;

                //temporarily modify width to force forge to handle wrapping correctly
                var backupWidth = this.width;
                this.width = this.width / 2; //not quite sure why exaclty / 2 works, but then forge wrapping handles it correctly on gui scale 3+4
                guiGraphics.renderTooltip(this.minecraft.font, hoverevent$itemstackinfo.getItemStack(), mouseX, mouseY);
                this.width = backupWidth;

                //then we reset so other item tooltip renders are not affected
                this.isHoveringItemLink = false;
            } else {
                HoverEvent.EntityTooltipInfo hoverevent$entitytooltipinfo = hoverevent.getValue(HoverEvent.Action.SHOW_ENTITY);
                if (hoverevent$entitytooltipinfo != null) {
                    if (this.minecraft.options.advancedItemTooltips) {
                        guiGraphics.renderComponentTooltip(this.minecraft.font, hoverevent$entitytooltipinfo.getTooltipLines(), mouseX, mouseY);
                    }
                } else {
                    Component component = hoverevent.getValue(HoverEvent.Action.SHOW_TEXT);
                    if (component != null) {
                        //var width = Math.max(this.width / 2, 200); //original width calc
                        var width = (this.width / 2) - mouseX - 10; //our own
                        guiGraphics.renderTooltip(this.minecraft.font, this.minecraft.font.split(component, width), mouseX, mouseY);
                    }
                }
            }

        }
        guiGraphics.pose().popPose();
    }

    /**
     * Used to be override of < 1.20.0 Screen.getTooltipFromItem, which is now static
     */
    public List<Component> getTooltipFromItem(ItemStack pItemStack) {
        var tooltip = getTooltipFromItem(Minecraft.getInstance(), pItemStack);

        if (this.isHoveringItemLink) {
            tooltip.add(Component.literal(""));
            if (ModonomiconJeiIntegration.isJeiLoaded()) {
                tooltip.add(Component.translatable(Gui.HOVER_ITEM_LINK_INFO).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.GREEN)));
                tooltip.add(Component.translatable(Gui.HOVER_ITEM_LINK_INFO_LINE2).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.GRAY)));
            } else {
                tooltip.add(Component.translatable(Gui.HOVER_ITEM_LINK_INFO_NO_JEI).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.RED)));
            }
        }

        return tooltip;
    }

    public List<Component> getTooltipFromFluid(FluidStack fluidStack) {
        var tooltip = FluidRenderHelper.getTooltip(fluidStack, FluidType.BUCKET_VOLUME, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL, FluidRenderHelper.TooltipMode.SHOW_AMOUNT_AND_CAPACITY);

        if (this.isHoveringItemLink) {
            tooltip.add(Component.literal(""));
            if (ModonomiconJeiIntegration.isJeiLoaded()) {
                tooltip.add(Component.translatable(Gui.HOVER_ITEM_LINK_INFO).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.GREEN)));
                tooltip.add(Component.translatable(Gui.HOVER_ITEM_LINK_INFO_LINE2).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.GRAY)));
            } else {
                tooltip.add(Component.translatable(Gui.HOVER_ITEM_LINK_INFO_NO_JEI).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.RED)));
            }
        }

        return tooltip;
    }


    @Override
    public boolean handleComponentClicked(@Nullable Style pStyle) {
        if (pStyle != null) {
            var event = pStyle.getClickEvent();
            if (event != null) {
                if (event.getAction() == Action.CHANGE_PAGE) {

                    //handle book links
                    if (BookLink.isBookLink(event.getValue())) {
                        var link = BookLink.from(this.getBook(), event.getValue());
                        var book = BookDataManager.get().getBook(link.bookId);
                        if (link.entryId != null) {
                            var entry = book.getEntry(link.entryId);

                            if (!BookUnlockCapability.isUnlockedFor(this.minecraft.player, entry)) {
                                //renderComponentHoverEffect will render a warning that it is locked so it is fine to exit here
                                return false;
                            }

                            int page = link.pageNumber;
                            if (link.pageAnchor != null) {
                                page = entry.getPageNumberForAnchor(link.pageAnchor);
                            }

                            //we push the page we are currently on to the history
                            BookGuiManager.get().pushHistory(this.entry.getBook().getId(), this.entry.getCategory().getId(), this.entry.getId(), this.openPagesIndex * 2);
                            BookGuiManager.get().openEntry(link.bookId, link.entryId, page);
                        } else if (link.categoryId != null) {
                            BookGuiManager.get().openEntry(link.bookId, link.categoryId, null, 0);
                            //Currently we do not push categories to history
                        } else {
                            BookGuiManager.get().openEntry(link.bookId, null, null, 0);
                            //Currently we do not push categories to history
                        }
                        return true;
                    }

                    //handle patchouli link clicks
                    if (PatchouliLink.isPatchouliLink(event.getValue())) {
                        var link = PatchouliLink.from(event.getValue());
                        if (link.bookId != null) {
                            //the integration class handles class loading guards if patchouli is not present
                            this.simulateEscClosing = true;
                            //this.onClose();

                            ModonomiconPatchouliIntegration.openEntry(link.bookId, link.entryId, link.pageNumber);
                            return true;
                        }
                    }

                    if (ItemLinkRenderer.isItemLink(event.getValue())) {

                        if (ModonomiconJeiIntegration.isJeiLoaded()) {
                            var itemId = event.getValue().substring(ItemLinkRenderer.PROTOCOL_ITEM_LENGTH);
                            var itemStack = ItemStackUtil.loadFromParsed(ItemStackUtil.parseItemStackString(itemId));

                            this.onClose(); //we have to do this before showing JEI, because super.onClose() clears Gui Layers, and thus would kill JEIs freshly spawned gui

                            if (Screen.hasShiftDown()) {
                                ModonomiconJeiIntegration.showUses(itemStack);
                            } else {
                                ModonomiconJeiIntegration.showRecipe(itemStack);
                            }

                            if (!ModonomiconJeiIntegration.isJEIRecipesGuiOpen()) {
                                this.minecraft.pushGuiLayer(this);
                            }

                            //TODO: Consider adding logic to restore content screen after JEI gui close
                            //      currently only the overview screen is restored (because JEI does not use Forges Gui Stack, only vanilla screen, thus only saves one parent screen)
                            //      we could fix that by listening to the Closing event from forge, and in that set the closing time
                            //      -> then on init of overview screen, if closing time is < delta, push last content screen from gui manager
                        }

                        return true;
                    }
                }
                if (event.getAction() == Action.RUN_COMMAND) {
                    //handle command link clicks
                    if (CommandLink.isCommandLink(event.getValue())) {
                        var link = CommandLink.from(this.getBook(), event.getValue());
                        var book = BookDataManager.get().getBook(link.bookId);
                        if (link.commandId != null) {
                            var command = book.getCommand(link.commandId);

                            if (BookUnlockCapability.canRunFor(this.minecraft.player, command)) {
                                Networking.sendToServer(new ClickCommandLinkMessage(link.bookId, link.commandId));

                                //we immediately count up the usage client side -> to avoid spamming the server
                                //if the server ends up not counting up the usage, it will sync the correct info back down to us
                                BookUnlockCapability.setRunFor(this.minecraft.player, command);
                            }

                            return true;
                        }
                    }
                }
            }
        }
        return super.handleComponentClicked(pStyle);
    }

    @Override
    protected void init() {
        super.init();

        this.bookLeft = (this.width - BOOK_BACKGROUND_WIDTH) / 2;
        this.bookTop = (this.height - BOOK_BACKGROUND_HEIGHT) / 2;

        this.maxOpenPagesIndex = (int) Math.ceil((float) this.entry.getPages().size() / 2);
        this.beginDisplayPages();

        this.addRenderableWidget(new BackButton(this, this.width / 2 - BackButton.WIDTH / 2, this.bookTop + FULL_HEIGHT - BackButton.HEIGHT / 2));
        this.addRenderableWidget(new ArrowButton(this, this.bookLeft - 4, this.bookTop + FULL_HEIGHT - 6, true, () -> this.canSeeArrowButton(true), this::handleArrowButton));
        this.addRenderableWidget(new ArrowButton(this, this.bookLeft + FULL_WIDTH - 14, this.bookTop + FULL_HEIGHT - 6, false, () -> this.canSeeArrowButton(false), this::handleArrowButton));
        this.addRenderableWidget(new ExitButton(this, this.bookLeft + FULL_WIDTH - 10, this.bookTop - 2, this::handleExitButton));
    }

    @Override
    public void tick() {
        super.tick();

        if (!hasShiftDown()) {
            this.ticksInBook++;
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            var style = this.getClickedComponentStyleAt(pMouseX, pMouseY);
            if (style != null && this.handleComponentClicked(style)) {
                return true;
            }
        }

        var clickPage = this.clickPage(this.leftPageRenderer, pMouseX, pMouseY, pButton)
                || this.clickPage(this.rightPageRenderer, pMouseX, pMouseY, pButton);


        if (this.clickOutsideEntry(pMouseX, pMouseY)) {
            this.onClose();
        }

        return clickPage || super.mouseClicked(pMouseX, pMouseY, pButton);
    }
}
