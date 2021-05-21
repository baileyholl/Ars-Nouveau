package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.EnchantersShield;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ShieldModel extends AnimatedGeoModel<EnchantersShield> {

    @Override
    public ResourceLocation getModelLocation(EnchantersShield wand) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/shield.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(EnchantersShield wand) {
        return  new ResourceLocation(ArsNouveau.MODID, "textures/items/enchanters_shield.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EnchantersShield wand) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/shield.json");
    }
}
