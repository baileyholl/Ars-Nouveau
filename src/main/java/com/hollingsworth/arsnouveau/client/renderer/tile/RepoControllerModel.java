package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryCatalogTile;
import net.minecraft.resources.ResourceLocation;

public class RepoControllerModel extends GenericModel<RepositoryCatalogTile> {

    public static final ResourceLocation FILLED = ArsNouveau.prefix("textures/block/storage_catalog_filtered.png");
    public static final ResourceLocation EMPTY = ArsNouveau.prefix("textures/block/storage_catalog_unfiltered.png");

    public RepoControllerModel() {
        super("storage_catalog");
    }

    @Override
    public ResourceLocation getTextureResource(RepositoryCatalogTile animatable) {
        if(animatable == null){
            return EMPTY;
        }
        return animatable.scrollStack.isEmpty() ? EMPTY : FILLED;
    }
}
