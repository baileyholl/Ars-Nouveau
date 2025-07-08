package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.DecorBlossomTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BlossomRenderer extends GeoBlockRenderer<DecorBlossomTile> {
    public BlossomRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new GenericModel<>("decor_blossom"));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, DecorBlossomTile animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        poseStack.pushPose();
        Direction direction = animatable.getBlockState().getValue(BasicSpellTurret.FACING);
//        poseStack.translate(0.5, 0.5, 0);
        switch (direction) {
            case UP -> {
                poseStack.translate(0, 0, 0);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            }
            case DOWN -> {
                poseStack.translate(0, 1, 0);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            }
            case NORTH -> {
                poseStack.translate(0, 0.5, 0.5);
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
            case EAST -> {
                poseStack.translate(-0.5, 0.5, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
            case WEST -> {
                poseStack.translate(0.5, 0.5, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            }
            case SOUTH -> {
                poseStack.translate(0, 0.5, -0.5);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-180));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
        }
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
        poseStack.popPose();
    }

}
