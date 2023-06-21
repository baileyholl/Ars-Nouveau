package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public abstract class ArsGeoBlockRenderer<T extends BlockEntity & GeoBlockEntity> extends GeoBlockRenderer<T> {
    public ArsGeoBlockRenderer(BlockEntityRendererProvider.Context rendererProvider, GeoModel<T> modelProvider) {
        super(rendererProvider, modelProvider);
    }

    @Override
    public void render(T tile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.translate(0, -0.01f, 0);
        super.render(tile, partialTick, poseStack, bufferSource, packedLight);
    }
}
