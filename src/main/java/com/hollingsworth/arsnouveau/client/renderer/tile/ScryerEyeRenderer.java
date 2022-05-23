package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.ScryersEyeTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class ScryerEyeRenderer extends GeoBlockRenderer<ScryersEyeTile> {
    ScryersEyeModel model;
    public ScryerEyeRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, ScryersEyeModel model) {
        super(rendererDispatcherIn, model);
        this.model = model;
    }

    @Override
    public void render(GeoModel model, ScryersEyeTile pBlockEntity, float pPartialTick, RenderType type, PoseStack pPoseStack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.render(model, pBlockEntity, pPartialTick, type, pPoseStack, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void render(BlockEntity tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        super.render(tile, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    @Override
    public void render(ScryersEyeTile tile, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(tile, partialTicks, stack, bufferIn, packedLightIn);

    }
    public static GenericItemRenderer getISTER(){
        return new GenericItemRenderer(new ScryersEyeModel());
    }
}
