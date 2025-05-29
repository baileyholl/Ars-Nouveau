package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleMotion;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.gui.HorizontalSlider;
import com.hollingsworth.arsnouveau.client.gui.buttons.SelectedParticleButton;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
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
import java.util.Optional;
import java.util.function.Consumer;

public class ParticleDensityProperty extends Property<ParticleDensityProperty>{
    public static MapCodec<ParticleDensityProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("density").forGetter(i -> i.density),
            Codec.DOUBLE.fieldOf("radius").forGetter(i -> i.radius),
            ANCodecs.createEnumCodec(ParticleMotion.SpawnType.class).optionalFieldOf("spawnType").forGetter(ParticleDensityProperty::spawnType)
    ).apply(instance, ParticleDensityProperty::new));

    public static StreamCodec<RegistryFriendlyByteBuf, ParticleDensityProperty> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ParticleDensityProperty::density,
            ByteBufCodecs.DOUBLE,
            ParticleDensityProperty::radius,
            ByteBufCodecs.optional(ANCodecs.createEnumStreamCodec(ParticleMotion.SpawnType.class)),
            ParticleDensityProperty::spawnType,
            ParticleDensityProperty::new
    );

    private int density;
    private double radius;
    private int maxDensity = 500;
    private int minDensity = 10;
    private int densityStepSize = 10;
    private boolean supportsShapes = true;
    private boolean supportsRadius = true;
    private ParticleMotion.SpawnType spawnType;

    public ParticleDensityProperty(int density, double radius, ParticleMotion.SpawnType spawnType) {
        super();
        this.density = density;
        this.radius = radius;
        this.spawnType = spawnType;
    }

    public ParticleDensityProperty(int density, double radius, Optional<ParticleMotion.SpawnType> spawnType) {
        super();
        this.density = density;
        this.radius = radius;
        this.spawnType = spawnType.orElse(ParticleMotion.SpawnType.SPHERE);
    }

    public ParticleDensityProperty(PropMap propMap, int densityMin, int densityMax, int stepSize, boolean supportsShapes){
        this(propMap, densityMin, densityMax, stepSize, supportsShapes, supportsShapes);
    }

    public ParticleDensityProperty(PropMap propMap, int densityMin, int densityMax, int stepSize, boolean supportsShapes, boolean supportsRadius){
        this(propMap);
        this.minDensity = densityMin;
        this.maxDensity = densityMax;
        this.supportsShapes = supportsShapes;
        this.supportsRadius = supportsRadius;
        this.densityStepSize = stepSize;
    }

    public ParticleDensityProperty(PropMap propMap){
        super(propMap);
        if(!propMap.has(getType())){
            this.density = 5;
            this.radius = 0.1f;
            this.spawnType = ParticleMotion.SpawnType.SPHERE;
        } else {
            ParticleDensityProperty densityProperty = propMap.get(getType());
            this.density = densityProperty.density;
            this.spawnType = densityProperty.spawnType().orElse(ParticleMotion.SpawnType.SPHERE);
            this.radius = densityProperty.radius;
        }
    }

    public int density() {
        return density;
    }

    public Optional<ParticleMotion.SpawnType> spawnType() {
        return Optional.ofNullable(spawnType);
    }

    public double radius() {
        return radius;
    }

    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        return new ParticleConfigWidgetProvider(x, y, width, height) {
            HorizontalSlider densitySlider;
            HorizontalSlider radiusSlider;
            SelectedParticleButton selectedButton;
            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.drawHeader(getName(), graphics, x, y, width, mouseX, mouseY, partialTicks);
                int yOffset = 20;
                if(supportsShapes) {
                    DocClientUtils.drawHeaderNoUnderline(Component.translatable("ars_nouveau.spawn_header", Component.translatable("ars_nouveau.spawn." + spawnType.name().toLowerCase())), graphics, x, y + yOffset, width, mouseX, mouseY, partialTicks);

                    yOffset += 32;

                    DocClientUtils.drawHeaderNoUnderline(Component.translatable("ars_nouveau.density_slider", densitySlider.getValueString()), graphics, x, y + yOffset, width, mouseX, mouseY, partialTicks);
                }else{
                    yOffset += 2;
                    DocClientUtils.drawHeaderNoUnderline(Component.translatable("ars_nouveau.density_slider", densitySlider.getValueString()), graphics, x, y + yOffset, width, mouseX, mouseY, partialTicks);
                }

                if(supportsRadius){
                    yOffset +=  32;
                    DocClientUtils.drawHeaderNoUnderline(Component.translatable("ars_nouveau.radius_slider", radiusSlider.getValueString()), graphics, x, y + yOffset, width, mouseX, mouseY, partialTicks);
                }
            }

            @Override
            public void addWidgets(List<AbstractWidget> widgets) {
                ParticleMotion.SpawnType[] values = ParticleMotion.SpawnType.values();
                int yOffset = 30;
                if(supportsShapes) {
                    for (int i = 0; i < values.length; i++) {
                        ParticleMotion.SpawnType spawnType1 = values[i];
                        SelectedParticleButton spawnTypeButton = new SelectedParticleButton(x + 10 + 20 * i, y + yOffset, fromShape(spawnType1), (button) -> {
                            spawnType = spawnType1;
                            if(button instanceof SelectedParticleButton selectedParticleButton) {
                                if(selectedButton != null){
                                    selectedButton.selected = false;
                                }
                                selectedButton = selectedParticleButton;
                                selectedParticleButton.selected = true;
                            }
                            writeChanges();
                        });
                        spawnTypeButton.withTooltip(Component.translatable("ars_nouveau.spawn." + spawnType1.name().toLowerCase()));
                        if(spawnType == spawnType1){
                            spawnTypeButton.selected = true;
                            selectedButton = spawnTypeButton;
                        }
                        widgets.add(spawnTypeButton);
                    }

                    yOffset += 30;
                }
                yOffset += 4;
                densitySlider = buildSlider(x + 4, y + yOffset, minDensity, maxDensity, densityStepSize, 1, Component.translatable("ars_nouveau.density_slider"), Component.empty(), Math.floor((maxDensity + minDensity) / 2.0), (value) -> {
                    density = densitySlider.getValueInt();
                    writeChanges();
                });
                yOffset += 30;

                densitySlider.setValue(Mth.clamp(density, minDensity, maxDensity));
                widgets.add(densitySlider);

                radiusSlider = buildSlider(x + 4, y + yOffset, 0.05, 1, 0.05, 1, Component.translatable("ars_nouveau.radius_slider"), Component.empty(), 0.1,  (value) -> {
                    radius = radiusSlider.getValue();
                    writeChanges();
                });
                radiusSlider.setValue(radius);

                if(supportsRadius) {
                    widgets.add(radiusSlider);
                }
            }

            public HorizontalSlider buildSlider(int x, int y, double min, double max, double stepSize, int precision, Component prefix, Component suffix, double currentVal, Consumer<Double> onValueChange) {
                return new HorizontalSlider(x, y, DocAssets.SLIDER_BAR_FILLED, DocAssets.SLIDER, prefix, suffix, min, max, currentVal, stepSize, precision, false, onValueChange);
            }

            @Override
            public void renderIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.blit(graphics, fromShape(spawnType), x, y);
            }

            public DocAssets.BlitInfo fromShape(ParticleMotion.SpawnType spawnType){
                if(spawnType == null){
                    return DocAssets.STYLE_ICON_SPHERE;
                }
                return switch (spawnType){
                    case SPHERE -> DocAssets.STYLE_ICON_SPHERE;
                    case CUBE -> DocAssets.STYLE_ICON_CUBE;
                };
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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ParticleDensityProperty that = (ParticleDensityProperty) o;
        return density == that.density && spawnType == that.spawnType && Double.compare(that.radius, radius) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(density, spawnType, radius);
    }
}
