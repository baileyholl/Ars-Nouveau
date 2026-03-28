package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public interface IParticleMotionType<T extends ParticleMotion> {

    MapCodec<T> codec();

    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

    T create();

    T create(PropMap propMap);

    default Component getName() {
        Identifier key = ParticleMotionRegistry.PARTICLE_CONFIG_REGISTRY.getKey(this);
        return Component.translatable(key.getNamespace() + ".particle_config." + key.getPath());
    }

    default Identifier getIconLocation() {
        Identifier key = ParticleMotionRegistry.PARTICLE_CONFIG_REGISTRY.getKey(this);
        return Identifier.fromNamespaceAndPath(key.getNamespace(), "textures/particle_config/" + key.getPath() + ".png");
    }

}
