package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Alakarkinos;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: getModelResource/getTextureResource now take GeoRenderState
// setCustomAnimations removed; bone visibility (sea_bunny/hat) must be ported via captureDefaultRenderState
// TODO: Port HAS_HAT bone visibility to captureDefaultRenderState
public class AlakarkinosModel extends GeoModel<Alakarkinos> {
    public static Identifier MODEL = ArsNouveau.prefix("alakarkinos");
    public static Identifier TEXTURE = ArsNouveau.prefix("textures/entity/alakarkinos.png");
    public static Identifier ANIMATION = ArsNouveau.prefix("alakarkinos");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(Alakarkinos animatable) {
        return ANIMATION;
    }
}
