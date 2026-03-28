package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WildenGuardian;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: setCustomAnimations() removed; bone visibility (spines) must be
// ported via addAdditionalStateData + custom GeoRenderState.
// TODO: Port spines bone visibility to addAdditionalStateData pattern.
public class WildenGuardianModel extends GeoModel<WildenGuardian> {

    public static final Identifier WARDER_NEUTRAL = ArsNouveau.prefix("wilden_guardian");
    public static final Identifier TEXT = ArsNouveau.prefix("textures/entity/wilden_guardian.png");
    public static final Identifier ANIM = ArsNouveau.prefix("wilden_defender_animations");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return WARDER_NEUTRAL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXT;
    }

    @Override
    public Identifier getAnimationResource(WildenGuardian wildenStalker) {
        return ANIM;
    }
}
