package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib3.core.GeoAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class AmethystGolemModel<T extends LivingEntity & GeoAnimatable> extends AnimatedGeoModel<T> {

    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/amethyst_golem.png");
    public static final ResourceLocation NORMAL_MODEL = new ResourceLocation(ArsNouveau.MODID, "geo/amethyst_golem.geo.json");
    public static final ResourceLocation ANIMATIONS = new ResourceLocation(ArsNouveau.MODID, "animations/amethyst_golem_animations.json");

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
