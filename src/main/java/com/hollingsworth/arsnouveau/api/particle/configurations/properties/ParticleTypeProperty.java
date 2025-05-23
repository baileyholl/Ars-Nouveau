package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.gui.buttons.SelectedParticleButton;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ParticleTypeProperty extends Property<ParticleTypeProperty> {
    public static final Map<ParticleType<? extends PropertyParticleOptions>, ParticleData> PARTICLE_TYPES = new ConcurrentHashMap<>();

    public static void addType(ParticleData data) {
        PARTICLE_TYPES.put(data.type, data);
    }


    public static MapCodec<ParticleTypeProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.PARTICLE_TYPE.byNameCodec().fieldOf("particleType").forGetter(i -> i.type),
            PropMap.CODEC.fieldOf("subProperties").forGetter(i -> i.subProperties)
    ).apply(instance, ParticleTypeProperty::new));

    public static StreamCodec<RegistryFriendlyByteBuf, ParticleTypeProperty> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(BuiltInRegistries.PARTICLE_TYPE.key()),
            ParticleTypeProperty::type,
            PropMap.STREAM_CODEC,
            (i) -> i.subProperties,
            ParticleTypeProperty::new
    );

    protected ParticleData selectedData;
    protected PropMap subProperties;
    protected ParticleType<? extends PropertyParticleOptions> type;

    public ParticleTypeProperty(ParticleType<?> type, PropMap subProperties) {
        super();
        this.subProperties = subProperties;;
        this.type = (ParticleType<? extends PropertyParticleOptions>) type;
        selectedData = PARTICLE_TYPES.get(type);
        if (selectedData == null) {
            System.out.println("UNREGISTERED PARTICLE TYPE FOR " + type);
            selectedData = new ParticleData(ModParticles.NEW_GLOW_TYPE.get(), false);
        }
    }

    public ParticleTypeProperty(PropMap propMap) {
        super(propMap);
        ParticleTypeProperty property = propMap.getOrDefault(getType(), new ParticleTypeProperty(ModParticles.NEW_GLOW_TYPE.get(), new PropMap()));
        this.type = property.type;
        this.subProperties = property.subProperties;
        selectedData = PARTICLE_TYPES.get(type);
        if (selectedData == null) {
            System.out.println("UNREGISTERED PARTICLE TYPE FOR " + type);
            selectedData = new ParticleData(ModParticles.NEW_GLOW_TYPE.get(), false);
        }
    }

    public ParticleType<? extends PropertyParticleOptions> type() {
        return type;
    }

    public ColorProperty getColor(){
        return subProperties.getOrDefault(ParticlePropertyRegistry.COLOR_PROPERTY.get(), new ColorProperty(new PropMap()));
    }

    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        ParticleTypeProperty self = this;
        return new ParticleConfigWidgetProvider(x, y, width, height) {
            SelectedParticleButton selectedParticleButton;
            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.drawHeader(getName(), graphics, x, y, width, mouseX, mouseY, partialTicks);
            }

            @Override
            public void addWidgets(List<AbstractWidget> widgets) {
                var particleEntries = new ArrayList<>(PARTICLE_TYPES.entrySet());
                particleEntries.sort((o1, o2) ->{
                    if(o1.getKey() == ModParticles.NEW_GLOW_TYPE.get()){
                        return -1;
                    }
                    return getTypeName(o1.getKey()).getString().compareTo(getTypeName(o2.getKey()).getString());
                });
                for (int i = 0; i < particleEntries.size(); i++) {
                    var particleType = particleEntries.get(i);
                    SelectedParticleButton button = new SelectedParticleButton(x + 6 + 16 * (i % 7), y + 20 + 20*(i/7), 14, 14, getImagePath(particleType.getKey()), (b) -> {
                        var didHaveColor = selectedData.acceptsColor;
                        selectedData = particleType.getValue();
                        type = particleType.getKey();
                        var nowHasColor = selectedData.acceptsColor;

                        propertyHolder.set(getType(), self);
                        if (didHaveColor != nowHasColor && onDependenciesChanged != null) {
                            onDependenciesChanged.run();
                        }

                        selectedParticleButton.selected = false;
                        if(b instanceof SelectedParticleButton selectedParticleButton1){
                            selectedParticleButton1.selected = true;
                            selectedParticleButton = selectedParticleButton1;
                        }
                    });
                    button.withTooltip(getTypeName(particleType.getKey()));
                    widgets.add(button);
                    if(selectedData.type == particleType.getKey()) {
                        this.selectedParticleButton = button;
                        selectedParticleButton.selected = true;
                    }
                }
            }

            private ResourceLocation getImagePath(ParticleType<?> type) {
                ResourceLocation location = getKey(type);
                return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "textures/particle_config/" + location.getPath() + ".png");
            }

            private ResourceLocation getKey(ParticleType<?> type) {
                return BuiltInRegistries.PARTICLE_TYPE.getKey(type);
            }

            private Component getTypeName(ParticleType<?> type) {
                ResourceLocation location = getKey(type);
                return Component.translatable(location.getNamespace() + ".particle." + location.getPath());
            }

            @Override
            public void renderIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
                graphics.blit(getImagePath(selectedData.type()), x, y, 0, 0, 14, 14, 14, 14);
            }

            @Override
            public Component getButtonTitle() {
                return Component.literal(getName().getString() + ": " + getTypeName(selectedData.type()).getString());
            }
        };
    }

    @Override
    public IPropertyType<ParticleTypeProperty> getType() {
        return ParticlePropertyRegistry.TYPE_PROPERTY.get();
    }

    @Override
    public List<SubProperty<?>> subProperties() {
        if (selectedData == null || !selectedData.acceptsColor) {
            return Collections.emptyList();
        }

        return List.of(new ColorProperty(subProperties, selectedData.useLegacyRGB()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ParticleTypeProperty property = (ParticleTypeProperty) o;
        return Objects.equals(type, property.type) && Objects.equals(subProperties, property.subProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, subProperties);
    }

    public record ParticleData(ParticleType<? extends PropertyParticleOptions> type, Supplier<PropertyParticleOptions> defaultOptions, boolean acceptsColor, boolean useLegacyRGB) {
        public ParticleData(ParticleType<? extends PropertyParticleOptions> type, boolean acceptsColor) {
            this(type, () -> new PropertyParticleOptions(type), acceptsColor, false);
        }

        public ParticleData(ParticleType<? extends PropertyParticleOptions> type, boolean acceptsColor, boolean useLegacyRGB) {
            this(type, () -> new PropertyParticleOptions(type), acceptsColor, useLegacyRGB);
        }
    }
}
