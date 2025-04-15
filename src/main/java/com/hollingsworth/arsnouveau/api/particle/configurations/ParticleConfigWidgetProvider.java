package com.hollingsworth.arsnouveau.api.particle.configurations;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;

/**
 * Provides a renderable page that is used in the book for configuring particles.
 */
public abstract class ParticleConfigWidgetProvider {
    /**
     * Should be mutated by the widgets on the screen
     */
    public IParticleConfig type;

    public ParticleConfigWidgetProvider(IParticleConfig type) {
        this.type = type;
    }

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);

    public abstract void addWidgets(List<AbstractWidget> widgets, int x, int y, int width, int height);
}
