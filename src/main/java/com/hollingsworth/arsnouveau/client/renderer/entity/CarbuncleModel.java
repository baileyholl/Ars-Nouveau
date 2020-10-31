package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.model.AnimatedGeoModel;

public class CarbuncleModel extends AnimatedGeoModel<EntityCarbuncle> {

    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_wild_orange.png");
    private static final ResourceLocation TAMED_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_orange.png");

    @Override
    public ResourceLocation getModelLocation(EntityCarbuncle carbuncle) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/carbuncle.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCarbuncle carbuncle) {
        return carbuncle.isTamed() ? TAMED_TEXTURE : WILD_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntityCarbuncle carbuncle) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/carbuncle_animations.json");
    }
}
