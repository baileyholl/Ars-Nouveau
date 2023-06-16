package com.hollingsworth.arsnouveau.client.renderer.item;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.GeoAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import javax.annotation.Nullable;

public abstract class TransformAnimatedModel<T extends GeoAnimatable> extends AnimatedGeoModel<T> {


    public ResourceLocation getModelResource(T object) {
        return getModelResource(object, null);
    }


    public abstract ResourceLocation getModelResource(T object, @Nullable ItemTransforms.TransformType transformType);

}
