package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.MycelialSourcelinkTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class RedstoneRelayRenderer extends ArsGeoBlockRenderer<MycelialSourcelinkTile>{
    public static GenericModel model = new GenericModel<>("redstone_relay");

    public RedstoneRelayRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
