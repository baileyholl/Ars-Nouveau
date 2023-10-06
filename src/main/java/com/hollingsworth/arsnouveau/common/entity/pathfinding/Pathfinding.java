package com.hollingsworth.arsnouveau.common.entity.pathfinding;

import com.hollingsworth.arsnouveau.common.entity.pathfinding.pathjobs.AbstractPathJob;
import com.hollingsworth.arsnouveau.common.util.Log;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ConcurrentModificationException;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Static class the handles all the Pathfinding.
 */
public final class Pathfinding {
    private static final BlockingQueue<Runnable> jobQueue = new LinkedBlockingDeque<>();
    private static ThreadPoolExecutor executor;

    /**
     * Minecolonies specific thread factory.
     */
    public static class MinecoloniesThreadFactory implements ThreadFactory {
        /**
         * Ongoing thread IDs.
         */
        public static int id;

        @Override
        public Thread newThread(final Runnable runnable) {
            final Thread thread = new Thread(runnable, "AN stolen Minecolonies Pathfinding Worker #" + (id++));
            thread.setDaemon(true);

            thread.setUncaughtExceptionHandler((thread1, throwable) -> Log.getLogger().error("AN stolen Minecolonies Pathfinding Thread errored! ", throwable));
            thread.setContextClassLoader(ClassLoader.getSystemClassLoader());
            return thread;
        }
    }

    /**
     * Creates a new thread pool for pathfinding jobs
     *
     * @return the threadpool executor.
     */
    public static ThreadPoolExecutor getExecutor() {
        if (executor == null) {
            executor = new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, jobQueue, new MinecoloniesThreadFactory());
        }
        return executor;
    }

    /**
     * Stops all running threads in this thread pool
     */
    public static void shutdown() {
        getExecutor().shutdownNow();
        jobQueue.clear();
        executor = null;
    }

    private Pathfinding() {
        //Hides default constructor.
    }

    /**
     * Add a job to the queue for processing.
     *
     * @param job PathJob
     */
    public static void enqueue(final AbstractPathJob job) {
        job.getResult().startJob(getExecutor());
    }

    /**
     * Render debugging information for the pathfinding system.
     *
     * @param frame       entity movement weight.
     * @param matrixStack the matrix stack to apply to.
     */
    @OnlyIn(Dist.CLIENT)
    public static void debugDraw(final double frame, final PoseStack matrixStack) {
        if (AbstractPathJob.lastDebugNodesNotVisited == null) {
            return;
        }

        final Vec3 vec = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition();
        final double dx = vec.x();
        final double dy = vec.y();
        final double dz = vec.z();

        matrixStack.pushPose();
        matrixStack.translate(-dx, -dy, -dz);
        //TODO: restore lighting
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
//        RenderSystem.disableLighting();

        final Set<ModNode> debugNodesNotVisited;
        final Set<ModNode> debugNodesVisited;
        final Set<ModNode> debugNodesPath;

        synchronized (PathingConstants.debugNodeMonitor) {
            debugNodesNotVisited = AbstractPathJob.lastDebugNodesNotVisited;
            debugNodesVisited = AbstractPathJob.lastDebugNodesVisited;
            debugNodesPath = AbstractPathJob.lastDebugNodesPath;
        }

        try {
            for (final ModNode n : debugNodesNotVisited) {
                debugDrawNode(n, 1.0F, 0F, 0F, matrixStack);
            }

            for (final ModNode n : debugNodesVisited) {
                debugDrawNode(n, 0F, 0F, 1.0F, matrixStack);
            }

            for (final ModNode n : debugNodesPath) {
                if (n.isReachedByWorker()) {
                    debugDrawNode(n, 1F, 0.4F, 0F, matrixStack);
                } else {
                    debugDrawNode(n, 0F, 1.0F, 0F, matrixStack);
                }
            }
        } catch (final ConcurrentModificationException exc) {
            Log.getLogger().catching(exc);
        }

        RenderSystem.disableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.enableBlend();
//        RenderSystem.enableLighting();
        matrixStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public static void debugDrawNode(final ModNode n, final float r, final float g, final float b, final PoseStack matrixStack) {
        matrixStack.pushPose();
        matrixStack.translate((double) n.pos.getX() + 0.375, (double) n.pos.getY() + 0.375, (double) n.pos.getZ() + 0.375);

        final Entity entity = Minecraft.getInstance().getCameraEntity();
        final double dx = n.pos.getX() - entity.getX();
        final double dy = n.pos.getY() - entity.getY();
        final double dz = n.pos.getZ() - entity.getZ();
        if (Math.sqrt(dx * dx + dy * dy + dz * dz) <= 5D) {
            renderDebugText(n, matrixStack);
        }

        matrixStack.scale(0.25F, 0.25F, 0.25F);

        final Tesselator tessellator = Tesselator.getInstance();
        final BufferBuilder vertexBuffer = tessellator.getBuilder();

        final Matrix4f matrix4f = matrixStack.last().pose();
        vertexBuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        RenderSystem.setShaderColor(r, g, b, 1.0f);

        //  X+
        vertexBuffer.vertex(matrix4f, 1.0f, 0.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 1.0f, 1.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 0.0f, 1.0f).endVertex();

        //  X-
        vertexBuffer.vertex(matrix4f, 0.0f, 0.0f, 1.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 1.0f, 1.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).endVertex();

        //  Z-
        vertexBuffer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 0.0f, 0.0f).endVertex();

        //  Z+
        vertexBuffer.vertex(matrix4f, 1.0f, 0.0f, 1.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 1.0f, 1.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 1.0f, 1.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 0.0f, 1.0f).endVertex();

        //  Y+
        vertexBuffer.vertex(matrix4f, 1.0f, 1.0f, 1.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 1.0f, 1.0f).endVertex();

        //  Y-
        vertexBuffer.vertex(matrix4f, 0.0f, 0.0f, 1.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 0.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 0.0f, 1.0f).endVertex();

        tessellator.end();

        if (n.parent != null) {
            final float pdx = n.parent.pos.getX() - n.pos.getX() + 0.125f;
            final float pdy = n.parent.pos.getY() - n.pos.getY() + 0.125f;
            final float pdz = n.parent.pos.getZ() - n.pos.getZ() + 0.125f;
            vertexBuffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
            vertexBuffer.vertex(matrix4f, 0.5f, 0.5f, 0.5f).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            vertexBuffer.vertex(matrix4f, pdx / 0.25f, pdy / 0.25f, pdz / 0.25f).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            tessellator.end();
        }

        matrixStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderDebugText(final ModNode n, final PoseStack matrixStack) {
        final String s1 = String.format("F: %.3f [%d]", n.getCost(), n.getCounterAdded());
        final String s2 = String.format("G: %.3f [%d]", n.getScore(), n.getCounterVisited());
        final Font fontrenderer = Minecraft.getInstance().font;

        matrixStack.pushPose();
        matrixStack.translate(0.0F, 0.75F, 0.0F);
        //TODO: restore normal?
        //RenderSystem.normal3f(0.0F, 1.0F, 0.0F);

        final EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        matrixStack.mulPose(renderManager.cameraOrientation());
        matrixStack.scale(-0.014F, -0.014F, 0.014F);
        matrixStack.translate(0.0F, 18F, 0.0F);

        RenderSystem.depthMask(false);

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        RenderSystem.disableTexture();

        final int i = Math.max(fontrenderer.width(s1), fontrenderer.width(s2)) / 2;

        final Matrix4f matrix4f = matrixStack.last().pose();
        final Tesselator tessellator = Tesselator.getInstance();
        final BufferBuilder vertexBuffer = tessellator.getBuilder();
        vertexBuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        vertexBuffer.vertex(matrix4f, (-i - 1), -5.0f, 0.0f).color(0.0F, 0.0F, 0.0F, 0.7F).endVertex();
        vertexBuffer.vertex(matrix4f, (-i - 1), 12.0f, 0.0f).color(0.0F, 0.0F, 0.0F, 0.7F).endVertex();
        vertexBuffer.vertex(matrix4f, (i + 1), 12.0f, 0.0f).color(0.0F, 0.0F, 0.0F, 0.7F).endVertex();
        vertexBuffer.vertex(matrix4f, (i + 1), -5.0f, 0.0f).color(0.0F, 0.0F, 0.0F, 0.7F).endVertex();
        tessellator.end();

        RenderSystem.enableTexture();

        final MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        matrixStack.translate(0.0F, -5F, 0.0F);
        fontrenderer.drawInBatch(s1, -fontrenderer.width(s1) / 2.0f, 0, 0xFFFFFFFF, false, matrix4f, buffer, false, 0, 15728880);
        matrixStack.translate(0.0F, 8F, 0.0F);
        fontrenderer.drawInBatch(s2, -fontrenderer.width(s2) / 2.0f, 0, 0xFFFFFFFF, false, matrix4f, buffer, false, 0, 15728880);

        RenderSystem.depthMask(true);
        matrixStack.translate(0.0F, -8F, 0.0F);
        fontrenderer.drawInBatch(s1, -fontrenderer.width(s1) / 2.0f, 0, 0xFFFFFFFF, false, matrix4f, buffer, false, 0, 15728880);
        matrixStack.translate(0.0F, 8F, 0.0F);
        fontrenderer.drawInBatch(s2, -fontrenderer.width(s2) / 2.0f, 0, 0xFFFFFFFF, false, matrix4f, buffer, false, 0, 15728880);
        buffer.endBatch();

        matrixStack.popPose();
    }
}
