package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;

// GeckoLib 5.4.2: getRenderType moved from GeoModel to GeoRenderer interface.
// PlanariumModel no longer overrides getRenderType - it should be set on the renderer instead.
// The renderer that uses this model should override getRenderType(R, Identifier).
public class PlanariumModel extends GenericModel {
    public PlanariumModel(boolean dimModel) {
        super(dimModel ? "planarium_dimension" : "planarium");
        this.textLoc = ArsNouveau.prefix("textures/" + textPathRoot + "/planarium.png");
    }
}
