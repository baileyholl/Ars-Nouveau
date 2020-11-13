package com.hollingsworth.arsnouveau.client.renderer.entity;


import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class CarbuncleHeldItemLayer extends GeoLayerRenderer<EntityCarbuncle> {
    public CarbuncleRenderer rend;
    public CarbuncleHeldItemLayer(IGeoRenderer<EntityCarbuncle> entityRendererIn) {
        super(entityRendererIn);
        rend = entityRendererIn;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityCarbuncle entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {


        matrixStackIn.push();
        CarbuncleModel model = ((CarbuncleModel)getEntityModel());
        IBone tail = model.getBone("tail");
        IBone carbuncle = ((CarbuncleModel)getEntityModel()).getBone("carbuncle");

        matrixStackIn.translate((tail.getPositionX())/64f, -(carbuncle.getPositionY())/10f ,
                (tail.getPositionZ())/64f);

//        matrixStackIn.rotate(Vector3f.XP.rotation((float) Math.cos(this.getEntityModel().carbuncle.positionOffsetY)));
//        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(this.getEntityModel().carbuncle.rotateAngleX));
//        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(this.getEntityModel().carbuncle.rotateAngleZ));
//        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(headPitch));

        matrixStackIn.translate((double)0f, (double)1.0f, .2D);
        matrixStackIn.scale(0.75f, 0.75f, 0.75f);

//        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(this.getEntityModel().carbuncle.rotateAngleX));
//        System.out.println(this.getEntityModel().carbuncle.rotateAngleX*180f);
        Quaternion quaternion = Vector3f.XP.rotationDegrees(carbuncle.getRotationX()  +180f );
//        quaternion.conjugate();
        matrixStackIn.rotate(quaternion);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180f));
//        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(this.getEntityModel().carbuncle.rotateAngleX));
//        matrixStackIn.rotate(Vector3f.YP.rotationDegrees( (float)Math.cos(this.getEntityModel().carbuncle.rotateAngleY) *360f));
//        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees( (float)Math.cos(this.getEntityModel().carbuncle.rotateAngleZ)));
//

//        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        ItemStack itemstack = entitylivingbaseIn.getHeldStack();
//        Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
        Minecraft.getInstance().getItemRenderer().renderItem(itemstack, ItemCameraTransforms.TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY , matrixStackIn, bufferIn);
        matrixStackIn.pop();
    }
}