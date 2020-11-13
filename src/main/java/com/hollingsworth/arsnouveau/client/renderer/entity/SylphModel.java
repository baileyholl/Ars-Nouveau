package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntitySylph;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
public class SylphModel extends AnimatedGeoModel<EntitySylph> {

    @Override
    public ResourceLocation getModelLocation(EntitySylph entitySylph) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/sylph.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(EntitySylph entitySylph) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/sylph.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntitySylph entitySylph) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/sylph_animations.json");
    }
}
