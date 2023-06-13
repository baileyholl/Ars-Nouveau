package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Lily;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class LilyModel extends AnimatedGeoModel<Lily> {

    @Override
    public void setCustomAnimations(Lily entity, int uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.017453292F);
        head.setRotationY(extraData.netHeadYaw * 0.017453292F);
    }

    @Override
    public ResourceLocation getModelResource(Lily whirlisprig) {
        return new ResourceLocation(ArsNouveau.MODID, "geo/lily.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Lily whirlisprig) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/lily.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Lily whirlisprig) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/lily_animations.json");
    }
}