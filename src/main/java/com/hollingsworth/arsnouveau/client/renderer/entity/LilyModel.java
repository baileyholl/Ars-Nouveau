package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Lily;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import javax.annotation.Nullable;

public class LilyModel extends GeoModel<Lily> {
    public static ResourceLocation TEXTURE = ArsNouveau.prefix("textures/entity/lily.png");
    public static ResourceLocation MODEL = ArsNouveau.prefix("geo/lily.geo.json");
    public static ResourceLocation ANIMATION = ArsNouveau.prefix("animations/lily_animations.json");

    @Override
    public void setCustomAnimations(Lily entity, long uniqueID, @Nullable AnimationState customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        GeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX(extraData.headPitch() * 0.017453292F);
        head.setRotY(extraData.netHeadYaw() * 0.017453292F);
    }

    @Override
    public ResourceLocation getModelResource(Lily whirlisprig) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Lily whirlisprig) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Lily whirlisprig) {
        return ANIMATION;
    }
}