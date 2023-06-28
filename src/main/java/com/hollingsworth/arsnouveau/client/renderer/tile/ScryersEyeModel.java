package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.ScryersOculusTile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ScryersEyeModel extends GeoModel<ScryersOculusTile> {
    public static ResourceLocation SQUINTING = new ResourceLocation(ArsNouveau.MODID, "textures/block/scryers_eye_squinting.png");
    public static ResourceLocation IDLE = new ResourceLocation(ArsNouveau.MODID, "textures/block/scryers_eye_idle.png");
    public static final ResourceLocation anim = new ResourceLocation(ArsNouveau.MODID, "animations/scryers_eye_animations.json");
    public static final ResourceLocation model = new ResourceLocation(ArsNouveau.MODID, "geo/scryers_eye.geo.json");

    @Override
    public ResourceLocation getModelResource(ScryersOculusTile object) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(ScryersOculusTile object) {
        return object != null && object.playerNear ? SQUINTING : IDLE;
    }

    @Override
    public ResourceLocation getAnimationResource(ScryersOculusTile animatable) {
        return anim;
    }
}
