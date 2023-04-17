package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.AgronomicSourcelinkTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class AgronomicRenderer extends ArsGeoBlockRenderer<AgronomicSourcelinkTile> {
    public static SourcelinkModel model = new SourcelinkModel("agronomic");

    public AgronomicRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}