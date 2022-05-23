package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorItem;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ModelLayerRenderer<T extends Entity & IAnimatable> extends GeoLayerRenderer implements IGeoRenderer<T> {
    AnimatedGeoModel geoModelProvider;
    private static Map<Class<? extends ArmorItem>, GeoArmorRenderer> renderers = new ConcurrentHashMap<>();

    static
    {
        AnimationController.addModelFetcher((IAnimatable object) ->
        {
            if (object instanceof Entity)
            {

            }
            return null;
        });
    }
    public ModelLayerRenderer(IGeoRenderer<T> entityRendererIn, AnimatedGeoModel modelProvider) {
        super(entityRendererIn);
        this.geoModelProvider = modelProvider;
    }

    @Override
    public AnimatedGeoModel<T> getGeoModelProvider() {
        return geoModelProvider;
    }

    @Override
    public ResourceLocation getTextureLocation(T instance) {
        return geoModelProvider.getTextureLocation(instance);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, Entity entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        renderCopyModel(geoModelProvider,new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_shades.png"), matrixStackIn, bufferIn, packedLightIn, entityLivingBaseIn, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }


//
//    @Override
//    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, Entity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
//        super.render();
//        matrixStackIn.pushPose();
//
//        GeoModel model = geoModelProvider.getModel(geoModelProvider.getModelLocation(entity));
//
//        IBone head = ((StarbuncleModel)getEntityModel()).getBone("head");
//
//        RenderUtils.translate((GeoBone) head, matrixStackIn);
////        RenderUtils.moveToPivot((GeoBone) head, matrixStackIn);
//        RenderUtils.rotate((GeoBone) head, matrixStackIn);
//        matrixStackIn.translate(0, -0.25, 0.15);
//
//
//        RenderSystem.setShaderTexture(0, getTextureLocation((T)entity));
//        Color renderColor = getRenderColor((T) entity, partialTicks, matrixStackIn, bufferIn, null, packedLightIn);
//        RenderType renderType = getRenderType((T) entity, partialTicks, matrixStackIn, bufferIn, null, packedLightIn, getTextureLocation((T) entity));
//        render(model, (T) entity, partialTicks, renderType, matrixStackIn, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
//        matrixStackIn.popPose();
//    }

}
