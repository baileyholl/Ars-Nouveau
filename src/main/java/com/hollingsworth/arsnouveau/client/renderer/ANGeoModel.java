package com.hollingsworth.arsnouveau.client.renderer;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class ANGeoModel<T extends GeoAnimatable> extends GeoModel<T> {
    public Identifier modelLocation;
    public Identifier textLoc;
    public Identifier animationLoc;

    public ANGeoModel(Identifier modelLocation, Identifier textLoc, Identifier animationLoc) {
        this.modelLocation = modelLocation;
        this.textLoc = textLoc;
        this.animationLoc = animationLoc;
    }

    public ANGeoModel(String modelLoc, String textLoc, String animationLoc) {
        this(ArsNouveau.prefix(modelLoc), ArsNouveau.prefix(textLoc), ArsNouveau.prefix(animationLoc));
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return modelLocation;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return textLoc;
    }

    @Override
    public Identifier getAnimationResource(T animatable) {
        return animationLoc;
    }
}
