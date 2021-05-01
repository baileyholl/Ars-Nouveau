package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class RitualBrazierRenderer extends GeoBlockRenderer<RitualTile> {
    public RitualBrazierRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn, new RitualBrazierModel());
    }

    public static GenericItemRenderer getISTER(){
        return new GenericItemRenderer(new RitualBrazierModel());
    }
}
