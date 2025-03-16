package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.IParticleProvider;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.RainbowParticleColor;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class ParticleColorRegistry {
    public static final Registry<IParticleProvider> PARTICLE_PROVIDERS = new DefaultedMappedRegistry<>(ParticleColor.ID.getPath(), ResourceKey.createRegistryKey(ArsNouveau.prefix("particle_providers")), Lifecycle.stable(), false);

    static IParticleProvider DEFAULT = new IParticleProvider() {
        @Override
        public ParticleColor create(CompoundTag tag) {
            return new ParticleColor(tag);
        }

        @Override
        public ParticleColor create(int r, int g, int b) {
            return new ParticleColor(r, g, b);
        }
    };

    static {
        Registry.registerForHolder(PARTICLE_PROVIDERS, ParticleColor.ID, DEFAULT);
        Registry.registerForHolder(PARTICLE_PROVIDERS, RainbowParticleColor.ID, new IParticleProvider() {
            @Override
            public ParticleColor create(CompoundTag tag) {
                return new RainbowParticleColor(tag);
            }

            @Override
            public ParticleColor create(int r, int g, int b) {
                return new RainbowParticleColor(r, g, b);
            }
        });
    }

    public static void register(ResourceLocation id, IParticleProvider factory) {
        Registry.registerForHolder(PARTICLE_PROVIDERS, id, factory);
    }

    public static ParticleColor from(@Nullable CompoundTag compoundTag) {
        if (compoundTag == null) {
            return new ParticleColor(0, 0, 0);
        }
        return PARTICLE_PROVIDERS.getOptional(ResourceLocation.tryParse(compoundTag.getString("type"))).orElse(DEFAULT).create(compoundTag);
    }

    public static ParticleColor from(ResourceLocation location, int r, int g, int b) {
        return PARTICLE_PROVIDERS.getOptional(location).orElse(DEFAULT).create(r, g, b);
    }
}
