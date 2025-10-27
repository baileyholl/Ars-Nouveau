package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import static com.hollingsworth.arsnouveau.client.ClientInfo.skyRenderTarget;

@EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class SkyTextureHandler {

    @SubscribeEvent
    public static void renderSky(RenderLevelStageEvent event) {
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_SKY)) {
            if (ArsNouveau.optifineLoaded || Config.DISABLE_SKY_SHADER.get()) {
                return;
            }
            Minecraft minecraft = Minecraft.getInstance();
            if (skyRenderTarget == null) {
                Window window = minecraft.getWindow();
                setupRenderTarget(window.getWidth(), window.getHeight());
            }
            PoseStack poseStack = event.getPoseStack();
            GameRenderer gameRenderer = minecraft.gameRenderer;
            LevelRenderer levelRenderer = minecraft.levelRenderer;
            Camera camera = gameRenderer.getMainCamera();
            Vec3 cameraPosition = camera.getPosition();
            Matrix4f projectionMatrix = event.getProjectionMatrix();

            float partialTick = event.getPartialTick().getGameTimeDeltaTicks();
            boolean isFoggy = minecraft.level.effects().isFoggyAt(Mth.floor(cameraPosition.x), Mth.floor(cameraPosition.y)) || minecraft.gui.getBossOverlay().shouldCreateWorldFog();

            //setting the render target to our sky target
            skyRenderTarget.bindWrite(true);
            //clearing what was rendered the previous frame
            RenderSystem.clear(16640, Minecraft.ON_OSX);

            FogRenderer.setupColor(camera, partialTick, minecraft.level, minecraft.options.getEffectiveRenderDistance(), gameRenderer.getDarkenWorldAmount(partialTick));
            FogRenderer.levelFogColor();
            //rendering the actual sky
            RenderSystem.setShader(GameRenderer::getPositionShader);
            levelRenderer.renderSky(event.getModelViewMatrix(), projectionMatrix, partialTick, camera, isFoggy, () -> {
                FogRenderer.setupFog(camera, FogRenderer.FogMode.FOG_SKY, gameRenderer.getRenderDistance(), isFoggy, partialTick);
            });

            Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushMatrix();
            modelViewStack.mul(poseStack.last().pose());
            RenderSystem.applyModelViewMatrix();

            //rendering the clouds
            if (minecraft.options.getCloudsType() != CloudStatus.OFF) {
                RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
                levelRenderer.renderClouds(poseStack, event.getModelViewMatrix(), projectionMatrix, partialTick, cameraPosition.x, cameraPosition.y, cameraPosition.z);
            }

            //the rain!
            RenderSystem.depthMask(false);
            levelRenderer.renderSnowAndRain(gameRenderer.lightTexture(), partialTick, cameraPosition.x, cameraPosition.y, cameraPosition.z);
            RenderSystem.depthMask(true);

            modelViewStack.popMatrix();
            RenderSystem.applyModelViewMatrix();
            minecraft.getMainRenderTarget().bindWrite(true);
        }
    }

    public static void setupRenderTarget(int width, int height) {
        if (skyRenderTarget != null) {
            skyRenderTarget.destroyBuffers();
        }
        skyRenderTarget = new TextureTarget(width, height, true, Minecraft.ON_OSX);
    }
}
