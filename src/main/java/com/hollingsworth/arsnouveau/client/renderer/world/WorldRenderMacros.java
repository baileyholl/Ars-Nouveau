package com.hollingsworth.arsnouveau.client.renderer.world;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import org.joml.Matrix4f;

public class WorldRenderMacros {


    public static void renderBox(final MultiBufferSource.BufferSource buffer,
                                 final PoseStack ps,
                                 final BlockPos posA,
                                 final BlockPos posB,
                                 final int argbColor)
    {
        renderBox(buffer.getBuffer(RenderType.LINES),
                ps,
                posA,
                posB,
                (argbColor >> 16) & 0xff,
                (argbColor >> 8) & 0xff,
                argbColor & 0xff,
                (argbColor >> 24) & 0xff);
    }

    public static void renderBox(final VertexConsumer buffer,
                                 final PoseStack ps,
                                 final BlockPos posA,
                                 final BlockPos posB,
                                 final int red,
                                 final int green,
                                 final int blue,
                                 final int alpha)
    {
        if (alpha == 0)
        {
            return;
        }

        final float minX = Math.min(posA.getX(), posB.getX());
        final float minY = Math.min(posA.getY(), posB.getY());
        final float minZ = Math.min(posA.getZ(), posB.getZ());

        final float maxX = Math.max(posA.getX(), posB.getX()) + 1;
        final float maxY = Math.max(posA.getY(), posB.getY()) + 1;
        final float maxZ = Math.max(posA.getZ(), posB.getZ()) + 1;

        final Matrix4f m = ps.last().pose();
        buffer.defaultColor(red, green, blue, alpha);

        populateCuboid(minX, minY, minZ, maxX, maxY, maxZ, m, buffer);

        buffer.unsetDefaultColor();
    }

    public static void populateCuboid(final float minX,
                                      final float minY,
                                      final float minZ,
                                      final float maxX,
                                      final float maxY,
                                      final float maxZ,
                                      final Matrix4f m,
                                      final VertexConsumer buf)
    {
        // z plane

        buf.vertex(m, minX, maxY, minZ).endVertex();
        buf.vertex(m, maxX, minY, minZ).endVertex();
        buf.vertex(m, minX, minY, minZ).endVertex();

        buf.vertex(m, minX, maxY, minZ).endVertex();
        buf.vertex(m, maxX, maxY, minZ).endVertex();
        buf.vertex(m, maxX, minY, minZ).endVertex();

        buf.vertex(m, minX, maxY, maxZ).endVertex();
        buf.vertex(m, minX, minY, maxZ).endVertex();
        buf.vertex(m, maxX, minY, maxZ).endVertex();

        buf.vertex(m, minX, maxY, maxZ).endVertex();
        buf.vertex(m, maxX, minY, maxZ).endVertex();
        buf.vertex(m, maxX, maxY, maxZ).endVertex();

        // y plane

        buf.vertex(m, minX, minY, maxZ).endVertex();
        buf.vertex(m, minX, minY, minZ).endVertex();
        buf.vertex(m, maxX, minY, minZ).endVertex();

        buf.vertex(m, minX, minY, maxZ).endVertex();
        buf.vertex(m, maxX, minY, minZ).endVertex();
        buf.vertex(m, maxX, minY, maxZ).endVertex();

        buf.vertex(m, minX, maxY, maxZ).endVertex();
        buf.vertex(m, maxX, maxY, minZ).endVertex();
        buf.vertex(m, minX, maxY, minZ).endVertex();

        buf.vertex(m, minX, maxY, maxZ).endVertex();
        buf.vertex(m, maxX, maxY, maxZ).endVertex();
        buf.vertex(m, maxX, maxY, minZ).endVertex();

        // x plane

        buf.vertex(m, minX, minY, maxZ).endVertex();
        buf.vertex(m, minX, maxY, minZ).endVertex();
        buf.vertex(m, minX, minY, minZ).endVertex();

        buf.vertex(m, minX, minY, maxZ).endVertex();
        buf.vertex(m, minX, maxY, maxZ).endVertex();
        buf.vertex(m, minX, maxY, minZ).endVertex();

        buf.vertex(m, maxX, minY, maxZ).endVertex();
        buf.vertex(m, maxX, minY, minZ).endVertex();
        buf.vertex(m, maxX, maxY, minZ).endVertex();

        buf.vertex(m, maxX, minY, maxZ).endVertex();
        buf.vertex(m, maxX, maxY, minZ).endVertex();
        buf.vertex(m, maxX, maxY, maxZ).endVertex();
    }


    public static void renderFillRectangle(final MultiBufferSource.BufferSource buffer,
                                           final PoseStack ps,
                                           final int x,
                                           final int y,
                                           final int z,
                                           final int w,
                                           final int h,
                                           final int argbColor)
    {
        populateRectangle(x,
                y,
                z,
                w,
                h,
                (argbColor >> 16) & 0xff,
                (argbColor >> 8) & 0xff,
                argbColor & 0xff,
                (argbColor >> 24) & 0xff,
                buffer.getBuffer(RenderType.LINES),
                ps.last().pose());
    }

    public static void populateRectangle(final int x,
                                         final int y,
                                         final int z,
                                         final int w,
                                         final int h,
                                         final int red,
                                         final int green,
                                         final int blue,
                                         final int alpha,
                                         final VertexConsumer buffer,
                                         final Matrix4f m)
    {
        if (alpha == 0)
        {
            return;
        }

        buffer.vertex(m, x, y, z).color(red, green, blue, alpha).endVertex();
        buffer.vertex(m, x, y + h, z).color(red, green, blue, alpha).endVertex();
        buffer.vertex(m, x + w, y + h, z).color(red, green, blue, alpha).endVertex();

        buffer.vertex(m, x, y, z).color(red, green, blue, alpha).endVertex();
        buffer.vertex(m, x + w, y + h, z).color(red, green, blue, alpha).endVertex();
        buffer.vertex(m, x + w, y, z).color(red, green, blue, alpha).endVertex();
    }
}
