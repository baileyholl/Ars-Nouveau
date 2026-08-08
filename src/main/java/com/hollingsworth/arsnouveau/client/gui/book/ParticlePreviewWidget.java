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
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4fStack;

import java.util.*;

public class ParticlePreviewWidget extends AbstractWidget {
    // Duplicate from ParticleEngine
    private static final Comparator<ParticleRenderType> RENDER_TYPE_ORDER = ClientHooks.makeParticleRenderTypeComparator(List.of(
            ParticleRenderType.TERRAIN_SHEET,
            ParticleRenderType.PARTICLE_SHEET_OPAQUE,
            ParticleRenderType.PARTICLE_SHEET_LIT,
            ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT,
            ParticleRenderType.CUSTOM));

    private final Minecraft mc = Minecraft.getInstance();
    private final List<Particle> particles = new ArrayList<>();
    private final PreviewCamera camera = new PreviewCamera();
    private ParticlePreviewLevel previewLevel;
    private ParticleTimelinePreview timelinePreview;
    private boolean timelineFinished;
    private int startDelay;

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
            Particle particle = makeParticle(options, x, y, z, xSpeed, ySpeed, zSpeed);
            if (particle != null) {
                particles.add(particle);
            }
        });
        camera.moveTo(Vec3.ZERO);
        camera.setAngles(225f, 30f);
        timelinePreview = createPreview(timeline, previewLevel);
        timelineFinished = false;
        startDelay = 5;
    }

    @SuppressWarnings("unchecked")
    private <T extends ParticleOptions> Particle makeParticle(T options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        ParticleProvider<T> provider = (ParticleProvider<T>) mc.particleEngine.providers.get(BuiltInRegistries.PARTICLE_TYPE.getKey(options.getType()));
        return provider == null ? null : provider.createParticle(options, previewLevel, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @SuppressWarnings("unchecked")
    private <T extends IParticleTimeline<T>> ParticleTimelinePreview createPreview(IParticleTimeline<T> timeline, ParticlePreviewLevel level) {
        return timeline.getType().createPreview((T) timeline, level).orElse(null);
    }

    public boolean isPlaying() {
        return timelinePreview != null && (!timelineFinished || !particles.isEmpty());
    }

    public void dismiss() {
        particles.clear();
        timelinePreview = null;
        timelineFinished = true;
        previewLevel = null;
        startDelay = 0;
    }

    public void tick() {
        if (startDelay > 0) {
            startDelay--;
            return;
        }
        if (timelinePreview != null && !timelineFinished && !timelinePreview.tick(previewLevel)) {
            timelineFinished = true;
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
        modelView.scale(timelinePreview.scale(), -timelinePreview.scale(), 4f);
        modelView.rotateX(30f * Mth.DEG_TO_RAD);
        modelView.rotateY(45f * Mth.DEG_TO_RAD);
        RenderSystem.applyModelViewMatrix();

        renderScene(partialTicks);
        renderParticles(partialTicks);

        modelView.popMatrix();
        RenderSystem.applyModelViewMatrix();
        graphics.disableScissor();
    }

    ParticleTimelinePreview.BlockRenderCallback blockRenderCallback = new ParticleTimelinePreview.BlockRenderCallback() {
        @Override
        public void renderBlock(BlockState state, BlockPos pos) {
            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
            mc.getBlockRenderer().renderSingleBlock(state, poseAt(pos), bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }

        @Override
        public void renderBlock(BlockState state, BlockPos pos, BlockEntity blockEntity) {
            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
            previewLevel.setBlock(pos, state, 0);
            previewLevel.blockEntityMap.put(pos, blockEntity);
            PoseStack poseStack = poseAt(pos);
            var model = mc.getBlockRenderer().getBlockModel(state);
            for (var renderType : model.getRenderTypes(state, RandomSource.create(state.getSeed(pos)), ModelData.EMPTY)) {
                mc.getBlockRenderer().getModelRenderer().tesselateBlock(previewLevel, model, state, pos, poseStack,
                        bufferSource.getBuffer(renderType), false, RandomSource.create(), state.getSeed(pos),
                        OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
            }
        }

        @Override
        public void renderBlockEntity(BlockEntity blockEntity) {
            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
            mc.getBlockEntityRenderDispatcher().renderItem(blockEntity, poseAt(blockEntity.getBlockPos()), bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }
    };

    private void renderScene(float partialTicks) {
        Lighting.setupLevel();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        timelinePreview.renderBlocks(blockRenderCallback);
        timelinePreview.renderEntities(entity -> {
            double x = Mth.lerp(partialTicks, entity.xOld, entity.getX()) - camera.getPosition().x;
            double y = Mth.lerp(partialTicks, entity.yOld, entity.getY()) - camera.getPosition().y;
            double z = Mth.lerp(partialTicks, entity.zOld, entity.getZ()) - camera.getPosition().z;
            float yaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
            mc.getEntityRenderDispatcher().render(entity, x, y, z, yaw, partialTicks, new PoseStack(), bufferSource, LightTexture.FULL_BRIGHT);
        });
        bufferSource.endBatch();
        Lighting.setupFor3DItems();
    }

    private void renderParticles(float partialTicks) {
        LightTexture lightTexture = mc.gameRenderer.lightTexture();
        lightTexture.turnOnLightLayer();
        RenderSystem.enableDepthTest();
        Map<ParticleRenderType, List<Particle>> byRenderType = new TreeMap<>(RENDER_TYPE_ORDER);
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
        lightTexture.turnOffLightLayer();
    }

    private static PoseStack poseAt(BlockPos pos) {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(pos.getX() - 0.5, pos.getY() - 1, pos.getZ() - 0.5);
        return poseStack;
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
