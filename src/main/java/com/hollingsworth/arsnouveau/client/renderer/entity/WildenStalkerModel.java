package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WildenStalker;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class WildenStalkerModel extends AnimatedGeoModel<WildenStalker> {

    @Override
    public void setLivingAnimations(WildenStalker entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        if(entity.isFlying())
            return;
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
    public ResourceLocation getModelLocation(WildenStalker wildenStalker) {
        return wildenStalker.isFlying() ?  new ResourceLocation(ArsNouveau.MODID , "geo/stalker_flying.geo.json") : new ResourceLocation(ArsNouveau.MODID , "geo/stalker_standing.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(WildenStalker wildenStalker) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/stalker_angry.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(WildenStalker wildenStalker) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/wilden_stalker_standing.geo.json");
    }

}
