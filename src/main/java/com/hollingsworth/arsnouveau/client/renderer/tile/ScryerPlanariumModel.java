package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: setCustomAnimations and getRenderType(GeoAnimatable, Identifier) removed from GeoModel
public class ScryerPlanariumModel extends GenericModel {
    public ScryerPlanariumModel() {
        super("planarium");
        this.textLoc = ArsNouveau.prefix("textures/" + textPathRoot + "/planarium.png");
    }

    // GeckoLib 5: getRenderType moved to GeoRenderer; override at renderer level instead
}
