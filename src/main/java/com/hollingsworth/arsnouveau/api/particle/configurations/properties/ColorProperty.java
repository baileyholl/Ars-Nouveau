package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.*;
import com.hollingsworth.arsnouveau.client.gui.buttons.ColorPresetButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.SelectedParticleButton;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.RainbowParticleColor;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ColorProperty extends SubProperty<ColorProperty>{

    public static MapCodec<ColorProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ParticleColor.CODEC.fieldOf("particleColor").forGetter(i -> i.particleColor)
    ).apply(instance, ColorProperty::new));

    public static StreamCodec<RegistryFriendlyByteBuf, ColorProperty> STREAM_CODEC = StreamCodec.composite(ParticleColor.STREAM, ColorProperty::color, ColorProperty::new);

    public ParticleColor particleColor;
    public boolean isLegacyRGB = false;

    public ColorProperty(PropMap propertyHolder, boolean legacyRGB) {
        this(propertyHolder);
        this.isLegacyRGB = legacyRGB;
    }

    public ColorProperty(PropMap propertyHolder) {
        super(propertyHolder);
        this.particleColor = propertyHolder.getOrDefault(ParticlePropertyRegistry.COLOR_PROPERTY.get(), new ColorProperty(ParticleColor.defaultParticleColor())).particleColor;
        this.displayColor = particleColor;
    }

    public ColorProperty(ParticleColor property) {
        super();
        this.particleColor = property;
        this.displayColor = particleColor;
    }

    public ParticleColor color(){
        return particleColor;
    }

    private ParticleColor displayColor;

    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        ColorProperty property = this;
        return new ParticleConfigWidgetProvider(x, y, width, height) {
            BookSlider redW;
            BookSlider greenW;
            BookSlider blueW;
            HueSlider hueSlider;
            SatLumSlider saturation;
            SatLumSlider lightness;
            SelectedParticleButton rainbowButton;

            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.drawHeader(getName(), graphics, x, y, width, mouseX, mouseY, partialTicks);
                Color color = new Color(displayColor.getColor(), false);

                if(!isLegacyRGB){
                    int xOffset = x + 7;
                    int yOffset = y + 18;
                    graphics.fill(xOffset + 2, yOffset + 3, xOffset - 2 +  DocAssets.SPELLSTYLE_COLOR_PREVIEW.width(),  yOffset - 3 + DocAssets.SPELLSTYLE_COLOR_PREVIEW.height(), color.getRGB());

                    graphics.fill(xOffset + 3, yOffset + 2, xOffset - 3 +  DocAssets.SPELLSTYLE_COLOR_PREVIEW.width(),  yOffset - 2 + DocAssets.SPELLSTYLE_COLOR_PREVIEW.height(), color.getRGB());

                    DocClientUtils.blit(graphics, DocAssets.SPELLSTYLE_COLOR_PREVIEW, xOffset, yOffset);
                    int hueOffset = 35;
                    DocClientUtils.drawParagraph(Component.translatable("ars_nouveau.hue"), graphics, x + 8, y + hueOffset, width, mouseX, mouseY, partialTicks);
                    DocClientUtils.drawParagraph(Component.translatable("ars_nouveau.sat"), graphics, x + 8, y + hueOffset + 20, width, mouseX, mouseY, partialTicks);
                    DocClientUtils.drawParagraph(Component.translatable("ars_nouveau.lightness"), graphics, x + 8, y + hueOffset + 40, width, mouseX, mouseY, partialTicks);
                }else{
                    int xOffset = x + 50;
                    int yOffset = y + 130;
                    int size = 16;
                    graphics.fill(xOffset, yOffset, xOffset + size,  yOffset + size, color.getRGB());
                }
            }

            @Override
            public void tick() {
                super.tick();
                if(displayColor != null){
                    displayColor = displayColor.transition(ClientInfo.ticksInGame * 50);
                }

                if(!(particleColor instanceof RainbowParticleColor)){
                    rainbowButton.selected = false;
                }else{
                    rainbowButton.selected = true;
                }
            }

            @Override
            public void addWidgets(List<AbstractWidget> widgets) {
                Consumer<Double> colorChanged = (value) -> {
                    ParticleColor color = new ParticleColor((int)redW.getValue(), (int)greenW.getValue(), (int)blueW.getValue());
                    particleColor = color;
                    displayColor = particleColor;
                    propertyHolder.set(getType(), property);
                };
                redW = buildSlider(x + 10, y + 30, Component.translatable("ars_nouveau.color_gui.red_slider"), Component.empty(), 255, colorChanged);
                greenW = buildSlider(x + 10, y + 50, Component.translatable("ars_nouveau.color_gui.green_slider"), Component.empty(), 25, colorChanged);
                blueW = buildSlider(x + 10, y + 70, Component.translatable("ars_nouveau.color_gui.blue_slider"), Component.empty(), 180, colorChanged);

                int hueOffset = 45;
                int xOffset = 7;
                hueSlider = new HueSlider(x + xOffset, y + hueOffset, false, () -> HSLColor.hsl(hueSlider.getValueInt(), saturation.getValue(), lightness.getValue()), (val) ->{
                    updateParticleColor();
                });
                saturation = new SatLumSlider(x + xOffset, y + hueOffset + 20, false, false, () -> HSLColor.hsl(hueSlider.getValueInt(), saturation.getValue(), lightness.getValue()), (val) -> {
                    updateParticleColor();
                });
                lightness = new SatLumSlider(x + xOffset, y + hueOffset + 40, false, true, () -> HSLColor.hsl(hueSlider.getValueInt(), saturation.getValue(), lightness.getValue()), (val) -> {
                    updateParticleColor();
                });

                setFromPreset(particleColor);
                int numPerRow = 6;
                for(int i = 0; i < 16; i++){
                    ParticleColor color = ParticleColor.PRESET_COLORS.get(i);
                    widgets.add(new ColorPresetButton(x + xOffset + (i % numPerRow) * 18, y + 100 + (i / numPerRow) * 18, color, (b) ->{
                        this.setFromPreset(color);
                        updateParticleColor();
                    }));
                }
                rainbowButton = new SelectedParticleButton(x + xOffset + (16 % numPerRow) * 18, y + 100 + (16 / numPerRow) * 18, DocAssets.SPELLSTYLE_RAINBOW, (button) ->{
                    particleColor = new RainbowParticleColor(particleColor.getRedInt(), particleColor.getGreenInt(), particleColor.getBlueInt());
                    displayColor = particleColor;
                    propertyHolder.set(getType(), property);
                });
                widgets.add(rainbowButton);
                if(particleColor instanceof RainbowParticleColor){
                    rainbowButton.selected = true;
                }

                if(isLegacyRGB) {
                    widgets.add(redW);
                    widgets.add(greenW);
                    widgets.add(blueW);
                }else{
                    widgets.add(hueSlider);
                    widgets.add(saturation);
                    widgets.add(lightness);
                }
            }

            public void updateParticleColor(){
                particleColor = HSLColor.hsl(hueSlider.getValueInt(), saturation.getValue(), lightness.getValue()).toColor().toParticle();
                displayColor = particleColor;
                propertyHolder.set(getType(), property);
            }

            public void setFromPreset(ParticleColor preset) {
                redW.setValue(Mth.clamp(preset.getRed() * 255.0, 1, 255));
                greenW.setValue(Mth.clamp(preset.getGreen() * 255.0, 1, 255));
                blueW.setValue(Mth.clamp(preset.getBlue() * 255.0, 1, 255));
                HSLColor color = HSLColor.rgb(preset.getRedInt(), preset.getGreenInt(), preset.getBlueInt());
                hueSlider.setValue(color.getHue());
                saturation.setValue(color.getSaturation());
                lightness.setValue(color.getLightness());
            }

            public BookSlider buildSlider(int x, int y, Component prefix, Component suffix, double currentVal, Consumer<Double> onValueChange) {
                return new BookSlider(x, y, 100, 20, prefix, suffix, 1.0D, 255.0D, currentVal, 1, 1, true, onValueChange);
            }

            @Override
            public void renderIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
                Color color = new Color(displayColor.getColor(), false);
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ColorProperty property = (ColorProperty) o;
        return Objects.equals(particleColor, property.particleColor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(particleColor);
    }
}
