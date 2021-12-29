package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.WhirlisprigTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import javax.annotation.Nullable;

public class WhirlisprigFlowerRenderer extends GeoBlockRenderer<WhirlisprigTile> {
    public static AnimatedGeoModel model = new GenericModel("whirlisprig_blossom");

    public WhirlisprigFlowerRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        this(rendererDispatcherIn, model);
    }

    public WhirlisprigFlowerRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, AnimatedGeoModel<WhirlisprigTile> modelProvider) {
        super(rendererDispatcherIn, modelProvider);
    }

    @Override
    public void render(GeoModel model, WhirlisprigTile animatable, float partialTicks, RenderType type, PoseStack matrixStackIn, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    public static GenericItemRenderer getISTER(){
        return new GenericItemRenderer(model);
    }
}
