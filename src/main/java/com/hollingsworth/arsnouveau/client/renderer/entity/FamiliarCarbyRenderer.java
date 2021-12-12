package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarCarbuncle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

public class FamiliarCarbyRenderer  extends GeoEntityRenderer<FamiliarCarbuncle> {
    private static final ResourceLocation ORANGE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_orange.png");
    private static final ResourceLocation PURPLE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_purple.png");
    private static final ResourceLocation GREEN = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_green.png");
    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_wild_orange.png");

    public FamiliarCarbyRenderer(EntityRendererProvider.Context manager) {
        super(manager, new FamiliarCarbyModel());
    }

    @Override
    protected void applyRotations(FamiliarCarbuncle entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public void render(FamiliarCarbuncle entity, float entityYaw, float p_225623_3_, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int p_225623_6_) {
        super.render(entity, entityYaw, p_225623_3_, matrixStack, iRenderTypeBuffer, p_225623_6_);
    }


    public ResourceLocation getColor(FamiliarCarbuncle e){
        String color = e.getEntityData().get(Starbuncle.COLOR).toLowerCase();

        if(color.isEmpty())
            return ORANGE;

        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_" + color +".png");
    }

    @Override
    public ResourceLocation getTextureLocation(FamiliarCarbuncle entity) {
        return WILD_TEXTURE;
    }

    @Override
    public RenderType getRenderType(FamiliarCarbuncle animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityCutoutNoCull(textureLocation);
    }

    public static class FamiliarCarbyModel extends AnimatedGeoModel<FamiliarCarbuncle> {

        private final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_wild_orange.png");
        private final ResourceLocation TAMED_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_orange.png");

        @Override
        public void setLivingAnimations(FamiliarCarbuncle entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
            super.setLivingAnimations(entity, uniqueID, customPredicate);
            IBone head = this.getAnimationProcessor().getBone("head");
            EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
            head.setRotationX(extraData.headPitch * 0.017453292F);
            head.setRotationY(extraData.netHeadYaw * 0.017453292F);
        }

        @Override
        public ResourceLocation getModelLocation(FamiliarCarbuncle carbuncle) {
            return new ResourceLocation(ArsNouveau.MODID , "geo/carbuncle.geo.json");
        }

        @Override
        public ResourceLocation getTextureLocation(FamiliarCarbuncle carbuncle) {
            return WILD_TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationFileLocation(FamiliarCarbuncle carbuncle) {
            return new ResourceLocation(ArsNouveau.MODID , "animations/carbuncle_animations.json");
        }

    }
}