package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.AlchemicalSourcelinkTile;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class AlchemicalModel extends AnimatedGeoModel<AlchemicalSourcelinkTile> {

    public static final ResourceLocation MODEL = new ResourceLocation(ArsNouveau.MODID , "geo/alchemical_sourcelink.geo.json");
    public static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/blocks/alchemical_sourcelink.png");
    public static final ResourceLocation ANIMATION = new ResourceLocation(ArsNouveau.MODID , "animations/volcanic_sourcelink_animations.json");

    @Override
    public ResourceLocation getModelLocation(AlchemicalSourcelinkTile volcanicSourcelinkTile) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureLocation(AlchemicalSourcelinkTile volcanicSourcelinkTile) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(AlchemicalSourcelinkTile volcanicSourcelinkTile) {
        return ANIMATION;
    }
}
