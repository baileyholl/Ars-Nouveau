package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.VolcanicSourcelinkTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class VolcanicRenderer extends GeoBlockRenderer<VolcanicSourcelinkTile> {
    public static SourcelinkModel model =  new SourcelinkModel("volcanic");

    public VolcanicRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    public static GenericItemBlockRenderer getISTER(){
        return new GenericItemBlockRenderer(model);
    }
}
