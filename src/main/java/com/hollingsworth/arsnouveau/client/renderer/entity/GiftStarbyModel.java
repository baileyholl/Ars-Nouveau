package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.GiftStarbuncle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animatable.model.CoreGeoBone;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import javax.annotation.Nullable;

public class GiftStarbyModel extends GeoModel<GiftStarbuncle> {
    private static final ResourceLocation WILD_TEXTURE = ArsNouveau.prefix( "textures/entity/gift_starby.png");
    public static final ResourceLocation NORMAL_MODEL = ArsNouveau.prefix( "geo/gift_starby.geo.json");
    public static final ResourceLocation ANIMATIONS = ArsNouveau.prefix( "animations/starbuncle_animations.json");

    @Override
    public void setCustomAnimations(GiftStarbuncle entity, long uniqueID, @Nullable AnimationState customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        if (entity.isTaming())
            return;

        if (customPredicate == null)
            return;
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX(extraData.headPitch() * 0.017453292F);
        head.setRotY(extraData.netHeadYaw() * 0.017453292F);
    }

    @Override
    public ResourceLocation getModelResource(GiftStarbuncle drygmy) {
        return NORMAL_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(GiftStarbuncle drygmy) {
        return WILD_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(GiftStarbuncle drygmy) {
        return ANIMATIONS;
    }
}
