package com.hollingsworth.arsnouveau.client.renderer.tile;


import com.hollingsworth.arsnouveau.client.registry.ShaderRegistry;
import com.hollingsworth.arsnouveau.common.block.tile.MirrorWeaveTile;
import com.hollingsworth.arsnouveau.common.block.tile.SkyBlockTile;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;

public class SkyBlockRenderer extends MirrorweaveRenderer<SkyBlockTile> {
    private BlockRenderDispatcher blockRenderer;

    public SkyBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn);
        this.blockRenderer = rendererDispatcherIn.getBlockRenderDispatcher();
    }

    public void render(SkyBlockTile tileEntityIn, float partialTicks, PoseStack pPoseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (tileEntityIn.showFacade() || Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(ModPotions.MAGIC_FIND_EFFECT)) {
            super.render((MirrorWeaveTile) tileEntityIn, partialTicks, pPoseStack, bufferIn, combinedLightIn, combinedOverlayIn);
        } else {
            if (tileEntityIn.renderInvalid) {
                updateCulling(tileEntityIn, tileEntityIn.getStateForCulling());
            }
            if (tileEntityIn.disableRender) {
                return;
            }
            renderCube(tileEntityIn, pPoseStack.last().pose(), bufferIn.getBuffer(ShaderRegistry.SKY_RENDER_TYPE));
        }
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
        if (tileEntityIn.shouldRenderDirection(direction)) {
            buffer.addVertex(matrix, f, h, j);
            buffer.addVertex(matrix, g, h, k);
            buffer.addVertex(matrix, g, i, l);
            buffer.addVertex(matrix, f, i, m);
        }
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}