package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WildenStalker;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: setCustomAnimations() removed; bone visibility (wings/head) must be
// ported via addAdditionalStateData + custom GeoRenderState. Head rotation similarly removed.
// TODO: Port wing bone visibility (fly vs run wings) and head rotation to addAdditionalStateData pattern.
public class WildenStalkerModel extends GeoModel<WildenStalker> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return ArsNouveau.prefix("wilden_stalker");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return ArsNouveau.prefix("textures/entity/wilden_stalker.png");
    }

    @Override
    public Identifier getAnimationResource(WildenStalker wildenStalker) {
        return ArsNouveau.prefix("wilden_stalker_animations");
    }
}
