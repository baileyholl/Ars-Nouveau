package com.hollingsworth.arsnouveau.client.registry;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleType;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleTypeProperty;
import com.hollingsworth.arsnouveau.client.particle.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;


@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MODID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<ColorParticleTypeData>> GLOW_TYPE = PARTICLES.register("glow", GlowParticleType::new);
    public static final DeferredHolder<ParticleType<?>, ParticleType<ColoredDynamicTypeData>> LINE_TYPE = PARTICLES.register("line", LineParticleType::new);
    public static final DeferredHolder<ParticleType<?>, ParticleType<ColoredDynamicTypeData>> SPARKLE_TYPE = PARTICLES.register("sparkle", SparkleParticleType::new);
    public static final DeferredHolder<ParticleType<?>, ParticleType<HelixParticleTypeData>> HELIX_TYPE = PARTICLES.register(HelixParticleData.NAME, HelixParticleType::new);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BUBBLE_TYPE = PARTICLES.register("bubble", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CUSTOM_TYPE = PARTICLES.register("custom", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SMOKE_TYPE = PARTICLES.register("smoke", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SNOW_TYPE = PARTICLES.register("snow", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> NEW_GLOW_TYPE = PARTICLES.register("new_glow", PropertyParticleType::new);

    @SubscribeEvent
    public static void registerFactories(RegisterParticleProvidersEvent evt) {
        Minecraft.getInstance().particleEngine.register(GLOW_TYPE.get(), GlowParticleProvider::new);
        Minecraft.getInstance().particleEngine.register(LINE_TYPE.get(), LineParticleProvider::new);
        Minecraft.getInstance().particleEngine.register(SPARKLE_TYPE.get(), SparkleParticleProvider::new);
        Minecraft.getInstance().particleEngine.register(HELIX_TYPE.get(), HelixParticleData::new);
        Minecraft.getInstance().particleEngine.register(BUBBLE_TYPE.get(), BubbleParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(CUSTOM_TYPE.get(), CustomParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(SMOKE_TYPE.get(), SmokeParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(SNOW_TYPE.get(), SnowParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(NEW_GLOW_TYPE.get(), NewGlowParticleProvider::new);

        ParticleTypeProperty.addType(SNOW_TYPE.get(), new ParticleTypeProperty.ParticleData(SNOW_TYPE.get(), false));
        ParticleTypeProperty.addType(NEW_GLOW_TYPE.get(), new ParticleTypeProperty.ParticleData(NEW_GLOW_TYPE.get(), true));
    }
}