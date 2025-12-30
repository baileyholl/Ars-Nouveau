package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;

public class ScryerPlanariumModel extends GenericModel {
    public ScryerPlanariumModel() {
        super("planarium");
        this.textLoc = ArsNouveau.prefix("textures/" + textPathRoot + "/planarium.png");
    }


    @Override
    public void setCustomAnimations(GeoAnimatable animatable, long instanceId, AnimationState animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

    }

    @Override
    public @Nullable RenderType getRenderType(GeoAnimatable animatable, ResourceLocation texture) {
        return RenderType.entityCutout(texture);
    }
}
