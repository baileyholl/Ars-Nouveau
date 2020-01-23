package com.hollingsworth.craftedmagic.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.config.GuiUtils;

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
        MainWindow res = minecraft.mainWindow;
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

//            List<String> tooltip = this.getTooltipFromItem(tooltipStack);
//            Pair<BookEntry, Integer> provider = book.contents.getEntryForStack(tooltipStack);
//            if(provider != null && (!(this instanceof GuiBookEntry) || ((GuiBookEntry) this).entry != provider.getLeft())) {
//                tooltip.add(TextFormatting.GOLD + "(" + I18n.format("patchouli.gui.lexicon.shift_for_recipe") + ')');
//            }
            //GuiUtils.preItemToolTip(tooltipStack);
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
        return minecraft.mainWindow.calcGuiScale(0, minecraft.getForceUnicodeFont());
    }

}
