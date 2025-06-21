package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.DocPlayerData;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.ListParticleWidgetProvider;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.client.gui.documentation.DocEntryButton;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.hollingsworth.arsnouveau.setup.registry.SoundRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class ParticleTypeProperty extends BaseProperty<ParticleTypeProperty> {
    public static final Map<ParticleType<? extends PropertyParticleOptions>, ParticleData> PARTICLE_TYPES = new ConcurrentHashMap<>();

    public static void addType(ParticleData data) {
        PARTICLE_TYPES.put(data.type, data);
    }


    public static MapCodec<ParticleTypeProperty> CODEC = buildCodec(ParticleTypeProperty::new);

    public static StreamCodec<RegistryFriendlyByteBuf, ParticleTypeProperty> STREAM_CODEC = buildStreamCodec(ParticleTypeProperty::new);

    protected static <T extends ParticleTypeProperty> MapCodec<T> buildCodec(BiFunction<ParticleType<?>, PropMap, T> constructor) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.PARTICLE_TYPE.byNameCodec().fieldOf("particleType").forGetter(i -> i.type),
                PropMap.CODEC.fieldOf("subProperties").forGetter(i -> i.subProperties)
        ).apply(instance, constructor));
    }

    protected static <T extends ParticleTypeProperty> StreamCodec<RegistryFriendlyByteBuf, T> buildStreamCodec(BiFunction<ParticleType<?>, PropMap, T> constructor) {
        return StreamCodec.composite(
                ByteBufCodecs.registry(BuiltInRegistries.PARTICLE_TYPE.key()),
                ParticleTypeProperty::type,
                PropMap.STREAM_CODEC,
                (i) -> i.subProperties,
                constructor
        );
    }

    protected ParticleData selectedData;
    protected PropMap subProperties;
    protected ParticleType<? extends PropertyParticleOptions> type;

    public ParticleTypeProperty() {
        this(ModParticles.NEW_GLOW_TYPE.get(), new PropMap());
    }

    public ParticleTypeProperty(ParticleType<?> type, PropMap subProperties) {
        super();
        this.subProperties = subProperties;
        this.type = (ParticleType<? extends PropertyParticleOptions>) type;
        selectedData = PARTICLE_TYPES.get(type);
        if (selectedData == null) {
            System.out.println("UNREGISTERED PARTICLE TYPE FOR " + type);
            System.out.println(BuiltInRegistries.PARTICLE_TYPE.getKey(type));
            selectedData = PARTICLE_TYPES.get(ModParticles.NEW_GLOW_TYPE.get());
        }
        subProperties.getOrCreate(ParticlePropertyRegistry.COLOR_PROPERTY.get(), () -> {
            return new ColorProperty(ParticleColor.defaultParticleColor(), true);
        });
    }

    public ParticleType<? extends PropertyParticleOptions> type() {
        return type;
    }

    public ColorProperty getColor() {
        return subProperties.getOrDefault(ParticlePropertyRegistry.COLOR_PROPERTY.get(), new ColorProperty());
    }

    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        List<Button> buttons = new ArrayList<>();
        var particleEntries = new ArrayList<>(PARTICLE_TYPES.entrySet());
        particleEntries.sort(Comparator.<Map.Entry<ParticleType<? extends PropertyParticleOptions>, ParticleData>>comparingInt(o -> DocPlayerData.favoriteParticles.contains(o.getKey()) ? -1 : 1).thenComparing((o1, o2) -> {
            if (o1.getKey() == ModParticles.NEW_GLOW_TYPE.get()) {
                return -3;
            } else if (o2.getKey() == ModParticles.NEW_GLOW_TYPE.get()) {
                return 3;
            }
            return getTypeName(o1.getKey()).getString().compareTo(getTypeName(o2.getKey()).getString());
        }));
        for (var particleType : particleEntries) {
            ParticleType key = particleType.getKey();
            DocEntryButton button = new DocEntryButton(0, 0, ItemStack.EMPTY, getTypeName(key), (b) -> {
                selectedData = particleType.getValue();
                type = key;
                onDependenciesChanged.run();
            }).setFavoritable(() -> DocPlayerData.favoriteParticles.contains(key), (b) -> {
                if (DocPlayerData.favoriteParticles.contains(key)) {
                    DocPlayerData.favoriteParticles.remove(key);
                } else {
                    DocPlayerData.favoriteParticles.add(key);
                }
            });
            buttons.add(button);
        }

        return new ListParticleWidgetProvider(x, y, width, height, buttons) {
            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.drawHeader(getName(), graphics, x, y, width, mouseX, mouseY, partialTicks);
            }

            @Override
            public void renderIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.blit(graphics, DocAssets.PARTICLES_ICON, x, y);
            }

            @Override
            public Component getButtonTitle() {
                return Component.literal(getName().getString() + ": " + getTypeName(selectedData.type()).getString());
            }

            @Override
            public void getButtonTooltips(List<Component> tooltip) {
                super.getButtonTooltips(tooltip);
                tooltip.add(Component.translatable("ars_nouveau.particle_type_tooltip"));
            }
        };
    }

    private ResourceLocation getKey(ParticleType<?> type) {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(type);
    }


    private Component getTypeName(ParticleType<?> type) {
        ResourceLocation location = getKey(type);
        return Component.translatable(location.getNamespace() + ".particle." + location.getPath());
    }

    @Override
    public IPropertyType getType() {
        return ParticlePropertyRegistry.TYPE_PROPERTY.get();
    }

    @Override
    public List<BaseProperty<?>> subProperties() {
        return selectedData.getProperties(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ParticleTypeProperty property = (ParticleTypeProperty) o;
        return Objects.equals(type, property.type) && Objects.equals(subProperties, property.subProperties) && Objects.equals(getType(), property.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), type, subProperties);
    }

    public record ParticleData(ParticleType<? extends PropertyParticleOptions> type, boolean acceptsColor,
                               boolean useLegacyRGB, boolean supportsSound) {

        public ParticleData(ParticleType<? extends PropertyParticleOptions> type, boolean acceptsColor, boolean useLegacyRGB) {
            this(type, acceptsColor, useLegacyRGB, false);
        }

        public ParticleData(ParticleType<? extends PropertyParticleOptions> type, boolean acceptsColor) {
            this(type, acceptsColor, false);
        }

        public ParticleData withSound() {
            return new ParticleData(type, acceptsColor, useLegacyRGB, true);
        }

        public List<BaseProperty<?>> getProperties(ParticleTypeProperty forProp) {
            List<BaseProperty<?>> properties = new ArrayList<>();
            PropMap subPropMap = forProp.subProperties;
            if (acceptsColor) {
                ColorProperty colorProperty = subPropMap.createIfMissing(new ColorProperty(ParticleColor.defaultParticleColor(), true));
                colorProperty.isLegacyRGB = useLegacyRGB;
                properties.add(colorProperty);
            }
            if (supportsSound) {
                properties.add(subPropMap.createIfMissing(new SoundProperty(new ConfiguredSpellSound(SoundRegistry.POINTED_DRIPSTONE_WATER))));
            }

            return properties;
        }
    }
}
