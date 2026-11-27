package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.Color;
import com.hollingsworth.arsnouveau.client.gui.HSLColor;
import com.hollingsworth.arsnouveau.client.gui.HueSlider;
import com.hollingsworth.arsnouveau.client.gui.SatLumSlider;
import com.hollingsworth.arsnouveau.client.gui.buttons.ColorPresetButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.SelectedParticleButton;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.RainbowParticleColor;
import com.hollingsworth.nuggets.client.gui.NoShadowTextField;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ColorProperty extends BaseProperty<ColorProperty> {

    public static MapCodec<ColorProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ParticleColor.CODEC.fieldOf("particleColor").forGetter(i -> i.particleColor),
            Codec.BOOL.fieldOf("tintDisabled").orElse(false).forGetter(i -> i.tintDisabled)
    ).apply(instance, ColorProperty::new));

    public static StreamCodec<RegistryFriendlyByteBuf, ColorProperty> STREAM_CODEC = StreamCodec.composite(ParticleColor.STREAM,
            (i) -> i.particleColor,
            ByteBufCodecs.BOOL,
            ColorProperty::isTintDisabled,
            ColorProperty::new);

    public ParticleColor particleColor;
    boolean tintDisabled = false;

    public ColorProperty(ParticleColor property, boolean tintDisabled) {
        super();
        this.particleColor = property;
        this.tintDisabled = tintDisabled;
        this.displayColor = particleColor;
    }

    public ColorProperty() {
        this(ParticleColor.defaultParticleColor(), true);
    }

    public ParticleColor color() {
        return tintDisabled ? ParticleColor.WHITE : particleColor;
    }

    public boolean isTintDisabled() {
        return tintDisabled;
    }

    private ParticleColor displayColor;

    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        ColorProperty property = this;
        return new ParticleConfigWidgetProvider(x, y, width, height) {
            HueSlider hueSlider;
            SatLumSlider saturation;
            SatLumSlider lightness;
            SelectedParticleButton rainbowButton;
            SelectedParticleButton noneButton;
            List<SelectedParticleButton> selectableButtons = new ArrayList<>();
            NoShadowTextField textField;
            boolean syncingSliders;

            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.drawHeader(getName(), graphics, x, y, width, mouseX, mouseY, partialTicks);

                int xOffset = x + 7;
                int yOffset = y + 18;

                DocClientUtils.blit(graphics, DocAssets.SPELLSTYLE_COLOR_PREVIEW, xOffset, yOffset);
                int hueOffset = 35;
                DocClientUtils.drawParagraph(Component.translatable("ars_nouveau.hue"), graphics, x + 8, y + hueOffset, width, mouseX, mouseY, partialTicks);
                DocClientUtils.drawParagraph(Component.translatable("ars_nouveau.sat"), graphics, x + 8, y + hueOffset + 20, width, mouseX, mouseY, partialTicks);
                DocClientUtils.drawParagraph(Component.translatable("ars_nouveau.lightness"), graphics, x + 8, y + hueOffset + 40, width, mouseX, mouseY, partialTicks);
            }

            @Override
            public void renderBg(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                super.renderBg(graphics, mouseX, mouseY, partialTicks);
                Color color = new Color(displayColor.transition(ClientInfo.ticksInGame * 20).getColor(), false);

                int xOffset = 160;
                int yOffset = 35;
                graphics.fill(xOffset + 2, yOffset + 3, xOffset - 2 + DocAssets.SPELLSTYLE_COLOR_PREVIEW.width(), yOffset - 3 + DocAssets.SPELLSTYLE_COLOR_PREVIEW.height(), color.getRGB());

                graphics.fill(xOffset + 3, yOffset + 2, xOffset - 3 + DocAssets.SPELLSTYLE_COLOR_PREVIEW.width(), yOffset - 2 + DocAssets.SPELLSTYLE_COLOR_PREVIEW.height(), color.getRGB());

            }

            @Override
            public void addWidgets(List<AbstractWidget> widgets) {
                int hueOffset = 45;
                int xOffset = x + 7;
                hueSlider = new HueSlider(xOffset, y + hueOffset, false, () -> HSLColor.hsl(hueSlider.getValueInt(), saturation.getValue(), lightness.getValue()), (val) -> {
                    setColorFromSliders();
                });
                saturation = new SatLumSlider(xOffset, y + hueOffset + 20, false, false, () -> HSLColor.hsl(hueSlider.getValueInt(), saturation.getValue(), lightness.getValue()), (val) -> {
                    setColorFromSliders();
                });
                lightness = new SatLumSlider(xOffset, y + hueOffset + 40, false, true, () -> HSLColor.hsl(hueSlider.getValueInt(), saturation.getValue(), lightness.getValue()), (val) -> {
                    setColorFromSliders();
                });

                textField = new NoShadowTextField(Minecraft.getInstance().font, xOffset, y + 17, 100, 20, Component.empty());
                setSlidersFromColor(particleColor);
                int numPerRow = 4;
                int perXOffset = 13;
                int perYOffset = 13;
                int yOffset = y + 97;
                for (int i = 0; i < ParticleColor.PRESET_COLORS.size(); i++) {
                    ParticleColor color = ParticleColor.PRESET_COLORS.get(i);
                    var button = new ColorPresetButton(xOffset + (i % numPerRow) * perXOffset, yOffset + (i / numPerRow) * perYOffset, color, (b) -> {
                        setSlidersFromColor(color);
                        applyColor(color);
                    });
                    selectableButtons.add(button);
                    widgets.add(button);
                }
                rainbowButton = new ColorPresetButton(xOffset + 4 * perXOffset, yOffset + 2 * perYOffset, new RainbowParticleColor(), (button) -> {
                    particleColor = new RainbowParticleColor();
                    displayColor = particleColor;
                    tintDisabled = false;
                    propertyHolder.set(getType(), property);
                    select(rainbowButton);
                });
                noneButton = new ColorPresetButton(xOffset + 4 * perXOffset, yOffset + 3 * perYOffset, ParticleColor.NONE, (button) -> {
                    tintDisabled = true;
                    particleColor = ParticleColor.NONE;
                    displayColor = particleColor;
                    propertyHolder.set(getType(), property);
                    select(noneButton);
                });
                rainbowButton.withTooltip(Component.translatable("ars_nouveau.color_rainbow"));
                noneButton.withTooltip(Component.translatable("ars_nouveau.color_none"));
                selectableButtons.add(noneButton);
                selectableButtons.add(rainbowButton);
                textField.setValue(displayColor.toHex());
                textField.textColor = displayColor.getOppositeColor().getColor();
                textField.setResponder((val) -> {
                    if (val.length() == 7) {
                        ParticleColor color = ParticleColor.fromHex(val);
                        setSlidersFromColor(color);
                        applyColor(color);
                    }
                });
                textField.setMaxLength(7);
                textField.setFilter((s) -> {
                    if (!s.startsWith("#")) {
                        return false;
                    }
                    if (s.length() > 7) {
                        return false;
                    }
                    if (s.length() < 7) {
                        return true;
                    }
                    try {
                        ParticleColor.fromHex(s);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                });
                widgets.add(rainbowButton);
                widgets.add(textField);
                widgets.add(hueSlider);
                widgets.add(saturation);
                widgets.add(lightness);
                widgets.add(noneButton);
                updateSelectedColor();
            }

            public void setColorFromSliders() {
                if (syncingSliders) {
                    return;
                }
                applyColor(HSLColor.hsl(hueSlider.getValueInt(), saturation.getValue(), lightness.getValue()).toColor().toParticle());
            }

            public void applyColor(ParticleColor color) {
                tintDisabled = false;
                particleColor = color;
                displayColor = color;
                propertyHolder.set(getType(), property);
                textField.setTextColor(color.getOppositeColor().getColor());
                if (!textField.value.equals(color.toHex())) {
                    textField.value = color.toHex();
                }
                updateSelectedColor();
            }

            public void select(SelectedParticleButton active) {
                for (SelectedParticleButton button : selectableButtons) {
                    button.selected = button == active;
                }
            }

            public void updateSelectedColor() {
                SelectedParticleButton match = null;
                if (tintDisabled) {
                    match = noneButton;
                } else if (particleColor instanceof RainbowParticleColor) {
                    match = rainbowButton;
                } else {
                    String hex = particleColor.toHex();
                    for (SelectedParticleButton button : selectableButtons) {
                        if (button instanceof ColorPresetButton presetButton && hex.equals(presetButton.particleColor.toHex())) {
                            match = button;
                            break;
                        }
                    }
                }
                select(match);
            }

            public void setSlidersFromColor(ParticleColor preset) {
                syncingSliders = true;
                HSLColor color = HSLColor.rgb(preset.getRedInt(), preset.getGreenInt(), preset.getBlueInt());
                hueSlider.setValue(color.getHue());
                saturation.setValue(color.getSaturation());
                lightness.setValue(color.getLightness());
                syncingSliders = false;
            }

            @Override
            public void renderIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
                if (particleColor == ParticleColor.NONE) {
                    DocClientUtils.blit(graphics, DocAssets.STYLE_ICON_NONE, x, y);
                    return;
                }
                Color color = new Color(displayColor.transition(ClientInfo.ticksInGame * 20).getColor(), false);
                graphics.fill(x + 3, y + 2, x + 11, y + 3, color.getRGB());
                graphics.fill(x + 2, y + 3, x + 12, y + 11, color.getRGB());
                graphics.fill(x + 3, y + 11, x + 11, y + 12, color.getRGB());
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
        return Objects.equals(particleColor, property.particleColor) && tintDisabled == property.tintDisabled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(particleColor, tintDisabled);
    }
}
