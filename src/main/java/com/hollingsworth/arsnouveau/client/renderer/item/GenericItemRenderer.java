package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.common.items.AnimModItem;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class GenericItemRenderer extends GeoItemRenderer<AnimModItem> {
    public GenericItemRenderer(AnimatedGeoModel<AnimModItem> modelProvider) {
        super(modelProvider);
    }

    public GenericItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet, AnimatedGeoModel<AnimModItem> modelProvider) {
        super(dispatcher, modelSet, modelProvider);
    }

}
