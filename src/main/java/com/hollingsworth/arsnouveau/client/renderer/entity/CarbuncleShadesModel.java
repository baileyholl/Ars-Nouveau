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
    public ResourceLocation getModelResource(Starbuncle object) {
        return new ResourceLocation(ArsNouveau.MODID, "geo/carbuncle_shades.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Starbuncle o) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_shades.png");
    }

    //carbuncle_animations
    @Override
    public ResourceLocation getAnimationResource(Starbuncle animatable) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/starbuncle_animations.json");
    }

    @Override
    public void setLivingAnimations(Starbuncle entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone specs = this.getAnimationProcessor().getBone("specs");
        IBone parentHead = modelProvider.getModel(modelProvider.getModelResource(entity)).getBone("specs").get();


    }
}
