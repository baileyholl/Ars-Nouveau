package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class StorageSettingsButton extends StateButton{
    public StorageSettingsButton(int x, int y, int width, int height, int imageWidth, int imageHeight, int tile, ResourceLocation texture, OnPress pressable) {
        super(x, y, width, height, imageWidth, imageHeight, tile, texture, pressable);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void renderButton(PoseStack st, int mouseX, int mouseY, float pt) {
        if(this.visible){
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, new ResourceLocation(ArsNouveau.MODID, "textures/gui/storage_tab1.png"));
            blit(st, x + 1, y, 0, 0, 22, 13, 22, 13);
        }
        super.renderButton(st, mouseX, mouseY, pt);
    }
}
