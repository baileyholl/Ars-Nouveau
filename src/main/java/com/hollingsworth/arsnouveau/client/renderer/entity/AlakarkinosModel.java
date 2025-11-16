package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
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
        // Show or hide the sea bunny based on whether the hat is equipped
        this.getBone("sea_bunny").ifPresent(
                bone -> bone.setHidden(!animatable.getEntityData().get(Alakarkinos.HAS_HAT)));
        // Hide the default hat bone if a cosmetic item is replacing the hat, or the hat is off
        this.getBone("hat").ifPresent(
                bone -> bone.setHidden(!animatable.getEntityData().get(Alakarkinos.HAS_HAT) || animatable.getCosmetic().getItem() instanceof ICosmeticItem cosmetic && cosmetic.getBone(animatable).equals("hat")));
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
