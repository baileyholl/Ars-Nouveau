package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegisterEvent;


@Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModParticles {

    public static final String particleRegistryKey = "minecraft:particle_types";
    @ObjectHolder(value = ArsNouveau.MODID + ":" + GlowParticleData.NAME, registryName = particleRegistryKey)
    public static ParticleType<ColorParticleTypeData> GLOW_TYPE;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + ParticleLineData.NAME, registryName = particleRegistryKey)
    public static ParticleType<ColoredDynamicTypeData> LINE_TYPE;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + ParticleSparkleData.NAME, registryName = particleRegistryKey)
    public static ParticleType<ColoredDynamicTypeData> SPARKLE_TYPE;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + VortexParticleData.NAME, registryName = particleRegistryKey)
    public static ParticleType<ColorParticleTypeData> VORTEX_TYPE;

    @SubscribeEvent
    public static void registerParticles(RegisterEvent event) {
        if (!event.getRegistryKey().equals(ForgeRegistries.Keys.PARTICLE_TYPES)) return;

        IForgeRegistry<ParticleType<?>> r = event.getForgeRegistry();
        r.register(GlowParticleData.NAME, new GlowParticleType());
        r.register(ParticleLineData.NAME, new LineParticleType());

        r.register((ParticleSparkleData.NAME), new GlowParticleType());
        r.register((VortexParticleData.NAME), new GlowParticleType());

    }

    @SubscribeEvent
    public static void registerFactories(ParticleFactoryRegisterEvent evt) {
        Minecraft.getInstance().particleEngine.register(GLOW_TYPE, GlowParticleData::new);
        Minecraft.getInstance().particleEngine.register(LINE_TYPE, ParticleLineData::new);
        Minecraft.getInstance().particleEngine.register(SPARKLE_TYPE, ParticleSparkleData::new);
        Minecraft.getInstance().particleEngine.register(VORTEX_TYPE, VortexParticleData::new);
    }


}