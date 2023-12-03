package com.hollingsworth.arsnouveau.client.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;


@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);

    public static final RegistryObject<ParticleType<ColorParticleTypeData>> GLOW_TYPE = PARTICLES.register("glow", GlowParticleType::new);
    public static final RegistryObject<ParticleType<ColoredDynamicTypeData>> LINE_TYPE = PARTICLES.register("line", LineParticleType::new);
    public static final RegistryObject<ParticleType<ColoredDynamicTypeData>> SPARKLE_TYPE = PARTICLES.register("sparkle", SparkleParticleType::new);

    @SubscribeEvent
    public static void registerFactories(RegisterParticleProvidersEvent evt) {
        evt.register(GLOW_TYPE.get(), GlowParticleProvider::new);
        evt.register(LINE_TYPE.get(), LineParticleProvider::new);
        evt.register(SPARKLE_TYPE.get(), SparkleParticleProvider::new);
    }


}