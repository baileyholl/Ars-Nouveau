package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.common.items.AnimModItem;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GenericItemRenderer extends GeoItemRenderer<AnimModItem> {
    public GenericItemRenderer(GeoModel<AnimModItem> modelProvider) {
        super(modelProvider);
    }

    public boolean isTranslucent;

    public GenericItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet, GeoModel<AnimModItem> modelProvider) {
        super(dispatcher, modelSet, modelProvider);
    }

    public GenericItemRenderer withTranslucency() {
        this.isTranslucent = true;
        return this;
    }

    @Override
    public RenderType getRenderType(AnimModItem animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
        return this.isTranslucent ? RenderType.entityTranslucent(texture) : super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
