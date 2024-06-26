package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import javax.annotation.Nullable;

public class StarbuncleModel extends GeoModel<Starbuncle> {

    @Override
    public void setCustomAnimations(Starbuncle entity, long uniqueID, @Nullable AnimationState customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        if (entity.partyCarby)
            return;
        if (customPredicate == null)
            return;
        this.getBone("basket").get().setHidden(!entity.isTamed());

        GeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX(extraData.headPitch() * 0.017453292F);
        head.setRotY(extraData.netHeadYaw() * 0.017453292F);
    }

    @Override
    public ResourceLocation getModelResource(Starbuncle carbuncle) {
        return ArsNouveau.prefix( "geo/starbuncle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Starbuncle carbuncle) {
        return carbuncle.getTexture(carbuncle);
    }

    @Override
    public ResourceLocation getAnimationResource(Starbuncle carbuncle) {
        return ArsNouveau.prefix( "animations/starbuncle_animations.json");
    }
}