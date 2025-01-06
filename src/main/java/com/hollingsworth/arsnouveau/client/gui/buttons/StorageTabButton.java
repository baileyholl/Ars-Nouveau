package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.gui.GuiGraphics;
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
    public void renderWidget(GuiGraphics p_281670_, int mouseX, int mouseY, float pt) {
        if(this.visible){
            p_281670_.blit(ArsNouveau.prefix( "textures/gui/storage_tab2" + (isSelected ? "_selected" : "")  + ".png"), x , y, 0, 0, 18, 13, 18, 13);
        }
        super.renderWidget(p_281670_, mouseX, mouseY, pt);
    }
}
