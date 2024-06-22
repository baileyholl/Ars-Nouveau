package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WealdWalker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;

import software.bernie.geckolib.animation.AnimationState;
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
    public void setCustomAnimations(W entity, long uniqueID, @Nullable AnimationState<W> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);

        GeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX(extraData.headPitch() * 0.010453292F);
        head.setRotY(extraData.netHeadYaw() * 0.015453292F);
        if (entity.getEntityData().get(WealdWalker.CASTING)) {
            GeoBone frontLeftLeg = this.getAnimationProcessor().getBone("leg_right");
            GeoBone frontRightLeg = this.getAnimationProcessor().getBone("leg_left");
            frontLeftLeg.setRotX(Mth.cos(entity.walkAnimation.position() * 0.6662F) * 1.4F * entity.walkAnimation.speed());
            frontRightLeg.setRotX(Mth.cos(entity.walkAnimation.position() * 0.6662F + (float) Math.PI) * 1.4F * entity.walkAnimation.speed());
        }
    }

    @Override
    public ResourceLocation getModelResource(WealdWalker walker) {
        return walker.isBaby() ? ArsNouveau.prefix( "geo/" + type + "_waddler.geo.json") : ArsNouveau.prefix( "geo/" + type + "_walker.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WealdWalker walker) {
        return walker.isBaby() ? ArsNouveau.prefix( "textures/entity/" + type + "_waddler.png") : ArsNouveau.prefix( "textures/entity/" + type + "_walker.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WealdWalker walker) {
        return walker.isBaby() ? ArsNouveau.prefix( "animations/weald_waddler_animations.json") : ArsNouveau.prefix( "animations/weald_walker_animations.json");
    }
}
