package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.registry.ParticleConfigRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public interface IConfigurableParticleType<T extends IConfigurableParticle> {

    MapCodec<T> codec();

    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

    T create();

    default Component getName(){
        ResourceLocation key = ParticleConfigRegistry.PARTICLE_CONFIG_REGISTRY.getKey(this);
        return Component.translatable(key.getNamespace() + ".particle_config." + key.getPath());
    }

    default ResourceLocation getIconLocation(){
        ResourceLocation key = ParticleConfigRegistry.PARTICLE_CONFIG_REGISTRY.getKey(this);
        return ResourceLocation.fromNamespaceAndPath(key.getNamespace(), "textures/particle_config/" + key.getPath() + ".png");
    }

}
