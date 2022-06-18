package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WildenGuardian;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class WildenGuardianModel extends AnimatedGeoModel<WildenGuardian> {

    public static final ResourceLocation WARDER_NEUTRAL = new ResourceLocation(ArsNouveau.MODID , "geo/wilden_warder_neutral.geo.json");
    public static final ResourceLocation WARDER_ARMORED = new ResourceLocation(ArsNouveau.MODID , "geo/wilden_warder_defense.geo.json");
    public static final ResourceLocation TEXT =  new ResourceLocation(ArsNouveau.MODID, "textures/entity/warder.png");
    public static final ResourceLocation ANIM =  new ResourceLocation(ArsNouveau.MODID, "animations/wilden_warder_animation_neutral.geo.json");

    //wilden_warder_defense

    @Override
    public void setLivingAnimations(WildenGuardian entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.017453292F);
        head.setRotationY(extraData.netHeadYaw * 0.017453292F);

        IBone frontLeftLeg = this.getAnimationProcessor().getBone("left_leg");
        IBone frontRightLeg = this.getAnimationProcessor().getBone("right_leg");

        frontLeftLeg.setRotationX(Mth.cos(entity.animationPosition * 0.6662F) * 1.4F * entity.animationSpeed);
        frontRightLeg.setRotationX(Mth.cos(entity.animationPosition * 0.6662F + (float)Math.PI) * 1.4F * entity.animationSpeed);
    }

    @Override
    public ResourceLocation getModelResource(WildenGuardian wildenStalker) {
        return wildenStalker.isArmored() ? WARDER_ARMORED: WARDER_NEUTRAL;
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