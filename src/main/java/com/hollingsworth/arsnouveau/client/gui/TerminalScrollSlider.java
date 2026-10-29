package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.nuggets.client.BlitInfo;
import com.hollingsworth.nuggets.client.gui.GuiHelpers;
import com.hollingsworth.nuggets.client.gui.VerticalSlider;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Consumer;

public class TerminalScrollSlider extends VerticalSlider {

    public TerminalScrollSlider(int x, int y, int width, int height, DocAssets.BlitInfo knob, Consumer<Integer> onChange) {
        super(x, y, width, height, new BlitInfo(knob.location(), knob.u(), knob.v(), knob.width(), knob.height()), null, 1, 1, 0, onChange);
    }

    public void setMaxRows(int rows) {
        int row = getValueInt();
        maxValue = Math.max(1, rows);
        active = rows > 0;
        setValue(Math.min(row, rows));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        GuiHelpers.blit(guiGraphics, scrollBar, getX(), getY() + 3 + (int) (value * (height - scrollBar.height() - 2)));
    }

    @Override
    public void setValueFromMouse(double mouseX, double mouseY) {
        setSliderValue((mouseY - getY() - 3 - scrollBar.height() / 2.0) / (height - scrollBar.height() - 2));
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
    }
}
