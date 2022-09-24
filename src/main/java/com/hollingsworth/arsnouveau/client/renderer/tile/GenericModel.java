package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GenericModel<T extends IAnimatable> extends AnimatedGeoModel<T> {
    public String path;

    public ResourceLocation modelLocation;
    public ResourceLocation textLoc;
    public ResourceLocation animationLoc;
    public String textPathRoot = "blocks";
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
    public ResourceLocation getModelResource(T iAnimatable) {
        return modelLocation;
    }

    @Override
    public ResourceLocation getTextureResource(T iAnimatable) {
        return textLoc;
    }

    @Override
    public ResourceLocation getAnimationResource(T iAnimatable) {
        return animationLoc;
    }
}