package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Nook;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import javax.annotation.Nullable;

public class NookModel extends GeoModel<Nook> {

    public static ResourceLocation TEXTURE = ArsNouveau.prefix("textures/entity/nook.png");
    public static ResourceLocation MODEL = ArsNouveau.prefix("geo/nook.geo.json");
    public static ResourceLocation ANIMATION = ArsNouveau.prefix("animations/nook_animation.json");

    @Override
    public void setCustomAnimations(Nook entity, long uniqueID, @Nullable AnimationState customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        GeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        
        if(entity.isOrderedToSit()) {
            head.setRotX(extraData.headPitch() * 0.017453292F - 32.5f * Mth.DEG_TO_RAD);
            head.setRotY(extraData.netHeadYaw() * 0.010453292F);
        }else{
            head.setRotX(extraData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(extraData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }

    @Override
    public ResourceLocation getModelResource(Nook nook) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Nook nook) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Nook nook) {
        return ANIMATION;
    }
}
