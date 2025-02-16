package com.hollingsworth.arsnouveau.client.renderer.tile;


import com.hollingsworth.arsnouveau.client.registry.ShaderRegistry;
import com.hollingsworth.arsnouveau.common.block.tile.SkyBlockTile;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ClientHooks;
import org.joml.Matrix4f;

public class SkyBlockRenderer<T extends SkyBlockTile> implements BlockEntityRenderer<T> {
    private BlockRenderDispatcher blockRenderer;

    public SkyBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        this.blockRenderer = rendererDispatcherIn.getBlockRenderDispatcher();
    }

    public void render(SkyBlockTile tileEntityIn, float partialTicks, PoseStack pPoseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if(tileEntityIn.showFacade() || Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(ModPotions.MAGIC_FIND_EFFECT)){
            BlockState renderState = tileEntityIn.mimicState;
            if (renderState == null)
                return;
            ModelBlockRenderer.enableCaching();
            pPoseStack.pushPose();
            renderBlock(tileEntityIn.getBlockPos(), renderState, pPoseStack, bufferIn, tileEntityIn.getLevel(), false, combinedOverlayIn);
            pPoseStack.popPose();
            ModelBlockRenderer.clearCache();
        }else {
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
        if (tileEntityIn.shouldRenderFace(direction)) {
            buffer.addVertex(matrix, f, h, j);
            buffer.addVertex(matrix, g, h, k);
            buffer.addVertex(matrix, g, i, l);
            buffer.addVertex(matrix, f, i, m);
        }
    }

    private void renderBlock(BlockPos pPos, BlockState pState, PoseStack pPoseStack, MultiBufferSource pBufferSource, Level pLevel, boolean pExtended, int pPackedOverlay) {
        ClientHooks.renderPistonMovedBlocks(pPos, pState, pPoseStack, pBufferSource, pLevel, pExtended, pPackedOverlay, blockRenderer == null ? blockRenderer = net.minecraft.client.Minecraft.getInstance().getBlockRenderer() : blockRenderer);
    }


    @Override
    public int getViewDistance() {
        return 256;
    }
}