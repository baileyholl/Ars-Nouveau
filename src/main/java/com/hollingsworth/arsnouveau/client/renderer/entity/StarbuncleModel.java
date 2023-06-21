package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import javax.annotation.Nullable;

public class StarbuncleModel extends GeoModel<Starbuncle> {

    @Override
    public void setCustomAnimations(Starbuncle entity, int uniqueID, @Nullable AnimationState customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        if (entity.partyCarby)
            return;
        IBone head = this.getAnimationProcessor().getBone("head");
        if (customPredicate == null)
            return;
        this.getBone("basket").setHidden(!entity.isTamed());
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.017453292F);
        head.setRotationY(extraData.netHeadYaw * 0.017453292F);
    }

    @Override
    public ResourceLocation getModelResource(Starbuncle carbuncle) {
        return new ResourceLocation(ArsNouveau.MODID, "geo/starbuncle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Starbuncle carbuncle) {
        return carbuncle.getTexture(carbuncle);
    }

    @Override
    public ResourceLocation getAnimationResource(Starbuncle carbuncle) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/starbuncle_animations.json");
    }
}