package com.hollingsworth.arsnouveau.common.tss.platform.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class GuiButton extends PlatformButton {
	public static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("toms_storage", "textures/gui/filter_buttons.png");

	public ResourceLocation texture;
	public int tile;
	public int state;
	public int texX = 0;
	public int texY = 0;
	public GuiButton(int x, int y, int tile, OnPress pressable) {
		super(x, y, 16, 16, null, pressable);
		this.tile = tile;
		this.texture = BUTTON_TEXTURES;
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void renderButton(PoseStack st, int mouseX, int mouseY, float pt) {
		if (this.visible) {
			int x = getX();
			int y = getY();
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, texture);
			this.isHovered = mouseX >= x && mouseY >= y && mouseX < x + this.width && mouseY < y + this.height;
			//int i = this.getYImage(this.isHovered);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			this.blit(st, x, y, texX + state * 16, texY + tile * 16, this.width, this.height);
		}
	}

	public static class CompositeButton extends GuiButton {
		public int texY_button = 16;
		public CompositeButton(int x, int y, int tile, OnPress pressable) {
			super(x, y, tile, pressable);
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void renderButton(PoseStack st, int mouseX, int mouseY, float pt) {
			if (this.visible) {
				int x = getX();
				int y = getY();
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderTexture(0, texture);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.isHovered = mouseX >= x && mouseY >= y && mouseX < x + this.width && mouseY < y + this.height;
				int i = this.getYImage(this.isHoveredOrFocused());
				this.blit(st, x, y, texX + i * 16, this.texY_button, this.width, this.height);
				this.blit(st, x, y, texX + tile * 16 + state * 16, texY, this.width, this.height);
			}
		}
	}
}