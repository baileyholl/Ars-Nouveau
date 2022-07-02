package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.AlchemicalSourcelinkTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class AlchemicalRenderer extends GeoBlockRenderer<AlchemicalSourcelinkTile> {

    public static SourcelinkModel model = new SourcelinkModel<>("alchemical");

    public AlchemicalRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
