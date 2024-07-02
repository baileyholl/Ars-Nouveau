package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class RuneRenderer extends ArsGeoBlockRenderer<RuneTile> {

    public static GenericModel model = new GenericModel("rune");

    public RuneRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    @Override
    public void preRender(PoseStack poseStack, RuneTile animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {

    }

    @Override
    public void renderFinal(PoseStack poseStack, RuneTile animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int color) {

    }

    @Override
    public void actuallyRender(PoseStack poseStack, RuneTile animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        ParticleColor particleColor = animatable.spell.color();
        poseStack.pushPose();
        Direction direction = animatable.getBlockState().getValue(BasicSpellTurret.FACING);
        if (direction == Direction.UP) {
            poseStack.translate(0, -0.5, 0.5);

            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        } else if (direction == Direction.EAST) {
            poseStack.translate(0, 0, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(-90));
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        }else if(direction == Direction.NORTH){
            poseStack.translate(1, 1, 1);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        }else if(direction == Direction.DOWN){
            poseStack.translate(1, 0.5, 0.5);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
        }else if(direction == Direction.WEST){
            poseStack.translate(1, 0, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90));
        }else if(direction == Direction.SOUTH){
            poseStack.translate(1, 0, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-180));
        }
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, animatable.isCharged ? particleColor.getColor() : color);
        poseStack.popPose();
    }

    @Override
    public RenderType getRenderType(RuneTile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}