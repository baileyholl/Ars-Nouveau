package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import vazkii.patchouli.client.base.PersistentData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ModdedScreen extends Screen {

    public int maxScale;
    public float scaleFactor;

    public ModdedScreen(Component titleIn) {
        super(titleIn);
    }

    @Override
    public void init() {
        super.init();
        Window res = this.minecraft.getWindow();
        double oldGuiScale = res.calculateScale(this.minecraft.options.guiScale().get(), this.minecraft.isEnforceUnicode());
        this.maxScale = this.getMaxAllowedScale();
        int persistentScale = Math.min(PersistentData.data.bookGuiScale, this.maxScale);
        double newGuiScale = res.calculateScale(persistentScale, this.minecraft.isEnforceUnicode());
        if (persistentScale > 0 && newGuiScale != oldGuiScale) {
            this.scaleFactor = (float)newGuiScale / (float)res.getGuiScale();
            res.setGuiScale(newGuiScale);
            this.width = res.getGuiScaledWidth();
            this.height = res.getGuiScaledHeight();
            res.setGuiScale(oldGuiScale);
        } else {
            this.scaleFactor = 1.0F;
        }
    }

    public void drawTooltip(GuiGraphics stack, int mouseX, int mouseY) {
        List<Component> tooltip = new ArrayList<>();
        collectTooltips(stack, mouseX, mouseY, tooltip);
        if (!tooltip.isEmpty()) {
            stack.renderComponentTooltip(font, tooltip, mouseX, mouseY);
        }
    }

    public void collectTooltips(GuiGraphics stack, int mouseX, int mouseY, List<Component> tooltip){
        for(Renderable renderable : renderables){
            if(renderable instanceof AbstractWidget widget && renderable instanceof ITooltipProvider tooltipProvider){
                if(GuiUtils.isMouseInRelativeRange(mouseX, mouseY, widget)){
                    tooltipProvider.getTooltip(tooltip);
                    break;
                }
            }
        }
    }

    public @Nullable Renderable getHoveredRenderable(int mouseX, int mouseY){
        for(Renderable renderable : renderables){
            if(renderable instanceof AbstractWidget widget){
                if(GuiUtils.isMouseInRelativeRange(mouseX, mouseY, widget)){
                    return renderable;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int getMaxAllowedScale() {
        return this.minecraft.getWindow().calculateScale(0, this.minecraft.isEnforceUnicode());
    }

}
