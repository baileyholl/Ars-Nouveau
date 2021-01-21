package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CarbuncleShadesModel extends AnimatedGeoModel<EntityCarbuncle> {

    @Override
    public ResourceLocation getModelLocation(EntityCarbuncle object) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/carbuncle_shades.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCarbuncle o) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_shades.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntityCarbuncle animatable) {
        return null;
    }
}
