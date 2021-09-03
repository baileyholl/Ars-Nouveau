package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

public class BookwyrmRenderer extends GeoEntityRenderer {

    public static ResourceLocation BLUE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/book_wyrm_blue.png");;
    public BookwyrmRenderer(EntityRendererManager manager) {
        super(manager, new BookwyrmModel());
    }

    public ResourceLocation getColor(EntityBookwyrm e){
        String color = e.getEntityData().get(EntityBookwyrm.COLOR).toLowerCase();
        if(color.isEmpty())
            return BLUE;
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/book_wyrm_" + color +".png");
    }

    @Override
    public void render(LivingEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        stack.pushPose();
        stack.scale(0.5f, 0.5f, 0.5f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
        stack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(LivingEntity entity) {
        if(!(entity instanceof EntityBookwyrm))
            return new ResourceLocation(ArsNouveau.MODID, "textures/entity/book_wyrm_blue.png");

        return getColor((EntityBookwyrm) entity);
    }

    @Override
    public RenderType getRenderType(Object animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityCutoutNoCull(textureLocation);
    }
}