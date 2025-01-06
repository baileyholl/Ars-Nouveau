package com.hollingsworth.arsnouveau.client.renderer;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class ANGeoModel<T extends GeoAnimatable> extends GeoModel<T> {
    public ResourceLocation modelLocation;
    public ResourceLocation textLoc;
    public ResourceLocation animationLoc;
    public ANGeoModel(ResourceLocation modelLocation, ResourceLocation textLoc, ResourceLocation animationLoc) {
        this.modelLocation = modelLocation;
        this.textLoc = textLoc;
        this.animationLoc = animationLoc;
    }

    public ANGeoModel(String modelLoc, String textLoc, String animationLoc) {
        this(ArsNouveau.prefix( modelLoc), ArsNouveau.prefix(textLoc), ArsNouveau.prefix( animationLoc));
    }

    @Override
    public ResourceLocation getModelResource(T t) {
        return modelLocation;
    }

    @Override
    public ResourceLocation getTextureResource(T t) {
        return textLoc;
    }

    @Override
    public ResourceLocation getAnimationResource(T t) {
        return animationLoc;
    }
}
