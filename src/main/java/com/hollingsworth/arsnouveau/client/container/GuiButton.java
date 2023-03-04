package com.hollingsworth.arsnouveau.client.container;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class GuiButton extends PlatformButton {
	public static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation(ArsNouveau.MODID, "textures/gui/filter_buttons.png");

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
}