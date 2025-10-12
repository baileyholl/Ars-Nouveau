package com.hollingsworth.arsnouveau.client.container;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonElement;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.NoShadowTextField;
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
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu.SlotAction.*;

public abstract class AbstractStorageTerminalScreen<T extends StorageTerminalMenu> extends AbstractContainerScreen<T> {
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

    protected Minecraft mc = Minecraft.getInstance();

    /**
     * Amount scrolled in Creative mode inventory (0 = top, 1 = bottom)
     */
    protected float currentScroll;
    /**
     * True if the scrollbar is being dragged
     */
    protected boolean isScrolling;
    /**
     * True if the left mouse button was held down last time drawScreen was
     * called.
     */
    private boolean refreshItemList;
    protected boolean wasClicking;
    protected NoShadowTextField searchField;
    protected int slotIDUnderMouse = -1;
    protected int controllMode;
    protected int rowCount;
    protected int searchType;
    protected boolean expanded;
    private String searchLast = "";
    protected boolean loadedSearch = false;
    private StoredItemStack.IStoredItemStackComparator comparator = new StoredItemStack.ComparatorAmount(false);
    protected static final ResourceLocation scrollBall = ArsNouveau.prefix("textures/gui/scroll_ball.png");
    protected static final ResourceLocation tabImages = ArsNouveau.prefix("textures/gui/bookwyrm_storage_tabs.png");
    protected StateButton buttonSortingType;
    protected StateButton buttonDirection;
    protected StateButton buttonSearchType;
    private Comparator<StoredItemStack> sortComp;
    List<String> tabNames = new ArrayList<>();
    public List<StorageTabButton> tabButtons = new ArrayList<>();

    boolean noSort;

    List<StoredItemStack> itemsSorted = new ArrayList<>();
    List<StoredItemStack> itemsUnsorted = new ArrayList<>();

    public AbstractStorageTerminalScreen(T screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
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
        menu.addStorageSlots(expanded);
    }

    protected void onPacket() {
        if (tabNames != null && !tabNames.isEmpty()) {
            for (StorageTabButton tabButton : tabButtons) {
                tabButton.visible = false;
            }
            // Set isAll tab visible
            tabButtons.get(0).visible = true;
            List<String> names = new ArrayList<>(new HashSet<>(tabNames));
            names.sort(String::compareToIgnoreCase);
            for (int i = 0; i < names.size() && i < tabButtons.size(); i++) {
                tabButtons.get(i + 1).visible = true;
                tabButtons.get(i + 1).highlightText = names.get(i);
            }
        }

        if (!loadedSearch && menu.search != null) {
            loadedSearch = true;
            searchField.setValue(menu.search);
            searchField.setFocused(true);
            if (searchField.getValue().isEmpty()) {
                searchField.setSuggestion(Component.translatable("ars_nouveau.spell_book_gui.search").getString());
            } else {
                searchField.setSuggestion("");
            }
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
        clearWidgets();
        scrollTo(0);
        inventoryLabelY = imageHeight - 92;
        super.init();

        this.searchField = new NoShadowTextField(getFont(), this.leftPos + 115, this.topPos + 6, 60, this.getFont().lineHeight, Component.translatable("narrator.ars_nouveau.search"));
        this.searchField.setMaxLength(100);
        this.searchField.setBordered(false);
        this.searchField.setVisible(true);
        this.searchField.setValue(searchLast);
        this.setFocused(this.searchField);
        this.searchField.active = false;
        searchLast = "";
        addRenderableWidget(searchField);

        buttonSortingType = addRenderableWidget(new StorageSettingsButton(leftPos - 17, topPos + 14, 22, 12, 44, 13, 0, ArsNouveau.prefix("textures/gui/sort_type.png"), b -> {
            comparator = StoredItemStack.SortingTypes.VALUES[(comparator.type() + 1) % StoredItemStack.SortingTypes.VALUES.length].create(comparator.isReversed());
            buttonSortingType.state = comparator.type();
            syncSortSettings();
            refreshItemList = true;
        }));

        buttonDirection = addRenderableWidget(new StorageSettingsButton(leftPos - 17, topPos + 29, 22, 12, 44, 13, 0, ArsNouveau.prefix("textures/gui/sort_order.png"), b -> {
            comparator.setReversed(!comparator.isReversed());
            buttonDirection.state = comparator.isReversed() ? 1 : 0;
            syncSortSettings();
            refreshItemList = true;
        }));
        buttonSearchType = addRenderableWidget(new StorageSettingsButton(leftPos - 17, topPos + 44, 22, 12, 44, 13, 0, ArsNouveau.prefix("textures/gui/search_sync.png"), b -> {
            searchType = searchType == 0 ? 1 : 0;
            buttonSearchType.state = searchType;
            syncSortSettings();
        }));
        for (int i = 0; i < 12; i++) {
            var button = addRenderableWidget(new StorageTabButton(leftPos - 13, topPos + 59 + i * 15, 18, 12, 256, 13, i, 0, tabImages, b -> {
                StorageTabButton tabButton = (StorageTabButton) b;
                setSelectedTab(tabButton.state);
                syncSortSettings();
            }));
            button.visible = false;
            if (i == 0) {
                button.isAll = true;
                button.isSelected = true;
            }
            this.tabButtons.add(button);
        }
        updateSearch();
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
                    addStackToClientList(stored);
                }
            }

            Collections.sort(this.itemsSorted, noSort ? sortComp : comparator);
            if (!searchLast.equals(searchString)) {
                this.scrollTo(0);
                this.currentScroll = 0;
                if (searchType == 1) {
                    IAutoFillTerminal.sync(searchString);
                }
                Networking.sendToServer(new ClientSearchPacket(searchString));
                onUpdateSearch(searchString);
            } else {
                this.scrollTo(this.currentScroll);
            }
            refreshItemList = false;
            this.searchLast = searchString;
        }
    }

    public final void scrollTo(float p_148329_1_) {
        int lines = this.getSortSettings() == null || !this.getSortSettings().expanded() ? 3 : 7;
        int i = (this.itemsSorted.size() + 9 - 1) / 9 - lines;
        int j = (int) (p_148329_1_ * i + 0.5D);

        if (j < 0) {
            j = 0;
        }

        for (int k = 0; k < lines; ++k) {
            for (int l = 0; l < 9; ++l) {
                int i1 = l + (k + j) * 9;

                if (i1 >= 0 && i1 < this.itemsSorted.size()) {
                    menu.setSlotContents(l + k * 9, this.itemsSorted.get(i1));
                } else {
                    menu.setSlotContents(l + k * 9, null);
                }
            }
        }
    }

    private void addStackToClientList(StoredItemStack is) {
        this.itemsSorted.add(is);
    }

    @Override
    protected void containerTick() {
        updateSearch();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack st = graphics.pose();
        boolean flag = GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_RELEASE;
        int i = this.leftPos;
        int j = this.topPos;
        int k = i + 187;
        int l = j + 18;
        int i1 = k + 14;
        int j1 = l + rowCount * 18;

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

        if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1) {
            this.isScrolling = this.needsScrollBars();
        }

        if (!flag) {
            this.isScrolling = false;
        }
        this.wasClicking = flag;

        if (this.isScrolling) {
            this.currentScroll = (mouseY - l - 7.5F) / (j1 - l - 15.0F);
            this.currentScroll = Mth.clamp(this.currentScroll, 0.0F, 1.0F);
            this.scrollTo(this.currentScroll);
        }
        super.render(graphics, mouseX, mouseY, partialTicks);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        i = k;
        j = l;
        k = j1;
        graphics.blit(scrollBall, i, j + 3 + (int) ((k - j - 14) * this.currentScroll), 0, 0, 12, 12, 12, 12);


        if (this.menu.getCarried().isEmpty() && slotIDUnderMouse != -1) {
            SlotStorage slot = getMenu().storageSlotList.get(slotIDUnderMouse);
            if (slot.stack() != null) {
                if (slot.stack().getQuantity() > 9999) {
                    ClientInfo.setTooltip(Component.translatable("tooltip.ars_nouveau.amount", slot.stack().getQuantity()));
                }
                graphics.renderTooltip(font, slot.stack().getActualStack(), mouseX, mouseY);
                ClientInfo.setTooltip();
            }
        } else
            this.renderTooltip(graphics, mouseX, mouseY);

        if (buttonSortingType.isHovered()) {
            graphics.renderTooltip(font, Component.translatable("tooltip.ars_nouveau.sorting_" + buttonSortingType.state), mouseX, mouseY);
        }
        if (buttonSearchType.isHovered()) {
            graphics.renderTooltip(font, Component.translatable("tooltip.ars_nouveau.search_" + buttonSearchType.state, IAutoFillTerminal.getHandlerName()), mouseX, mouseY);
        }
        if (buttonDirection.isHovered()) {
            graphics.renderTooltip(font, Component.translatable("tooltip.ars_nouveau.direction_" + buttonDirection.state, IAutoFillTerminal.getHandlerName()), mouseX, mouseY);
        }
        for (StorageTabButton tabButton : tabButtons) {
            if (tabButton.isHovered() && tabButton.isAll) {
                graphics.renderTooltip(font, Component.translatable("tooltip.ars_nouveau.master_tab"), mouseX, mouseY);
            } else if (tabButton.isHovered() && tabButton.highlightText != null) {
                graphics.renderTooltip(font, Component.literal(tabButton.highlightText), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics p_281635_, int mouseX, int mouseY) {
        PoseStack st = p_281635_.pose();
        st.pushPose();
        slotIDUnderMouse = drawSlots(p_281635_, mouseX, mouseY);
        st.popPose();
    }

    protected int drawSlots(GuiGraphics st, int mouseX, int mouseY) {
        StorageTerminalMenu term = getMenu();
        int slotHover = -1;
        for (int i = 0; i < term.storageSlotList.size(); i++) {
            if (drawSlot(st, term.storageSlotList.get(i), mouseX, mouseY)) {
                slotHover = i;
            }
        }
        return slotHover;
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

            drawStackSize(st, getFont(), slot.stack().getQuantity(), i, j);
        }

        if (mouseX >= getGuiLeft() + slot.xPosition() - 1 && mouseY >= getGuiTop() + slot.yPosition() - 1 && mouseX < getGuiLeft() + slot.xPosition() + 17 && mouseY < getGuiTop() + slot.yPosition() + 17) {
            int l = slot.xPosition();
            int t = slot.yPosition();

            renderSlotHighlight(st, l, t, 0);
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
        int X = (int) (((float) x + 0 + 16.0f - fr.width(stackSize) * scaleFactor) * inverseScaleFactor);
        int Y = (int) (((float) y + 0 + 16.0f - 7.0f * scaleFactor) * inverseScaleFactor);
        graphics.drawString(font, stackSize, X, Y, 16777215);
        st.popPose();
        RenderSystem.enableDepthTest();
    }

    protected boolean needsScrollBars() {
        return itemsSorted.size() > rowCount * 9;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
        if (slotIDUnderMouse > -1) {
            SlotStorage slot = getMenu().getSlotByID(slotIDUnderMouse);
            if (isPullOne(mouseButton)) {
                if (slot.stack() != null && slot.stack().getQuantity() > 0) {
                    storageSlotClick(slot.stack(), PULL_ONE, isTransferOne(mouseButton));
                    return true;
                }
                return true;
            } else if (pullHalf(mouseButton)) {
                if (!menu.getCarried().isEmpty()) {
                    storageSlotClick(null, hasControlDown() ? GET_QUARTER : GET_HALF, false);
                } else {
                    if (slot.stack() != null && slot.stack().getQuantity() > 0) {
                        storageSlotClick(slot.stack(), hasControlDown() ? GET_QUARTER : GET_HALF, false);
                        return true;
                    }
                }
            } else if (pullNormal(mouseButton)) {
                if (!menu.getCarried().isEmpty()) {
                    storageSlotClick(null, PULL_OR_PUSH_STACK, false);
                } else {
                    if (slot.stack() != null) {
                        if (slot.stack().getQuantity() > 0) {
                            storageSlotClick(slot.stack(), hasShiftDown() ? SHIFT_PULL : PULL_OR_PUSH_STACK, false);
                            return true;
                        }
                    }
                }
            }
        } else if (GLFW.glfwGetKey(mc.getWindow().getWindow(), GLFW.GLFW_KEY_SPACE) != GLFW.GLFW_RELEASE) {
            storageSlotClick(null, SPACE_CLICK, false);
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void storageSlotClick(StoredItemStack slotStack, StorageTerminalMenu.SlotAction act, boolean pullOne) {
        Networking.sendToServer(new ClientSlotClick(pullOne, Optional.ofNullable(slotStack), act));
    }

    public boolean isPullOne(int mouseButton) {
        return mouseButton == 1 && hasShiftDown();
    }

    public boolean isTransferOne(int mouseButton) {
        return hasShiftDown() && hasControlDown();
    }

    public boolean pullHalf(int mouseButton) {
        return mouseButton == 1;
    }

    public boolean pullNormal(int mouseButton) {
        return mouseButton == 0;
    }


    public Font getFont() {
        return font;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
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
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_, double scrollY) {
        if (!this.needsScrollBars()) {
            return false;
        } else {
            int i = (itemsSorted.size() + 9 - 1) / 9 - 5;
            this.currentScroll = (float) (this.currentScroll + (Config.INVERT_LECTERN_SCROLLING.getAsBoolean() ? -scrollY : scrollY) / i);
            this.currentScroll = Mth.clamp(this.currentScroll, 0.0F, 1.0F);
            this.scrollTo(this.currentScroll);
            return true;
        }
    }

    public abstract ResourceLocation getGui();

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(getGui(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        graphics.blit(ArsNouveau.prefix("textures/gui/search_paper.png"), this.leftPos + 102, this.topPos + 3, 0, 0, 72, 15, 72, 15);
    }

    protected void onUpdateSearch(String text) {
    }

    public void receiveServerSettings(String searchString, List<String> tabs) {
        menu.receiveServerSearchString(searchString);
        refreshItemList = true;
        Set<String> nameSet = new HashSet<>(tabs);
        tabNames = new ArrayList<>(nameSet).subList(0, Math.min(nameSet.size(), 11));
        Collections.sort(tabNames);
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
            SlotStorage slot = getMenu().getSlotByID(slotIDUnderMouse);
            if (slot.stack == null) {
                return null;
            }
            fakeSlotUnderMouse.container.setItem(0, slot.stack.getStack());
            return fakeSlotUnderMouse;
        }
        return null;
    }
}
