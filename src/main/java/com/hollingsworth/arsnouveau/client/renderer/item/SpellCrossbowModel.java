package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.SpellCrossbow;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpellCrossbowModel extends GeoModel<SpellCrossbow> {

    @Override
    public ResourceLocation getModelResource(SpellCrossbow wand) {
        return new ResourceLocation(ArsNouveau.MODID, "geo/spell_crossbow.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SpellCrossbow wand) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/items/spell_crossbow.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SpellCrossbow wand) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/wand_animation.json");
    }
}