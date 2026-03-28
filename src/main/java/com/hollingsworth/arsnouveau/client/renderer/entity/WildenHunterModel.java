package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WildenHunter;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: setCustomAnimations() removed; head rotation must be ported
// via addAdditionalStateData + custom GeoRenderState.
// TODO: Port head pitch/yaw rotation to addAdditionalStateData pattern.
public class WildenHunterModel extends GeoModel<WildenHunter> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return ArsNouveau.prefix("wilden_hunter");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return ArsNouveau.prefix("textures/entity/wilden_hunter.png");
    }

    @Override
    public Identifier getAnimationResource(WildenHunter hunter) {
        return ArsNouveau.prefix("wilden_hunter_animations");
    }
}
