package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningCrystalTile;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SummoningCrystalModel extends AnimatedGeoModel<SummoningCrystalTile> {
    @Override
    public ResourceLocation getModelLocation(SummoningCrystalTile volcanicTile) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/summoning_crystal.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(SummoningCrystalTile volcanicTile) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/blocks/summoning_crystal.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(SummoningCrystalTile volcanicTile) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/summoning_crystal_animation.json");
    }
}
