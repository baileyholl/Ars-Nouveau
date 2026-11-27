package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.gui.BookSlider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public class TransparencyProperty extends BaseProperty<TransparencyProperty> {

    public static MapCodec<TransparencyProperty> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.INT
                            .fieldOf("transparency")
                            .orElse(255)
                            .forGetter(i -> i.transparency)
            ).apply(instance, TransparencyProperty::new));

    public static StreamCodec<RegistryFriendlyByteBuf, TransparencyProperty> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,
                    i -> i.transparency,
                    TransparencyProperty::new
            );

    public int transparency;

    public TransparencyProperty(int transparency) {
        super();
        this.transparency = transparency;
    }

    public TransparencyProperty() {
        this(255);
    }

    public int getTransparency() {
        return transparency;
    }

    @Override
    public ParticleConfigWidgetProvider buildWidgets(
            int x, int y, int width, int height
    ) {
        TransparencyProperty property = this;

        return new ParticleConfigWidgetProvider(x, y, width, height) {

            BookSlider transparencySlider;

            @Override
            public void render(
                    GuiGraphics graphics,
                    int mouseX,
                    int mouseY,
                    float partialTicks
            ) {
                DocClientUtils.drawHeader(
                        getName(), graphics, x, y, width,
                        mouseX, mouseY, partialTicks
                );

                DocClientUtils.drawParagraph(
                        Component.translatable("ars_nouveau.transparency"),
                        graphics, x + 8, y + 35, width,
                        mouseX, mouseY, partialTicks
                );
            }

            @Override
            public void addWidgets(List<AbstractWidget> widgets) {

                transparencySlider = new BookSlider(
                        x + 10,
                        y + 50,
                        100,
                        20,
                        Component.translatable("ars_nouveau.transparency"),
                        Component.literal("%"),
                        50D,
                        255D,
                        transparency,
                        1,
                        1,
                        false,
                        value -> {
                            transparency = (int) (50 + value.floatValue() * 205);
                            propertyHolder.set(getType(), property);
                        }
                );

                widgets.add(transparencySlider);
            }

            @Override
            public void renderIcon(
                    GuiGraphics graphics,
                    int x,
                    int y,
                    int mouseX,
                    int mouseY,
                    float partialTicks
            ) {
                // Optional: draw a transparency icon here.
            }

            @Override
            public Component getButtonTitle() {
                return getName();
            }
        };
    }

    @Override
    public IPropertyType<TransparencyProperty> getType() {
        return ParticlePropertyRegistry.TRANSPARENCY_PROPERTY.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransparencyProperty property = (TransparencyProperty) o;
        return transparency == property.transparency;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(transparency);
    }
}