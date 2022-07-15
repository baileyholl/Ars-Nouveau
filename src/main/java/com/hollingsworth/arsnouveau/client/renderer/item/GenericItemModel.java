package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.common.items.AnimBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GenericItemModel extends AnimatedGeoModel<AnimBlockItem> {
    AnimatedGeoModel model;

    public GenericItemModel(AnimatedGeoModel model) {
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
