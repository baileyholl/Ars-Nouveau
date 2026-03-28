package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WildenChimera;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: setCustomAnimations() removed; bone visibility (wings/spikes/horns) must be
// ported via addAdditionalStateData + custom GeoRenderState. Head rotation similarly removed.
// TODO: Port wing/spike/horn bone visibility to addAdditionalStateData pattern.
public class WildenChimeraModel extends GeoModel<WildenChimera> {

    private static final Identifier TEXTURE = ArsNouveau.prefix("textures/entity/wilden_chimera.png");
    public static final Identifier NORMAL_MODEL = ArsNouveau.prefix("wilden_chimera");
    public static final Identifier ANIMATIONS = ArsNouveau.prefix("wilden_chimera_animations");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return NORMAL_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(WildenChimera animatable) {
        return ANIMATIONS;
    }
}
