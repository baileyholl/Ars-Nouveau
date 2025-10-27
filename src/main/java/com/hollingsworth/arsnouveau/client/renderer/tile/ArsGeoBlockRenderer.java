package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public abstract class ArsGeoBlockRenderer<T extends BlockEntity & GeoBlockEntity> extends GeoBlockRenderer<T> {
    public ArsGeoBlockRenderer(BlockEntityRendererProvider.Context rendererProvider, GeoModel<T> modelProvider) {
        super(modelProvider);
    }


    @Override
    public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        try {
            super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
