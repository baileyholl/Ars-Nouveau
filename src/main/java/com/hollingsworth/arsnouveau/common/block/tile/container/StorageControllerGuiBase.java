/*
 * MIT License
 *
 * Copyright 2020 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.hollingsworth.arsnouveau.common.block.tile.container;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.network.MessageRequestStacks;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class StorageControllerGuiBase<T extends StorageControllerContainerBase> extends AbstractContainerScreen<T> implements IStorageControllerGui, IStorageControllerGuiContainer, ContainerListener {
    //region Fields
    public static final int ORDER_AREA_OFFSET = 48;
    protected static final ResourceLocation BACKGROUND = new ResourceLocation(ArsNouveau.MODID,
            "textures/gui/storage_controller_droparea.png");
    protected static final ResourceLocation BUTTONS = new ResourceLocation(ArsNouveau.MODID, "textures/gui/buttons.png");
    protected static final String TRANSLATION_KEY_BASE = "gui." + ArsNouveau.MODID + ".storage_controller";
    public List<ItemStack> stacks;
    public List<MachineReference> linkedMachines;
    public IStorageControllerContainer storageControllerContainer;
    public int usedSlots;
    public int maxSlots;
    public StorageControllerGuiMode guiMode = StorageControllerGuiMode.INVENTORY;
    protected ItemStack stackUnderMouse = ItemStack.EMPTY;
    protected EditBox searchBar;
    protected List<ItemSlotWidget> itemSlots = new ArrayList<>();
    protected List<MachineSlotWidget> machineSlots = new ArrayList<>();
    protected Button clearTextButton;
    protected Button clearRecipeButton;
    protected Button sortTypeButton;
    protected Button sortDirectionButton;
    protected Button jeiSyncButton;
    protected Button autocraftingModeButton;
    protected Button inventoryModeButton;
    protected LabelWidget storageSpaceLabel;
    protected int rows;
    protected int columns;
    public static final int MOUSE_BUTTON_LEFT = 0;
    public static final int MOUSE_BUTTON_RIGHT = 1;
    protected int currentPage;
    protected int totalPages;

    protected boolean forceFocus;
    protected long lastClick;
    //endregion Fields

    //region Initialization
    public StorageControllerGuiBase(T container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name);
        this.storageControllerContainer = container;
        this.storageControllerContainer.getOrderSlot().addListener(this);

        //size of the gui texture
        this.imageWidth = 224;
        this.imageHeight = 256;

        this.rows = 4;
        this.columns = 9;

        this.currentPage = 1;
        this.totalPages = 1;

        this.stacks = new ArrayList<>();
        this.linkedMachines = new ArrayList<>();

        this.lastClick = System.currentTimeMillis();

        Networking.sendToServer(new MessageRequestStacks());
    }
    //endregion Initialization

    //region Getter / Setter
    protected abstract boolean isGuiValid();

    protected abstract BlockPos getEntityPosition();

    public abstract SortDirection getSortDirection();

    public abstract void setSortDirection(SortDirection sortDirection);

    public abstract SortType getSortType();

    public abstract void setSortType(SortType sortType);

    //endregion Getter / Setter

    //region Overrides
    @Override
    public Font getFontRenderer() {
        return this.font;
    }

    @Override
    public void drawGradientRect(PoseStack poseStack, int left, int top, int right, int bottom, int startColor,
                                 int endColor) {
        super.fillGradient(poseStack, left, top, right, bottom, startColor, endColor);
    }

    @Override
    public boolean isPointInRegionController(int rectX, int rectY, int rectWidth, int rectHeight, double pointX,
                                             double pointY) {
        return this.isHovering(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
    }

    @Override
    public void renderToolTip(PoseStack poseStack, ItemStack stack, int x, int y) {
        super.renderTooltip(poseStack, stack, x, y);
    }

    @Override
    public void renderToolTip(PoseStack poseStack, MachineReference machine, int x, int y) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(machine.getInsertItemStack().getDisplayName());
        if (machine.customName != null) {
            tooltip.add(Component.literal(ChatFormatting.GRAY.toString() +
                    ChatFormatting.BOLD + machine.customName +
                    ChatFormatting.RESET));
        }

        if (this.minecraft.player.level.dimension() != machine.insertGlobalPos.getDimensionKey())
            tooltip.add(Component.translatable(ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC +
                    machine.insertGlobalPos.getDimensionKey().location() +
                    ChatFormatting.RESET));
        this.renderComponentTooltip(poseStack, tooltip, x, y);
    }

    @Override
    public void setStacks(List<ItemStack> stacks) {
        this.stacks = stacks;
    }

    @Override
    public void setUsedSlots(int slots) {
        this.usedSlots = slots;
    }

    @Override
    public void markDirty() {
        this.init();
    }

    @Override
    public void setMaxSlots(int slots) {
        this.maxSlots = slots;
    }

    @Override
    public void setLinkedMachines(List<MachineReference> machines) {
        this.linkedMachines = machines;
    }

    @Override
    public void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2 - ORDER_AREA_OFFSET;
        this.topPos = Math.max(0, (this.height - this.imageHeight) / 2);

        this.clearWidgets();

        int searchBarLeft = 9 + ORDER_AREA_OFFSET;
        int searchBarTop = 7;
        boolean focus = false;

        String searchBarText = "";
        if (this.searchBar != null) {
            searchBarText = this.searchBar.getValue();
            if (this.searchBar.isFocused()) {
                focus = true;
            }
        }


        this.searchBar = new EditBox(this.font, this.leftPos + searchBarLeft,
                this.topPos + searchBarTop, 90, this.font.lineHeight, Component.literal("search"));
        this.searchBar.setMaxLength(30);

        this.searchBar.setBordered(false);
        this.searchBar.setVisible(true);
        this.searchBar.setTextColor(Color.WHITE.getRGB());
        this.searchBar.setFocus(focus);

        this.searchBar.setValue(searchBarText);
//        if (JeiSettings.isJeiLoaded() && JeiSettings.isJeiSearchSynced()) {
//            this.searchBar.setValue(JeiAccess.getFilterText());
//        }

        int storageSpaceInfoLabelLeft = 186;
        int storageSpaceInfoLabelTop = 115;
        this.storageSpaceLabel =
                new LabelWidget(this.leftPos + storageSpaceInfoLabelLeft, this.topPos + storageSpaceInfoLabelTop, true,
                        -1, 2, 0x404040);
        this.storageSpaceLabel
                .addLine(I18n.get(TRANSLATION_KEY_BASE + ".space_info_label", this.usedSlots, this.maxSlots), false);
        this.addRenderableWidget(this.storageSpaceLabel);
        this.initButtons();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);

        this.renderTooltip(poseStack, mouseX, mouseY);
        if (!this.isGuiValid()) {
            this.minecraft.player.closeContainer();
            return;
        }
        try {
            this.drawTooltips(poseStack, mouseX, mouseY);
        } catch (Throwable e) {
//            Occultism.LOGGER.error("Error drawing tooltip.", e);
        }

        //previous content of drawGuiForegroundLayer
        if (!this.isGuiValid()) {
            return;
        }
        if (this.forceFocus) {
            this.searchBar.setFocus(true);
            if (this.searchBar.isFocused()) {
                this.forceFocus = false;
            }
        }
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        //prevent default labels being rendered
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX,
                            int mouseY) {
        if (!this.isGuiValid()) {
            return;
        }

        this.drawBackgroundTexture(poseStack);

        switch (this.guiMode) {
            case INVENTORY:
                this.drawItems(poseStack, partialTicks, mouseX, mouseY);
                break;
            case AUTOCRAFTING:
                this.drawMachines(poseStack, partialTicks, mouseX, mouseY);
                break;
        }
        this.searchBar.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.searchBar.setFocus(false);

        //right mouse button clears search bar
        if (this.isPointInSearchbar(mouseX, mouseY)) {
            this.searchBar.setFocus(true);

            if (mouseButton == MOUSE_BUTTON_RIGHT) {
                this.clearSearch();
            }
        } else if (this.guiMode == StorageControllerGuiMode.INVENTORY) {
            ItemStack stackCarriedByMouse = this.minecraft.player.containerMenu.getCarried();
            if (!this.stackUnderMouse.isEmpty() &&
                    (mouseButton == MOUSE_BUTTON_LEFT || mouseButton == MOUSE_BUTTON_RIGHT) &&
                    stackCarriedByMouse.isEmpty() && this.canClick()) {
                //take item out of storage
                Networking.sendToServer(new MessageTakeItem(this.stackUnderMouse, mouseButton, Screen.hasShiftDown(),
                                Screen.hasControlDown()));
                this.lastClick = System.currentTimeMillis();
            } else if (!stackCarriedByMouse.isEmpty() && this.isPointInItemArea(mouseX, mouseY) && this.canClick()) {
                //put item into storage
                Networking.sendToServer(new MessageInsertMouseHeldItem(mouseButton));
                this.lastClick = System.currentTimeMillis();
            }
        } else if (this.guiMode == StorageControllerGuiMode.AUTOCRAFTING) {
//            for (MachineSlotWidget slot : this.machineSlots) {
//                if (slot.isMouseOverSlot(mouseX, mouseY)) {
//                    if (mouseButton == InputUtil.MOUSE_BUTTON_LEFT) {
//                        ItemStack orderStack = this.storageControllerContainer.getOrderSlot().getItem(0);
//                        if (Screen.hasShiftDown()) {
//                            long time = System.currentTimeMillis() + 5000;
//                            Occultism.SELECTED_BLOCK_RENDERER.selectBlock(slot.getMachine().insertGlobalPos.getPos(), time, Color.GREEN);
//                            Occultism.SELECTED_BLOCK_RENDERER.selectBlock(slot.getMachine().extractGlobalPos.getPos(), time, Color.YELLOW);
//                        } else if (!orderStack.isEmpty()) {
//                            //this message both clears the order slot and creates the order
//                            GlobalBlockPos storageControllerPos = this.storageControllerContainer.getStorageControllerGlobalBlockPos();
//                            if (storageControllerPos != null) {
//                                OccultismPackets.sendToServer(new MessageRequestOrder(
//                                        storageControllerPos,
//                                        slot.getMachine().insertGlobalPos, orderStack));
//                            } else {
//                                Occultism.LOGGER.warn("Linked Storage Controller Position null.");
//                            }
//
//                            //now switch back gui mode.
//                            this.guiMode = StorageControllerGuiMode.INVENTORY;
//                        }
//                    }
//                    break;
//                }
//            }
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_) {
        if (this.searchBar.isFocused() && this.searchBar.keyPressed(keyCode, scanCode, p_keyPressed_3_)) {
//            if (JeiSettings.isJeiLoaded() && JeiSettings.isJeiSearchSynced()) {
//                JeiAccess.setFilterText(this.searchBar.getValue());
//            }
            return true;
        }

        //Handle inventory key down in search bar:
        if (this.searchBar.isFocused()) {
            InputConstants.Key mouseKey = InputConstants.getKey(keyCode, scanCode);
            if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void containerChanged(Container inventory) {
        if (inventory == this.storageControllerContainer.getOrderSlot() && !inventory.getItem(0).isEmpty()) {
            this.guiMode = StorageControllerGuiMode.AUTOCRAFTING;
            this.init();
        }
    }

    @Override
    public boolean mouseScrolled(double x, double y, double mouseButton) {
        super.mouseScrolled(x, y, mouseButton);

        //check if mouse is over item area, then handle scrolling
        if (this.isPointInItemArea(x, y)) {
            if (mouseButton > 0 && this.currentPage > 1) {
                this.currentPage--;
            }
            if (mouseButton < 0 && this.currentPage < this.totalPages) {
                this.currentPage++;
            }
        }
        return true;
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        if (this.searchBar.isFocused() && this.searchBar.charTyped(typedChar, keyCode)) {
            Networking.sendToServer(new MessageRequestStacks());
//            if (JeiSettings.isJeiLoaded() && JeiSettings.isJeiSearchSynced()) {
//                JeiAccess.setFilterText(this.searchBar.getValue());
//            }
        }

        return false;
    }
    //endregion Overrides

    //region Methods
    public void initButtons() {
        int controlButtonSize = 12;

        int clearRecipeButtonLeft = 93 + ORDER_AREA_OFFSET;
        int clearRecipeButtonTop = 112;
        this.clearRecipeButton = new SizedImageButton(this.leftPos + clearRecipeButtonLeft,
                this.topPos + clearRecipeButtonTop, controlButtonSize, controlButtonSize, 0, 196, 28, 28, 28, 256, 256,
                BUTTONS, (button) -> {
            Networking.sendToServer(new MessageClearCraftingMatrix());
            Networking.sendToServer(new MessageRequestStacks());
            this.init();
        });
        this.addRenderableWidget(this.clearRecipeButton);

        int controlButtonTop = 5;

        int clearTextButtonLeft = 99 + ORDER_AREA_OFFSET;
        this.clearTextButton = new SizedImageButton(this.leftPos + clearTextButtonLeft,
                this.topPos + controlButtonTop, controlButtonSize, controlButtonSize, 0, 196, 28, 28, 28, 256, 256,
                BUTTONS, (button) -> {
            this.clearSearch();
            this.forceFocus = true;
            this.init();
        });
        this.addRenderableWidget(this.clearTextButton);


        int sortTypeOffset = this.getSortType().getValue() * 28;
        this.sortTypeButton = new SizedImageButton(this.leftPos + clearTextButtonLeft + controlButtonSize + 3,
                this.topPos + controlButtonTop, controlButtonSize, controlButtonSize, 0, sortTypeOffset, 28, 28, 28,
                256, 256, BUTTONS, (button) -> {
            this.setSortType(this.getSortType().next());
            Networking.sendToServer(
                    new MessageSortItems(this.getEntityPosition(), this.getSortDirection(), this.getSortType()));
            this.init();
        });
        this.addRenderableWidget(this.sortTypeButton);

        int sortDirectionOffset = 84 + (1 - this.getSortDirection().getValue()) * 28;
        this.sortDirectionButton = new SizedImageButton(
                this.leftPos + clearTextButtonLeft + controlButtonSize + 3 + controlButtonSize + 3,
                this.topPos + controlButtonTop, controlButtonSize, controlButtonSize, 0, sortDirectionOffset, 28, 28,
                28, 256, 256, BUTTONS, (button) -> {
            this.setSortDirection(this.getSortDirection().next());
            Networking.sendToServer(
                    new MessageSortItems(this.getEntityPosition(), this.getSortDirection(), this.getSortType()));
            this.init();
        });
        this.addRenderableWidget(this.sortDirectionButton);

//        int jeiSyncOffset = 140 + (JeiSettings.isJeiSearchSynced() ? 0 : 1) * 28;
//        this.jeiSyncButton = new SizedImageButton(
//                this.leftPos + clearTextButtonLeft + controlButtonSize + 3 + controlButtonSize + 3 + controlButtonSize +
//                        3, this.topPos + controlButtonTop, controlButtonSize, controlButtonSize, 0, jeiSyncOffset, 28, 28, 28,
//                256, 256, BUTTONS, (button) -> {
//            JeiSettings.setJeiSearchSync(!JeiSettings.isJeiSearchSynced());
//            this.init();
//        });
//
//        if (JeiSettings.isJeiLoaded())
//            this.addRenderableWidget(this.jeiSyncButton);


        int guiModeButtonTop = 112;
        int guiModeButtonLeft = 27;
        int guiModeButtonHeight = 29;
        int guiModeButtonWidth = 24;

        switch (this.guiMode) {
            case INVENTORY:
                //active tab button for inventory
                this.inventoryModeButton = new SizedImageButton(this.leftPos + guiModeButtonLeft,
                        this.topPos + 112, guiModeButtonWidth, guiModeButtonHeight, 160, 0, 0, guiModeButtonWidth * 2,
                        guiModeButtonHeight * 2, 256, 256, BUTTONS, (button) -> {
                    this.guiMode = StorageControllerGuiMode.INVENTORY;
                    this.init();
                });
                //inactive tab button for crafting
                this.autocraftingModeButton = new SizedImageButton(this.leftPos + guiModeButtonLeft,
                        this.topPos + guiModeButtonTop + guiModeButtonHeight, guiModeButtonWidth, guiModeButtonHeight,
                        160, 174, 0, guiModeButtonWidth * 2, guiModeButtonHeight * 2, 256, 256, BUTTONS,
                        (button) -> {
                            this.guiMode = StorageControllerGuiMode.AUTOCRAFTING;
                            this.init();
                        });
                break;
            case AUTOCRAFTING:
                //inactive tab button for inventory
                this.inventoryModeButton = new SizedImageButton(this.leftPos + guiModeButtonLeft,
                        this.topPos + 112, guiModeButtonWidth, guiModeButtonHeight, 160, 58, 0, guiModeButtonWidth * 2,
                        guiModeButtonHeight * 2, 256, 256, BUTTONS, (button) -> {
                    this.guiMode = StorageControllerGuiMode.INVENTORY;
                    this.init();
                });
                //active tab button for crafting
                this.autocraftingModeButton = new SizedImageButton(this.leftPos + guiModeButtonLeft,
                        this.topPos + guiModeButtonTop + guiModeButtonHeight, guiModeButtonWidth, guiModeButtonHeight,
                        160, 116, 0, guiModeButtonWidth * 2, guiModeButtonHeight * 2, 256, 256, BUTTONS,
                        (button) -> {
                            this.guiMode = StorageControllerGuiMode.AUTOCRAFTING;
                            this.init();
                        });
                break;
        }
        this.addRenderableWidget(this.inventoryModeButton);
        this.addRenderableWidget(this.autocraftingModeButton);
    }

    protected void drawItems(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        List<ItemStack> stacksToDisplay = this.applySearchToItems();
        this.sortItemStacks(stacksToDisplay);
        this.buildPage(stacksToDisplay);
        this.buildItemSlots(stacksToDisplay);
        this.drawItemSlots(poseStack, mouseX, mouseY);
    }

    protected void drawMachines(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        List<MachineReference> machinesToDisplay = this.applySearchToMachines();
        this.sortMachines(machinesToDisplay);
        this.buildPage(machinesToDisplay);
        this.buildMachineSlots(machinesToDisplay);
        this.drawMachineSlots(poseStack, mouseX, mouseY);
    }

    protected boolean canClick() {
        return System.currentTimeMillis() > this.lastClick + 100L;
    }

    protected boolean isPointInSearchbar(double mouseX, double mouseY) {
        return this.isHovering(this.searchBar.x - this.leftPos, this.searchBar.y - this.topPos,
                this.searchBar.getWidth() - 5, this.font.lineHeight + 6, mouseX, mouseY);
    }

    protected boolean isPointInItemArea(double mouseX, double mouseY) {
        int itemAreaHeight = 82;
        int itemAreaWidth = 160;
        int itemAreaTop = 24;
        int itemAreaLeft = 8 + ORDER_AREA_OFFSET;
        return mouseX > (this.leftPos + itemAreaLeft) && mouseX < (this.leftPos + itemAreaWidth + itemAreaLeft) &&
                mouseY > (this.topPos + itemAreaTop) && mouseY < (this.topPos + itemAreaTop + itemAreaHeight);
    }

    protected void drawTooltips(PoseStack poseStack, int mouseX, int mouseY) {
        switch (this.guiMode) {
            case INVENTORY:
                for (ItemSlotWidget s : this.itemSlots) {
                    if (s != null && s.isMouseOverSlot(mouseX, mouseY)) {
                        s.drawTooltip(poseStack, mouseX, mouseY);
                    }
                }
                break;
            case AUTOCRAFTING:
                for (MachineSlotWidget s : this.machineSlots) {
                    if (s != null && s.isMouseOverSlot(mouseX, mouseY)) {
                        s.drawTooltip(poseStack, mouseX, mouseY);
                    }
                }
                break;
        }

        if (this.isPointInSearchbar(mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            if (!Screen.hasShiftDown()) {
                tooltip.add(Component.translatable(TRANSLATION_KEY_BASE + ".shift"));
            } else {
                switch (this.guiMode) {
                    case INVENTORY:
                        tooltip.add(Component.translatable(TRANSLATION_KEY_BASE + ".search.tooltip@"));
                        tooltip.add(Component.translatable(TRANSLATION_KEY_BASE + ".search.tooltip#"));
                        tooltip.add(Component.translatable(TRANSLATION_KEY_BASE + ".search.tooltip$"));
                        break;
                    case AUTOCRAFTING:
                        tooltip.add(Component.translatable(TRANSLATION_KEY_BASE + ".search.machines.tooltip@"));
                        break;
                }
                tooltip.add(Component.translatable(TRANSLATION_KEY_BASE + ".search.tooltip_rightclick"));
            }
            this.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY);
        }
        if (this.clearTextButton != null && this.clearTextButton.isMouseOver(mouseX, mouseY)) {
            this.renderComponentTooltip(poseStack,
                    Lists.newArrayList(Component.translatable(TRANSLATION_KEY_BASE + ".search.tooltip_clear")),
                    mouseX, mouseY);
        }
        if (this.sortTypeButton != null && this.sortTypeButton.isMouseOver(mouseX, mouseY)) {
            String translationKey = "";
            switch (this.guiMode) {
                case INVENTORY:
                    translationKey =
                            TRANSLATION_KEY_BASE + ".search.tooltip_sort_type_" + this.getSortType().getSerializedName();
                    break;
                case AUTOCRAFTING:
                    translationKey =
                            TRANSLATION_KEY_BASE + ".search.machines.tooltip_sort_type_" +
                                    this.getSortType().getSerializedName();
                    break;
            }
            this.renderTooltip(poseStack, Component.translatable(translationKey), mouseX, mouseY);
        }
        if (this.sortDirectionButton != null && this.sortDirectionButton.isMouseOver(mouseX, mouseY)) {
            this.renderTooltip(poseStack, Component.translatable(
                            TRANSLATION_KEY_BASE + ".search.tooltip_sort_direction_" + this.getSortDirection().getSerializedName()),
                    mouseX, mouseY);
        }
//        if (this.jeiSyncButton != null && this.jeiSyncButton.isMouseOver(mouseX, mouseY)) {
//            this.renderTooltip(poseStack, Component.translatable(
//                            TRANSLATION_KEY_BASE + ".search.tooltip_jei_" +
//                                    (JeiSettings.isJeiSearchSynced() ? "on" : "off")),
//                    mouseX, mouseY);
//        }
    }

    protected void drawBackgroundTexture(PoseStack poseStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    protected void drawItemSlots(PoseStack poseStack, int mouseX, int mouseY) {
        this.stackUnderMouse = ItemStack.EMPTY;
        for (ItemSlotWidget slot : this.itemSlots) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            slot.drawSlot(poseStack, mouseX, mouseY);
            if (slot.isMouseOverSlot(mouseX, mouseY)) {
                this.stackUnderMouse = slot.getStack();
                //        break;
            }
        }
    }

    protected void buildItemSlots(List<ItemStack> stacksToDisplay) {

        int itemAreaLeft = 8 + ORDER_AREA_OFFSET;
        int itemAreaTop = 24;

        this.itemSlots = new ArrayList<>();
        int index = (this.currentPage - 1) * (this.columns);
        for (int row = 0; row < this.rows; row++) {
            if (index >= stacksToDisplay.size()) {
                break;
            }
            for (int col = 0; col < this.columns; col++) {
                if (index >= stacksToDisplay.size()) {
                    break;
                }
                this.itemSlots
                        .add(new ItemSlotWidget(this, stacksToDisplay.get(index),
                                this.leftPos + itemAreaLeft + col * 18,
                                this.topPos + itemAreaTop + row * 18, stacksToDisplay.get(index).getCount(),
                                this.leftPos, this.topPos, true));
                index++;
            }
        }
    }

    protected void buildPage(List<?> objectsToDisplay) {
        this.totalPages = objectsToDisplay.size() / this.columns;
        if (objectsToDisplay.size() % this.columns != 0) {
            this.totalPages++;
        }
        this.totalPages -= (this.rows - 1);
        if (this.totalPages < 1) {
            this.totalPages = 1;
        }
        if (this.currentPage < 1) {
            this.currentPage = 1;
        }
        if (this.currentPage > this.totalPages) {
            this.currentPage = this.totalPages;
        }
    }

    protected void sortItemStacks(List<ItemStack> stacksToDisplay) {
        stacksToDisplay.sort(new Comparator<ItemStack>() {

            //region Fields
            final int direction = StorageControllerGuiBase.this.getSortDirection().isDown() ? -1 : 1;
            //endregion Fields

            //region Overrides
            @Override
            public int compare(ItemStack a, ItemStack b) {
                switch (StorageControllerGuiBase.this.getSortType()) {
                    case AMOUNT:
                        return Integer.compare(b.getCount(), a.getCount()) * this.direction;
                    case NAME:
                        return a.getDisplayName().getString()
                                .compareToIgnoreCase(b.getDisplayName().getString()) *
                                this.direction;
                    case MOD:
                        return TextUtil.getModNameForGameObject(a.getItem())
                                .compareToIgnoreCase(TextUtil.getModNameForGameObject(b.getItem())) *
                                this.direction;
                }
                return 0;
            }
            //endregion Overrides
        });
    }

    protected List<ItemStack> applySearchToItems() {
        String searchText = this.searchBar.getValue();

        if (!searchText.equals("")) {
            List<ItemStack> stacksToDisplay = new ArrayList<>();
            for (ItemStack stack : this.stacks) {
                if (this.itemMatchesSearch(stack))
                    stacksToDisplay.add(stack);
            }
            return stacksToDisplay;
        }
        return new ArrayList<>(this.stacks);
    }

    protected List<MachineReference> applySearchToMachines() {
        String searchText = this.searchBar.getValue();

        if (!searchText.equals("")) {
            List<MachineReference> machinesToDisplay = new ArrayList<>();
            for (MachineReference machine : this.linkedMachines) {
                if (this.machineMatchesSearch(machine))
                    machinesToDisplay.add(machine);
            }
            return machinesToDisplay;
        }

        return new ArrayList<>(this.linkedMachines);
    }

    protected boolean itemMatchesSearch(ItemStack stack) {
        String searchText = this.searchBar.getValue();
        if (searchText.startsWith("@")) {
            String name = TextUtil.getModNameForGameObject(stack.getItem());
            return name.toLowerCase().contains(searchText.toLowerCase().substring(1));
        } else if (searchText.startsWith("#")) {
            List<String> tooltip = stack.getTooltipLines(this.minecraft.player, TooltipFlag.Default.NORMAL).stream()
                    .map(Component::getString).collect(
                            Collectors.toList());
            String tooltipString = Joiner.on(' ').join(tooltip).toLowerCase().trim();
            return tooltipString.toLowerCase().contains(searchText.toLowerCase().substring(1));
        } else if (searchText.startsWith("$")) {
            StringBuilder tagStringBuilder = new StringBuilder();
            stack.getTags().forEach(
                    tag -> tagStringBuilder.append(tag.location()).append(" ")
            );
            return tagStringBuilder.toString().toLowerCase().contains(searchText.toLowerCase().substring(1));
        } else {
            //Note: If search stops working, forge may have re-implemented .getUnformattedComponentText() for translated text components
            return stack.getDisplayName().getString().toLowerCase()
                    .contains(searchText.toLowerCase());
        }
    }

    protected boolean machineMatchesSearch(MachineReference machine) {
        String searchText = this.searchBar.getValue();
        if (searchText.startsWith("@")) {
            String name = TextUtil.getModNameForGameObject(machine.getInsertItem());
            return name.toLowerCase().contains(searchText.toLowerCase().substring(1));
        } else {
            String customName = machine.customName == null ? "" : machine.customName.toLowerCase();
            return machine.getInsertItemStack().getDisplayName().getString().toLowerCase()
                    .contains(searchText.toLowerCase()) ||
                    customName.contains(searchText.toLowerCase().substring(1));
        }
    }

    protected void sortMachines(List<MachineReference> machinesToDisplay) {
        BlockPos entityPosition = this.getEntityPosition();
        ResourceKey<Level> dimensionKey = this.minecraft.player.level.dimension();
        machinesToDisplay.sort(new Comparator<MachineReference>() {

            //region Fields
            final int direction = StorageControllerGuiBase.this.getSortDirection().isDown() ? -1 : 1;
            //endregion Fields

            //region Overrides
            @Override
            public int compare(MachineReference a, MachineReference b) {
                switch (StorageControllerGuiBase.this.getSortType()) {
                    case AMOUNT: //use distance in this case
                        double distanceA =
                                a.insertGlobalPos.getDimensionKey() == dimensionKey ? a.insertGlobalPos.getPos().distSqr(
                                        entityPosition) : Double.MAX_VALUE;
                        double distanceB =
                                b.insertGlobalPos.getDimensionKey() == dimensionKey ? b.insertGlobalPos.getPos().distSqr(
                                        entityPosition) : Double.MAX_VALUE;
                        return Double.compare(distanceB, distanceA) * this.direction;
                    case NAME:
                        return a.getInsertItemStack().getDisplayName().getString()
                                .compareToIgnoreCase(
                                        b.getInsertItemStack().getDisplayName().getString()) *
                                this.direction;
                    case MOD:
                        return TextUtil.getModNameForGameObject(a.getInsertItem())
                                .compareToIgnoreCase(TextUtil.getModNameForGameObject(b.getInsertItem())) *
                                this.direction;
                }
                return 0;
            }
            //endregion Overrides
        });
    }

    protected void buildMachineSlots(List<MachineReference> machinesToDisplay) {

        int itemAreaLeft = 8 + ORDER_AREA_OFFSET;
        int itemAreaTop = 24;

        this.machineSlots = new ArrayList<>();
        int index = (this.currentPage - 1) * (this.columns);
        for (int row = 0; row < this.rows; row++) {
            for (int col = 0; col < this.columns; col++) {
                if (index >= machinesToDisplay.size()) {
                    break;
                }
                this.machineSlots.add(new MachineSlotWidget(this, machinesToDisplay.get(index),
                        this.leftPos + itemAreaLeft + col * 18, this.topPos + itemAreaTop + row * 18, this.leftPos,
                        this.topPos));
                index++;
            }
        }
    }

    protected void drawMachineSlots(PoseStack poseStack, int mouseX, int mouseY) {
        for (MachineSlotWidget slot : this.machineSlots) {
            slot.drawSlot(poseStack, mouseX, mouseY);
        }
    }

    protected void clearSearch() {
        this.searchBar.setValue("");
//        if (JeiSettings.isJeiLoaded() && JeiSettings.isJeiSearchSynced()) {
//            JeiAccess.setFilterText("");
//        }
    }
    
}
