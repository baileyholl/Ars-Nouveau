package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.PotionMelderTile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PotionMelderModel extends GeoModel<PotionMelderTile> {

    public static final ResourceLocation model = ArsNouveau.prefix( "geo/potion_stirrer.geo.json");
    public static final ResourceLocation texture = ArsNouveau.prefix( "textures/block/potion_stirrer.png");
    public static final ResourceLocation anim = ArsNouveau.prefix( "animations/potion_melder_animation.json");

    @Override
    public ResourceLocation getModelResource(PotionMelderTile volcanicTile) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(PotionMelderTile volcanicTile) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(PotionMelderTile volcanicTile) {
        return anim;
    }
}
