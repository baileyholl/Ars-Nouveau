package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class StarbuncleModel extends AnimatedGeoModel<Starbuncle> {

    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_wild_orange.png");
    private static final ResourceLocation TAMED_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_orange.png");

    @Override
    public void setLivingAnimations(Starbuncle entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        if (entity.partyCarby)
            return;
        IBone head = this.getAnimationProcessor().getBone("head");
        if (customPredicate == null)
            return;
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.017453292F);
        head.setRotationY(extraData.netHeadYaw * 0.017453292F);
    }

    @Override
    public ResourceLocation getModelResource(Starbuncle carbuncle) {
        return new ResourceLocation(ArsNouveau.MODID, "geo/carbuncle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Starbuncle carbuncle) {
        return carbuncle.isTamed() ? TAMED_TEXTURE : WILD_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Starbuncle carbuncle) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/starbuncle_animations.json");
    }
}