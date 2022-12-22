package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.FalseWeaveTile;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FalseweaveRenderer implements BlockEntityRenderer<FalseWeaveTile> {
    private BlockRenderDispatcher blockRenderer;

    public FalseweaveRenderer(BlockEntityRendererProvider.Context pContext) {
        this.blockRenderer = pContext.getBlockRenderDispatcher();
    }

    @Override
    public void render(FalseWeaveTile tileEntityIn, float partialTick, PoseStack pPoseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState renderState = tileEntityIn.mimicState;
        boolean hasMagicFind = Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(ModPotions.MAGIC_FIND_EFFECT.get());
        if(hasMagicFind)
            return;
        ModelBlockRenderer.enableCaching();
        pPoseStack.pushPose();
        renderBlock(tileEntityIn.getBlockPos(), renderState, pPoseStack, bufferIn, tileEntityIn.getLevel(), false, combinedOverlayIn);
        pPoseStack.popPose();

        ModelBlockRenderer.clearCache();
    }

    private void renderBlock(BlockPos pPos, BlockState pState, PoseStack pPoseStack, MultiBufferSource pBufferSource, Level pLevel, boolean pExtended, int pPackedOverlay) {
        net.minecraftforge.client.ForgeHooksClient.renderPistonMovedBlocks(pPos, pState, pPoseStack, pBufferSource, pLevel, pExtended, pPackedOverlay, blockRenderer == null ? blockRenderer = net.minecraft.client.Minecraft.getInstance().getBlockRenderer() : blockRenderer);
    }

    public int getViewDistance() {
        return 68;
    }
}
