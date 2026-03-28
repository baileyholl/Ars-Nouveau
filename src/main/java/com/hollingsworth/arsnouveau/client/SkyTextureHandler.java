package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.mojang.blaze3d.pipeline.TextureTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.InputQuirks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import static com.hollingsworth.arsnouveau.client.ClientInfo.skyRenderTarget;

/**
 * Handles sky texture rendering for Ars Nouveau sky effects.
 *
 * TODO: Sky rendering was completely reworked in MC 1.21.11:
 *   - FogRenderer.setupColor/levelFogColor removed
 *   - RenderSystem.setShader/setShaderColor removed (use RenderPipelines)
 *   - LevelRenderer.renderSky/renderClouds/renderSnowAndRain signatures changed
 *   - RenderLevelStageEvent uses subclasses instead of Stage enum
 * Sky rendering is disabled until proper 1.21.11 implementation is done.
 */
@EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class SkyTextureHandler {

    @SubscribeEvent
    public static void renderSky(RenderLevelStageEvent.AfterSky event) {
        // TODO: Sky rendering disabled pending 1.21.11 render pipeline update
        // The custom sky capture into skyRenderTarget is not functional in 1.21.11
        // until the FogRenderer/LevelRenderer/RenderPipeline APIs are properly ported.
        if (ArsNouveau.optifineLoaded || Config.DISABLE_SKY_SHADER.get()) {
            return;
        }
        // Sky rendering temporarily disabled - see class Javadoc
    }

    public static void setupRenderTarget(int width, int height) {
        if (skyRenderTarget != null) {
            skyRenderTarget.destroyBuffers();
        }
        skyRenderTarget = new TextureTarget("ars_nouveau_sky", width, height, true);
    }
}
