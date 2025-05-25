package com.hollingsworth.arsnouveau.api.particle;

import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleTypeProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public class PropertyParticleOptions implements ParticleOptions {

    public static final MapCodec<PropertyParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    PropMap.CODEC.fieldOf("properties").forGetter(i -> i.map)
            )
            .apply(instance, PropertyParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PropertyParticleOptions> STREAM_CODEC = StreamCodec.of(
            PropertyParticleOptions::toNetwork, PropertyParticleOptions::fromNetwork
    );

    public static void toNetwork(RegistryFriendlyByteBuf buf, PropertyParticleOptions data) {
        PropMap.STREAM_CODEC.encode(buf, data.map);
    }

    public static PropertyParticleOptions fromNetwork(RegistryFriendlyByteBuf buffer) {
        PropMap propMap = PropMap.STREAM_CODEC.decode(buffer);
        return new PropertyParticleOptions(propMap);
    }

    public PropMap map;

    public static PropertyParticleOptions defaultGlow() {
        return new PropertyParticleOptions(defaultPropMap());
    }

    public PropertyParticleOptions(PropMap propMap) {
        this.map = propMap;
    }

    public PropertyParticleOptions(ParticleType<?> type) {
        this(new PropMap());
        this.map.set(ParticlePropertyRegistry.TYPE_PROPERTY.get(), new ParticleTypeProperty(type, new PropMap()));
    }

    public static PropMap defaultPropMap(){
        PropMap propMap = new PropMap();
        propMap.set(ParticlePropertyRegistry.TYPE_PROPERTY.get(), new ParticleTypeProperty(ModParticles.NEW_GLOW_TYPE.get(), new PropMap()));
        return propMap;
    }

    public ParticleColor getColor() {
        ParticleTypeProperty typeProperty = map.get(ParticlePropertyRegistry.TYPE_PROPERTY.get());
        if (typeProperty != null) {
            return typeProperty.getColor().particleColor;
        }
        return ParticleColor.DEFAULT;
    }

    @Override
    public ParticleType<?> getType() {
        return map.getOptional(ParticlePropertyRegistry.TYPE_PROPERTY.get()).orElse(new ParticleTypeProperty(ModParticles.NEW_GLOW_TYPE.get(), new PropMap())).type();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PropertyParticleOptions that = (PropertyParticleOptions) o;
        return Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(map);
    }
}
