package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.EnchantersSword;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SwordModel extends AnimatedGeoModel<EnchantersSword> {

    @Override
    public ResourceLocation getModelLocation(EnchantersSword wand) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/sword.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(EnchantersSword wand) {
        return  new ResourceLocation(ArsNouveau.MODID, "textures/items/enchanters_sword.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EnchantersSword wand) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/sword.json");
    }


}
