package com.hollingsworth.arsnouveau.client.renderer.entity;// Made with Blockbench 3.6.6

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

public class BookwyrmModel<T extends LivingEntity & GeoAnimatable> extends GeoModel<T> {

    private static final ResourceLocation WILD_TEXTURE = ArsNouveau.prefix( "textures/entity/book_wyrm_blue.png");
    public static final ResourceLocation NORMAL_MODEL = ArsNouveau.prefix( "geo/book_wyrm.geo.json");
    public static final ResourceLocation ANIMATIONS = ArsNouveau.prefix( "animations/book_wyrm_animation.json");

    @Override
    public void setCustomAnimations(T entity, long uniqueID, @Nullable AnimationState<T> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);

        GeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX(extraData.headPitch() * 0.010453292F);
        head.setRotY(extraData.netHeadYaw() * 0.015453292F);
    }

    @Override
    public ResourceLocation getModelResource(T wyrm) {
        return NORMAL_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(T wyrm) {
        return WILD_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(T wyrm) {
        return ANIMATIONS;
    }
}