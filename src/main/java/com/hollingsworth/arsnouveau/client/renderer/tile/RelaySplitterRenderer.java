package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.ArcaneRelaySplitterTile;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class RelaySplitterRenderer extends GeoBlockRenderer<ArcaneRelaySplitterTile> {

    public RelaySplitterRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn, new RelaySplitterModel());
    }

    public static GenericItemRenderer getISTER(){
        return new GenericItemRenderer(new RelaySplitterModel());
    }
}
