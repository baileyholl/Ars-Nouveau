package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.setup.config.Config;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

public class ParticleRenderTypes {
    static final ParticleRenderType EMBER_RENDER = new ParticleRenderType() {

        @Override
        public BufferBuilder begin(Tesselator buffer, TextureManager textureManager) {
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
            if(Config.DISABLE_TRANSLUCENT_PARTICLES.get()) {
                RenderSystem.disableBlend();
                RenderSystem.depthMask(true);
                RenderSystem.setShader(GameRenderer::getParticleShader);
            }
            else {
                RenderSystem.enableBlend();
                RenderSystem.depthMask(false);
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE.value);
            }
            RenderSystem.enableCull();
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableDepthTest();
            return buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "ars_nouveau:em_rend";
        }
    };

    static final ParticleRenderType EMBER_RENDER_NO_MASK = new ParticleRenderType() {



        @Override
        public BufferBuilder begin(Tesselator buffer, TextureManager textureManager) {
            if(Config.DISABLE_TRANSLUCENT_PARTICLES.get()) {
                RenderSystem.disableBlend();
                RenderSystem.depthMask(true);
                RenderSystem.setShader(GameRenderer::getParticleShader);
            }
            else {
                RenderSystem.enableBlend();
                RenderSystem.depthMask(false);
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE.value);
            }
            RenderSystem.enableCull();
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.disableDepthTest();
            return buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        //TODO: fix scry particles

//        @Override
//        public void end(Tesselator tessellator) {
//            tessellator.end();
//            RenderSystem.enableDepthTest();
//        }

        @Override
        public String toString() {
            return "ars_nouveau:em_rend_no_mask";
        }
    };
}
