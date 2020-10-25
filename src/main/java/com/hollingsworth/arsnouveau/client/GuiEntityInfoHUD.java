package com.hollingsworth.arsnouveau.client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

import java.util.List;


public class GuiEntityInfoHUD extends AbstractGui {
    private static final Minecraft minecraft = Minecraft.getInstance();

    public void drawHUD(List<String> tooltips){
        int offsetLeft = 5;
        fill(offsetLeft, 50, 100+ offsetLeft, 0, 300);
        int counter = 0;
        for(String s : tooltips){
            minecraft.fontRenderer.drawStringWithShadow(s, offsetLeft, 5f + 10 * counter, 0xFFFFFF);
            counter++;
        }

    }
}
