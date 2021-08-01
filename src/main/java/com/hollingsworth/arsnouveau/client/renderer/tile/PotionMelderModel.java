package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.PotionMelderTile;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PotionMelderModel extends AnimatedGeoModel<PotionMelderTile> {

    public static final ResourceLocation model = new ResourceLocation(ArsNouveau.MODID , "geo/potion_melder.geo.json");
    public static final ResourceLocation texture = new ResourceLocation(ArsNouveau.MODID, "textures/blocks/potion_melder.png");
    public static final ResourceLocation anim = new ResourceLocation(ArsNouveau.MODID , "animations/potion_melder_animation.json");;
    @Override
    public ResourceLocation getModelLocation(PotionMelderTile volcanicTile) {
        return model;
    }

    @Override
    public ResourceLocation getTextureLocation(PotionMelderTile volcanicTile) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(PotionMelderTile volcanicTile) {
        return anim;
    }
}
