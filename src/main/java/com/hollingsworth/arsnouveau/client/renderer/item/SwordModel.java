package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.EnchantersSword;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SwordModel extends AnimatedGeoModel<EnchantersSword> {

    @Override
    public ResourceLocation getModelResource(EnchantersSword wand) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/sword.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EnchantersSword wand) {
        return  new ResourceLocation(ArsNouveau.MODID, "textures/items/enchanters_sword.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EnchantersSword wand) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/sword.json");
    }


}
