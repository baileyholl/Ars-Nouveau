package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.ArcaneCoreTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;

public class ArcaneCoreRenderer extends ArsGeoBlockRenderer<ArcaneCoreTile> {
    public static GeoModel model = new GenericModel<>("arcane_core");

    public ArcaneCoreRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        this(rendererDispatcherIn, model);
    }

    public ArcaneCoreRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, GeoModel<ArcaneCoreTile> modelProvider) {
        super(rendererDispatcherIn, modelProvider);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
