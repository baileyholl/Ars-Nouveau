package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryTile;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class RepositoryRenderer extends ArsGeoBlockRenderer<RepositoryTile> {
    public static GeoModel<RepositoryTile> model = new RepositoryModel();

    public RepositoryRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(rendererProvider, model);
    }

    public static GenericItemBlockRenderer getISTER() {
        // TODO: GeckoLib 5 - actuallyRender and GeoBone.setHidden removed;
        // bone visibility based on repository level is not yet implemented
        return new GenericItemBlockRenderer(model);
    }
}
