package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.AnimBlockSummon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class AnimBlockRenderer extends GeoEntityRenderer<AnimBlockSummon> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/anim_block.png");
    public static final ResourceLocation BASE_MODEL = new ResourceLocation(ArsNouveau.MODID, "geo/animated_block.geo.json");
    public static final ResourceLocation ANIMATIONS = new ResourceLocation(ArsNouveau.MODID, "animations/animated_block_animations.json");

    private final BlockRenderDispatcher dispatcher;
    MultiBufferSource buffer;

    public AnimBlockRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AnimatedGeoModel<>() {
            @Override
            public ResourceLocation getModelResource(AnimBlockSummon object) {
                return BASE_MODEL;
            }

            @Override
            public ResourceLocation getTextureResource(AnimBlockSummon object) {
                return TEXTURE;
            }

            @Override
            public ResourceLocation getAnimationResource(AnimBlockSummon animatable) {
                return ANIMATIONS;
            }

            @Override
            public void setCustomAnimations(AnimBlockSummon animatable, int instanceId, AnimationEvent customPredicate) {
                super.setCustomAnimations(animatable, instanceId, customPredicate);
                IBone head = this.getAnimationProcessor().getBone("block");
                if (customPredicate == null) return;
                EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
                head.setRotationX(extraData.headPitch * 0.017453292519943295F);
                head.setRotationY(extraData.netHeadYaw * 0.017453292519943295F);
            }
        });
        dispatcher = renderManager.getBlockRenderDispatcher();
    }

    @Override
    public void render(AnimBlockSummon animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.8F, 0.8F, 0.8F);
        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    public void renderEarly(AnimBlockSummon animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
        super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, partialTicks);
        this.buffer = bufferSource;
    }

    @Override
    public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("block")){
            bone.setHidden(true);
            try {
                AnimBlockSummon animBlock = animatable;
                if (animBlock == null) return;
                BlockState blockstate = animatable.getBlockState();
                Level level = animatable.getLevel();
                if (blockstate != level.getBlockState(animBlock.blockPosition()) && blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                    poseStack.pushPose();
                    BlockPos blockpos = animBlock.blockPosition().above();
                    poseStack.translate(-0.5D, 0.85D, -0.5D);
                    var model = this.dispatcher.getBlockModel(blockstate);
                    for (var renderType : model.getRenderTypes(blockstate, RandomSource.create(blockstate.getSeed(animBlock.blockPosition())), ModelData.EMPTY))
                        this.dispatcher.getModelRenderer().tesselateBlock(level, model, blockstate, blockpos, poseStack, this.buffer.getBuffer(renderType), false, RandomSource.create(), blockstate.getSeed(animBlock.getOnPos()), OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
                    poseStack.popPose();
                    buffer = this.buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
                }
            }catch (Exception e){
                // We typically don't render non-models like this, so catch our shenanigans.
            }
        }
        super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
