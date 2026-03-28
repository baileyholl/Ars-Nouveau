package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.DecorBlossomTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

public class BlossomRenderer extends GeoBlockRenderer<DecorBlossomTile, ArsBlockEntityRenderState> {
    public BlossomRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new GenericModel<>("decor_blossom"));
    }

    @Override
    public ArsBlockEntityRenderState createRenderState() {
        return new ArsBlockEntityRenderState();
    }

    // GeckoLib 5: actuallyRender replaced with adjustRenderPose for pose manipulation.
    // blockState is available in the render state so we can still read facing direction.
    @Override
    public void adjustRenderPose(RenderPassInfo<ArsBlockEntityRenderState> renderPassInfo) {
        super.adjustRenderPose(renderPassInfo);
        PoseStack poseStack = renderPassInfo.poseStack();
        Direction direction = renderPassInfo.renderState().blockState.getValue(BasicSpellTurret.FACING);
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
    }
}
