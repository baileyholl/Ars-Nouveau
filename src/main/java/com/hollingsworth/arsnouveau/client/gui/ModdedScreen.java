package com.hollingsworth.arsnouveau.client.gui;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModdedScreen extends Screen {

    public int maxScale;
    public float scaleFactor;
    public List<String> tooltip;


    protected ModdedScreen(ITextComponent titleIn) {
        super(titleIn);
    }


    @Override
    protected void init() {
        super.init();
        MainWindow res = getMinecraft().getMainWindow();
        double oldGuiScale = res.calcGuiScale(minecraft.gameSettings.guiScale, minecraft.getForceUnicodeFont());
        maxScale = getMaxAllowedScale();
        int persistentScale = Math.min(0, maxScale);;
        double newGuiScale = res.calcGuiScale(persistentScale, minecraft.getForceUnicodeFont());

        if(persistentScale > 0 && newGuiScale != oldGuiScale) {
            scaleFactor = (float) newGuiScale / (float) res.getGuiScaleFactor();

            res.setGuiScale(newGuiScale);
            width = res.getScaledWidth();
            height = res.getScaledHeight();
            res.setGuiScale(oldGuiScale);
        } else scaleFactor = 1;
    }

    public boolean isMouseInRelativeRange(int mouseX, int mouseY, int x, int y, int w, int h) {

        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }
    final void drawTooltip(int mouseX, int mouseY) {
        if(tooltip != null) {
            FontRenderer font = Minecraft.getInstance().fontRenderer;
            this.renderTooltip(tooltip, mouseX, mouseY, (font == null ? this.font : font));

        } else if(tooltip != null && !tooltip.isEmpty()) {
            List<String> wrappedTooltip = new ArrayList<>();
            for (String s : tooltip)
                Collections.addAll(wrappedTooltip, s.split("\n"));
            GuiUtils.drawHoveringText(wrappedTooltip, mouseX, mouseY, width, height, -1, this.font);
        }
    }

    final void resetTooltip() {
        tooltip = null;
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }

    int getMaxAllowedScale() {
        return getMinecraft().getMainWindow().calcGuiScale(0, minecraft.getForceUnicodeFont());
    }

}
