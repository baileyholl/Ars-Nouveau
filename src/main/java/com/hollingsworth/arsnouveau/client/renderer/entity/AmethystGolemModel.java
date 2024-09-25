package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;

public class AmethystGolemModel<T extends LivingEntity & GeoEntity> extends GeoModel<T> {

    private static final ResourceLocation WILD_TEXTURE = ArsNouveau.prefix( "textures/entity/amethyst_golem.png");
    public static final ResourceLocation NORMAL_MODEL = ArsNouveau.prefix( "geo/amethyst_golem.geo.json");
    public static final ResourceLocation ANIMATIONS = ArsNouveau.prefix( "animations/amethyst_golem_animations.json");

    @Override
    public ResourceLocation getModelResource(T drygmy) {
        return NORMAL_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(T drygmy) {
        return WILD_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(T drygmy) {
        return ANIMATIONS;
    }

}
