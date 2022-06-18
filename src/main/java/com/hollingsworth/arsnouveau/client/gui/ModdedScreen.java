package com.hollingsworth.arsnouveau.client.gui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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
        Window res = getMinecraft().getWindow();
        double oldGuiScale = res.calculateScale(minecraft.options.guiScale().get(), minecraft.isEnforceUnicode());
        maxScale = getMaxAllowedScale();
        int persistentScale = Math.min(0, maxScale);
        double newGuiScale = res.calculateScale(persistentScale, minecraft.isEnforceUnicode());

        if(persistentScale > 0 && newGuiScale != oldGuiScale) {
            scaleFactor = (float) newGuiScale / (float) res.getGuiScale();

            res.setGuiScale(newGuiScale);
            width = res.getGuiScaledWidth();
            height = res.getGuiScaledHeight();
            res.setGuiScale(oldGuiScale);
        } else scaleFactor = 1;
    }

    public boolean isMouseInRelativeRange(int mouseX, int mouseY, int x, int y, int w, int h) {

        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    public void drawTooltip(PoseStack stack, int mouseX, int mouseY) {
        if (tooltip != null && !tooltip.isEmpty()) {
            this.renderComponentTooltip(stack, tooltip, mouseX, mouseY, font);
        }
    }


    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY,partialTicks);
    }

    public final void resetTooltip() {
        tooltip = null;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    int getMaxAllowedScale() {
        return getMinecraft().getWindow().calculateScale(0, minecraft.isEnforceUnicode());
    }

}
