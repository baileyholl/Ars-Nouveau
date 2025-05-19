package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleMotion;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.gui.BookSlider;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
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
        this(propMap);
        this.minDensity = densityMin;
        this.maxDensity = densityMax;
        this.supportsShapes = supportsShapes;
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
            BookSlider densitySlider;
            BookSlider radiusSlider;

            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.drawHeader(getName(), graphics, x, y, width, mouseX, mouseY, partialTicks);
                DocClientUtils.drawHeaderNoUnderline(Component.translatable("ars_nouveau.spawn." + spawnType.name().toLowerCase()), graphics, x, y + 50, width, mouseX, mouseY, partialTicks);
            }

            @Override
            public void addWidgets(List<AbstractWidget> widgets) {
                densitySlider = buildSlider(x + 10, y + 30, minDensity, maxDensity, densityStepSize, 1, Component.translatable("ars_nouveau.density_slider"), Component.empty(), Math.floor((maxDensity + minDensity) / 2.0), (value) -> {
                    density = densitySlider.getValueInt();
                    writeChanges();
                });
                densitySlider.setValue(Mth.clamp(density, minDensity, maxDensity));

                radiusSlider = buildSlider(x + 10, y + 140, 0.05, 1, 0.05, 1, Component.translatable("ars_nouveau.radius_slider"), Component.empty(), 0.1,  (value) -> {
                    radius = radiusSlider.getValue();
                    System.out.println(radius);
                    writeChanges();
                });
                radiusSlider.setValue(radius);


                widgets.add(densitySlider);
                GuiImageButton spawnTypeButton = new GuiImageButton(x + 10, y + 70, 20, 20, ArsNouveau.prefix("gui/particle_config/" + spawnType.name().toLowerCase() + ".png"), (button) -> {
                    GuiImageButton thisButton = (GuiImageButton) button;
                    spawnType = ParticleMotion.SpawnType.values()[(spawnType.ordinal() + 1) % ParticleMotion.SpawnType.values().length];
                    thisButton.image = ArsNouveau.prefix("gui/particle_config/" + spawnType.name().toLowerCase() + ".png");
                    writeChanges();
                });
                if(supportsShapes) {
                    widgets.add(spawnTypeButton);
                    widgets.add(radiusSlider);
                }
            }

            public BookSlider buildSlider(int x, int y, double min, double max, double stepSize, int precision, Component prefix, Component suffix, double currentVal, Consumer<Double> onValueChange) {
                return new BookSlider(x, y, 100, 20, prefix, suffix, min, max, currentVal, stepSize, precision, true, onValueChange);
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
