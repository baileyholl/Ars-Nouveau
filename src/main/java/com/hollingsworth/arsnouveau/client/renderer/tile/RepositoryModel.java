package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryTile;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5.4.2: getModelResource and getTextureResource now take GeoRenderState, not T.
// TODO: Bone visibility (fill level) needs migration from setCustomAnimations to addAdditionalStateData.
// setCustomAnimations no longer exists on GeoModel in GeckoLib 5 - bones are always visible now.
public class RepositoryModel extends GeoModel<RepositoryTile> {

    public static final Identifier TEXTURE = ArsNouveau.prefix("textures/block/repository.png");
    public static final Identifier MODEL = ArsNouveau.prefix("repository");
    public static final Identifier ANIMATION = ArsNouveau.prefix("empty");

    public RepositoryModel() {
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(RepositoryTile animatable) {
        return ANIMATION;
    }
}
