package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.GiftStarbuncle;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: setCustomAnimations() removed; head rotation must be ported
// via addAdditionalStateData + custom GeoRenderState.
// TODO: Port head pitch/yaw rotation (with taming check) to addAdditionalStateData pattern.
public class GiftStarbyModel extends GeoModel<GiftStarbuncle> {
    private static final Identifier WILD_TEXTURE = ArsNouveau.prefix("textures/entity/gift_starby.png");
    public static final Identifier NORMAL_MODEL = ArsNouveau.prefix("gift_starby");
    public static final Identifier ANIMATIONS = ArsNouveau.prefix("starbuncle_animations");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return NORMAL_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return WILD_TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(GiftStarbuncle drygmy) {
        return ANIMATIONS;
    }
}
