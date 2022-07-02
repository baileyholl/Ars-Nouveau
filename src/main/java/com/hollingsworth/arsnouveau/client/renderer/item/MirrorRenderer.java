package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.items.EnchantersMirror;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class MirrorRenderer extends GeoItemRenderer<EnchantersMirror> {
    public static AnimatedGeoModel model = new GenericModel("enchanters_mirror", "items");

    public MirrorRenderer() {
        super(model);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
