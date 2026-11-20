package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.IParticleColorType;
import com.hollingsworth.arsnouveau.api.particle.IParticleProvider;
import com.hollingsworth.arsnouveau.api.particle.SimpleParticleColorType;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.RainbowParticleColor;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

// TODO: breaking change, remove old registry stuff in favor of the new deferred register system
public class ParticleColorRegistry {

    public static final ResourceKey<Registry<IParticleColorType<?>>> PARTICLE_COLOR_REGISTRY_KEY = ResourceKey.createRegistryKey(ArsNouveau.prefix("particle_color"));

    public static final Registry<IParticleColorType<?>> PARTICLE_COLOR_REGISTRY = new RegistryBuilder<>(PARTICLE_COLOR_REGISTRY_KEY).sync(true).create();
    public static final DeferredRegister<IParticleColorType<?>> PARTICLE_COLOR = DeferredRegister.create(PARTICLE_COLOR_REGISTRY, ArsNouveau.MODID);


    // TODO: to be removed, this is just a fallback for addons that implemented or overrode particle color types before the new system was implemented.
    public static final DeferredHolder<IParticleColorType<?>, IParticleColorType<ParticleColor>> LEGACY_TYPE = PARTICLE_COLOR.register("legacy", () -> new SimpleParticleColorType<>(ParticleColor.OLD_REGISTRY_CODEC, ParticleColor.STREAM));

    public static final DeferredHolder<IParticleColorType<?>, IParticleColorType<ParticleColor>> CONSTANT_TYPE = PARTICLE_COLOR.register("constant", () -> new SimpleParticleColorType<>(ParticleColor.CONSTANT_CODEC, ParticleColor.CONSTANT_STREAM));

    public static final DeferredHolder<IParticleColorType<?>, IParticleColorType<RainbowParticleColor>> RAINBOW_TYPE = PARTICLE_COLOR.register("rainbow", () -> new SimpleParticleColorType<>(RainbowParticleColor.RAINBOW_CODEC, RainbowParticleColor.RAINBOW_STREAM));

    @Deprecated(forRemoval = true)
    private static ConcurrentHashMap<ResourceLocation, IParticleProvider> MAP = new ConcurrentHashMap<>();

    @Deprecated(forRemoval = true)
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
        MAP.put(ParticleColor.ID, DEFAULT);
        MAP.put(RainbowParticleColor.ID, new IParticleProvider() {
            @Override
            public ParticleColor create(CompoundTag tag) {
                return new RainbowParticleColor(tag);
            }

            @Override
            public ParticleColor create(int r, int g, int b) {
                return new RainbowParticleColor();
            }
        });
    }

    @Deprecated(forRemoval = true)
    public static void register(ResourceLocation id, IParticleProvider factory) {
        MAP.put(id, factory);
    }

    @Deprecated(forRemoval = true)
    public static ParticleColor from(@Nullable CompoundTag compoundTag) {
        if (compoundTag == null) {
            return new ParticleColor(0, 0, 0);
        }
        return MAP.getOrDefault(ResourceLocation.tryParse(compoundTag.getString("type")), DEFAULT).create(compoundTag);
    }

    @Deprecated(forRemoval = true)
    public static ParticleColor from(ResourceLocation location, int r, int g, int b) {
        return MAP.getOrDefault(location, DEFAULT).create(r, g, b);
    }
}
