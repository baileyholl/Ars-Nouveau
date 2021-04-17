package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;


@Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModParticles {
    @ObjectHolder(ArsNouveau.MODID + ":" + GlowParticleData.NAME) public static ParticleType<ColorParticleTypeData> GLOW_TYPE;
    @ObjectHolder(ArsNouveau.MODID + ":" + ParticleLineData.NAME) public static ParticleType<ColoredDynamicTypeData> LINE_TYPE;
    @ObjectHolder(ArsNouveau.MODID + ":" + ParticleSparkleData.NAME) public static ParticleType<ColoredDynamicTypeData> SPARKLE_TYPE;
    @ObjectHolder(ArsNouveau.MODID + ":" + VortexParticleData.NAME) public static ParticleType<ColorParticleTypeData> VORTEX_TYPE;

    @SubscribeEvent
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> event) {
        System.out.println("Rendering particles");
        IForgeRegistry<ParticleType<?>> r = event.getRegistry();
        r.register( new GlowParticleType().setRegistryName(GlowParticleData.NAME));
        r.register( new LineParticleType().setRegistryName(ParticleLineData.NAME));

        r.register( new GlowParticleType().setRegistryName(ParticleSparkleData.NAME));
        r.register( new GlowParticleType().setRegistryName(VortexParticleData.NAME));

    }

    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void registerFactories(ParticleFactoryRegisterEvent evt) {
        System.out.println("Rendering factories");
        Minecraft.getInstance().particleEngine.register(GLOW_TYPE, GlowParticleData::new);
        Minecraft.getInstance().particleEngine.register(LINE_TYPE, ParticleLineData::new);
        Minecraft.getInstance().particleEngine.register(SPARKLE_TYPE, ParticleSparkleData::new);
        Minecraft.getInstance().particleEngine.register(VORTEX_TYPE, VortexParticleData::new);

    }


}