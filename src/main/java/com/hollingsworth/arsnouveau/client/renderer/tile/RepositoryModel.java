package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryTile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RepositoryModel extends AnimatedGeoModel<RepositoryTile> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/blocks/repository.png");
    public static final ResourceLocation MODEL = new ResourceLocation(ArsNouveau.MODID, "geo/repository.geo.json");
    public static final ResourceLocation ANIMATION = new ResourceLocation(ArsNouveau.MODID, "animations/empty.geo.json");

    public RepositoryModel(){
    }


    @Override
    public void setCustomAnimations(RepositoryTile repo, int instanceId) {
        super.setCustomAnimations(repo, instanceId);
        int level = repo.fillLevel;
        String[] configuration = repo.configuration > RepositoryTile.CONFIGURATIONS.length ? RepositoryTile.CONFIGURATIONS[0] : RepositoryTile.CONFIGURATIONS[repo.configuration];
        this.getBone(configuration[0]).setHidden(level == 0);
        this.getBone(configuration[1]).setHidden(level < 3);
        this.getBone(configuration[2]).setHidden(level < 5);
        this.getBone(configuration[3]).setHidden(level < 7);
        this.getBone(configuration[4]).setHidden(level < 9);
        this.getBone(configuration[5]).setHidden(level < 11);
        this.getBone(configuration[6]).setHidden(level < 12);
        this.getBone(configuration[7]).setHidden(level < 13);
        this.getBone(configuration[8]).setHidden(level < 14);
        this.getBone(configuration[9]).setHidden(level < 15);
    }

    @Override
    public void setCustomAnimations(RepositoryTile repo, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(repo, instanceId, animationEvent);
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
