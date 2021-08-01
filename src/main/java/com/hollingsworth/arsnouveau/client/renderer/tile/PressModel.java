package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.GlyphPressTile;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PressModel extends AnimatedGeoModel<GlyphPressTile> {
    @Override
    public ResourceLocation getModelLocation(GlyphPressTile volcanicTile) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/glyph_press.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(GlyphPressTile volcanicTile) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/blocks/glyph_press.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(GlyphPressTile volcanicTile) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/press_animations.json");
    }
}
