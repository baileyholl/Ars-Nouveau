package com.hollingsworth.arsnouveau.client.renderer.tile;


import com.hollingsworth.arsnouveau.client.ShaderRegistry;
import com.hollingsworth.arsnouveau.common.block.tile.PortalTile;
import com.hollingsworth.arsnouveau.common.block.tile.SkyBlockTile;
import com.hollingsworth.arsnouveau.setup.Config;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@OnlyIn(Dist.CLIENT)
public class SkyBlockRenderer<T extends SkyBlockTile> implements BlockEntityRenderer<T> {

    public SkyBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {

    }

    public void render(SkyBlockTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        renderCube(tileEntityIn, matrixStackIn.last().pose(), bufferIn.getBuffer(ShaderRegistry.SKY_RENDER_TYPE));
    }

    private void renderCube(SkyBlockTile tileEntityIn, Matrix4f p_228883_4_, VertexConsumer p_228883_5_) {
        renderFace(tileEntityIn, p_228883_4_, p_228883_5_, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, Direction.SOUTH);
        renderFace(tileEntityIn, p_228883_4_, p_228883_5_, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Direction.NORTH);
        renderFace(tileEntityIn, p_228883_4_, p_228883_5_, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.EAST);
        renderFace(tileEntityIn, p_228883_4_, p_228883_5_, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.WEST);
        renderFace(tileEntityIn, p_228883_4_, p_228883_5_, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, Direction.DOWN);
        renderFace(tileEntityIn, p_228883_4_, p_228883_5_, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, Direction.UP);
    }

    private void renderFace(SkyBlockTile tileEntityIn, Matrix4f matrix, VertexConsumer buffer, float f, float g, float h, float i, float j, float k, float l, float m, Direction direction) {
      //  if (tileEntityIn.shouldRenderFace(direction)) {
            buffer.vertex(matrix, f, h, j).endVertex();
            buffer.vertex(matrix, g, h, k).endVertex();
            buffer.vertex(matrix, g, i, l).endVertex();
            buffer.vertex(matrix, f, i, m).endVertex();
     //   }
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}