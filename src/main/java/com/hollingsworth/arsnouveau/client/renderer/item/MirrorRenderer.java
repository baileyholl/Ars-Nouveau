package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.items.EnchantersMirror;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MirrorRenderer extends GeoItemRenderer<EnchantersMirror> {
    public static GeoModel model = new GenericModel("enchanters_mirror", "item");

    public MirrorRenderer() {
        super(model);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
