package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.common.items.AnimBlockItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class GenericItemRenderer extends GeoItemRenderer<AnimBlockItem> {
    public GenericItemRenderer(AnimatedGeoModel modelProvider) {
        super(new GenericItemModel(modelProvider));
    }
}
