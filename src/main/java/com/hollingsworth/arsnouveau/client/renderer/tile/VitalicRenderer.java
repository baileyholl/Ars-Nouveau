package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.VitalicSourcelinkTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class VitalicRenderer extends GeoBlockRenderer<VitalicSourcelinkTile> {
    public static SourcelinkModel model = new SourcelinkModel("vitalic");

    public VitalicRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
