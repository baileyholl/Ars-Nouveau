package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.entity.DiscEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DiscRenderer extends GeoEntityRenderer<DiscEntity> {

    public DiscRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GenericModel<>("terra_top").withEmptyAnim());
    }

    @Override
    public void render(DiscEntity entityIn, float pEntityYaw, float pPartialTick, PoseStack poseStack, MultiBufferSource bufferSource, int pPackedLight) {
        poseStack.pushPose();
        float angle = Mth.lerp(pPartialTick, entityIn.spinAngleO, entityIn.spinAngle);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        super.render(entityIn, pEntityYaw, pPartialTick, poseStack, bufferSource, pPackedLight);
        poseStack.popPose();
//        BlockState state = Blocks.DIRT.defaultBlockState();
//        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, bufferSource, pPackedLight, OverlayTexture.NO_OVERLAY);
    }

    @Override
    public ResourceLocation getTextureLocation(DiscEntity entity) {
        return ResourceLocation.withDefaultNamespace("textures/block/dirt.png");
    }

}
