package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StyledProjectileModel extends GeoModel<EntityProjectileSpell> {
    private static final ResourceLocation TEXTURE = ArsNouveau.prefix( "textures/entity/projectile.png");
    public static final ResourceLocation NORMAL_MODEL = ArsNouveau.prefix( "geo/projectile.geo.json");
    public static final ResourceLocation ANIMATIONS = ArsNouveau.prefix( "animations/empty.json");

    @Override
    public ResourceLocation getModelResource(EntityProjectileSpell animatable) {
        return NORMAL_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityProjectileSpell animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityProjectileSpell animatable) {
        return ANIMATIONS;
    }
}
