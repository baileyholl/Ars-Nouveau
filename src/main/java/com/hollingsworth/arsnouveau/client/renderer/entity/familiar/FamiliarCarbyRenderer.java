package com.hollingsworth.arsnouveau.client.renderer.entity.familiar;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarStarbuncle;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class FamiliarCarbyRenderer<T extends FamiliarStarbuncle> extends GenericFamiliarRenderer<T> {

    public FamiliarCarbyRenderer(EntityRendererProvider.Context manager) {
        super(manager, new FamiliarCarbyModel<>());
    }

    public ResourceLocation getColor(FamiliarStarbuncle e){
        String color = e.getColor().toLowerCase();

        if (color.isEmpty()) color = "orange";

        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_" + color +".png");
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return getColor(entity);
    }

    public static class FamiliarCarbyModel<T extends FamiliarStarbuncle> extends AnimatedGeoModel<T> {

        private final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_wild_orange.png");
        private final ResourceLocation TAMED_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_orange.png");

        @Override
        public void setLivingAnimations(T entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
            super.setLivingAnimations(entity, uniqueID, customPredicate);
            IBone head = this.getAnimationProcessor().getBone("head");
            EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
            head.setRotationX(extraData.headPitch * 0.017453292F);
            head.setRotationY(extraData.netHeadYaw * 0.017453292F);
        }

        @Override
        public ResourceLocation getModelResource(FamiliarStarbuncle carbuncle) {
            return new ResourceLocation(ArsNouveau.MODID , "geo/carbuncle.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(FamiliarStarbuncle carbuncle) {
            return WILD_TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(FamiliarStarbuncle carbuncle) {
            return new ResourceLocation(ArsNouveau.MODID , "animations/starbuncle_animations.json");
        }

    }
}