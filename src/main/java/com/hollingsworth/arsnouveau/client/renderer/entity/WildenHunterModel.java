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

        IBone frontLeft = this.getAnimationProcessor().getBone("left_leg");
        IBone frontRight = this.getAnimationProcessor().getBone("right_leg");
        frontLeft.setRotationX(MathHelper.cos(entity.limbSwing * 0.6662F) * 1.4F * entity.limbSwingAmount);
        frontRight.setRotationX(MathHelper.cos(entity.limbSwing * 0.6662F + (float)Math.PI) * 1.4F * entity.limbSwingAmount);
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
