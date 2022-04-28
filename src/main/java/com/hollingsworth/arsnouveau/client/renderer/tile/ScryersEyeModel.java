package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.ScryersEyeTile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ScryersEyeModel extends AnimatedGeoModel<ScryersEyeTile> {
    public static ResourceLocation SQUINTING = new ResourceLocation(ArsNouveau.MODID, "textures/blocks/scryers_eye_squinting.png");
    public static ResourceLocation ALERT = new ResourceLocation(ArsNouveau.MODID, "textures/blocks/scryers_eye_alert.png");
    public static ResourceLocation IDLE = new ResourceLocation(ArsNouveau.MODID, "textures/blocks/scryers_eye_idle.png");
    public static ResourceLocation SLEEPING = new ResourceLocation(ArsNouveau.MODID, "textures/blocks/scryers_eye_sleeping.png");
    public static final ResourceLocation anim = new ResourceLocation(ArsNouveau.MODID , "animations/scryers_eye_animations.json");
    public static final ResourceLocation model = new ResourceLocation(ArsNouveau.MODID , "geo/scryers_eye.geo.json");
    @Override
    public ResourceLocation getModelLocation(ScryersEyeTile object) {
        return model;
    }

    @Override
    public ResourceLocation getTextureLocation(ScryersEyeTile object) {
        return ALERT;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ScryersEyeTile animatable) {
        return anim;
    }
}
