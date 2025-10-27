package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import javax.annotation.Nullable;

public class DrygmyModel<T extends LivingEntity & GeoAnimatable> extends GeoModel<T> {

    private static final ResourceLocation WILD_TEXTURE = ArsNouveau.prefix("textures/entity/drygmy.png");
    public static final ResourceLocation NORMAL_MODEL = ArsNouveau.prefix("geo/drygmy.geo.json");
    public static final ResourceLocation ANIMATIONS = ArsNouveau.prefix("animations/drygmy_animations.json");

    @Override
    public void setCustomAnimations(T entity, long uniqueID, @Nullable AnimationState<T> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        GeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX(extraData.headPitch() * 0.017453292F);
        head.setRotY(extraData.netHeadYaw() * 0.017453292F);
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