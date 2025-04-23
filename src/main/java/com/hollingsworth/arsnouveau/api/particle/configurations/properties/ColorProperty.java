package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.gui.BookSlider;
import com.hollingsworth.arsnouveau.client.gui.Color;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.function.Consumer;

public class ColorProperty extends SubProperty{

    public static MapCodec<ColorProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ParticleColor.CODEC.fieldOf("particleColor").forGetter(i -> i.particleColor)
    ).apply(instance, ColorProperty::new));

    public static StreamCodec<RegistryFriendlyByteBuf, ColorProperty> STREAM_CODEC = StreamCodec.composite(ParticleColor.STREAM, ColorProperty::color, ColorProperty::new);

    public ParticleColor particleColor;

    public ColorProperty(PropMap propertyHolder) {
        super(propertyHolder);
        this.particleColor = propertyHolder.getOrDefault(ParticlePropertyRegistry.COLOR_PROPERTY.get(), new ColorProperty(ParticleColor.defaultParticleColor())).particleColor;
    }

    public ColorProperty(ParticleColor property) {
        super();
        this.particleColor = property;
    }

    public ParticleColor color(){
        return particleColor;
    }

    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        ColorProperty property = this;
        return new ParticleConfigWidgetProvider(x, y, width, height) {
            BookSlider redW;
            BookSlider greenW;
            BookSlider blueW;

            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.drawHeader(getName(), graphics, x, y, width, mouseX, mouseY, partialTicks);
                Color color = new Color(particleColor.getColor(), false);
                int xOffset = x + 50;
                int yOffset = y + 130;
                int size = 16;
                graphics.fill(xOffset, yOffset, xOffset + size,  yOffset + size, color.getRGB());
            }

            @Override
            public void addWidgets(List<AbstractWidget> widgets) {
                Consumer<Double> colorChanged = (value) -> {
                    ParticleColor color = new ParticleColor((int)redW.getValue(), (int)greenW.getValue(), (int)blueW.getValue());
                    particleColor = color;
                    propertyHolder.set(getType(), property);
                };
                redW = buildSlider(x + 10, y + 30, Component.translatable("ars_nouveau.color_gui.red_slider"), Component.empty(), 255, colorChanged);
                greenW = buildSlider(x + 10, y + 70, Component.translatable("ars_nouveau.color_gui.green_slider"), Component.empty(), 25, colorChanged);
                blueW = buildSlider(x + 10, y + 110, Component.translatable("ars_nouveau.color_gui.blue_slider"), Component.empty(), 180, colorChanged);
                setFromPreset(particleColor);
                widgets.add(redW);
                widgets.add(greenW);
                widgets.add(blueW);
            }

            public void setFromPreset(ParticleColor preset) {
                redW.setValue(Mth.clamp(preset.getRed() * 255.0, 1, 255));
                greenW.setValue(Mth.clamp(preset.getGreen() * 255.0, 1, 255));
                blueW.setValue(Mth.clamp(preset.getBlue() * 255.0, 1, 255));
            }

            public BookSlider buildSlider(int x, int y, Component prefix, Component suffix, double currentVal, Consumer<Double> onValueChange) {
                return new BookSlider(x, y, 100, 20, prefix, suffix, 1.0D, 255.0D, currentVal, 1, 1, true, onValueChange);
            }

            @Override
            public void renderIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
                Color color = new Color(particleColor.getColor(), false);
                graphics.fill(x + 2, y + 2, x + 12,  y + 12, color.getRGB());
            }

            @Override
            public Component getButtonTitle() {
                return getName();
            }
        };
    }

    @Override
    public IPropertyType<ColorProperty> getType() {
        return ParticlePropertyRegistry.COLOR_PROPERTY.get();
    }
}
