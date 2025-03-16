package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.particle.IParticleProvider;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.RainbowParticleColor;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class ParticleColorRegistry {

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
        Registry.registerForHolder(ANRegistries.PARTICLE_PROVIDERS, ParticleColor.ID, DEFAULT);
        Registry.registerForHolder(ANRegistries.PARTICLE_PROVIDERS, RainbowParticleColor.ID, new IParticleProvider() {
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
        Registry.registerForHolder(ANRegistries.PARTICLE_PROVIDERS, id, factory);
    }

    public static ParticleColor from(@Nullable CompoundTag compoundTag) {
        if (compoundTag == null) {
            return new ParticleColor(0, 0, 0);
        }
        return ANRegistries.PARTICLE_PROVIDERS.getOptional(ResourceLocation.tryParse(compoundTag.getString("type"))).orElse(DEFAULT).create(compoundTag);
    }

    public static ParticleColor from(ResourceLocation location, int r, int g, int b) {
        return ANRegistries.PARTICLE_PROVIDERS.getOptional(location).orElse(DEFAULT).create(r, g, b);
    }
}
