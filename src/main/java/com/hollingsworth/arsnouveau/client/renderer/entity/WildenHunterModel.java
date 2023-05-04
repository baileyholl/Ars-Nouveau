package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WildenHunter;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class WildenHunterModel extends AnimatedGeoModel<WildenHunter> {


    @Override
    public void setCustomAnimations(WildenHunter entity, int uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.017453292F);
        head.setRotationY(extraData.netHeadYaw * 0.017453292F);
    }

    @Override
    public ResourceLocation getModelResource(WildenHunter hunter) {
        return new ResourceLocation(ArsNouveau.MODID, "geo/wilden_hunter.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WildenHunter hunter) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/wilden_hunter.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WildenHunter hunter) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/wilden_hunter_animations.json");
    }
}
