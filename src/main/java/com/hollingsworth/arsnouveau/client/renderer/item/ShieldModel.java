package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.EnchantersShield;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ShieldModel extends AnimatedGeoModel<EnchantersShield> {

    @Override
    public ResourceLocation getModelResource(EnchantersShield wand) {
        return new ResourceLocation(ArsNouveau.MODID, "geo/shield.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EnchantersShield wand) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/items/enchanters_shield.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EnchantersShield wand) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/shield.json");
    }
}
