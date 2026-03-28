package com.hollingsworth.arsnouveau.client.container;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.lwjgl.glfw.GLFW;

public class CraftingTerminalScreen extends AbstractStorageTerminalScreen<CraftingTerminalMenu> implements RecipeUpdateListener {
    private static final Identifier gui = ArsNouveau.prefix("textures/gui/crafting_terminal.png");
    private static final Identifier gui_expanded = ArsNouveau.prefix("textures/gui/crafting_terminal_expanded.png");
    // TODO: Recipe book removed - RecipeBookComponent requires AbstractCraftingMenu, but our menu extends RecipeBookMenu via StorageTerminalMenu
    // private final RecipeBookComponent recipeBookGui;
    private boolean widthTooNarrow;
    private static final Identifier RECIPE_BUTTON_TEXTURE = ArsNouveau.prefix("textures/gui/recipe_book.png");
    private static final Identifier CLEAR_CRAFT_TEXTURE = ArsNouveau.prefix("textures/gui/craft_clear.png");
    private static final Identifier EXPAND_TEXTURE = ArsNouveau.prefix("textures/gui/expand_inventory.png");
    private static final Identifier COLLAPSE_TEXTURE = ArsNouveau.prefix("textures/gui/collapse_inventory.png");
    // private EditBox recipeBookSearch; // TODO: unused without recipe book
    // GhostRecipe removed - replaced by GhostSlots in 1.21.11, but not used without recipe book

    public GuiImageButton btnClr;

    public GuiImageButton btnRecipeBook;
    public GuiImageButton btnExpand;
    public GuiImageButton btnCollapse;

    public CraftingTerminalScreen(CraftingTerminalMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        // TODO: RecipeBookComponent removed - no compatible subclass for non-AbstractCraftingMenu menu type
    }

    @Override
    public Identifier getGui() {
        return expanded ? gui_expanded : gui;
    }

    @Override
    protected void onUpdateSearch(String text) {
        // TODO: recipe book search sync removed
    }

    @Override
    protected void init() {
        imageWidth = 202;
        imageHeight = 248;
        rowCount = 3;
        super.init();
        this.widthTooNarrow = this.width < 379;
        // TODO: recipeBookGui.init(...) removed
        int recipeButtonY = this.height / 2 - 34;
        int collapseButtonY = this.height / 2 + 23;
        btnClr = new GuiImageButton(leftPos + 86, recipeButtonY, 0, 0, 9, 9, 9, 9, CLEAR_CRAFT_TEXTURE, b -> clearGrid());
        btnExpand = new GuiImageButton(leftPos + 86, recipeButtonY - 12, 0, 0, 14, 3, 14, 3, EXPAND_TEXTURE, b -> expandScreen());
        btnCollapse = new GuiImageButton(leftPos + 86, collapseButtonY, 0, 0, 14, 3, 14, 3, COLLAPSE_TEXTURE, b -> collapseScreen());
        addRenderableWidget(btnClr);
        addRenderableWidget(btnCollapse);
        addRenderableWidget(btnExpand);
        btnRecipeBook = addRenderableWidget(new GuiImageButton(this.leftPos + 98, recipeButtonY, 0, 0, 9, 9, 9, 9, RECIPE_BUTTON_TEXTURE, (thisButton) -> {
            // TODO: recipe book toggle removed
        }));
        btnRecipeBook.visible = Config.SHOW_RECIPE_BOOK.get();
        onExpandedChanged(expanded);
        onPacket();
    }

    @Override
    protected void onExpandedChanged(boolean expanded) {
        this.expanded = expanded;
        super.onExpandedChanged(expanded);
        btnCollapse.visible = this.expanded;
        btnExpand.visible = !this.expanded;
        btnClr.visible = !this.expanded;
        btnRecipeBook.visible = !this.expanded && Config.SHOW_RECIPE_BOOK.get();
    }

    public void collapseScreen() {
        rowCount = 3;
        this.expanded = false;
        onExpandedChanged(false);
        syncSortSettings();
    }

    public void expandScreen() {
        rowCount = 7;
        this.expanded = true;
        onExpandedChanged(true);
        syncSortSettings();
    }

    @Override
    public void containerTick() {
        super.containerTick();
        // TODO: recipeBookGui.tick() removed
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        // TODO: recipeBookGui.render and renderGhostRecipe removed
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean consumed) {
        return super.mouseClicked(event, consumed);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn) {
        return mouseX < guiLeftIn || mouseY < guiTopIn || mouseX >= guiLeftIn + this.imageWidth || mouseY >= guiTopIn + this.imageHeight;
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    @Override
    protected void slotClicked(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.slotClicked(slotIn, slotId, mouseButton, type);
        // TODO: recipeBookGui.slotClicked removed
    }

    @Override
    public void recipesUpdated() {
        // TODO: recipe book integration removed
    }

    @Override
    public void fillGhostRecipe(RecipeDisplay display) {
        // TODO: GhostSlots integration removed - no compatible RecipeBookComponent for our menu type
    }

    // TODO: getRecipeBookComponent() removed - no longer part of RecipeUpdateListener in 1.21.11

    private void clearGrid() {
        this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 0);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_S && hoveredSlot != null) {
            // TODO: ghost recipe slot search removed - GhostRecipe API replaced by GhostSlots in 1.21.11
        }
        return super.keyPressed(event);
    }
}
