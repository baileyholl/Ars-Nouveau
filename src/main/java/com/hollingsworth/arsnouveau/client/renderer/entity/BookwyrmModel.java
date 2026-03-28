package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: getModelResource/getTextureResource now take GeoRenderState
// setCustomAnimations, getAnimationProcessor().getBone(), GeoBone.setRotX/Y/Z removed
// TODO: Port head rotation (headPitch, netHeadYaw) to captureDefaultRenderState
public class BookwyrmModel<T extends LivingEntity & GeoAnimatable> extends GeoModel<T> {

    private static final Identifier WILD_TEXTURE = ArsNouveau.prefix("textures/entity/book_wyrm_blue.png");
    public static final Identifier NORMAL_MODEL = ArsNouveau.prefix("book_wyrm");
    public static final Identifier ANIMATIONS = ArsNouveau.prefix("book_wyrm_animation");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return NORMAL_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        // TODO: Bookwyrm texture is dynamic; need DataTicket in render state set during captureDefaultRenderState
        return WILD_TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(T wyrm) {
        return ANIMATIONS;
    }
}
