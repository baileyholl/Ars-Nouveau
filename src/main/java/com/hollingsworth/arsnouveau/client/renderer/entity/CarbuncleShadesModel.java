package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;

public class CarbuncleShadesModel extends AnimatedGeoModel<Starbuncle> {
    public GeoModelProvider<Starbuncle> modelProvider;
    public CarbuncleShadesModel(GeoModelProvider<Starbuncle> geoModelProvider) {
        this.modelProvider = geoModelProvider;
    }


    @Override
    public ResourceLocation getModelLocation(Starbuncle object) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/carbuncle_shades.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(Starbuncle o) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_shades.png");
    }
//carbuncle_animations
    @Override
    public ResourceLocation getAnimationFileLocation(Starbuncle animatable) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/starbuncle_animations.json");
    }

    @Override
    public void setLivingAnimations(Starbuncle entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("specs");
        IBone carby = ((StarbuncleModel) modelProvider).getBone("starbuncle");
        IBone parentHead = modelProvider.getModel(modelProvider.getModelLocation(entity)).getBone("head").get();
//        head.setPivotX(parentHead.getPivotX());
//        head.setPivotY(parentHead.getPivotY());
//        head.setPivotZ(parentHead.getPivotZ());
//        head.setRotationX(parentHead.getRotationX());
//        head.setRotationY(parentHead.getRotationY());
//        head.setRotationZ(parentHead.getRotationZ());
//        float scale = 11f;
//        head.setPositionY(carby.getPositionY()/16f);
//        head.setPositionZ(carby.getPositionZ() * -1.2f);


//        try {
//
//            EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
//            head.setRotationX(-extraData.headPitch * 0.017453292F);
//            head.setRotationY(-extraData.netHeadYaw * 0.017453292F);
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }
}
