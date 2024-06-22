package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WildenHunter;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;

import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class WildenHunterModel extends GeoModel<WildenHunter> {

    @Override
    public void setCustomAnimations(WildenHunter entity, long uniqueID, AnimationState<WildenHunter> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        GeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX(extraData.headPitch() * 0.017453292F);
        head.setRotY(extraData.netHeadYaw() * 0.017453292F);
    }

    @Override
    public ResourceLocation getModelResource(WildenHunter hunter) {
        return ArsNouveau.prefix( "geo/wilden_hunter.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WildenHunter hunter) {
        return ArsNouveau.prefix( "textures/entity/wilden_hunter.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WildenHunter hunter) {
        return ArsNouveau.prefix( "animations/wilden_hunter_animations.json");
    }
}
