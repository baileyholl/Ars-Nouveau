package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("rawtypes")
public class RotatingTurretRenderer extends ArsGeoBlockRenderer<RotatingTurretTile> {
    public static GeoModel model = new GenericModel("basic_spell_turret") {
        @Override
        public void setCustomAnimations(GeoAnimatable animatable, long instanceId, AnimationState animationState) {
            super.setCustomAnimations(animatable, instanceId, animationState);
            if (animatable instanceof RotatingTurretTile tile) {
                CoreGeoBone master = this.getAnimationProcessor().getBone("spell_turret");
                master.setRotY((tile.getRotationX() + 90) * Mth.DEG_TO_RAD);
                master.setRotX(tile.getRotationY() * Mth.DEG_TO_RAD);
            }
        }
    };

    public RotatingTurretRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    @Override
    public void render(RotatingTurretTile tile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(tile, partialTick, poseStack, bufferSource, packedLight);
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
