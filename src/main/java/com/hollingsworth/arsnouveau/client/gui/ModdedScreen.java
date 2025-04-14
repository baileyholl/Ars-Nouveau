package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModdedScreen extends Screen {

    public int maxScale;
    public float scaleFactor;

    public ModdedScreen(Component titleIn) {
        super(titleIn);
    }

    @Override
    public void init() {
        super.init();
        this.maxScale = this.getMaxAllowedScale();
        this.scaleFactor = 1.0F;
    }

    public void drawTooltip(GuiGraphics stack, int mouseX, int mouseY) {
        List<Component> tooltip = new ArrayList<>();
        collectTooltips(stack, mouseX, mouseY, tooltip);
        if (!tooltip.isEmpty()) {
            stack.renderTooltip(font, tooltip, Optional.ofNullable(collectComponent(mouseX, mouseY)), mouseX, mouseY);
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

    protected TooltipComponent collectComponent(int mouseX, int mouseY) {
        for (Renderable renderable : renderables) {
            if (renderable instanceof AbstractWidget widget && (widget instanceof ITooltipProvider tooltipProvider)) {
                if (GuiUtils.isMouseInRelativeRange(mouseX, mouseY, widget)) {
                    return tooltipProvider.getTooltipImage();
                }
            }
        }
        return null;
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
