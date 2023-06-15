package com.hollingsworth.arsnouveau.client.renderer.entity.familiar;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarStarbuncle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class FamiliarStarbyModel<T extends FamiliarStarbuncle> extends AnimatedGeoModel<T> {

    @Override
    public void setCustomAnimations(T entity, int uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.017453292F);
        head.setRotationY(extraData.netHeadYaw * 0.017453292F);
    }

    @Override
    public ResourceLocation getModelResource(FamiliarStarbuncle carbuncle) {
        return new ResourceLocation(ArsNouveau.MODID, "geo/starbuncle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FamiliarStarbuncle carbuncle) {
        return carbuncle.getTexture(carbuncle);
    }

    @Override
    public ResourceLocation getAnimationResource(FamiliarStarbuncle carbuncle) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/starbuncle_animations.json");
    }

}
