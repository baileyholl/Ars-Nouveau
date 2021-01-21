package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.awt.*;
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
    public void render(MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn, Entity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        stack.push();
        GeoModel model = geoModelProvider.getModel(geoModelProvider.getModelLocation(entity));
        geoModelProvider.setLivingAnimations(entity, this.getUniqueID((T) entity));

        Minecraft.getInstance().textureManager.bindTexture(getTextureLocation((T) entity));
        Color renderColor = getRenderColor((T) entity, partialTicks, stack, bufferIn, null, packedLightIn);
        RenderType renderType = getRenderType((T) entity, partialTicks, stack, bufferIn, null, packedLightIn, getTextureLocation((T) entity));
        render(model, (T) entity, partialTicks, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
        stack.pop();
    }

}
