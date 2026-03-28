package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Lily;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: setCustomAnimations() removed; head rotation must be ported
// via addAdditionalStateData + custom GeoRenderState.
// TODO: Port head pitch/yaw rotation to addAdditionalStateData pattern.
public class LilyModel extends GeoModel<Lily> {
    public static Identifier TEXTURE = ArsNouveau.prefix("textures/entity/lily.png");
    public static Identifier MODEL = ArsNouveau.prefix("lily");
    public static Identifier ANIMATION = ArsNouveau.prefix("lily_animations");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(Lily whirlisprig) {
        return ANIMATION;
    }
}
