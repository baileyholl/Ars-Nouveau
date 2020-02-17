package com.hollingsworth.craftedmagic.client.particle;

import com.hollingsworth.craftedmagic.ArsNouveau;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModParticles {
    @ObjectHolder(ArsNouveau.MODID + ":wisp")
    public static ParticleType<WispParticleData> WISP;


    @SubscribeEvent
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> evt) {
        evt.getRegistry().register(new WispParticleType());
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class FactoryHandler {
        @SubscribeEvent
        public static void registerFactories(ParticleFactoryRegisterEvent evt) {
            Minecraft.getInstance().particles.registerFactory(ModParticles.WISP, new WispParticleType.Factory());
        }
    }
}