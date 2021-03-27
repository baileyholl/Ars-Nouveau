package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WildenGuardian;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class WildenGuardianModel extends AnimatedGeoModel<WildenGuardian> {

    public static final ResourceLocation WARDER_NEUTRAL = new ResourceLocation(ArsNouveau.MODID , "geo/wilden_warder_neutral.geo.json");
    public static final ResourceLocation WARDER_ARMORED = new ResourceLocation(ArsNouveau.MODID , "geo/wilden_warder_defense.geo.json");;
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
        IBone frontLeftArm = this.getAnimationProcessor().getBone("left_arm");
        IBone frontRightArm = this.getAnimationProcessor().getBone("right_arm");


//        this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
//        this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
//        frontLeftArm.setRotationX(MathHelper.cos(entity.limbSwing * 0.6662F) * 1.4F * entity.limbSwingAmount);
//        frontRightArm.setRotationX(MathHelper.cos(entity.limbSwing * 0.6662F + (float)Math.PI) * 1.4F * entity.limbSwingAmount);

        frontLeftLeg.setRotationX(MathHelper.cos(entity.limbSwing * 0.6662F) * 1.4F * entity.limbSwingAmount);
        frontRightLeg.setRotationX(MathHelper.cos(entity.limbSwing * 0.6662F + (float)Math.PI) * 1.4F * entity.limbSwingAmount);
    }

    @Override
    public ResourceLocation getModelLocation(WildenGuardian wildenStalker) {
        return wildenStalker.isArmored() ? WARDER_ARMORED: WARDER_NEUTRAL;
    }

    @Override
    public ResourceLocation getTextureLocation(WildenGuardian wildenStalker) {
        return TEXT;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(WildenGuardian wildenStalker) {
        return ANIM;
    }

}