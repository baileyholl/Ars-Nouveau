package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.entity.WildenGuardian;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

public class WildenGuardianRenderer  extends GeoEntityRenderer<WildenGuardian> {
    protected WildenGuardianRenderer(EntityRendererManager renderManager) {
        super(renderManager, new WildenGuardianModel());
    }
    private static final ResourceLocation GUARDIAN_BEAM_TEXTURE = new ResourceLocation("textures/entity/guardian_beam.png");
    private static final RenderType field_229107_h_ = RenderType.getEntityCutoutNoCull(GUARDIAN_BEAM_TEXTURE);

    @Override
    public boolean shouldRender(WildenGuardian livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            if (livingEntityIn.hasTargetedEntity()) {
                LivingEntity livingentity = livingEntityIn.getTargetedEntity();
                if (livingentity != null) {
                    Vector3d vector3d = this.getPosition(livingentity, (double)livingentity.getHeight() * 0.5D, 1.0F);
                    Vector3d vector3d1 = this.getPosition(livingEntityIn, (double)livingEntityIn.getEyeHeight(), 1.0F);
                    return camera.isBoundingBoxInFrustum(new AxisAlignedBB(vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y, vector3d.z));
                }
            }

            return false;
        }
    }

    @Override
    public void render(WildenGuardian entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        LivingEntity livingentity = entityIn.getTargetedEntity();
        if (livingentity != null && entityIn.getIsLaser()) {
            float f = entityIn.getAttackAnimationScale(partialTicks);
            float f1 = (float) ClientInfo.ticksInGame + partialTicks;
            float f2 = f1 * 0.5F % 1.0F;
            float f3 = entityIn.getEyeHeight() / 2;
            matrixStackIn.push();
            matrixStackIn.translate(0.0D, (double)f3, 0.0D);
            Vector3d vector3d = this.getPosition(livingentity, (double)livingentity.getHeight() * 0.5D, partialTicks);
            Vector3d vector3d1 = this.getPosition(entityIn, (double)f3, partialTicks);
            Vector3d vector3d2 = vector3d.subtract(vector3d1);
            float f4 = (float)(vector3d2.length() + 1.0D);
            vector3d2 = vector3d2.normalize();
            float f5 = (float)Math.acos(vector3d2.y);
            float f6 = (float)Math.atan2(vector3d2.z, vector3d2.x);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees((((float)Math.PI / 2F) - f6) * (180F / (float)Math.PI)));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f5 * (180F / (float)Math.PI)));
            int i = 1;
            float f7 = f1 * 0.05F * -1.5F;
            float f8 = f * f;
            int j = 64 + (int)(f8 * 191.0F);
            int k = 32 + (int)(f8 * 191.0F);
            int l = 128 - (int)(f8 * 64.0F);
            float f9 = 0.2F;
            float f10 = 0.282F;
            float f11 = MathHelper.cos(f7 + 2.3561945F) * 0.282F;
            float f12 = MathHelper.sin(f7 + 2.3561945F) * 0.282F;
            float f13 = MathHelper.cos(f7 + ((float)Math.PI / 4F)) * 0.282F;
            float f14 = MathHelper.sin(f7 + ((float)Math.PI / 4F)) * 0.282F;
            float f15 = MathHelper.cos(f7 + 3.926991F) * 0.282F;
            float f16 = MathHelper.sin(f7 + 3.926991F) * 0.282F;
            float f17 = MathHelper.cos(f7 + 5.4977875F) * 0.282F;
            float f18 = MathHelper.sin(f7 + 5.4977875F) * 0.282F;
            float f19 = MathHelper.cos(f7 + (float)Math.PI) * 0.2F;
            float f20 = MathHelper.sin(f7 + (float)Math.PI) * 0.2F;
            float f21 = MathHelper.cos(f7 + 0.0F) * 0.2F;
            float f22 = MathHelper.sin(f7 + 0.0F) * 0.2F;
            float f23 = MathHelper.cos(f7 + ((float)Math.PI / 2F)) * 0.2F;
            float f24 = MathHelper.sin(f7 + ((float)Math.PI / 2F)) * 0.2F;
            float f25 = MathHelper.cos(f7 + ((float)Math.PI * 1.5F)) * 0.2F;
            float f26 = MathHelper.sin(f7 + ((float)Math.PI * 1.5F)) * 0.2F;
            float f27 = 0.0F;
            float f28 = 0.4999F;
            float f29 = -1.0F + f2;
            float f30 = f4 * 2.5F + f29;
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(field_229107_h_);
            MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
            Matrix4f matrix4f = matrixstack$entry.getMatrix();
            Matrix3f matrix3f = matrixstack$entry.getNormal();
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f19, f4, f20, j, k, l, 0.4999F, f30);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f21, 0.0F, f22, j, k, l, 0.0F, f29);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f21, f4, f22, j, k, l, 0.0F, f30);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f23, f4, f24, j, k, l, 0.4999F, f30);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f25, 0.0F, f26, j, k, l, 0.0F, f29);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f25, f4, f26, j, k, l, 0.0F, f30);
            float f31 = 0.0F;
            if (entityIn.ticksExisted % 2 == 0) {
                f31 = 0.5F;
            }

            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f17, f4, f18, j, k, l, 1.0F, f31);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f15, f4, f16, j, k, l, 0.5F, f31);
            matrixStackIn.pop();
        }
    }
    private Vector3d getPosition(LivingEntity entityLivingBaseIn, double yOffset, float partialTicks) {
        double d0 = MathHelper.lerp(partialTicks, entityLivingBaseIn.lastTickPosX, entityLivingBaseIn.getPosX());
        double d1 = MathHelper.lerp(partialTicks, entityLivingBaseIn.lastTickPosY, entityLivingBaseIn.getPosY()) + yOffset;
        double d2 = MathHelper.lerp(partialTicks, entityLivingBaseIn.lastTickPosZ, entityLivingBaseIn.getPosZ());
        return new Vector3d(d0, d1, d2);
    }

    private static void func_229108_a_(IVertexBuilder p_229108_0_, Matrix4f p_229108_1_, Matrix3f p_229108_2_, float p_229108_3_, float p_229108_4_, float p_229108_5_, int p_229108_6_, int p_229108_7_, int p_229108_8_, float p_229108_9_, float p_229108_10_) {
        p_229108_0_.pos(p_229108_1_, p_229108_3_, p_229108_4_, p_229108_5_).color(p_229108_6_, p_229108_7_, p_229108_8_, 255).tex(p_229108_9_, p_229108_10_).overlay(OverlayTexture.NO_OVERLAY).lightmap(15728880).normal(p_229108_2_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    @Override
    public RenderType getRenderType(WildenGuardian animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.getEntityCutoutNoCull(textureLocation);
    }
}
