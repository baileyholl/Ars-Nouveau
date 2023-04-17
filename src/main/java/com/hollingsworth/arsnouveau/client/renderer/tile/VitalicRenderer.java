package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.VitalicSourcelinkTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class VitalicRenderer extends ArsGeoBlockRenderer<VitalicSourcelinkTile> {
    public static SourcelinkModel model = new SourcelinkModel("vitalic");

    public VitalicRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
