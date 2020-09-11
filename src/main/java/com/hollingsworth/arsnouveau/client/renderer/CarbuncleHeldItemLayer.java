package com.hollingsworth.arsnouveau.client.renderer;

import com.hollingsworth.arsnouveau.client.renderer.entity.CarbuncleModel;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class CarbuncleHeldItemLayer extends LayerRenderer<EntityCarbuncle, CarbuncleModel> {
    public CarbuncleHeldItemLayer(IEntityRenderer<EntityCarbuncle, CarbuncleModel> entityRendererIn) {
        super(entityRendererIn);

    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityCarbuncle entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        matrixStackIn.push();


        matrixStackIn.translate(((this.getEntityModel()).tail.rotationPointX / 16.0F), ((this.getEntityModel()).tail.rotationPointY / 16.0F), ((this.getEntityModel()).tail.rotationPointZ / 16.0F));
        float f1 = 0.2f;
//        matrixStackIn.rotate(Vector3f.ZP.rotation(f1));
//        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(netHeadYaw));
//        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(headPitch));

        matrixStackIn.translate((double)0.00F, (double)-.4F, .15D);
        matrixStackIn.scale(0.5f, 0.5f, 0.5f);

        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90f));


//        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        ItemStack itemstack = entitylivingbaseIn.getHeldStack();
//        Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
        Minecraft.getInstance().getItemRenderer().renderItem(itemstack, ItemCameraTransforms.TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY , matrixStackIn, bufferIn);
        matrixStackIn.pop();
    }
}
