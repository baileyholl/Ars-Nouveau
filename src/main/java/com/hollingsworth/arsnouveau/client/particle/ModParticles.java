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

    @SubscribeEvent
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> event) {
        System.out.println("Rendering particles");
        IForgeRegistry<ParticleType<?>> r = event.getRegistry();
        r.register( new ParticleType<ColorParticleTypeData>(false, ColorParticleTypeData.DESERIALIZER).setRegistryName(GlowParticleData.NAME));
        r.register(new ParticleType<>(false, ColoredDynamicTypeData.DESERIALIZER).setRegistryName(ParticleLineData.NAME));

    }

    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void registerFactories(ParticleFactoryRegisterEvent evt) {
        System.out.println("Rendering factories");
//        Minecraft.getInstance().particles.registerFactory(ParticleSource.TYPE, ParticleSource.Factory::new);
//        Minecraft.getInstance().particles.registerFactory(ParticleArc.TYPE, ParticleArc.Factory::new);
        Minecraft.getInstance().particles.registerFactory(GLOW_TYPE, GlowParticleData::new);
        Minecraft.getInstance().particles.registerFactory(LINE_TYPE, ParticleLineData::new);

//        Minecraft.getInstance().particles.registerFactory(ParticleLineGlow.TYPE, ParticleLineGlow.Factory::new);
//        Minecraft.getInstance().particles.registerFactory(ParticleElementFlow.TYPE, ParticleElementFlow.Factory::new);
    }


}