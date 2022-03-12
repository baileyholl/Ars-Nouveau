package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class SelectableButton extends GuiImageButton{
    public ResourceLocation secondImage;
    public boolean isSelected;
    public SelectableButton(int x, int y, int u, int v, int w, int h, int image_width, int image_height, ResourceLocation resource_image, ResourceLocation secondImage, OnPress onPress) {
        super(x, y, u, v, w, h, image_width, image_height, resource_image.getPath(), onPress);
        this.secondImage = secondImage;
    }
    @Override
    public void render(PoseStack ms, int parX, int parY, float partialTicks) {
//        super.render(ms, parX, parY, partialTicks);
        if (visible)
        {
            if(parent != null && parent.isMouseInRelativeRange(parX, parY, x, y, width, height) && toolTip != null){
                if(!toolTip.toString().isEmpty()){
                    List<Component> tip = new ArrayList<>();
                    tip.add(toolTip);
                    parent.tooltip = tip;
                }
            }
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            ResourceLocation renderImage = isSelected ? secondImage : image;
            GuiSpellBook.drawFromTexture(renderImage, x, y, u, v, width, height, image_width, image_height,ms);
        }
    }

}
