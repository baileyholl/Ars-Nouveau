package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Alakarkinos;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class AlakarkinosModel extends GeoModel<Alakarkinos> {
    public static ResourceLocation MODEL = ArsNouveau.prefix("geo/alakarkinos.geo.json");
    public static ResourceLocation TEXTURE = ArsNouveau.prefix("textures/entity/alakarkinos.png");
    public static ResourceLocation ANIMATION = ArsNouveau.prefix("animations/alakarkinos.animation.json");


    @Override
    public void setCustomAnimations(Alakarkinos animatable, long instanceId, AnimationState<Alakarkinos> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        this.getBone("sea_bunny").get().setHidden(animatable.getEntityData().get(Alakarkinos.HAS_HAT));
        this.getBone("hat").get().setHidden(!animatable.getEntityData().get(Alakarkinos.HAS_HAT));
    }

    @Override
    public ResourceLocation getModelResource(Alakarkinos animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Alakarkinos animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Alakarkinos animatable) {
        return ANIMATION;
    }
}
