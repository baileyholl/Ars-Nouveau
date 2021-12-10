package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.client.RenderTypes;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

public class ParticleRenderTypes {

//    static final ParticleRenderType AN_RENDER = new ParticleRenderType() {
//        @Override
//        public void begin(BufferBuilder buffer, TextureManager textureManager) {
//            RenderSystem.depthMask(false);
//            textureManager.bindForSetup(TextureAtlas.LOCATION_PARTICLES);
//            RenderSystem.enableBlend();
//            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//            RenderSystem.alphaFunc(516, 0.003921569F);
//
//            buffer.begin(7, DefaultVertexFormat.PARTICLE);
//        }
//
//        @Override
//        public void end(Tesselator tessellator) {
//            tessellator.end();
//            RenderSystem.depthMask(true);
//        }
//
//        @Override
//        public String toString() {
//            return "ars_nouveau:renderer";
//        }
//    };
    static final ParticleRenderType EMBER_RENDER = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder buffer, TextureManager textureManager) {
//            RenderSystem.disableAlphaTest();
//            RenderSystem.disableDepthTest();

            RenderSystem.enableBlend();

            RenderSystem.enableCull();

            textureManager.bindForSetup(TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.depthMask(false);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE.value);
            RenderSystem.setShader(() -> RenderTypes.CustomRenderTypes.brightSolidShader);

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tessellator) {
            tessellator.end();
//            RenderSystem.enableDepthTest();
////            RenderSystem.enableAlphaTest();
//
//            RenderSystem.depthMask(true);
//            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE.value);
//            RenderSystem.disableCull();

//            RenderSystem.alphaFunc(516, 0.1F);
        }

        @Override
        public String toString() {
            return "ars_nouveau:em_rend";
        }
    };

    static final ParticleRenderType EMBER_RENDER_NO_MASK = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder buffer, TextureManager textureManager) {
//            RenderSystem.disableAlphaTest();
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
//            RenderSystem.enableFog();
//            RenderSystem.alphaFunc(516, 0.3f);

            textureManager.bindForSetup(TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.depthMask(false);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE.value);
            RenderSystem.disableCull();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tessellator) {
            tessellator.end();
            RenderSystem.enableDepthTest();
//            RenderSystem.enableAlphaTest();

            RenderSystem.depthMask(true);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE.value);

//            RenderSystem.alphaFunc(516, 0.1F);
        }

        @Override
        public String toString() {
            return "ars_nouveau:em_rend_no_mask";
        }
    };
}
