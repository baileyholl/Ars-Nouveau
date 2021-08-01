package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.AgronomicSourcelinkTile;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class AgronomicSourcelinkModel extends AnimatedGeoModel<AgronomicSourcelinkTile> {
    @Override
    public ResourceLocation getModelLocation(AgronomicSourcelinkTile agronomicSourcelink) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/agronomic_sourcelink.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(AgronomicSourcelinkTile agronomicSourcelink) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/blocks/agronomic_sourcelink.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(AgronomicSourcelinkTile agronomicSourcelink) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/volcanic_sourcelink_animations.json");
    }
}
