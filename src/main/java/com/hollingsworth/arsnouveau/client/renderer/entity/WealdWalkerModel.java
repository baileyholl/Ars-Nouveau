package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WealdWalker;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class WealdWalkerModel extends AnimatedGeoModel<WealdWalker> {
    String type;
    public WealdWalkerModel(String type){
        super();
        this.type = type;
    }

    @Override
    public void setLivingAnimations(WealdWalker entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.010453292F);
        head.setRotationY(extraData.netHeadYaw * 0.015453292F);
    }

    @Override
    public ResourceLocation getModelLocation(WealdWalker walker) {
        return walker.isBaby() ? new ResourceLocation(ArsNouveau.MODID , "geo/" + type + "_waddler.geo.json") : new ResourceLocation(ArsNouveau.MODID , "geo/" + type + "_walker.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(WealdWalker walker) {
        return walker.isBaby() ? new ResourceLocation(ArsNouveau.MODID , "textures/entity/" + type + "_waddler.png") :new ResourceLocation(ArsNouveau.MODID, "textures/entity/" + type + "_walker.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(WealdWalker walker) {
        return walker.isBaby() ? new ResourceLocation(ArsNouveau.MODID , "animations/weald_waddler_animations.json") :new ResourceLocation(ArsNouveau.MODID , "animations/weald_walker_animations.json");
    }
}
