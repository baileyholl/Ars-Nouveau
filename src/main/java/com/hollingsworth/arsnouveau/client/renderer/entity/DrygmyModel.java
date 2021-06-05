package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.DrygmyEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class DrygmyModel extends AnimatedGeoModel<DrygmyEntity> {

    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/drygmy.png");

    @Override
    public void setLivingAnimations(DrygmyEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.017453292F);
        head.setRotationY(extraData.netHeadYaw * 0.017453292F);
    }

    @Override
    public ResourceLocation getModelLocation(DrygmyEntity carbuncle) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/drygmy.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(DrygmyEntity carbuncle) {
        return WILD_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(DrygmyEntity carbuncle) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/drygmy_animations.json");
    }


}