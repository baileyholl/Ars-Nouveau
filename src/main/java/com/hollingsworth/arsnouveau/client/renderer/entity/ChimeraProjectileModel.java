package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityChimeraProjectile;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: getModelResource/getTextureResource now take GeoRenderState not entity
public class ChimeraProjectileModel extends GeoModel<EntityChimeraProjectile> {

    private static final Identifier TEXTURE = ArsNouveau.prefix("textures/entity/chimera_spike.png");

    public static final Identifier NORMAL_MODEL = ArsNouveau.prefix("chimera_spike");

    public static final Identifier ANIMATIONS = ArsNouveau.prefix("spike_animations");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return NORMAL_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(EntityChimeraProjectile animatable) {
        return ANIMATIONS;
    }
}
