package com.hollingsworth.arsnouveau.client.renderer.entity.familiar;

import com.hollingsworth.arsnouveau.client.renderer.entity.AnimBlockRenderer;
import com.hollingsworth.arsnouveau.client.renderer.entity.EnchantedSkullRenderer;
import com.hollingsworth.arsnouveau.common.entity.AnimHeadSummon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.util.RenderUtils;

public class AnimSkullRenderer extends AnimBlockRenderer<AnimHeadSummon> {
    public AnimSkullRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("block")) {
            AnimHeadSummon animBlock = animatable;
            if (animBlock == null) return;
            poseStack.pushPose();
            RenderUtils.translateToPivotPoint(poseStack, bone);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
            poseStack.translate(0,0.2,0);
            poseStack.scale(1.4F, 1.4F, 1.4F);
            EnchantedSkullRenderer.renderSkull(animBlock.getStack(), poseStack, bufferSource, packedLight);
            poseStack.popPose();
            buffer = this.bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        }
        super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
