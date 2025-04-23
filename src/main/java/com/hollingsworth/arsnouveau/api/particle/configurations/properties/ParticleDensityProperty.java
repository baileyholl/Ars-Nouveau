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
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ParticleDensityProperty extends Property<ParticleDensityProperty>{
    public static MapCodec<ParticleDensityProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("density").forGetter(i -> i.density)
    ).apply(instance, ParticleDensityProperty::new));

    public static StreamCodec<RegistryFriendlyByteBuf, ParticleDensityProperty> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ParticleDensityProperty::density,
            ParticleDensityProperty::new
    );

    public int density;

    public ParticleDensityProperty(int density) {
        super();
        this.density = density;
    }

    public ParticleDensityProperty(PropMap propMap){
        super(propMap);
        if(!propMap.has(getType())){
            this.density = 5;
        } else {
            this.density = propMap.getOptional(getType()).orElse(new ParticleDensityProperty(5)).density;
        }
    }

    public int density() {
        return density;
    }


    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        ParticleDensityProperty property = this;
        return new ParticleConfigWidgetProvider(x, y, width, height) {
            BookSlider redW;

            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.drawHeader(getName(), graphics, x, y, width, mouseX, mouseY, partialTicks);

            }

            @Override
            public void addWidgets(List<AbstractWidget> widgets) {
                Consumer<Double> colorChanged = (value) -> {
                    density = redW.getValueInt();
                    propertyHolder.set(getType(), property);
                };
                redW = buildSlider(x + 10, y + 30, Component.translatable("ars_nouveau.density_slider"), Component.empty(), 5, colorChanged);
                redW.setValue(Mth.clamp(density, 1, 10));
                widgets.add(redW);

            }

            public BookSlider buildSlider(int x, int y, Component prefix, Component suffix, double currentVal, Consumer<Double> onValueChange) {
                return new BookSlider(x, y, 100, 20, prefix, suffix, 1.0D, 10, currentVal, 1, 1, true, onValueChange);
            }

            @Override
            public void renderIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
//                Color color = new Color(particleColor.getColor(), false);
//                graphics.fill(x + 2, y + 2, x + 12,  y + 12, color.getRGB());
            }

            @Override
            public Component getButtonTitle() {
                return getName();
            }
        };
    }

    @Override
    public IPropertyType<ParticleDensityProperty> getType() {
        return ParticlePropertyRegistry.DENSITY_PROPERTY.get();
    }

    @Override
    public ParticleDensityProperty copy() {
        return new ParticleDensityProperty(density);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ParticleDensityProperty that = (ParticleDensityProperty) o;
        return density == that.density;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(density);
    }
}
