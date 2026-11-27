package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class RotatingTurretRenderer extends ArsGeoBlockRenderer<RotatingTurretTile> {

    public RotatingTurretRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, BasicTurretRenderer.model);
    }

    @Override
    public void preRender(PoseStack poseStack, RotatingTurretTile animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
        if (!isReRender) {
            poseStack.translate(0, 0.5, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.rotLerp(partialTick, animatable.prevRotationX, animatable.rotationX) + 90));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, animatable.prevRotationY, animatable.rotationY)));
            poseStack.translate(0, -0.5, 0);
        }
    }

    //Disable geckolib automatic rotation based on blockstate
    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
    }
}
