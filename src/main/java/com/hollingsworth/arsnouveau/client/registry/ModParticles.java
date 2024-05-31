package com.hollingsworth.arsnouveau.client.registry;

import com.hollingsworth.arsnouveau.client.particle.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;


@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);

    public static final RegistryObject<ParticleType<ColorParticleTypeData>> GLOW_TYPE = PARTICLES.register("glow", GlowParticleType::new);
    public static final RegistryObject<ParticleType<ColoredDynamicTypeData>> LINE_TYPE = PARTICLES.register("line", LineParticleType::new);
    public static final RegistryObject<ParticleType<ColoredDynamicTypeData>> SPARKLE_TYPE = PARTICLES.register("sparkle", () -> new SparkleParticleType());
    public static final RegistryObject<ParticleType<HelixParticleTypeData>> HELIX_TYPE = PARTICLES.register(HelixParticleData.NAME, () -> new HelixParticleType());

    @SubscribeEvent
    public static void registerFactories(RegisterParticleProvidersEvent evt) {
        Minecraft.getInstance().particleEngine.register(GLOW_TYPE.get(), GlowParticleProvider::new);
        Minecraft.getInstance().particleEngine.register(LINE_TYPE.get(), LineParticleProvider::new);
        Minecraft.getInstance().particleEngine.register(SPARKLE_TYPE.get(), SparkleParticleProvider::new);
        Minecraft.getInstance().particleEngine.register(HELIX_TYPE.get(), HelixParticleData::new);
    }
}