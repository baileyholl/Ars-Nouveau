package com.hollingsworth.arsnouveau.common.tss.platform.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class StorageTerminalScreen extends AbstractStorageTerminalScreen<StorageTerminalMenu> {
	private static final ResourceLocation gui = new ResourceLocation("toms_storage", "textures/gui/storage_terminal.png");

	public StorageTerminalScreen(StorageTerminalMenu screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
	}

	@Override
	protected void init() {
		imageWidth = 194;
		imageHeight = 202;
		rowCount = 5;
		super.init();
	}

	@Override
	protected void renderBg(PoseStack st, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, getGui());
		this.blit(st, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
	}

	@Override
	public ResourceLocation getGui() {
		return gui;
	}

	@Override
	public void render(PoseStack st, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(st);
		super.render(st, mouseX, mouseY, partialTicks);
	}
}
