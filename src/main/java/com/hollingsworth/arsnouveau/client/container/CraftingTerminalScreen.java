package com.hollingsworth.arsnouveau.client.container;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.mojang.blaze3d.vertex.PoseStack;
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
	private static final ResourceLocation gui = new ResourceLocation(ArsNouveau.MODID, "textures/gui/crafting_terminal.png");
	private final RecipeBookComponent recipeBookGui;
	private boolean widthTooNarrow;
	private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/gui/recipe_book.png");
	private static final ResourceLocation CLEAR_CRAFT_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/gui/craft_clear.png");
	private EditBox recipeBookSearch;
	private GhostRecipe ghostRecipe;

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
		return gui;
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
		GuiImageButton btnClr = new GuiImageButton(leftPos + 86, recipeButtonY, 0,0,9,9,9,9, CLEAR_CRAFT_TEXTURE, b -> clearGrid());
		addRenderableWidget(btnClr);

		addRenderableWidget(new GuiImageButton( this.leftPos + 98, recipeButtonY , 0, 0, 9, 9, 9,9, RECIPE_BUTTON_TEXTURE, (thisButton) -> {
			this.recipeBookGui.initVisuals();
			recipeBookSearch = recipeBookGui.searchBox;

			this.recipeBookGui.toggleVisibility();
			this.leftPos = this.recipeBookGui.updateScreenPosition(this.width, this.imageWidth);
			((GuiImageButton)thisButton).setPosition(this.leftPos + 98, recipeButtonY);

			super.searchField.setX(this.leftPos + 114);
			btnClr.setX(this.leftPos + 86);
			buttonSortingType.setX(leftPos - 18);
			buttonDirection.setX(leftPos - 18);
			buttonSearchType.setX(leftPos - 18);
		}));
		if(recipeBookGui.isVisible()) {
			buttonSortingType.setX(leftPos - 18);
			buttonDirection.setX(leftPos - 18);
			buttonSearchType.setX(leftPos - 18);
			super.searchField.setX(this.leftPos + 114);
			recipeBookSearch = recipeBookGui.searchBox;
		}
		onPacket();
	}

	@Override
	public void containerTick() {
		super.containerTick();
		this.recipeBookGui.tick();
	}

	@Override
	public void render(PoseStack st, int mouseX, int mouseY, float partialTicks) {

		this.renderBackground(st);

		if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
			this.renderBg(st, partialTicks, mouseX, mouseY);
			this.recipeBookGui.render(st, mouseX, mouseY, partialTicks);
		} else {
			this.recipeBookGui.render(st, mouseX, mouseY, partialTicks);
			super.render(st, mouseX, mouseY, partialTicks);
			this.recipeBookGui.renderGhostRecipe(st, this.leftPos, this.topPos, true, partialTicks);
		}
		this.renderTooltip(st, mouseX, mouseY);
		this.recipeBookGui.renderTooltip(st, this.leftPos, this.topPos, mouseX, mouseY);
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
	public boolean keyPressed(int code, int p_231046_2_, int p_231046_3_) {
		if(code == GLFW.GLFW_KEY_S && hoveredSlot != null) {
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
				super.searchField.setFocus(false);
				return true;
			}
		}
		return super.keyPressed(code, p_231046_2_, p_231046_3_);
	}
}
