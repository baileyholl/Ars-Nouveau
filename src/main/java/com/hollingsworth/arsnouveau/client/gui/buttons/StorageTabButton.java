package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class StorageTabButton extends StateButton{
    public boolean isSelected;
    public String highlightText;
    public boolean isAll = false;

    public StorageTabButton(int x, int y, int width, int height, int imageWidth, int imageHeight, int tile, ResourceLocation texture, OnPress pressable) {
        super(x, y, width, height, imageWidth, imageHeight, tile, texture, pressable);
    }

    public StorageTabButton(int x, int y, int width, int height, int imageWidth, int imageHeight, int state, int tile, ResourceLocation texture, OnPress pressable) {
        super(x, y, width, height, imageWidth, imageHeight, tile, texture, pressable);
        this.state = state;
    }

    @Override
    public void renderButton(PoseStack st, int mouseX, int mouseY, float pt) {
        if(this.visible){
            RenderSystem.setShader(GameRenderer::getPositionTexShader);

            RenderSystem.setShaderTexture(0, new ResourceLocation(ArsNouveau.MODID, "textures/gui/storage_tab2" + (isSelected ? "_selected" : "")  + ".png"));

            blit(st, x , y, 0, 0, 18, 13, 18, 13);
        }
        super.renderButton(st, mouseX, mouseY, pt);

    }
}
