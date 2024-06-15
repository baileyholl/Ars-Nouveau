package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import software.bernie.geckolib.cache.object.BakedGeoModel;

@SuppressWarnings("rawtypes")
public class RotatingTurretRenderer extends ArsGeoBlockRenderer<RotatingTurretTile> {

    public RotatingTurretRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, BasicTurretRenderer.model);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, RotatingTurretTile tile, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(poseStack, tile, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        float rotationX = tile.rotationX;
        float neededRotationX = tile.clientNeededX;
        float rotationY = tile.rotationY;
        float neededRotationY = tile.clientNeededY;
        float step = (0.1f + partialTick);
        if(rotationX != neededRotationX){
            float diff = neededRotationX - rotationX;
            if(Math.abs(diff) < step){
                tile.setRotationX(neededRotationX);
            }else{
                tile.setRotationX(rotationX + diff * step);
            }
        }
        if(rotationY != neededRotationY){
            float diff = neededRotationY - rotationY;
            if(Math.abs(diff) < step){
                tile.setRotationY(neededRotationY);
            }else{
                tile.setRotationY(rotationY + diff * step);
            }
        }
    }

    //Disable geckolib automatic rotation based on blockstate
    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
    }
}
