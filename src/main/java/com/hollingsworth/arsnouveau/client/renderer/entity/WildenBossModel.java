package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WildenBoss;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class WildenBossModel extends AnimatedGeoModel<WildenBoss> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/chimera.png");
    private static final ResourceLocation DEFENSIVE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/chimera_defense.png");

    public static final ResourceLocation NORMAL_MODEL = new ResourceLocation(ArsNouveau.MODID , "geo/wilden_chimera.geo.json");
    public static final ResourceLocation DEFENSIVE_MODEL = new ResourceLocation(ArsNouveau.MODID , "geo/wilden_chimera_defense.geo.json");


    @Override
    public ResourceLocation getModelLocation(WildenBoss object) {
        return NORMAL_MODEL;
    }

    @Override
    public ResourceLocation getTextureLocation(WildenBoss object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(WildenBoss animatable) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/wilden_chimera_animations.geo.json");
    }
}
