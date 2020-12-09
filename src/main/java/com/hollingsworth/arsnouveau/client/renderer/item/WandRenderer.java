package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.common.items.Wand;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class WandRenderer extends GeoItemRenderer<Wand> {
    public WandRenderer() {
        super(new WandModel());
    }
}
