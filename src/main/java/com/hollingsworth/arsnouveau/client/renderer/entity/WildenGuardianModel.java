package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WildenGuardian;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class WildenGuardianModel extends AnimatedGeoModel<WildenGuardian> {

    public static final ResourceLocation WARDER_NEUTRAL = new ResourceLocation(ArsNouveau.MODID, "geo/wilden_guardian.geo.json");
    public static final ResourceLocation TEXT = new ResourceLocation(ArsNouveau.MODID, "textures/entity/wilden_guardian.png");
    public static final ResourceLocation ANIM = new ResourceLocation(ArsNouveau.MODID, "animations/wilden_defender_animations.json");


    @Override
    public void setCustomAnimations(WildenGuardian entity, int uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.017453292F);
        head.setRotationY(extraData.netHeadYaw * 0.017453292F);

        this.getBone("body_spines_retracted").setHidden(entity.isArmored());
        this.getBone("left_arm_spines_retracted").setHidden(entity.isArmored());
        this.getBone("right_arm_spines_retracted").setHidden(entity.isArmored());
        this.getBone("right_leg_spines_retracted").setHidden(entity.isArmored());
        this.getBone("left_leg_spines_retracted").setHidden(entity.isArmored());

        this.getBone("body_spines_extended").setHidden(!entity.isArmored());
        this.getBone("left_arm_spines_extended").setHidden(!entity.isArmored());
        this.getBone("right_arm_spines_extended").setHidden(!entity.isArmored());
        this.getBone("right_leg_spines_extended").setHidden(!entity.isArmored());
        this.getBone("left_leg_spines_extended").setHidden(!entity.isArmored());
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