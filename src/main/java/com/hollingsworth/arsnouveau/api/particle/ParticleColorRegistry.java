package com.hollingsworth.arsnouveau.api.particle;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.RainbowParticleColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ParticleColorRegistry {

    private static ConcurrentHashMap<ResourceLocation, Function<CompoundTag, ParticleColor>> MAP = new ConcurrentHashMap<>();

    static{
        MAP.put(ParticleColor.ID, ParticleColor::new);
        MAP.put(RainbowParticleColor.ID, RainbowParticleColor::new);
    }

    public static void register(ResourceLocation id, Function<CompoundTag, ParticleColor> factory){
        MAP.put(id, factory);
    }

    public static ParticleColor from(@Nullable CompoundTag compoundTag){
        if(compoundTag == null){
            return new ParticleColor(0,0,0);
        }
        return MAP.getOrDefault(ResourceLocation.tryParse(compoundTag.getString("type")), ParticleColor::new).apply(compoundTag);
    }
}
