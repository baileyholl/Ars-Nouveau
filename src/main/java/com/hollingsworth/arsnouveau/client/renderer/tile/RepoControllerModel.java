package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.repository.RepositoryControllerTile;
import net.minecraft.resources.ResourceLocation;

public class RepoControllerModel extends GenericModel<RepositoryControllerTile> {

    public static final ResourceLocation FILLED = ArsNouveau.prefix("textures/block/storage_catalog_filtered.png");
    public static final ResourceLocation EMPTY = ArsNouveau.prefix("textures/block/storage_catalog_unfiltered.png");

    public RepoControllerModel() {
        super("storage_catalog");
    }

    @Override
    public ResourceLocation getTextureResource(RepositoryControllerTile animatable) {
        return ArsNouveau.prefix("textures/block/storage_catalog_unfiltered.png");
    }
}
