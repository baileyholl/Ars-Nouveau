package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.CraftingLecternTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LecternRenderer extends ArsGeoBlockRenderer<CraftingLecternTile> {
    public static AnimatedGeoModel model = new GenericModel<>("book_wyrm_lectern");

    public LecternRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    public LecternRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, AnimatedGeoModel<CraftingLecternTile> modelProvider) {
        super(rendererDispatcherIn, modelProvider);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
