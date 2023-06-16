package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.GiftStarbuncle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationState;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class GiftStarbyModel extends AnimatedGeoModel<GiftStarbuncle> {
    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/gift_starby.png");
    public static final ResourceLocation NORMAL_MODEL = new ResourceLocation(ArsNouveau.MODID, "geo/gift_starby.geo.json");
    public static final ResourceLocation ANIMATIONS = new ResourceLocation(ArsNouveau.MODID, "animations/starbuncle_animations.json");

    @Override
    public void setCustomAnimations(GiftStarbuncle entity, int uniqueID, @Nullable AnimationState customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        if (entity.isTaming())
            return;
        IBone head = this.getAnimationProcessor().getBone("head");
        if (customPredicate == null)
            return;
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.017453292F);
        head.setRotationY(extraData.netHeadYaw * 0.017453292F);
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
