package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib3.core.GeoAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationState;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class WhirlisprigModel<T extends LivingEntity & GeoAnimatable> extends GeoModel<T> {

    @Override
    public void setCustomAnimations(T entity, int uniqueID, @Nullable AnimationState customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.010453292F);
        head.setRotationY(extraData.netHeadYaw * 0.015453292F);
    }

    @Override
    public ResourceLocation getModelResource(T whirlisprig) {
        return new ResourceLocation(ArsNouveau.MODID, "geo/whirlisprig.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T whirlisprig) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/whirlisprig.png");
    }

    @Override
    public ResourceLocation getAnimationResource(T whirlisprig) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/whirlisprig_animations.json");
    }
}
