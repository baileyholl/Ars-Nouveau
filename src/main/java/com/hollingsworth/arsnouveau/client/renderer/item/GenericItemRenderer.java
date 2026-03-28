package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.common.items.AnimModItem;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

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

    // GeckoLib 5: getRenderType(R renderState, Identifier texture) — no animatable/bufferSource/partialTick
    @Override
    public RenderType getRenderType(GeoRenderState renderState, Identifier texture) {
        return this.isTranslucent ? RenderTypes.entityTranslucent(texture) : super.getRenderType(renderState, texture);
    }
}
