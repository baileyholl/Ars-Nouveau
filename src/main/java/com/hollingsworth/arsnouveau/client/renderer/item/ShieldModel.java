package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.EnchantersShield;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShieldModel extends GeoModel<EnchantersShield> {

    @Override
    public ResourceLocation getModelResource(EnchantersShield wand) {
        return ArsNouveau.prefix( "geo/shield.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EnchantersShield wand) {
        return ArsNouveau.prefix( "textures/item/enchanters_shield.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EnchantersShield wand) {
        return ArsNouveau.prefix( "animations/shield.json");
    }
}
