package com.hollingsworth.arsnouveau.client.renderer.layer;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;

public class VinesEffectHandler {

    public static final Material VINES_1 =new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(ArsNouveau.MODID,"block/snare_0"));
    public static final Material VINES_2 =new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(ArsNouveau.MODID,"block/snare_1"));
    public static void renderWorldVinesEffect(PoseStack pMatrixStack, MultiBufferSource pBuffer, Camera camera, Entity pEntity) {
        TextureAtlasSprite textureAtlasSprite0 = VINES_1.sprite();
        TextureAtlasSprite textureAtlasSprite1 = VINES_2.sprite();
        pMatrixStack.pushPose();
        float f = pEntity.getBbWidth() * 1.4F;
        pMatrixStack.scale(f, f, f);
        float f1 = 0.5F;
        float f3 = pEntity.getBbHeight() / f;
        float f4 = 0.0F;
        pMatrixStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
        pMatrixStack.translate(0.0D, 0.0D, -0.3F + (float) ((int) f3) * 0.02F);
        float f5 = 0.0F;
        int i = 0;
        VertexConsumer vertexconsumer = pBuffer.getBuffer(Sheets.cutoutBlockSheet());

        for (PoseStack.Pose last = pMatrixStack.last(); f3 > 0.0F; ++i) {
            TextureAtlasSprite finalSprite = i % 2 == 0 ? textureAtlasSprite0 : textureAtlasSprite1;
            float f6 = finalSprite.getU0();
            float f7 = finalSprite.getV0();
            float f8 = finalSprite.getU1();
            float f9 = finalSprite.getV1();
            if (i / 2 % 2 == 0) {
                float f10 = f8;
                f8 = f6;
                f6 = f10;
            }

            fireVertex(last, vertexconsumer, f1 - 0.0F, 0.0F - f4, f5, f8, f9);
            fireVertex(last, vertexconsumer, -f1 - 0.0F, 0.0F - f4, f5, f6, f9);
            fireVertex(last, vertexconsumer, -f1 - 0.0F, 1.4F - f4, f5, f6, f7);
            fireVertex(last, vertexconsumer, f1 - 0.0F, 1.4F - f4, f5, f8, f7);
            f3 -= 0.45F;
            f4 -= 0.45F;
            f1 *= 0.9F;
            f5 += 0.03F;
        }

        pMatrixStack.popPose();
    }

    protected static void fireVertex(PoseStack.Pose pMatrixEntry, VertexConsumer pBuffer, float pX, float pY, float pZ, float pTexU, float pTexV) {
        pBuffer.vertex(pMatrixEntry.pose(), pX, pY, pZ).color(255, 255, 255, 255).uv(pTexU, pTexV).overlayCoords(0, 10).uv2(240).normal(pMatrixEntry.normal(), 0.0F, 1.0F, 0.0F).endVertex();
    }

}