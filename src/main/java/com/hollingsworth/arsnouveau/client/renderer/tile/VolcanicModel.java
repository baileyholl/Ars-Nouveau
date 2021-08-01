package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.VolcanicSourcelinkTile;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class VolcanicModel extends AnimatedGeoModel<VolcanicSourcelinkTile> {

    public static final ResourceLocation MODEL = new ResourceLocation(ArsNouveau.MODID , "geo/volcanic_sourcelink.geo.json");
    public static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/blocks/volcanic_sourcelink.png");
    public static final ResourceLocation ANIMATION = new ResourceLocation(ArsNouveau.MODID , "animations/volcanic_sourcelink_animations.json");

    @Override
    public ResourceLocation getModelLocation(VolcanicSourcelinkTile volcanicSourcelinkTile) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureLocation(VolcanicSourcelinkTile volcanicSourcelinkTile) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(VolcanicSourcelinkTile volcanicSourcelinkTile) {
        return ANIMATION;
    }
}
