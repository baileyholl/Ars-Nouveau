package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.SpellBow;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SpellBowModel extends AnimatedGeoModel<SpellBow> {

    @Override
    public ResourceLocation getModelLocation(SpellBow wand) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/spellbow.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(SpellBow wand) {
        return  new ResourceLocation(ArsNouveau.MODID, "textures/items/spellbow.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(SpellBow wand) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/wand_animation.json");
    }


}
