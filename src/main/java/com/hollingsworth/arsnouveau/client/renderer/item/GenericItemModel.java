package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.common.items.AnimBlockItem;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GenericItemModel extends AnimatedGeoModel<AnimBlockItem> {
    AnimatedGeoModel model;
    public GenericItemModel(AnimatedGeoModel model){
        this.model = model;
    }

    @Override
    public ResourceLocation getModelLocation(AnimBlockItem animBlockItem) {
        return model.getModelLocation(null);
    }

    @Override
    public ResourceLocation getTextureLocation(AnimBlockItem animBlockItem) {
        return model.getTextureLocation(null);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(AnimBlockItem animBlockItem) {
        return model.getAnimationFileLocation(null);
    }
}
