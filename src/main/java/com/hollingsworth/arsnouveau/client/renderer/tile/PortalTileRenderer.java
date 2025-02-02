package com.hollingsworth.arsnouveau.client.renderer.tile;


import com.hollingsworth.arsnouveau.common.block.PortalBlock;
import com.hollingsworth.arsnouveau.common.block.tile.PortalTile;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;

import java.util.Random;

public class PortalTileRenderer<T extends PortalTile> implements BlockEntityRenderer<T> {
    public PortalTileRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {

    }

    public void render(PortalTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (Config.ALTERNATE_PORTAL_RENDER.get() || tileEntityIn.getBlockState().getValue(PortalBlock.ALTERNATE)) return;
        float f = this.getOffset();
        Matrix4f pose = matrixStackIn.last().pose();
        VertexConsumer buffer = bufferIn.getBuffer(RenderType.endPortal());

        var beAxis = tileEntityIn.getBlockState().getValue(PortalBlock.AXIS);
        this.renderCube(tileEntityIn, beAxis, f, pose, buffer);
    }

    private void renderCube(PortalTile tileEntityIn, Direction.Axis beAxis, float offset, Matrix4f pose, VertexConsumer vertexConsumer) {
        this.renderFace(tileEntityIn, pose, vertexConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH, beAxis);
        this.renderFace(tileEntityIn, pose, vertexConsumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH, beAxis);
        this.renderFace(tileEntityIn, pose, vertexConsumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST, beAxis);
        this.renderFace(tileEntityIn, pose, vertexConsumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST, beAxis);
        this.renderFace(tileEntityIn, pose, vertexConsumer, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN, beAxis);
        this.renderFace(tileEntityIn, pose, vertexConsumer, 0.0F, 1.0F, offset, offset, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP, beAxis);
    }

    private void renderFace(PortalTile tileEntityIn, Matrix4f pose, VertexConsumer vertexConsumer, float x0, float x1, float y0, float y1, float z0, float z1, float z2, float z3, Direction direction, Direction.Axis beAxis) {
        var directionAxis = direction.getAxis();
        if (!tileEntityIn.isHorizontal && (beAxis == Direction.Axis.X && directionAxis == Direction.Axis.Z) || (beAxis == Direction.Axis.Z && directionAxis == Direction.Axis.X)) {
            vertexConsumer.addVertex(pose, x0, y0, z0);
            vertexConsumer.addVertex(pose, x1, y0, z1);
            vertexConsumer.addVertex(pose, x1, y1, z2);
            vertexConsumer.addVertex(pose, x0, y1, z3);
        } else if (tileEntityIn.isHorizontal && direction.getAxis() == Direction.Axis.Y) {
            vertexConsumer.addVertex(pose, x0, y0, z0);
            vertexConsumer.addVertex(pose, x1, y0, z1);
            vertexConsumer.addVertex(pose, x1, y1, z2);
            vertexConsumer.addVertex(pose, x0, y1, z3);
        }
    }

    protected float getOffset() {
        return 0.75F;
    }
}