package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: getModelResource/getTextureResource now take GeoRenderState
// setCustomAnimations, getAnimationProcessor().getBone(), GeoBone.setRotX/Y/Z removed
// TODO: Port head rotation (headPitch, netHeadYaw) and hat bone visibility to captureDefaultRenderState
public class WixieModel<T extends LivingEntity & GeoAnimatable> extends GeoModel<T> {

    private static final Identifier WILD_TEXTURE = ArsNouveau.prefix("textures/entity/wixie.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return ArsNouveau.prefix("wixie");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return WILD_TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(T entityWixie) {
        return ArsNouveau.prefix("wixie_animations");
    }
}
