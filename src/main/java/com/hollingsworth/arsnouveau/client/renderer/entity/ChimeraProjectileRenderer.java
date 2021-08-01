package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.EntityChimeraProjectile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class ChimeraProjectileRenderer extends GeoProjectilesRenderer<EntityChimeraProjectile> {
    protected ChimeraProjectileRenderer(EntityRendererManager renderManager) {
        super(renderManager, new ChimeraProjectileModel());
    }

    @Override
    public void render(EntityChimeraProjectile entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.yRotO, entityIn.yRot) - 90.0F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.xRotO, entityIn.xRot)));

        float f9 = (float)entityIn.shakeTime - partialTicks;
        if (f9 > 0.0F) {
            float f10 = -MathHelper.sin(f9 * 3.0F) * f9;
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(f10));
        }
//        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(45.0F));
//
//        matrixStackIn.translate(-4.0D, 0.0D, 0.0D);
//        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

    }
}
