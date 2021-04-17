package com.hollingsworth.arsnouveau.client.gui;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

import java.util.List;
public class GuiEntityInfoHUD extends AbstractGui {

    private static final Minecraft minecraft = Minecraft.getInstance();

    public void drawHUD(MatrixStack ms, List<String> tooltips){
        int offsetLeft = 5;
        fill(ms, offsetLeft, 50, 100+ offsetLeft, 0, 300);
        int counter = 0;
        if(tooltips == null)
            return;
        for(String s : tooltips){
            minecraft.font.drawShadow(ms, s, offsetLeft, 5f + 10 * counter, 0xFFFFFF);
            counter++;
        }
    }
}
