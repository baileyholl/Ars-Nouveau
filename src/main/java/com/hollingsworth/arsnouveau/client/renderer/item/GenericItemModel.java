package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.common.items.AnimBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericItemModel extends GeoModel<AnimBlockItem> {
    GeoModel model;

    public GenericItemModel(GeoModel model) {
        this.model = model;
    }

    @Override
    public ResourceLocation getModelResource(AnimBlockItem animBlockItem) {
        return model.getModelResource(null);
    }

    @Override
    public ResourceLocation getTextureResource(AnimBlockItem animBlockItem) {
        return model.getTextureResource(null);
    }

    @Override
    public ResourceLocation getAnimationResource(AnimBlockItem animBlockItem) {
        return model.getAnimationResource(null);
    }
}
