package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: setCustomAnimations() removed - basket bone visibility and head rotation must be
// ported via addAdditionalStateData + custom GeoRenderState.
// TODO: Port basket bone visibility (entity.isTamed()) to addAdditionalStateData pattern.
// TODO: Port entity-specific model/texture (carbuncle.getModel()/getTexture()) via addAdditionalStateData pattern;
//       currently returns static defaults.
public class StarbuncleModel extends GeoModel<Starbuncle> {

    public static final Identifier ANIMATION = ArsNouveau.prefix("starbuncle_animations");
    private static final Identifier DEFAULT_MODEL = ArsNouveau.prefix("starbuncle");
    private static final Identifier DEFAULT_TEXTURE = ArsNouveau.prefix("textures/entity/starbuncle_blue.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        // TODO: Return entity-specific model via GeoRenderState data ticket once ported.
        return DEFAULT_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        // TODO: Return entity-specific texture via GeoRenderState data ticket once ported.
        return DEFAULT_TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(Starbuncle carbuncle) {
        return ANIMATION;
    }
}
