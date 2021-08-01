package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.VitalicSourcelinkTile;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class VitalicModel extends AnimatedGeoModel<VitalicSourcelinkTile> {

    public static final ResourceLocation MODEL = new ResourceLocation(ArsNouveau.MODID , "geo/vitalic_sourcelink.geo.json");
    public static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/blocks/vitalic_sourcelink.png");
    public static final ResourceLocation ANIMATION = new ResourceLocation(ArsNouveau.MODID , "animations/volcanic_sourcelink_animations.json");

    @Override
    public ResourceLocation getModelLocation(VitalicSourcelinkTile volcanicSourcelinkTile) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureLocation(VitalicSourcelinkTile volcanicSourcelinkTile) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(VitalicSourcelinkTile volcanicSourcelinkTile) {
        return ANIMATION;
    }
}
