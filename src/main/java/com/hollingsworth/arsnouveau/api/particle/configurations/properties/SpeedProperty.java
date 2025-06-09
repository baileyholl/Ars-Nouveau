package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.gui.HorizontalSlider;
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
import java.util.Objects;
import java.util.function.Consumer;

public class SpeedProperty extends BaseProperty<SpeedProperty>{
    public static MapCodec<SpeedProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("yMinSpeed").forGetter(i -> i.yMinSpeed),
            Codec.DOUBLE.fieldOf("yMaxSpeed").forGetter(i -> i.yMaxSpeed),
            Codec.DOUBLE.fieldOf("xzMinSpeed").forGetter(i -> i.xzMinSpeed),
            Codec.DOUBLE.fieldOf("xzMaxSpeed").forGetter(i -> i.xzMaxSpeed)
    ).apply(instance, SpeedProperty::new));

    public static StreamCodec<RegistryFriendlyByteBuf, SpeedProperty> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE,
            SpeedProperty::minY,
            ByteBufCodecs.DOUBLE,
            SpeedProperty::maxY,
            ByteBufCodecs.DOUBLE,
            SpeedProperty::minXZ,
            ByteBufCodecs.DOUBLE,
            SpeedProperty::maxXZ,
            SpeedProperty::new
    );


    private double yMinSpeed = 0.0;
    private double xzMinSpeed = 0.0;

    private double yMaxSpeed = 0.0;
    private double xzMaxSpeed = 0.0;

    public SpeedProperty(){
        super();

    }

    public SpeedProperty(double yMinSpeed, double yMaxSpeed, double minXZ, double xzMaxSpeed) {
        super();
        this.yMinSpeed = yMinSpeed;
        this.xzMinSpeed = minXZ;
        this.yMaxSpeed = yMaxSpeed;
        this.xzMaxSpeed = xzMaxSpeed;
    }

    public double minY() {
        return yMinSpeed;
    }

    public double minXZ() {
        return xzMinSpeed;
    }

    public double maxY(){
        return yMaxSpeed;
    }

    public double maxXZ(){
        return xzMaxSpeed;
    }

    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        return new ParticleConfigWidgetProvider(x, y, width, height) {
            HorizontalSlider minYSpeedSlider;
            HorizontalSlider yMaxSpeedSlider;

            HorizontalSlider xzSpeedSlider;
            HorizontalSlider xzMaxSpeedSlider;
            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.drawHeader(getName(), graphics, x, y, width, mouseX, mouseY, partialTicks);
                int yOffset = 20;
                int sliderSpacing = 25;
                DocClientUtils.drawHeaderNoUnderline(Component.translatable("ars_nouveau.yspeed_slider", minYSpeedSlider.getValueString()), graphics, x, y + yOffset, width, mouseX, mouseY, partialTicks);

                yOffset += sliderSpacing;
                DocClientUtils.drawHeaderNoUnderline(Component.translatable("ars_nouveau.ymaxspeed_slider", yMaxSpeedSlider.getValueString()), graphics, x, y + yOffset, width, mouseX, mouseY, partialTicks);

                yOffset += sliderSpacing;
                DocClientUtils.drawHeaderNoUnderline(Component.translatable("ars_nouveau.xzspeed_slider", xzSpeedSlider.getValueString()), graphics, x, y + yOffset, width, mouseX, mouseY, partialTicks);

                yOffset += sliderSpacing;
                DocClientUtils.drawHeaderNoUnderline(Component.translatable("ars_nouveau.xzmaxspeed_slider", xzMaxSpeedSlider.getValueString()), graphics, x, y + yOffset, width, mouseX, mouseY, partialTicks);
            }

            @Override
            public void addWidgets(List<AbstractWidget> widgets) {
                int yOffset = 30;
                int xSliderOffset = x + 4;
                yOffset += 4;
                int sliderSpacing = 25;
                double min = -0.20;
                double max = 0.20;
                double stepSize = 0.02;
                minYSpeedSlider = buildSlider(xSliderOffset, y + yOffset, min, max, stepSize, 1, Component.translatable("ars_nouveau.yspeed_slider"), Component.empty(), 0.0, (value) ->{
                    yMinSpeed = minYSpeedSlider.getValue();
                    yMaxSpeed = Math.max(yMaxSpeed, yMinSpeed);
                    clampSliders();
                    writeChanges();
                });
                yOffset += sliderSpacing;

                yMaxSpeedSlider = buildSlider(xSliderOffset, y + yOffset,  min, max, stepSize, 1, Component.translatable("ars_nouveau.ymaxspeed_slider"), Component.empty(), 0.0, (value) ->{
                    yMaxSpeed = yMaxSpeedSlider.getValue();
                    yMinSpeed = Math.min(yMaxSpeed, yMinSpeed);
                    clampSliders();
                    writeChanges();
                });
                yOffset += sliderSpacing;

                xzSpeedSlider = buildSlider(xSliderOffset, y + yOffset,  min, max, stepSize, 1, Component.translatable("ars_nouveau.xzspeed_slider"), Component.empty(), 0.0, (value) ->{
                    xzMinSpeed = xzSpeedSlider.getValue();
                    xzMaxSpeed = Math.max(xzMaxSpeed, xzMinSpeed);
                    clampSliders();
                    writeChanges();
                });
                yOffset += sliderSpacing;

                xzMaxSpeedSlider = buildSlider(xSliderOffset, y + yOffset,  min, max, stepSize, 1, Component.translatable("ars_nouveau.xzmaxspeed_slider"), Component.empty(), 0.0, (value) ->{
                    xzMaxSpeed = xzMaxSpeedSlider.getValue();
                    xzMinSpeed = Math.min(xzMaxSpeed, xzMinSpeed);
                    clampSliders();
                    writeChanges();
                });

                clampSliders();

                widgets.add(minYSpeedSlider);
                widgets.add(yMaxSpeedSlider);
                widgets.add(xzSpeedSlider);
                widgets.add(xzMaxSpeedSlider);
            }

            private void clampSliders() {
                yMaxSpeedSlider.setValue(yMaxSpeed);
                minYSpeedSlider.setValue(yMinSpeed);
                xzSpeedSlider.setValue(xzMinSpeed);
                xzMaxSpeedSlider.setValue(xzMaxSpeed);
            }

            public HorizontalSlider buildSlider(int x, int y, double min, double max, double stepSize, int precision, Component prefix, Component suffix, double currentVal, Consumer<Double> onValueChange) {
                return new HorizontalSlider(x, y, DocAssets.SLIDER_BAR_FILLED, DocAssets.SLIDER, prefix, suffix, min, max, currentVal, stepSize, precision, false, onValueChange);
            }

            @Override
            public void renderIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
//                DocClientUtils.blit(graphics, fromShape(spawnType), x, y);
            }

            @Override
            public Component getButtonTitle() {
                return getName();
            }
        };
    }

    @Override
    public IPropertyType<SpeedProperty> getType() {
        return ParticlePropertyRegistry.SPEED_PROPERTY.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SpeedProperty that = (SpeedProperty) o;
        return Double.compare(that.yMinSpeed, yMinSpeed) == 0 && Double.compare(that.xzMinSpeed, xzMinSpeed) == 0 &&
               Double.compare(that.yMaxSpeed, yMaxSpeed) == 0 && Double.compare(that.xzMaxSpeed, xzMaxSpeed) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(yMinSpeed, xzMinSpeed, yMaxSpeed, xzMaxSpeed);
    }
}
