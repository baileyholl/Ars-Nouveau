package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.ScryersOculusTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ScryerEyeRenderer extends ArsGeoBlockRenderer<ScryersOculusTile> {
    ScryersEyeModel model;

    public ScryerEyeRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, ScryersEyeModel model) {
        super(rendererDispatcherIn, model);
        this.model = model;
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(new ScryersEyeModel());
    }
}
