package com.hollingsworth.arsnouveau.client.renderer.item;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.Nullable;

public abstract class TransformAnimatedModel<T extends GeoAnimatable> extends GeoModel<T> {


    public ResourceLocation getModelResource(T object) {
        return getModelResource(object, null);
    }


    public abstract ResourceLocation getModelResource(T object, @Nullable ItemDisplayContext transformType);

}
