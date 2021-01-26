package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class CarbuncleShadesModel extends AnimatedGeoModel<EntityCarbuncle> {

    @Override
    public ResourceLocation getModelLocation(EntityCarbuncle object) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/carbuncle_shades.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCarbuncle o) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_shades.png");
    }
//carbuncle_animations
    @Override
    public ResourceLocation getAnimationFileLocation(EntityCarbuncle animatable) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/carbuncle_animations.json");
    }

    @Override
    public void setLivingAnimations(EntityCarbuncle entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("specs");
        try {

            EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
            head.setRotationX(-extraData.headPitch * 0.017453292F);
            head.setRotationY(-extraData.netHeadYaw * 0.017453292F);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
