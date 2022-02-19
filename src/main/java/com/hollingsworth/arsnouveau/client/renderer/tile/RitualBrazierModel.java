package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RitualBrazierModel  extends AnimatedGeoModel<RitualBrazierTile> {
    public static final ResourceLocation model = new ResourceLocation(ArsNouveau.MODID , "geo/ritual.geo.json");
    public static final ResourceLocation texture = new ResourceLocation(ArsNouveau.MODID, "textures/blocks/ritual.png");
    public static final ResourceLocation anim = new ResourceLocation(ArsNouveau.MODID , "animations/ritual.json");

    @Override
    public ResourceLocation getModelLocation(RitualBrazierTile volcanicTile) {
        return model;
    }

    @Override
    public ResourceLocation getTextureLocation(RitualBrazierTile volcanicTile) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(RitualBrazierTile volcanicTile) {
        return anim;
    }
}
