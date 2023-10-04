package com.hollingsworth.arsnouveau.client.registry;

import com.hollingsworth.arsnouveau.client.particle.*;
import net.minecraft.client.Minecraft;
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

    public static final RegistryObject<ParticleType<ColorParticleTypeData>> GLOW_TYPE = PARTICLES.register(GlowParticleData.NAME, () -> new GlowParticleType());
    public static final RegistryObject<ParticleType<ColoredDynamicTypeData>> LINE_TYPE = PARTICLES.register(ParticleLineData.NAME, () -> new LineParticleType());
    public static final RegistryObject<ParticleType<ColoredDynamicTypeData>> SPARKLE_TYPE = PARTICLES.register(ParticleSparkleData.NAME, () -> new SparkleParticleType());
    public static final RegistryObject<ParticleType<HelixParticleTypeData>> HELIX_TYPE = PARTICLES.register(HelixParticleData.NAME, () -> new HelixParticleType());

    @SubscribeEvent
    public static void registerFactories(RegisterParticleProvidersEvent evt) {
        Minecraft.getInstance().particleEngine.register(GLOW_TYPE.get(), GlowParticleData::new);
        Minecraft.getInstance().particleEngine.register(LINE_TYPE.get(), ParticleLineData::new);
        Minecraft.getInstance().particleEngine.register(SPARKLE_TYPE.get(), ParticleSparkleData::new);
        Minecraft.getInstance().particleEngine.register(HELIX_TYPE.get(), HelixParticleData::new);
    }


}