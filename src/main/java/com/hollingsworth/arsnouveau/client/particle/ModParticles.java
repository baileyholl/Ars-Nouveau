package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModParticles {

    @SubscribeEvent
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> event) {
        IForgeRegistry<ParticleType<?>> r = event.getRegistry();
        r.register( new ParticleType<ColorParticleTypeData>(false, ColorParticleTypeData.DESERIALIZER).setRegistryName(ParticleSource.NAME));
        r.register( new ParticleType<ArcParticleTypeData>(false, ArcParticleTypeData.DESERIALIZER).setRegistryName(ParticleArc.NAME));
//        RegistryHelper.register(r, new ParticleType<ElementTypeParticleData>(false, ElementTypeParticleData.DESERIALIZER), ParticleSource.NAME);
//        RegistryHelper.register(r, new ParticleType<ElementTypeParticleData>(false, ElementTypeParticleData.DESERIALIZER), ParticleElementFlow.NAME);
    }

    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void registerFactories(ParticleFactoryRegisterEvent evt) {
        Minecraft.getInstance().particles.registerFactory(ParticleSource.TYPE, ParticleSource.Factory::new);
        Minecraft.getInstance().particles.registerFactory(ParticleArc.TYPE, ParticleArc.Factory::new);
//        Minecraft.getInstance().particles.registerFactory(ParticleElementFlow.TYPE, ParticleElementFlow.Factory::new);
    }

    @SuppressWarnings("deprecation")
    static final IParticleRenderType AN_RENDER = new IParticleRenderType() {
        @Override
        public void beginRender(BufferBuilder buffer, TextureManager textureManager) {
            RenderSystem.depthMask(false);
            textureManager.bindTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.alphaFunc(516, 0.003921569F);
            buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }

        @Override
        public void finishRender(Tessellator tessellator) {
            tessellator.draw();
            RenderSystem.depthMask(true);
        }

        @Override
        public String toString() {
            return "ars_nouveau:renderer";
        }
    };


}