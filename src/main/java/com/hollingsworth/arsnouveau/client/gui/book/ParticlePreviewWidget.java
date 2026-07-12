package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.particle.timelines.IParticleTimeline;
import com.hollingsworth.arsnouveau.api.particle.timelines.ParticleTimelinePreview;
import com.hollingsworth.arsnouveau.client.particle.ParticlePreviewLevel;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fStack;

import java.util.*;

public class ParticlePreviewWidget extends AbstractWidget {
    private static final float PITCH = 30f;
    private static final float YAW = 45f;

    private final Minecraft mc = Minecraft.getInstance();
    private final List<Particle> particles = new ArrayList<>();
    private final PreviewCamera camera = new PreviewCamera();
    private ParticlePreviewLevel previewLevel;
    private ParticleTimelinePreview timelinePreview;
    private ParticleTimelinePreview.Scene scene;

    public ParticlePreviewWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.active = false;
    }

    public void play(IParticleTimeline<?> timeline) {
        if (mc.player == null || mc.level == null) {
            return;
        }
        particles.clear();
        previewLevel = new ParticlePreviewLevel(mc.level, (options, x, y, z, xSpeed, ySpeed, zSpeed) -> {
            Particle particle = mc.particleEngine.makeParticle(options, x, y, z, xSpeed, ySpeed, zSpeed);
            if (particle != null) {
                particles.add(particle);
            }
        });
        Vec3 origin = mc.player.getEyePosition();
        camera.moveTo(origin);
        camera.setAngles(180 + YAW, PITCH);
        timelinePreview = createPreview(timeline, origin);
        if (timelinePreview != null) {
            scene = timelinePreview.scene();
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends IParticleTimeline<T>> ParticleTimelinePreview createPreview(IParticleTimeline<T> timeline, Vec3 origin) {
        return timeline.getType().createPreview((T) timeline, origin).orElse(null);
    }

    public boolean isPlaying() {
        return timelinePreview != null || !particles.isEmpty();
    }

    public void tick() {
        if (timelinePreview != null && !timelinePreview.tick(previewLevel)) {
            timelinePreview = null;
        }
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            particle.tick();
            if (!particle.isAlive()) {
                iterator.remove();
            }
        }
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!isPlaying()) {
            return;
        }

        graphics.flush();
        graphics.enableScissor(getX(), getY(), getX() + width, getY() + height);

        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.translate(getX() + width / 2f, getY() + height / 2f, 50f);
        modelView.scale(scene.scale(), -scene.scale(), 4f);
        modelView.rotateX(PITCH * Mth.DEG_TO_RAD);
        modelView.rotateY(YAW * Mth.DEG_TO_RAD);
        RenderSystem.applyModelViewMatrix();

        LightTexture lightTexture = mc.gameRenderer.lightTexture();
        lightTexture.turnOnLightLayer();
        RenderSystem.enableDepthTest();
        renderGrassField();

        Map<ParticleRenderType, List<Particle>> byRenderType = new LinkedHashMap<>();
        for (Particle particle : particles) {
            byRenderType.computeIfAbsent(particle.getRenderType(), type -> new ArrayList<>()).add(particle);
        }
        for (Map.Entry<ParticleRenderType, List<Particle>> entry : byRenderType.entrySet()) {
            RenderSystem.setShader(GameRenderer::getParticleShader);
            BufferBuilder buffer = entry.getKey().begin(Tesselator.getInstance(), mc.getTextureManager());
            if (buffer == null) {
                continue;
            }
            for (Particle particle : entry.getValue()) {
                particle.render(buffer, camera, partialTicks);
            }
            MeshData mesh = buffer.build();
            if (mesh != null) {
                BufferUploader.drawWithShader(mesh);
            }
        }
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        lightTexture.turnOffLightLayer();

        modelView.popMatrix();
        RenderSystem.applyModelViewMatrix();
        graphics.disableScissor();
    }

    private void renderGrassField() {
        mc.gameRenderer.overlayTexture().setupOverlayColor();
        Lighting.setupLevel();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        PoseStack poseStack = new PoseStack();
        BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
        for (int x = -scene.grassRadiusX(); x <= scene.grassRadiusX(); x++) {
            for (int z = -scene.grassRadiusZ(); z <= scene.grassRadiusZ(); z++) {
                poseStack.pushPose();
                poseStack.translate(x - 0.5, -2, z - 0.5);
                mc.getBlockRenderer().renderSingleBlock(grass, poseStack, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
        }
        bufferSource.endBatch();
        Lighting.setupFor3DItems();
        RenderSystem.enableDepthTest();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }

    private static class PreviewCamera extends Camera {
        void moveTo(Vec3 pos) {
            setPosition(pos);
        }

        void setAngles(float yRot, float xRot) {
            setRotation(yRot, xRot);
        }
    }
}
