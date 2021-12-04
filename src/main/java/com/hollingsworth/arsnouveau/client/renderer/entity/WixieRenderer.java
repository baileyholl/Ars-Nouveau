package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

public class WixieRenderer extends GeoEntityRenderer {
    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/wixie.png");

    public WixieRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WixieModel());
    }

    @Override
    public ResourceLocation getTextureLocation(LivingEntity entity) {
        return WILD_TEXTURE;
    }

    @Override
    public RenderType getRenderType(Object animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityCutoutNoCull(textureLocation);
    }
}
