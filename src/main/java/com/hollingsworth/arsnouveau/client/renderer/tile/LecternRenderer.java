package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.CraftingLecternTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;

public class LecternRenderer extends ArsGeoBlockRenderer<CraftingLecternTile> {
    public static GeoModel model = new GenericModel<>("book_wyrm_lectern");

    public LecternRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    public LecternRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, GeoModel<CraftingLecternTile> modelProvider) {
        super(rendererDispatcherIn, modelProvider);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
