package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.IVariantTextureProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

public class BookwyrmRenderer extends GeoEntityRenderer {

    public static ResourceLocation BLUE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/book_wyrm_blue.png");

    public BookwyrmRenderer(EntityRendererProvider.Context manager) {
        super(manager, new BookwyrmModel());
    }

    @Override
    public void render(LivingEntity entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        stack.pushPose();
        stack.scale(0.5f, 0.5f, 0.5f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
        stack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(LivingEntity entity) {
        if(entity instanceof IVariantTextureProvider variantTextureProvider)
            return variantTextureProvider.getTexture(entity);
        return BLUE;
    }

    @Override
    public RenderType getRenderType(Object animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityCutoutNoCull(textureLocation);
    }
}