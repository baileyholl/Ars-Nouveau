package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.PhantomBlockTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import javax.annotation.Nullable;

public class MageBlockRenderer extends GeoBlockRenderer<PhantomBlockTile> {

    public static GenericModel model = new GenericModel("mageblock");

    public MageBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    @Override
    public void render(GeoModel model, PhantomBlockTile animatable, float partialTicks, RenderType type, PoseStack matrixStackIn, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder,
                       int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        ParticleColor color = animatable.color;
        matrixStackIn.pushPose();
        matrixStackIn.translate(0, -0.01, 0);
        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn,
                color.getRed(), color.getGreen(), color.getBlue(), alpha);
        matrixStackIn.popPose();
    }

    public static GenericItemRenderer getISTER() {
        return new GenericItemRenderer(model);
    }
}