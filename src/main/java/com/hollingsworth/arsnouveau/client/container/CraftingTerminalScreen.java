package com.hollingsworth.arsnouveau.client.container;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonElement;
import com.ibm.icu.text.Collator;
import com.ibm.icu.text.RuleBasedCollator;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.NoShadowTextField;
import com.hollingsworth.arsnouveau.client.gui.TerminalScrollSlider;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.StateButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.StorageSettingsButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.StorageTabButton;
import com.hollingsworth.arsnouveau.common.network.ClientSearchPacket;
import com.hollingsworth.arsnouveau.common.network.ClientSlotClick;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.SetTerminalSettingsPacket;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu.SlotAction.*;

public class CraftingTerminalScreen extends AbstractContainerScreen<CraftingTerminalMenu> implements RecipeUpdateListener {
    private static final LoadingCache<StoredItemStack, List<String>> tooltipCache =
            CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.SECONDS).build(CacheLoader.from(key -> {
                var mc = Minecraft.getInstance();
                var flag = mc.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
                return key.getStack().getTooltipLines(Item.TooltipContext.of(mc.level), mc.player, flag).
                        stream().map(Component::getString).collect(Collectors.toList());
            }));

    private static final LoadingCache<StoredItemStack, String> componentCache =
            CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.SECONDS).build(CacheLoader.from(key -> {
                var ctx = Minecraft.getInstance().level.registryAccess().createSerializationContext(JsonOps.COMPRESSED);
                return DataComponentPatch.CODEC.encodeStart(ctx, key.getStack().getComponentsPatch()).
                        mapOrElse(JsonElement::toString, e -> "");
            }));

    private static final LoadingCache<StoredItemStack, List<String>> tagCache =
            CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.SECONDS).build(CacheLoader.from(
                    key -> key.getStack().getTags().map(t -> t.location().toString()).toList()
            ));

    private static final ResourceLocation gui = ArsNouveau.prefix("textures/gui/crafting_terminal.png");
    private static final ResourceLocation gui_expanded = ArsNouveau.prefix("textures/gui/crafting_terminal_expanded.png");

    protected Minecraft mc = Minecraft.getInstance();

    protected TerminalScrollSlider scrollSlider;
    protected boolean draggingScroll;
    private boolean refreshItemList;
    protected NoShadowTextField searchField;
    protected int slotIDUnderMouse = -1;
    protected int controllMode;
    protected int searchType;
    protected boolean expanded;
    private String searchLast = "";
    protected boolean loadedSearch = false;
    private StoredItemStack.IStoredItemStackComparator comparator = new StoredItemStack.ComparatorAmount(false);
    protected StateButton buttonSortingType;
    protected StateButton buttonDirection;
    protected StateButton buttonSearchType;
    private Comparator<StoredItemStack> sortComp;
    List<String> tabNames = new ArrayList<>();
    public List<StorageTabButton> tabButtons = new ArrayList<>();

    boolean noSort;

    List<StoredItemStack> itemsSorted = new ArrayList<>();
    List<StoredItemStack> itemsUnsorted = new ArrayList<>();

    private final RecipeBookComponent recipeBookGui;
    private boolean widthTooNarrow;
    private EditBox recipeBookSearch;
    private GhostRecipe ghostRecipe;

    public GuiImageButton btnClr;

    public GuiImageButton btnRecipeBook;
    public GuiImageButton btnExpand;
    public GuiImageButton btnCollapse;

    public CraftingTerminalScreen(CraftingTerminalMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);

        recipeBookGui = new RecipeBookComponent();
        try {
            recipeBookGui.stackedContents = getMenu().new TerminalRecipeItemHelper();
            ghostRecipe = recipeBookGui.ghostRecipe;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void receiveSettings(SortSettings settings) {
        boolean wasExpanded = expanded;
        if (settings != null) {
            controllMode = settings.controlMode();
            comparator = StoredItemStack.SortingTypes.VALUES[settings.sortType() % StoredItemStack.SortingTypes.VALUES.length].create(settings.reverseSort());
            searchType = settings.searchType();
            buttonSortingType.state = settings.sortType();
            buttonDirection.state = settings.reverseSort() ? 1 : 0;
            buttonSearchType.state = searchType;
            expanded = settings.expanded();
        }
        if (expanded != wasExpanded) {
            onExpandedChanged(expanded);
        }
    }

    protected void onExpandedChanged(boolean expanded) {
        this.expanded = expanded;
        menu.setExpanded(expanded);
        btnCollapse.visible = this.expanded;
        btnExpand.visible = !this.expanded;
        btnClr.visible = !this.expanded;
        btnRecipeBook.visible = !this.expanded && Config.SHOW_RECIPE_BOOK.get();
        refreshItemList = true;
    }

    public void collapseScreen() {
        this.expanded = false;
        onExpandedChanged(false);
        syncSortSettings();
    }

    public void expandScreen() {
        this.expanded = true;
        if (this.recipeBookGui.isVisible()) {
            btnRecipeBook.onPress();
        }
        onExpandedChanged(true);
        syncSortSettings();
    }

    protected void onPacket() {
        if (tabNames != null && !tabNames.isEmpty()) {
            for (StorageTabButton tabButton : tabButtons) {
                tabButton.visible = false;
            }
            // Set isAll tab visible
            tabButtons.get(0).visible = true;
            for (int i = 0; i < tabNames.size(); i++) {
                tabButtons.get(i + 1).visible = true;
                tabButtons.get(i + 1).highlightText = tabNames.get(i);
            }
        }

        if (!loadedSearch && menu.search != null) {
            loadedSearch = true;
            searchField.setValue(menu.search);
            searchField.setFocused(true);
        }
    }

    protected void syncSortSettings() {
        StorageTabButton selectedTabButton = tabButtons.stream().filter(i -> i.visible && i.isSelected).findFirst().orElse(null);
        Networking.sendToServer(new SetTerminalSettingsPacket(getSortSettings(), selectedTabButton == null ? null : selectedTabButton.highlightText));
    }

    public SortSettings getSortSettings() {
        return new SortSettings(
                controllMode,
                comparator.isReversed(),
                comparator.type(),
                searchType,
                expanded
        );
    }

    @Override
    protected void init() {
        imageWidth = 202;
        imageHeight = 248;
        clearWidgets();
        inventoryLabelY = imageHeight - 92;
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookGui.init(this.width, this.height + 30, this.minecraft, this.widthTooNarrow, this.menu);
        this.leftPos = this.recipeBookGui.updateScreenPosition(this.width, this.imageWidth);

        this.searchField = new NoShadowTextField(font, this.leftPos + 115, this.topPos + 6, 60, font.lineHeight, Component.translatable("narrator.ars_nouveau.search"));
        this.searchField.setMaxLength(100);
        this.searchField.setBordered(false);
        this.searchField.setVisible(true);
        this.searchField.setValue(searchLast);
        this.setFocused(this.searchField);
        this.searchField.active = false;
        searchLast = "";
        addRenderableWidget(searchField);

        buttonSortingType = addRenderableWidget(new StorageSettingsButton(leftPos - 17, topPos + 14, 22, 12, 0, DocAssets.STORAGE_SORT_TYPE, b -> {
            comparator = StoredItemStack.SortingTypes.VALUES[(comparator.type() + 1) % StoredItemStack.SortingTypes.VALUES.length].create(comparator.isReversed());
            buttonSortingType.state = comparator.type();
            syncSortSettings();
            refreshItemList = true;
        }).withTooltip(state -> Component.translatable("tooltip.ars_nouveau.sorting_" + state)));

        buttonDirection = addRenderableWidget(new StorageSettingsButton(leftPos - 17, topPos + 29, 22, 12, 0, DocAssets.STORAGE_SORT_ORDER, b -> {
            comparator.setReversed(!comparator.isReversed());
            buttonDirection.state = comparator.isReversed() ? 1 : 0;
            syncSortSettings();
            refreshItemList = true;
        }).withTooltip(state -> Component.translatable("tooltip.ars_nouveau.direction_" + state, IAutoFillTerminal.getHandlerName())));
        buttonSearchType = addRenderableWidget(new StorageSettingsButton(leftPos - 17, topPos + 44, 22, 12, 0, DocAssets.STORAGE_SEARCH_SYNC, b -> {
            searchType = searchType == 0 ? 1 : 0;
            buttonSearchType.state = searchType;
            syncSortSettings();
        }).withTooltip(state -> Component.translatable("tooltip.ars_nouveau.search_" + state, IAutoFillTerminal.getHandlerName())));
        buttonSortingType.state = comparator.type();
        buttonDirection.state = comparator.isReversed() ? 1 : 0;
        buttonSearchType.state = searchType;
        int selectedTab = tabButtons.stream().filter(b -> b.isSelected).mapToInt(b -> b.state).findFirst().orElse(0);
        tabButtons.clear();
        for (int i = 0; i < 12; i++) {
            var button = addRenderableWidget(new StorageTabButton(leftPos - 13, topPos + 59 + i * 15, 18, 12, i, 0, DocAssets.STORAGE_TAB_ICONS, b -> {
                StorageTabButton tabButton = (StorageTabButton) b;
                setSelectedTab(tabButton.state);
                syncSortSettings();
            }));
            button.visible = false;
            button.isSelected = i == selectedTab;
            if (i == 0) {
                button.isAll = true;
            }
            this.tabButtons.add(button);
        }
        if (scrollSlider == null) {
            scrollSlider = new TerminalScrollSlider(0, 0, 14, 3 * 18, DocAssets.STORAGE_SCROLL_BALL, this::scrollTo);
        }
        scrollSlider.setPosition(leftPos + 187, topPos + 18);
        addRenderableWidget(scrollSlider);
        scrollTo(scrollSlider.getValueInt());
        updateSearch();

        addRenderableWidget(recipeBookGui);
        this.setInitialFocus(this.recipeBookGui);
        int recipeButtonY = this.height / 2 - 34;
        int collapseButtonY = this.height / 2 + 23;
        btnClr = new GuiImageButton(leftPos + 86, recipeButtonY, DocAssets.CRAFT_CLEAR_BUTTON, b -> clearGrid());
        btnExpand = new GuiImageButton(leftPos + 86, recipeButtonY - 12, DocAssets.EXPAND_INVENTORY_BUTTON, b -> expandScreen());
        btnCollapse = new GuiImageButton(leftPos + 86, collapseButtonY, DocAssets.COLLAPSE_INVENTORY_BUTTON, b -> collapseScreen());
        addRenderableWidget(btnClr);
        addRenderableWidget(btnCollapse);
        addRenderableWidget(btnExpand);
        btnRecipeBook = addRenderableWidget(new GuiImageButton(this.leftPos + 98, recipeButtonY, DocAssets.RECIPE_BOOK_BUTTON, (thisButton) -> {
            this.recipeBookGui.initVisuals();
            recipeBookSearch = recipeBookGui.searchBox;

            this.recipeBookGui.toggleVisibility();
            int oldLeft = leftPos;
            this.leftPos = this.recipeBookGui.updateScreenPosition(this.width, this.imageWidth);
            for (GuiEventListener child : children()) {
                if (child instanceof AbstractWidget widget) {
                    widget.setX(widget.getX() + leftPos - oldLeft);
                }
            }
        }));
        if (recipeBookGui.isVisible()) {
            recipeBookSearch = recipeBookGui.searchBox;
        }
        btnRecipeBook.visible = Config.SHOW_RECIPE_BOOK.get();
        onExpandedChanged(expanded);
        onPacket();
    }

    public void setSelectedTab(int index) {
        for (int i = 0; i < tabButtons.size(); i++) {
            tabButtons.get(i).isSelected = i == index;
        }
    }

    enum SearchType {
        ITEM,
        MOD("@"),
        TAG("#"),
        COMPONENT("$");

        private String prefix = null;

        SearchType(String prefix) {
            this.prefix = prefix;
        }

        SearchType() {
        }

        boolean isMatch(String search) {
            return this.prefix != null && search.startsWith(this.prefix);
        }
    }

    protected void updateSearch() {
        String searchString = searchField.getValue().trim();
        if (searchField.getValue().isEmpty()) {
            searchField.setSuggestion(Component.translatable("ars_nouveau.spell_book_gui.search").getString());
        } else {
            searchField.setSuggestion("");
        }

        if (refreshItemList || !searchLast.equals(searchString)) {
            this.itemsSorted = new ArrayList<>();
            String search = searchString.toLowerCase();

            SearchType searchFieldType = SearchType.ITEM;
            for (SearchType value : SearchType.values()) {
                if (value.isMatch(search)) {
                    searchFieldType = value;
                }
            }
            if (searchFieldType != SearchType.ITEM) {
                search = searchString.substring(1);
            }
            String finalSearch = search;

            for (StoredItemStack stored : this.itemsUnsorted) {
                if (stored == null || stored.getStack() == null) continue;

                boolean isMatch = switch (searchFieldType) {
                    case ITEM -> {
                        boolean contains = stored.getStack().getHoverName().getString().toLowerCase().contains(finalSearch);
                        if (contains) yield true;
                        yield tooltipCache.getUnchecked(stored).stream().anyMatch(tooltip -> tooltip.toLowerCase().contains(finalSearch));
                    }
                    case MOD ->
                            BuiltInRegistries.ITEM.getKey(stored.getStack().getItem()).getNamespace().contains(finalSearch);
                    case TAG -> tagCache.getUnchecked(stored).stream().anyMatch(tag -> tag.contains(finalSearch));
                    case COMPONENT -> {
                        if (stored.getStack().getComponentsPatch().isEmpty()) yield false;
                        yield componentCache.getUnchecked(stored).contains(finalSearch);
                    }
                };

                if (isMatch) {
                    itemsSorted.add(stored);
                }
            }

            Collections.sort(this.itemsSorted, noSort ? sortComp : comparator);
            scrollSlider.setMaxRows((itemsSorted.size() + 8) / 9 - (expanded ? 7 : 3));
            if (!searchLast.equals(searchString)) {
                scrollSlider.setValue(0);
                if (searchType == 1) {
                    IAutoFillTerminal.sync(searchString);
                }
                Networking.sendToServer(new ClientSearchPacket(searchString));
                if (IAutoFillTerminal.hasSync() || searchType == 1) {
                    if (recipeBookSearch != null) {
                        recipeBookSearch.setValue(searchString);
                    }
                    recipeBookGui.recipesUpdated();
                }
            }
            scrollTo(scrollSlider.getValueInt());
            refreshItemList = false;
            this.searchLast = searchString;
        }
    }

    public final void scrollTo(int row) {
        int lines = expanded ? 7 : 3;
        int j = Math.max(row, 0);

        for (int k = 0; k < lines; ++k) {
            for (int l = 0; l < 9; ++l) {
                int i1 = l + (k + j) * 9;
                menu.storageSlotList.get(l + k * 9).setStack(i1 < itemsSorted.size() ? itemsSorted.get(i1) : null);
            }
        }
    }

    @Override
    public void containerTick() {
        updateSearch();
        this.recipeBookGui.tick();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics, mouseX, mouseY, partialTicks);
        if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
            this.renderBg(graphics, partialTicks, mouseX, mouseY);
            this.recipeBookGui.render(graphics, mouseX, mouseY, partialTicks);
        } else {
            this.recipeBookGui.render(graphics, mouseX, mouseY, partialTicks);
            if (hasShiftDown()) {
                if (!noSort) {
                    List<StoredItemStack> list = this.itemsSorted;
                    Object2IntMap<StoredItemStack> map = new Object2IntOpenHashMap<>();
                    map.defaultReturnValue(Integer.MAX_VALUE);
                    for (int m = 0; m < list.size(); m++) {
                        map.put(list.get(m), m);
                    }
                    sortComp = Comparator.comparing(map::getInt);
                    noSort = true;
                }
            } else if (noSort) {
                sortComp = null;
                noSort = false;
                refreshItemList = true;
                this.itemsUnsorted = new ArrayList<>(menu.itemList);
            }

            super.render(graphics, mouseX, mouseY, partialTicks);

            if (this.menu.getCarried().isEmpty() && slotIDUnderMouse != -1) {
                SlotStorage slot = getMenu().storageSlotList.get(slotIDUnderMouse);
                if (slot.stack() != null) {
                    if (slot.stack().getQuantity() > 9999) {
                        ClientInfo.setTooltip(Component.translatable("tooltip.ars_nouveau.amount", slot.stack().getQuantity()));
                    }
                    graphics.renderTooltip(font, slot.stack().getActualStack(), mouseX, mouseY);
                    ClientInfo.setTooltip();
                }
            }

            for (Renderable renderable : renderables) {
                if (renderable instanceof AbstractWidget widget && widget.visible && widget.isHovered() && renderable instanceof ITooltipProvider provider) {
                    List<Component> lines = new ArrayList<>();
                    provider.getTooltip(lines);
                    graphics.renderComponentTooltip(font, lines, mouseX, mouseY);
                    break;
                }
            }
            this.recipeBookGui.renderGhostRecipe(graphics, this.leftPos, this.topPos, true, partialTicks);
        }
        this.renderTooltip(graphics, mouseX, mouseY);
        this.recipeBookGui.renderTooltip(graphics, this.leftPos, this.topPos, mouseX, mouseY);
        this.setInitialFocus(this.recipeBookGui);
    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        List<SlotStorage> slots = getMenu().storageSlotList;
        slotIDUnderMouse = -1;
        for (int i = 0; i < slots.size(); i++) {
            if (drawSlot(graphics, slots.get(i), mouseX, mouseY)) {
                slotIDUnderMouse = i;
            }
        }
    }

    protected boolean drawSlot(GuiGraphics st, SlotStorage slot, int mouseX, int mouseY) {
        if (!slot.show) {
            return false;
        }
        if (slot.stack() != null) {
            ItemStack stack = slot.stack().getStack().copy().split(1);
            int i = slot.xPosition(), j = slot.yPosition();

            st.renderItem(stack, i, j);
            st.renderItemDecorations(this.font, stack, i, j, null);

            drawStackSize(st, font, slot.stack().getQuantity(), i, j);
        }

        if (isHovering(slot.xPosition(), slot.yPosition(), 16, 16, mouseX, mouseY)) {
            renderSlotHighlight(st, slot.xPosition(), slot.yPosition(), 0);
            return true;
        }
        return false;
    }

    private void drawStackSize(GuiGraphics graphics, Font fr, long size, int x, int y) {
        float scaleFactor = 0.6f;
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        String stackSize = NumberFormatUtil.formatNumber(size);
        PoseStack st = graphics.pose();
        st.pushPose();
        st.scale(scaleFactor, scaleFactor, scaleFactor);
        st.translate(0, 0, 450);
        float inverseScaleFactor = 1.0f / scaleFactor;
        int X = (int) (((float) x + 16.0f - fr.width(stackSize) * scaleFactor) * inverseScaleFactor);
        int Y = (int) (((float) y + 16.0f - 7.0f * scaleFactor) * inverseScaleFactor);
        graphics.drawString(font, stackSize, X, Y, 16777215);
        st.popPose();
        RenderSystem.enableDepthTest();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (this.recipeBookGui.mouseClicked(mouseX, mouseY, mouseButton) || this.widthTooNarrow && this.recipeBookGui.isVisible()) {
            return true;
        }
        if (scrollSlider.mouseClicked(mouseX, mouseY, mouseButton)) {
            draggingScroll = true;
            return true;
        }
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
        if (slotIDUnderMouse > -1) {
            StoredItemStack stack = getMenu().storageSlotList.get(slotIDUnderMouse).stack();
            boolean hasStack = stack != null && stack.getQuantity() > 0;
            boolean carrying = !menu.getCarried().isEmpty();
            if (mouseButton == 1 && hasShiftDown()) {
                storageSlotClick(stack, PULL_ONE, hasControlDown());
                return true;
            } else if (mouseButton == 1 && (carrying || hasStack)) {
                storageSlotClick(stack, hasControlDown() ? GET_QUARTER : GET_HALF, false);
                if (!carrying) {
                    return true;
                }
            } else if (mouseButton == 0 && carrying) {
                storageSlotClick(null, PULL_OR_PUSH_STACK, false);
            } else if (mouseButton == 0 && hasStack) {
                storageSlotClick(stack, hasShiftDown() ? SHIFT_PULL : PULL_OR_PUSH_STACK, false);
                return true;
            }
        } else if (GLFW.glfwGetKey(mc.getWindow().getWindow(), GLFW.GLFW_KEY_SPACE) != GLFW.GLFW_RELEASE) {
            storageSlotClick(null, SPACE_CLICK, false);
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingScroll) {
            return scrollSlider.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingScroll = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
        boolean flag = mouseX < guiLeftIn || mouseY < guiTopIn || mouseX >= guiLeftIn + this.imageWidth || mouseY >= guiTopIn + this.imageHeight;
        return this.recipeBookGui.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, mouseButton) && flag;
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    @Override
    protected void slotClicked(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.slotClicked(slotIn, slotId, mouseButton, type);
        this.recipeBookGui.slotClicked(slotIn);
    }

    protected void storageSlotClick(StoredItemStack slotStack, CraftingTerminalMenu.SlotAction act, boolean pullOne) {
        Networking.sendToServer(new ClientSlotClick(pullOne, Optional.ofNullable(slotStack), act));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_S && hoveredSlot != null) {
            ItemStack itemstack = null;

            for (int i = 0; i < this.ghostRecipe.size(); ++i) {
                GhostRecipe.GhostIngredient ghostrecipe$ghostingredient = this.ghostRecipe.get(i);
                int j = ghostrecipe$ghostingredient.getX();
                int k = ghostrecipe$ghostingredient.getY();
                if (j == hoveredSlot.x && k == hoveredSlot.y) {
                    itemstack = ghostrecipe$ghostingredient.getItem();
                }
            }
            if (itemstack != null) {
                this.searchField.setValue(itemstack.getHoverName().getString());
                this.searchField.setFocused(false);
                return true;
            }
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }

        if (!(keyCode >= GLFW.GLFW_KEY_LEFT_SHIFT && keyCode <= GLFW.GLFW_KEY_MENU) && !searchField.isFocused() || !searchField.active) {
            var prevFocus = this.getFocused();
            this.clearFocus();
            this.setFocused(searchField);
            searchField.active = true;
            if (!searchField.keyPressed(keyCode, scanCode, modifiers)) {
                searchField.active = false;
                this.clearFocus();
                this.setFocused(prevFocus);
                return false;
            }
            return true;
        }

        return this.searchField.canConsumeInput() && this.searchField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (super.charTyped(codePoint, modifiers)) {
            return true;
        }

        if (!searchField.isFocused() || !searchField.active) {
            this.clearFocus();
            this.setFocused(searchField);
            searchField.active = true;
            this.searchField.setValue("");
            if (this.searchField.onClear != null) {
                this.searchField.onClear.apply("");
            }
            return searchField.charTyped(codePoint, modifiers);
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!scrollSlider.active) {
            return false;
        }
        return scrollSlider.mouseScrolled(mouseX, mouseY, scrollX, Config.INVERT_LECTERN_SCROLLING.getAsBoolean() ? scrollY : -scrollY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(expanded ? gui_expanded : gui, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        DocClientUtils.blit(graphics, DocAssets.STORAGE_SEARCH_PAPER, this.leftPos + 102, this.topPos + 3);
    }

    public void receiveServerSettings(String searchString, List<String> tabs) {
        menu.receiveServerSearchString(searchString);
        refreshItemList = true;
        RuleBasedCollator collator = (RuleBasedCollator) Collator.getInstance(Locale.ROOT);
        collator.setNumericCollation(true);
        tabNames = tabs.stream().distinct().sorted(collator).limit(11).toList();
        this.onPacket();
    }

    public void updateItems(List<StoredItemStack> items) {
        menu.updateItems(items);
        refreshItemList = true;

        if (noSort) {
            itemsUnsorted.forEach(s -> {
                StoredItemStack mapStack = menu.itemMap.get(s);
                s.setCount(mapStack != null ? mapStack.getQuantity() : 0L);
            });
        } else {
            itemsUnsorted = new ArrayList<>(menu.itemList);
        }
    }

    private FakeSlot fakeSlotUnderMouse = new FakeSlot();

    @Override
    public Slot getSlotUnderMouse() {
        Slot s = super.getSlotUnderMouse();
        if (s != null) return s;
        if (slotIDUnderMouse > -1) {
            SlotStorage slot = getMenu().storageSlotList.get(slotIDUnderMouse);
            if (slot.stack == null) {
                return null;
            }
            fakeSlotUnderMouse.container.setItem(0, slot.stack.getStack());
            return fakeSlotUnderMouse;
        }
        return null;
    }

    @Override
    public void recipesUpdated() {
        this.recipeBookGui.recipesUpdated();
    }

    @Override
    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookGui;
    }

    private void clearGrid() {
        this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 0);
    }
}
