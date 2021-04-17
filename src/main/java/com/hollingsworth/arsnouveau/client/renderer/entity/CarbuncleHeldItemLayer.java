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
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class CarbuncleHeldItemLayer extends GeoLayerRenderer<EntityCarbuncle> {

    public CarbuncleHeldItemLayer(IGeoRenderer<EntityCarbuncle> entityRendererIn) {
        super(entityRendererIn);

    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityCarbuncle entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStackIn.pushPose();
        CarbuncleModel model = ((CarbuncleModel)getEntityModel());
        IBone tail = model.getBone("tail");
        IBone carbuncle = ((CarbuncleModel)getEntityModel()).getBone("carbuncle");

        matrixStackIn.translate((tail.getPositionX())/64f, (carbuncle.getPositionY())/32f ,
                (tail.getPositionZ())/64f);

        matrixStackIn.translate((double)0f, (double)0.5f, .2D);
        matrixStackIn.scale(0.75f, 0.75f, 0.75f);

        Quaternion quaternion = Vector3f.XP.rotationDegrees(carbuncle.getRotationX() );

        matrixStackIn.mulPose(quaternion);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));

        ItemStack itemstack = entitylivingbaseIn.getHeldStack();
        Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemCameraTransforms.TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY , matrixStackIn, bufferIn);
        matrixStackIn.popPose();
    }
}