package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WildenStalker;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationState;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class WildenStalkerModel extends AnimatedGeoModel<WildenStalker> {

    @Override
    public void setCustomAnimations(WildenStalker entity, int uniqueID, @Nullable AnimationState customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.017453292F);
        head.setRotationY(extraData.netHeadYaw * 0.017453292F);

        this.getBone("wing_fly_left").setHidden(!entity.isFlying());
        this.getBone("wing_fly_right").setHidden(!entity.isFlying());

        this.getBone("wing_run_left").setHidden(entity.isFlying());
        this.getBone("wing_run_right").setHidden(entity.isFlying());
    }

    @Override
    public ResourceLocation getModelResource(WildenStalker wildenStalker) {
        return new ResourceLocation(ArsNouveau.MODID, "geo/wilden_stalker.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WildenStalker wildenStalker) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/wilden_stalker.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WildenStalker wildenStalker) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/wilden_stalker_animations.json");
    }

}
