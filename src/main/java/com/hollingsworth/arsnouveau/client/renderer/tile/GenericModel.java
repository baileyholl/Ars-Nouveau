package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;


public class GenericModel<T extends GeoAnimatable> extends GeoModel<T> {
    public String path;

    public ResourceLocation modelLocation;
    public ResourceLocation textLoc;
    public ResourceLocation animationLoc;
    public String textPathRoot = "block";
    public String name;

    public GenericModel(String name) {
        this.modelLocation = new ResourceLocation(ArsNouveau.MODID, "geo/" + name + ".geo.json");
        this.textLoc = new ResourceLocation(ArsNouveau.MODID, "textures/" + textPathRoot + "/" + name + ".png");
        this.animationLoc = new ResourceLocation(ArsNouveau.MODID, "animations/" + name + "_animations.json");
        this.name = name;
    }

    public GenericModel(String name, String textPath) {
        this(name);
        this.textPathRoot = textPath;
        this.textLoc = new ResourceLocation(ArsNouveau.MODID, "textures/" + textPathRoot + "/" + name + ".png");
    }

    public GenericModel withEmptyAnim(){
        this.animationLoc = new ResourceLocation(ArsNouveau.MODID, "animations/empty.json");
        return this;
    }

    @Override
    public ResourceLocation getModelResource(T GeoAnimatable) {
        return modelLocation;
    }

    @Override
    public ResourceLocation getTextureResource(T GeoAnimatable) {
        return textLoc;
    }

    @Override
    public ResourceLocation getAnimationResource(T GeoAnimatable) {
        return animationLoc;
    }
}