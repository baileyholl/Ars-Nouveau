package com.hollingsworth.arsnouveau.api.particle;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.RainbowParticleColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

public class ParticleColorRegistry {

    private static ConcurrentHashMap<ResourceLocation, IParticleProvider> MAP = new ConcurrentHashMap<>();

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

    static{
        MAP.put(ParticleColor.ID, DEFAULT);
        MAP.put(RainbowParticleColor.ID, new IParticleProvider() {
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

    public static void register(ResourceLocation id, IParticleProvider factory){
        MAP.put(id, factory);
    }

    public static ParticleColor from(@Nullable CompoundTag compoundTag){
        if(compoundTag == null){
            return new ParticleColor(0,0,0);
        }
        return MAP.getOrDefault(ResourceLocation.tryParse(compoundTag.getString("type")), DEFAULT).create(compoundTag);
    }

    public static ParticleColor from(ResourceLocation location, int r, int g, int b){
        return MAP.getOrDefault(location, DEFAULT).create(r, g, b);
    }
}
