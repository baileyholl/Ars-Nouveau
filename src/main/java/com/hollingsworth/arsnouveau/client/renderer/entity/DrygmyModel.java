package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib3.core.GeoAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationState;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class DrygmyModel<T extends LivingEntity & GeoAnimatable> extends AnimatedGeoModel<T> {

    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/drygmy.png");
    public static final ResourceLocation NORMAL_MODEL = new ResourceLocation(ArsNouveau.MODID, "geo/drygmy.geo.json");
    public static final ResourceLocation ANIMATIONS = new ResourceLocation(ArsNouveau.MODID, "animations/drygmy_animations.json");

    @Override
    public void setCustomAnimations(T entity, int uniqueID, @Nullable AnimationState customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.017453292F);
        head.setRotationY(extraData.netHeadYaw * 0.017453292F);
    }

    @Override
    public ResourceLocation getModelResource(T drygmy) {
        return NORMAL_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(T drygmy) {
        return WILD_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(T drygmy) {
        return ANIMATIONS;
    }
}