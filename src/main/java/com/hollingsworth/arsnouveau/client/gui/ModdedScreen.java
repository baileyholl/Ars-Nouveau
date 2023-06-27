package com.hollingsworth.arsnouveau.client.gui;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import vazkii.patchouli.client.base.PersistentData;

import java.util.List;

public class ModdedScreen extends Screen {

    public int maxScale;
    public float scaleFactor;
    public List<Component> tooltip;

    public ModdedScreen(Component titleIn) {
        super(titleIn);
    }

    @Override
    public void init() {
        super.init();
        Window res = this.minecraft.getWindow();
        double oldGuiScale = (double)res.calculateScale((Integer)this.minecraft.options.guiScale().get(), this.minecraft.isEnforceUnicode());
        this.maxScale = this.getMaxAllowedScale();
        int persistentScale = Math.min(PersistentData.data.bookGuiScale, this.maxScale);
        double newGuiScale = (double)res.calculateScale(persistentScale, this.minecraft.isEnforceUnicode());
        if (persistentScale > 0 && newGuiScale != oldGuiScale) {
            this.scaleFactor = (float)newGuiScale / (float)res.getGuiScale();
            res.setGuiScale(newGuiScale);
            this.width = res.getGuiScaledWidth();
            this.height = res.getGuiScaledHeight();
            res.setGuiScale(oldGuiScale);
        } else {
            this.scaleFactor = 1.0F;
        }

//        this. = this.width / 2 - 136;
//        this.bookTop = this.height / 2 - 90;
    }

    public boolean isMouseInRelativeRange(int mouseX, int mouseY, int x, int y, int w, int h) {

        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    public void drawTooltip(GuiGraphics stack, int mouseX, int mouseY) {
        if (tooltip != null && !tooltip.isEmpty()) {
            stack.renderComponentTooltip(font, tooltip, mouseX, mouseY);
        }
    }

    public final void resetTooltip() {
        tooltip = null;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int getMaxAllowedScale() {
        return this.minecraft.getWindow().calculateScale(0, this.minecraft.isEnforceUnicode());
    }

}
