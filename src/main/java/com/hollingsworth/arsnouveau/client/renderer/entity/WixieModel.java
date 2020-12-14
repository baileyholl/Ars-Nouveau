package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class WixieModel extends AnimatedGeoModel<EntityWixie> {

    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/wixie.png");

    @Override
    public ResourceLocation getModelLocation(EntityWixie entityWixie) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/wixie.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(EntityWixie entityWixie) {
        return WILD_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntityWixie entityWixie) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/wixie_animations.json");
    }
}
