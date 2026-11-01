package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.StructureProjectorTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class StructureProjectorRenderer extends PlanariumRenderer<StructureProjectorTile> {

    public StructureProjectorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(StructureProjectorTile tile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.render(tile, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
