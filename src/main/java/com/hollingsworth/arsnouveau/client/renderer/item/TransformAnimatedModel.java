package com.hollingsworth.arsnouveau.client.renderer.item;

import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import javax.annotation.Nullable;

public abstract class TransformAnimatedModel<T extends IAnimatable> extends AnimatedGeoModel<T> {


    public ResourceLocation getModelLocation(T object){
        return getModelLocation(object,null);
    }


    public abstract ResourceLocation getModelLocation(T object, @Nullable ItemCameraTransforms.TransformType transformType);

}
