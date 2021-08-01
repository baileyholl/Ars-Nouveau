package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityChimeraProjectile;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ChimeraProjectileModel extends AnimatedGeoModel<EntityChimeraProjectile> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/spike.png");

    public static final ResourceLocation NORMAL_MODEL = new ResourceLocation(ArsNouveau.MODID , "geo/spike.geo.json");

    public static final ResourceLocation ANIMATIONS = new ResourceLocation(ArsNouveau.MODID , "animations/spike_animations.json");

    @Override
    public ResourceLocation getModelLocation(EntityChimeraProjectile object) {
        return NORMAL_MODEL;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityChimeraProjectile object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntityChimeraProjectile animatable) {
        return ANIMATIONS;
    }
}
