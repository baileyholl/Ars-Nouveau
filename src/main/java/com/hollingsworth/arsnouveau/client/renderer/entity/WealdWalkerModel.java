package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WealdWalker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import javax.annotation.Nullable;

public class WealdWalkerModel<W extends WealdWalker> extends GeoModel<W> {
    String type;

    public WealdWalkerModel(String type) {
        super();
        this.type = type;
    }

    @Override
    public void setCustomAnimations(W entity, int uniqueID, @Nullable AnimationState customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.010453292F);
        head.setRotationY(extraData.netHeadYaw * 0.015453292F);
        if (entity.getEntityData().get(WealdWalker.CASTING)) {
            IBone frontLeftLeg = this.getAnimationProcessor().getBone("leg_right");
            IBone frontRightLeg = this.getAnimationProcessor().getBone("leg_left");
            frontLeftLeg.setRotationX(Mth.cos(entity.animationPosition * 0.6662F) * 1.4F * entity.animationSpeed);
            frontRightLeg.setRotationX(Mth.cos(entity.animationPosition * 0.6662F + (float) Math.PI) * 1.4F * entity.animationSpeed);
        }
    }

    @Override
    public ResourceLocation getModelResource(WealdWalker walker) {
        return walker.isBaby() ? new ResourceLocation(ArsNouveau.MODID, "geo/" + type + "_waddler.geo.json") : new ResourceLocation(ArsNouveau.MODID, "geo/" + type + "_walker.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WealdWalker walker) {
        return walker.isBaby() ? new ResourceLocation(ArsNouveau.MODID, "textures/entity/" + type + "_waddler.png") : new ResourceLocation(ArsNouveau.MODID, "textures/entity/" + type + "_walker.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WealdWalker walker) {
        return walker.isBaby() ? new ResourceLocation(ArsNouveau.MODID, "animations/weald_waddler_animations.json") : new ResourceLocation(ArsNouveau.MODID, "animations/weald_walker_animations.json");
    }
}
