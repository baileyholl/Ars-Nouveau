package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class CarbuncleModel extends AnimatedGeoModel<EntityCarbuncle> {

    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_wild_orange.png");
    private static final ResourceLocation TAMED_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_orange.png");



    @Override
    public void setLivingAnimations(EntityCarbuncle entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.017453292F);
        head.setRotationY(extraData.netHeadYaw * 0.017453292F);

        IBone backLeft = this.getAnimationProcessor().getBone("back_leg_right");

        IBone backRight = this.getAnimationProcessor().getBone("back_leg_right");
        IBone frontLeft = this.getAnimationProcessor().getBone("front_leg_left");
        IBone frontRight = this.getAnimationProcessor().getBone("front_leg_right");
        backRight.setRotationX(MathHelper.cos(entity.animationPosition * 0.6662F) * 1.4F * entity.animationSpeed);
        backLeft.setRotationX(MathHelper.cos(entity.animationPosition * 0.6662F + (float)Math.PI) * 1.4F * entity.animationSpeed);
        frontLeft.setRotationX(MathHelper.cos(entity.animationPosition * 0.6662F) * 1.4F * entity.animationSpeed);
        frontRight.setRotationX(MathHelper.cos(entity.animationPosition * 0.6662F + (float)Math.PI) * 1.4F * entity.animationSpeed);
    }

    @Override
    public ResourceLocation getModelLocation(EntityCarbuncle carbuncle) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/carbuncle.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCarbuncle carbuncle) {
        return carbuncle.isTamed() ? TAMED_TEXTURE : WILD_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntityCarbuncle carbuncle) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/carbuncle_animations.json");
    }


}