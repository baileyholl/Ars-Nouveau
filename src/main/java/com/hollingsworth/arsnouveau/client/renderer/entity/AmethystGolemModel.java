package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.AmethystGolem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class AmethystGolemModel extends AnimatedGeoModel<AmethystGolem> {

    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/amethyst_golem.png");
    public static final ResourceLocation NORMAL_MODEL = new ResourceLocation(ArsNouveau.MODID , "geo/amethyst_golem.geo.json");
    public static final ResourceLocation ANIMATIONS = new ResourceLocation(ArsNouveau.MODID , "animations/amethyst_golem_animations.json");
    @Override
    public ResourceLocation getModelLocation(AmethystGolem drygmy) {
        return NORMAL_MODEL;
    }

    @Override
    public ResourceLocation getTextureLocation(AmethystGolem drygmy) {
        return WILD_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(AmethystGolem drygmy) {
        return ANIMATIONS;
    }

}
