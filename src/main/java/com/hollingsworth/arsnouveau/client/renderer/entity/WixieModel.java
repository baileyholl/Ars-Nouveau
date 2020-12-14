package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class WixieModel extends AnimatedGeoModel<EntityWixie> {

    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/wixie.png");


    @Override
    public void setLivingAnimations(EntityWixie entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.010453292F);
        head.setRotationY(extraData.netHeadYaw * 0.015453292F);
    }

    @Override
    public ResourceLocation getModelLocation(EntityWixie entityWixie) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/wixie.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(EntityWixie entityWixie) {
        return WILD_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntityWixie entityWixie) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/wixie_animations.json");
    }
}
