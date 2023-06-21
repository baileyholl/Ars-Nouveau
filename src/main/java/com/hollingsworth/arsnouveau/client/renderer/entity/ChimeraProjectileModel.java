package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityChimeraProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ChimeraProjectileModel extends GeoModel<EntityChimeraProjectile> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/spike.png");

    public static final ResourceLocation NORMAL_MODEL = new ResourceLocation(ArsNouveau.MODID, "geo/spike.geo.json");

    public static final ResourceLocation ANIMATIONS = new ResourceLocation(ArsNouveau.MODID, "animations/spike_animations.json");

    @Override
    public ResourceLocation getModelResource(EntityChimeraProjectile object) {
        return NORMAL_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityChimeraProjectile object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityChimeraProjectile animatable) {
        return ANIMATIONS;
    }
}
