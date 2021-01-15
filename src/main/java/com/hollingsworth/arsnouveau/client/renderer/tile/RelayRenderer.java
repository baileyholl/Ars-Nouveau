package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.ArcaneRelayTile;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class RelayRenderer extends GeoBlockRenderer<ArcaneRelayTile> {

    public RelayRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn, new RelayModel());
    }

    public static GenericItemRenderer getISTER(){
        return new GenericItemRenderer(new RelayModel());
    }
}
