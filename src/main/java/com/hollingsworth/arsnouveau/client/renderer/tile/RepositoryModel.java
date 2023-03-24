package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryTile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RepositoryModel extends AnimatedGeoModel<RepositoryTile> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/repository.png");
    public static final ResourceLocation MODEL = new ResourceLocation(ArsNouveau.MODID, "geo/repository.geo.json");
    public static final ResourceLocation ANIMATION = new ResourceLocation(ArsNouveau.MODID, "animations/empty.geo.json");

    @Override
    public void setCustomAnimations(RepositoryTile animatable, int instanceId) {
        super.setCustomAnimations(animatable, instanceId);
    }

    @Override
    public void setCustomAnimations(RepositoryTile repo, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(repo, instanceId, animationEvent);
        this.getBone("1").setHidden(repo.fillLevel == 0);
        this.getBone("2_3").setHidden(repo.fillLevel < 3);
        this.getBone("4_6").setHidden(repo.fillLevel < 5);
        this.getBone("7_9").setHidden(repo.fillLevel < 7);
        this.getBone("10_12").setHidden(repo.fillLevel < 9);
        this.getBone("13_15").setHidden(repo.fillLevel < 11);
        this.getBone("16_18").setHidden(repo.fillLevel < 12);
        this.getBone("19_21").setHidden(repo.fillLevel < 13);
        this.getBone("22_24").setHidden(repo.fillLevel < 14);
        this.getBone("25_27").setHidden(repo.fillLevel < 15);
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
