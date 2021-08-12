package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.common.entity.FamiliarCarbuncle;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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

    public FamiliarCarbyRenderer(EntityRendererManager manager) {
        super(manager, new FamiliarCarbyModel());
    }

    @Override
    protected void applyRotations(FamiliarCarbuncle entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public void render(FamiliarCarbuncle entity, float entityYaw, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int p_225623_6_) {
        super.render(entity, entityYaw, p_225623_3_, matrixStack, iRenderTypeBuffer, p_225623_6_);
    }


    public ResourceLocation getColor(FamiliarCarbuncle e){
        String color = e.getEntityData().get(EntityCarbuncle.COLOR).toLowerCase();

        if(color.isEmpty())
            return ORANGE;

        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_" + color +".png");
    }

    @Override
    public ResourceLocation getTextureLocation(FamiliarCarbuncle entity) {
        return WILD_TEXTURE;
    }

    @Override
    public RenderType getRenderType(FamiliarCarbuncle animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
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

            IBone backLeft = this.getAnimationProcessor().getBone("back_leg_right");

            IBone backRight = this.getAnimationProcessor().getBone("back_leg_right");
            IBone frontLeft = this.getAnimationProcessor().getBone("front_leg_left");
            IBone frontRight = this.getAnimationProcessor().getBone("front_leg_right");
            backRight.setRotationX(MathHelper.cos(entity.animationPosition * 0.6662F) * 1.4F * entity.animationSpeed);
            backLeft.setRotationX(MathHelper.cos(entity.animationPosition * 0.6662F + (float)Math.PI) * 1.4F * entity.animationSpeed);
            frontLeft.setRotationX(MathHelper.cos(entity.animationPosition * 0.6662F) * 1.4F * entity.animationSpeed);
            frontRight.setRotationX(MathHelper.cos(entity.animationPosition * 0.6662F + (float)Math.PI) * 1.4F * entity.animationSpeed);
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