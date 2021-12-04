package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.WildenHunter;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

public class WildenRenderer extends GeoEntityRenderer<WildenHunter> {
    public WildenRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WildenHunterModel());
    }

    @Override
    public RenderType getRenderType(WildenHunter animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityCutoutNoCull(textureLocation);
    }
}
