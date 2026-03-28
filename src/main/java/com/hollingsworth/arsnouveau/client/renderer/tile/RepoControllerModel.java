package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryCatalogTile;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class RepoControllerModel extends GenericModel<RepositoryCatalogTile> {

    public static final Identifier FILLED = ArsNouveau.prefix("textures/block/storage_catalog_filtered.png");
    public static final Identifier EMPTY = ArsNouveau.prefix("textures/block/storage_catalog_unfiltered.png");

    public RepoControllerModel() {
        super("storage_catalog");
    }

    // GeckoLib 5: getTextureResource takes GeoRenderState, not T directly
    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        // TODO: GeckoLib 5 - need to extract animatable from renderState to check scrollStack
        // For now, return EMPTY as fallback; texture switching based on tile state is not yet implemented
        return EMPTY;
    }
}
