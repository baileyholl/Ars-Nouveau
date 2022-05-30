package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.common.items.AnimModItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

import javax.annotation.Nullable;

public class GenericItemRenderer extends GeoItemRenderer<AnimModItem> {
    public GenericItemRenderer(AnimatedGeoModel<AnimModItem> modelProvider) {
        super(modelProvider);
    }
    public boolean isTranslucent;
    public GenericItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet, AnimatedGeoModel<AnimModItem> modelProvider) {
        super(dispatcher, modelSet, modelProvider);
    }

    public GenericItemRenderer withTranslucency(){
        this.isTranslucent = true;
        return this;
    }

    @Override
    public RenderType getRenderType(AnimModItem animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return this.isTranslucent ? RenderType.entityTranslucent(textureLocation) : super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }

}
