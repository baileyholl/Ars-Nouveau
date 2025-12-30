package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.ScryerPlanariumTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ScryerPlanariumRenderer extends GeoBlockRenderer<ScryerPlanariumTile> {

    public ScryerPlanariumRenderer(BlockEntityRendererProvider.Context blockRenderDispatcher) {
        super(new ScryerPlanariumModel());

    }

    @Override
    public void render(ScryerPlanariumTile blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
