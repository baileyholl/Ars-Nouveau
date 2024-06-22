package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.MageBlock;
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
import net.neoforged.neoforge.client.model.data.ModelData;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animatable.model.CoreGeoBone;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.object.Color;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtil;



public class AnimBlockRenderer<BOBBY extends AnimBlockSummon> extends GeoEntityRenderer<BOBBY> {

    protected static final ResourceLocation TEXTURE = ArsNouveau.prefix( "textures/entity/anim_block.png");
    public static final ResourceLocation BASE_MODEL = ArsNouveau.prefix( "geo/animated_block.geo.json");
    public static final ResourceLocation ANIMATIONS = ArsNouveau.prefix( "animations/animated_block_animations.json");

    private final BlockRenderDispatcher dispatcher;
    protected MultiBufferSource bufferSource;

    public AnimBlockRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GeoModel<BOBBY>() {
            @Override
            public ResourceLocation getModelResource(BOBBY object) {
                return BASE_MODEL;
            }

            @Override
            public ResourceLocation getTextureResource(BOBBY object) {
                return TEXTURE;
            }

            @Override
            public ResourceLocation getAnimationResource(BOBBY animatable) {
                return ANIMATIONS;
            }

            @Override
            public void setCustomAnimations(BOBBY animatable, long instanceId, AnimationState<BOBBY> customPredicate) {
                super.setCustomAnimations(animatable, instanceId, customPredicate);
                CoreGeoBone head = this.getAnimationProcessor().getBone("block");
                head.setHidden(!(animatable.getBlockState().getBlock() instanceof MageBlock));
            }
        });
        dispatcher = renderManager.getBlockRenderDispatcher();
    }

    @Override
    public void render(BOBBY animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.8F, 0.8F, 0.8F);
        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    public void preRender(PoseStack poseStack, BOBBY animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.bufferSource = bufferSource;
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, BOBBY animatable, GeoBone bone, RenderType ty, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("block")) {
            AnimBlockSummon animBlock = animatable;
            if (animBlock == null) return;
            BlockState blockstate = animatable.getBlockState();
            //don't override the block and color it or
            //hide the block and render the blockstate
            if (!(blockstate.getBlock() instanceof MageBlock)) {
                try {
                    Level level = animatable.level();
                    if (blockstate != level.getBlockState(animBlock.blockPosition()) && blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                        poseStack.pushPose();
                        BlockPos blockpos = animBlock.blockPosition().above();
                        RenderUtil.translateToPivotPoint(poseStack, bone);
                        poseStack.translate(-0.5D, -0.5, -0.5D);
                        var model = this.dispatcher.getBlockModel(blockstate);
                        for (var renderType : model.getRenderTypes(blockstate, RandomSource.create(blockstate.getSeed(animBlock.blockPosition())), ModelData.EMPTY))
                            this.dispatcher.getModelRenderer().tesselateBlock(level, model, blockstate, blockpos, poseStack, this.bufferSource.getBuffer(renderType), false, RandomSource.create(), blockstate.getSeed(animBlock.getOnPos()), OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
                        poseStack.popPose();
                        buffer = this.bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
                    }
                } catch (Exception e) {
                    // We typically don't render non-models like this, so catch our shenanigans.
                }
            }
        }
        super.renderRecursively(poseStack, animatable, bone, ty, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public Color getRenderColor(BOBBY animatable, float partialTick, int packedLight) {
        if (animatable != null) {
            ParticleColor color = ParticleColor.fromInt(animatable.getColor());
            return Color.ofRGBA(color.getRed(), color.getGreen(), color.getBlue(), 0.75f);
        }
        return super.getRenderColor(animatable, partialTick, packedLight);
    }
}
