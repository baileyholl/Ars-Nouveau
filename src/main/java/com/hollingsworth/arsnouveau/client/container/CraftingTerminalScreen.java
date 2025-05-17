package com.hollingsworth.arsnouveau.client.container;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class CraftingTerminalScreen extends AbstractStorageTerminalScreen<CraftingTerminalMenu> implements RecipeUpdateListener {
	private static final ResourceLocation gui = ArsNouveau.prefix( "textures/gui/crafting_terminal.png");
	private static final ResourceLocation gui_expanded = ArsNouveau.prefix( "textures/gui/crafting_terminal_expanded.png");
	private final RecipeBookComponent recipeBookGui;
	private boolean widthTooNarrow;
	private static final ResourceLocation RECIPE_BUTTON_TEXTURE = ArsNouveau.prefix( "textures/gui/recipe_book.png");
	private static final ResourceLocation CLEAR_CRAFT_TEXTURE = ArsNouveau.prefix( "textures/gui/craft_clear.png");
	private static final ResourceLocation EXPAND_TEXTURE = ArsNouveau.prefix( "textures/gui/expand_inventory.png");
	private static final ResourceLocation COLLAPSE_TEXTURE = ArsNouveau.prefix( "textures/gui/collapse_inventory.png");
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

	@Override
	public ResourceLocation getGui() {
		return expanded ? gui_expanded : gui;
	}

	@Override
	protected void onUpdateSearch(String text) {
		if(IAutoFillTerminal.hasSync() || searchType == 1) {
			if(recipeBookSearch != null) {
				recipeBookSearch.setValue(text);
			}
			recipeBookGui.recipesUpdated();
		}
	}

	@Override
	protected void init() {
		imageWidth = 202;
		imageHeight = 248;
		rowCount = 3;
		super.init();
		this.widthTooNarrow = this.width < 379;
		this.recipeBookGui.init(this.width, this.height + 30, this.minecraft, this.widthTooNarrow, this.menu);
		this.leftPos = this.recipeBookGui.updateScreenPosition(this.width, this.imageWidth);
		addRenderableWidget(recipeBookGui);
		this.setInitialFocus(this.recipeBookGui);
		int recipeButtonY = this.height / 2 - 34;
		int collapseButtonY = this.height / 2 + 23;
		btnClr = new GuiImageButton(leftPos + 86, recipeButtonY, 0,0,9,9,9,9, CLEAR_CRAFT_TEXTURE, b -> clearGrid());
		btnExpand = new GuiImageButton(leftPos + 86, recipeButtonY - 12, 0,0,14,3,14, 3, EXPAND_TEXTURE, b -> expandScreen());
		btnCollapse = new GuiImageButton(leftPos + 86, collapseButtonY, 0,0,14,3,14, 3, COLLAPSE_TEXTURE, b -> collapseScreen());
		addRenderableWidget(btnClr);
		addRenderableWidget(btnCollapse);
		addRenderableWidget(btnExpand);
		btnRecipeBook = addRenderableWidget(new GuiImageButton( this.leftPos + 98, recipeButtonY , 0, 0, 9, 9, 9,9, RECIPE_BUTTON_TEXTURE, (thisButton) -> {
			this.recipeBookGui.initVisuals();
			recipeBookSearch = recipeBookGui.searchBox;

			this.recipeBookGui.toggleVisibility();
			this.leftPos = this.recipeBookGui.updateScreenPosition(this.width, this.imageWidth);
			((GuiImageButton)thisButton).setPosition(this.leftPos + 98, recipeButtonY);

			super.searchField.setX(this.leftPos + 115);
			btnClr.setX(this.leftPos + 86);
			buttonSortingType.setX(leftPos - 18);
			buttonDirection.setX(leftPos - 18);
			buttonSearchType.setX(leftPos - 18);

			btnCollapse.setX(leftPos + 86);
			btnExpand.setX(leftPos + 86);
		}));
		if(recipeBookGui.isVisible()) {
			buttonSortingType.setX(leftPos - 18);
			buttonDirection.setX(leftPos - 18);
			buttonSearchType.setX(leftPos - 18);
			super.searchField.setX(this.leftPos + 115);
			recipeBookSearch = recipeBookGui.searchBox;
			btnCollapse.setX(leftPos + 86);
			btnExpand.setX(leftPos + 86);
		}
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

	public void collapseScreen(){
		rowCount = 3;
		this.expanded = false;
		onExpandedChanged(false);
		syncSortSettings();
	}

	public void expandScreen(){
		rowCount = 7;
		this.expanded = true;
		if(this.recipeBookGui.isVisible()){
			btnRecipeBook.onPress();
		}
		onExpandedChanged(true);
		syncSortSettings();
	}

	@Override
	public void containerTick() {
		super.containerTick();
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
			super.render(graphics, mouseX, mouseY, partialTicks);
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
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
		if (this.recipeBookGui.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
			return true;
		} else {
			return this.widthTooNarrow && this.recipeBookGui.isVisible() || super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
		}
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

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if(keyCode == GLFW.GLFW_KEY_S && hoveredSlot != null) {
			ItemStack itemstack = null;

			for (int i = 0; i < this.ghostRecipe.size(); ++i) {
				GhostRecipe.GhostIngredient ghostrecipe$ghostingredient = this.ghostRecipe.get(i);
				int j = ghostrecipe$ghostingredient.getX();
				int k = ghostrecipe$ghostingredient.getY();
				if (j == hoveredSlot.x && k == hoveredSlot.y) {
					itemstack = ghostrecipe$ghostingredient.getItem();
				}
			}
			if(itemstack != null) {
				super.searchField.setValue(itemstack.getHoverName().getString());
				super.searchField.setFocused(false);
				return true;
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
