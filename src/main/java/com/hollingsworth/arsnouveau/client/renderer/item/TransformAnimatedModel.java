package com.hollingsworth.arsnouveau.client.renderer.item;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import javax.annotation.Nullable;

public abstract class TransformAnimatedModel<T extends IAnimatable> extends AnimatedGeoModel<T> {


    public ResourceLocation getModelLocation(T object){
        return getModelLocation(object,null);
    }


    public abstract ResourceLocation getModelLocation(T object, @Nullable ItemTransforms.TransformType transformType);

}
