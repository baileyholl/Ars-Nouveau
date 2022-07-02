package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.ArcaneCoreTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class ArcaneCoreRenderer extends GeoBlockRenderer<ArcaneCoreTile> {
    public static AnimatedGeoModel model = new GenericModel("arcane_core");

    public ArcaneCoreRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        this(rendererDispatcherIn, model);
    }

    public ArcaneCoreRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, AnimatedGeoModel<ArcaneCoreTile> modelProvider) {
        super(rendererDispatcherIn, modelProvider);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
