package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.Wand;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WandModel extends GeoModel<Wand> {

    @Override
    public ResourceLocation getModelResource(Wand wand) {
        return new ResourceLocation(ArsNouveau.MODID, "geo/wand.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Wand wand) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/item/wand.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Wand wand) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/wand_animation.json");
    }
}
