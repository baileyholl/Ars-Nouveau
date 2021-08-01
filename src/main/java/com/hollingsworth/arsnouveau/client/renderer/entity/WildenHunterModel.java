package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WildenHunter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class WildenHunterModel extends AnimatedGeoModel<WildenHunter> {


    @Override
    public void setLivingAnimations(WildenHunter entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
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

        frontLeftLeg.setRotationX(MathHelper.cos(entity.animationPosition * 0.6662F) * 1.4F * entity.animationSpeed);
        frontRightLeg.setRotationX(MathHelper.cos(entity.animationPosition * 0.6662F + (float)Math.PI) * 1.4F * entity.animationSpeed);

    }

    @Override
    public ResourceLocation getModelLocation(WildenHunter hunter) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/wilden_hunter.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(WildenHunter hunter) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/packhunter.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(WildenHunter hunter) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/wilden_hunter_animations.json");
    }
}
