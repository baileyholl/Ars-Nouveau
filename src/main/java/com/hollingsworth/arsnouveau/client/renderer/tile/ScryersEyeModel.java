package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.ScryersOculusTile;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;

// GeckoLib 5: GeoModel<T> now requires getModelResource(GeoRenderState) and getTextureResource(GeoRenderState).
// ScryersOculusTile-specific state (playerNear) must be passed via the render state.
// Using ArsBlockEntityRenderState for type bounds; tile state is TODO.
public class ScryersEyeModel extends GeoModel<ScryersOculusTile> {
    public static Identifier SQUINTING = ArsNouveau.prefix("textures/block/scryers_eye_squinting.png");
    public static Identifier IDLE = ArsNouveau.prefix("textures/block/scryers_eye_idle.png");
    public static final Identifier anim = ArsNouveau.prefix("scryers_eye_animation");
    public static final Identifier model = ArsNouveau.prefix("scryers_eye");

    @Override
    public Identifier getModelResource(software.bernie.geckolib.renderer.base.GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(software.bernie.geckolib.renderer.base.GeoRenderState renderState) {
        // TODO: ScryersOculusTile.playerNear state needs to be passed via a custom GeoRenderState
        return IDLE;
    }

    @Override
    public Identifier getAnimationResource(ScryersOculusTile animatable) {
        return anim;
    }
}
