package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryTile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class RepositoryModel extends GeoModel<RepositoryTile> {

    public static final ResourceLocation TEXTURE = ArsNouveau.prefix("textures/block/repository.png");
    public static final ResourceLocation MODEL = ArsNouveau.prefix("geo/repository.geo.json");
    public static final ResourceLocation ANIMATION = ArsNouveau.prefix("animations/empty.geo.json");

    public RepositoryModel() {
    }

    @Override
    public void setCustomAnimations(RepositoryTile repo, long instanceId, AnimationState<RepositoryTile> animationState) {
        super.setCustomAnimations(repo, instanceId, animationState);
        int level = repo.fillLevel;
        String[] configuration = repo.configuration > RepositoryTile.CONFIGURATIONS.length ? RepositoryTile.CONFIGURATIONS[0] : RepositoryTile.CONFIGURATIONS[repo.configuration];
        this.getBone(configuration[0]).get().setHidden(level == 0);
        this.getBone(configuration[1]).get().setHidden(level < 3);
        this.getBone(configuration[2]).get().setHidden(level < 5);
        this.getBone(configuration[3]).get().setHidden(level < 7);
        this.getBone(configuration[4]).get().setHidden(level < 9);
        this.getBone(configuration[5]).get().setHidden(level < 11);
        this.getBone(configuration[6]).get().setHidden(level < 12);
        this.getBone(configuration[7]).get().setHidden(level < 13);
        this.getBone(configuration[8]).get().setHidden(level < 14);
        this.getBone(configuration[9]).get().setHidden(level < 15);
    }

    @Override
    public ResourceLocation getModelResource(RepositoryTile object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(RepositoryTile object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(RepositoryTile animatable) {
        return ANIMATION;
    }
}
