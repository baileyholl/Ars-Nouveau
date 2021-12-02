package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ScribesModel extends AnimatedGeoModel<ScribesTile> {
    @Override
    public ResourceLocation getModelLocation(ScribesTile volcanicTile) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/scribes_table.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ScribesTile volcanicTile) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/blocks/scribes_table.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ScribesTile volcanicTile) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/mana_splitter_animation.json");
    }
}
