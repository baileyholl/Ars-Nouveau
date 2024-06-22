package com.hollingsworth.arsnouveau.client.renderer.entity.familiar;

import com.hollingsworth.arsnouveau.client.renderer.entity.AnimBlockRenderer;
import com.hollingsworth.arsnouveau.client.renderer.entity.EnchantedSkullRenderer;
import com.hollingsworth.arsnouveau.common.entity.AnimHeadSummon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.RenderUtil;


public class AnimSkullRenderer extends AnimBlockRenderer<AnimHeadSummon> {
    public AnimSkullRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }


    @Override
    public void renderRecursively(PoseStack poseStack, AnimHeadSummon animatable, GeoBone bone, RenderType ty, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("block")) {
            AnimHeadSummon animBlock = animatable;
            if (animBlock == null) return;
            poseStack.pushPose();
            RenderUtil.translateToPivotPoint(poseStack, bone);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.translate(0,0.2,0);
            poseStack.scale(1.4F, 1.4F, 1.4F);
            EnchantedSkullRenderer.renderSkull(animatable.level, animBlock.getStack(), poseStack, bufferSource, packedLight);
            poseStack.popPose();
            buffer = this.bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        }
        super.renderRecursively(poseStack, animatable, bone, ty, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
