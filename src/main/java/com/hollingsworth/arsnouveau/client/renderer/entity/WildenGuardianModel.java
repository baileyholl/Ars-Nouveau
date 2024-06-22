package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WildenGuardian;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;

import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class WildenGuardianModel extends GeoModel<WildenGuardian> {

    public static final ResourceLocation WARDER_NEUTRAL = ArsNouveau.prefix( "geo/wilden_guardian.geo.json");
    public static final ResourceLocation TEXT = ArsNouveau.prefix( "textures/entity/wilden_guardian.png");
    public static final ResourceLocation ANIM = ArsNouveau.prefix( "animations/wilden_defender_animations.json");


    @Override
    public void setCustomAnimations(WildenGuardian entity, long uniqueID, AnimationState<WildenGuardian> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        GeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX(extraData.headPitch() * 0.017453292F);
        head.setRotY(extraData.netHeadYaw() * 0.017453292F);

        this.getBone("body_spines_retracted").get().setHidden(entity.isArmored());
        this.getBone("left_arm_spines_retracted").get().setHidden(entity.isArmored());
        this.getBone("right_arm_spines_retracted").get().setHidden(entity.isArmored());
        this.getBone("right_leg_spines_retracted").get().setHidden(entity.isArmored());
        this.getBone("left_leg_spines_retracted").get().setHidden(entity.isArmored());

        this.getBone("body_spines_extended").get().setHidden(!entity.isArmored());
        this.getBone("left_arm_spines_extended").get().setHidden(!entity.isArmored());
        this.getBone("right_arm_spines_extended").get().setHidden(!entity.isArmored());
        this.getBone("right_leg_spines_extended").get().setHidden(!entity.isArmored());
        this.getBone("left_leg_spines_extended").get().setHidden(!entity.isArmored());
    }

    @Override
    public ResourceLocation getModelResource(WildenGuardian wildenStalker) {
        return WARDER_NEUTRAL;
    }

    @Override
    public ResourceLocation getTextureResource(WildenGuardian wildenStalker) {
        return TEXT;
    }

    @Override
    public ResourceLocation getAnimationResource(WildenGuardian wildenStalker) {
        return ANIM;
    }

}