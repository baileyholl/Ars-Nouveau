package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RitualBrazierModel  extends AnimatedGeoModel<RitualTile> {
    public static final ResourceLocation model = new ResourceLocation(ArsNouveau.MODID , "geo/ritual.geo.json");
    public static final ResourceLocation texture = new ResourceLocation(ArsNouveau.MODID, "textures/blocks/ritual.png");
    public static final ResourceLocation anim = new ResourceLocation(ArsNouveau.MODID , "animations/ritual.json");;
    @Override
    public ResourceLocation getModelLocation(RitualTile volcanicTile) {
        return model;
    }

    @Override
    public ResourceLocation getTextureLocation(RitualTile volcanicTile) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(RitualTile volcanicTile) {
        return anim;
    }
}
