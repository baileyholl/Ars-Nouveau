package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Nook;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: setCustomAnimations() removed; head rotation must be ported
// via addAdditionalStateData + custom GeoRenderState.
// TODO: Port head pitch/yaw rotation (with sit posture adjustment) to addAdditionalStateData pattern.
public class NookModel extends GeoModel<Nook> {

    public static Identifier TEXTURE = ArsNouveau.prefix("textures/entity/nook.png");
    public static Identifier MODEL = ArsNouveau.prefix("nook");
    public static Identifier ANIMATION = ArsNouveau.prefix("nook_animation");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(Nook nook) {
        return ANIMATION;
    }
}
