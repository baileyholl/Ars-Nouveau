package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.client.gui.BookSlider;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.function.Consumer;

public class ColorProperty extends Property{
    public static final ResourceLocation ID = ArsNouveau.prefix("color");

    public ColorProperty(PropertyHolder propertyHolder) {
        super(propertyHolder);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        return new ParticleConfigWidgetProvider(x, y, width, height) {
            BookSlider redW;
            BookSlider greenW;
            BookSlider blueW;

            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.drawHeader(getName(), graphics, x, y, width, mouseX, mouseY, partialTicks);

            }

            @Override
            public void addWidgets(List<AbstractWidget> widgets) {
                Consumer<Double> colorChanged = (value) -> {
                    ParticleColor color = new ParticleColor((int)redW.getValue(), (int)greenW.getValue(), (int)blueW.getValue());
                    propertyHolder.colorChanged.accept(color);
                };
                redW = buildSlider(x + 28, y + 49, Component.translatable("ars_nouveau.color_gui.red_slider"), Component.empty(), 255, colorChanged);
                greenW = buildSlider(x + 28, y + 89, Component.translatable("ars_nouveau.color_gui.green_slider"), Component.empty(), 25, colorChanged);
                blueW = buildSlider(x + 28, y + 129, Component.translatable("ars_nouveau.color_gui.blue_slider"), Component.empty(), 180, colorChanged);
                widgets.add(redW);
                widgets.add(greenW);
                widgets.add(blueW);
                setFromPreset(propertyHolder.defaultColor);
            }

            public void setFromPreset(ParticleColor preset) {
                redW.setValue(Mth.clamp(preset.getRed() * 255.0, 1, 255));
                greenW.setValue(Mth.clamp(preset.getGreen() * 255.0, 1, 255));
                blueW.setValue(Mth.clamp(preset.getBlue() * 255.0, 1, 255));
            }

            public BookSlider buildSlider(int x, int y, Component prefix, Component suffix, double currentVal, Consumer<Double> onValueChange) {
                return new BookSlider(x, y, 100, 20, prefix, suffix, 1.0D, 255.0D, currentVal, 1, 1, true, onValueChange);
            }
        };
    }


}
