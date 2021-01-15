package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.ArcaneRelayTile;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RelayModel extends AnimatedGeoModel<ArcaneRelayTile> {
    @Override
    public ResourceLocation getModelLocation(ArcaneRelayTile volcanicTile) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/mana_relay.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ArcaneRelayTile volcanicTile) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/blocks/mana_relay.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ArcaneRelayTile volcanicTile) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/mana_relay_animation.json");
    }
}