package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;


// GeckoLib 5.4.2: GeoModel.getModelResource and getTextureResource now take GeoRenderState, not T.
public class GenericModel<T extends GeoAnimatable> extends GeoModel<T> {
    public String path;

    public Identifier modelLocation;
    public Identifier textLoc;
    public Identifier animationLoc;
    public String textPathRoot = "block";
    public String name;

    public GenericModel(String name) {
        this.modelLocation = ArsNouveau.prefix(name);
        this.textLoc = ArsNouveau.prefix("textures/" + textPathRoot + "/" + name + ".png");
        this.animationLoc = ArsNouveau.prefix(name + "_animations");
        this.name = name;
    }

    public GenericModel(String name, String textPath) {
        this(name);
        this.textPathRoot = textPath;
        this.textLoc = ArsNouveau.prefix("textures/" + textPathRoot + "/" + name + ".png");
    }

    public GenericModel withEmptyAnim() {
        this.animationLoc = ArsNouveau.prefix("empty");
        return this;
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
