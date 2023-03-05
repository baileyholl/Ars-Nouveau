package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.CraftingTerminalBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class LecternRenderer extends GeoBlockRenderer<CraftingTerminalBlockEntity> {
    public static AnimatedGeoModel model = new GenericModel<>("book_wyrm_lectern");

    public LecternRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    public LecternRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, AnimatedGeoModel<CraftingTerminalBlockEntity> modelProvider) {
        super(rendererDispatcherIn, modelProvider);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
