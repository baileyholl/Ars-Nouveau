package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.GhostWeaveTile;
import com.hollingsworth.arsnouveau.common.block.tile.MirrorWeaveTile;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class GhostweaveRenderer extends MirrorweaveRenderer<GhostWeaveTile> {

    public GhostweaveRenderer(BlockEntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(GhostWeaveTile tileEntityIn, float partialTick, PoseStack pPoseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        boolean hasMagicFind = Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(ModPotions.MAGIC_FIND_EFFECT);
        boolean shouldShow = hasMagicFind || !tileEntityIn.isInvisible();
        if(!shouldShow)
            return;
        super.render((MirrorWeaveTile) tileEntityIn, partialTick, pPoseStack, bufferIn, combinedLightIn, combinedOverlayIn);
    }
}
