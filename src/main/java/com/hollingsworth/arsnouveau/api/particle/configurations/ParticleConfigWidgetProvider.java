package com.hollingsworth.arsnouveau.api.particle.configurations;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Provides a renderable page that is used in the book for configuring particles.
 */
public abstract class ParticleConfigWidgetProvider {

    public int x;
    public int y;
    public int width;
    public int height;

    public ParticleConfigWidgetProvider(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);

    public abstract void addWidgets(List<AbstractWidget> widgets);

    public abstract void renderIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTicks);

    public abstract Component getButtonTitle();

    public void tick(){}
}
