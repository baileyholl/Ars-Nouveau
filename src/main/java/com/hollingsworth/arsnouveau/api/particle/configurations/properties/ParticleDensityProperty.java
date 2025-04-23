package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class ParticleDensityProperty extends Property{

    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        return new ParticleConfigWidgetProvider(x, y, width, height) {
            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

            }

            @Override
            public void addWidgets(List<AbstractWidget> widgets) {

            }

            @Override
            public void renderIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTicks) {

            }

            @Override
            public Component getButtonTitle() {
                return Component.literal("Particle Density");
            }
        };
    }
}
