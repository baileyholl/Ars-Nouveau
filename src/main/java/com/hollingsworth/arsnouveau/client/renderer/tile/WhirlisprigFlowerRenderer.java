package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.WhirlisprigTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;

public class WhirlisprigFlowerRenderer extends ArsGeoBlockRenderer<WhirlisprigTile> {
    public static GeoModel model = new GenericModel("whirlisprig_blossom");

    public WhirlisprigFlowerRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        this(rendererDispatcherIn, model);
    }

    public WhirlisprigFlowerRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, GeoModel<WhirlisprigTile> modelProvider) {
        super(rendererDispatcherIn, modelProvider);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
