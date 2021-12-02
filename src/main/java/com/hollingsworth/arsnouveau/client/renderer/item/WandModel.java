package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.Wand;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class WandModel extends AnimatedGeoModel<Wand> {

    @Override
    public ResourceLocation getModelLocation(Wand wand) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/wand.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(Wand wand) {
        return  new ResourceLocation(ArsNouveau.MODID, "textures/items/wand.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Wand wand) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/wand_animation.json");
    }
}
