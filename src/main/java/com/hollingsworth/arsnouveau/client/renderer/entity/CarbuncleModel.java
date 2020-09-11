package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class CarbuncleModel extends EntityModel<EntityCarbuncle> {
    public final ModelRenderer basket;
    public final ModelRenderer head;
    public final ModelRenderer ear_right;
    public final ModelRenderer ear_left;
    public final ModelRenderer front_leg_right;
    public final ModelRenderer front_leg_left;
    public final ModelRenderer back_leg_left;
    public final ModelRenderer back_leg_right;
    public final ModelRenderer tail;
    public final ModelRenderer body;

    public CarbuncleModel() {
        textureWidth = 32;
        textureHeight = 32;

        basket = new ModelRenderer(this);
        basket.setRotationPoint(-0.5F, 16.0F, 4.0F);
        basket.setTextureOffset(15, 0).addBox(-1.5F, -1.0F, -2.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        basket.setTextureOffset(0, 18).addBox(-2.5F, -2.0F, 2.0F, 6.0F, 2.0F, 1.0F, 0.0F, false);
        basket.setTextureOffset(14, 10).addBox(-2.5F, -2.0F, -3.0F, 6.0F, 2.0F, 1.0F, 0.0F, false);
        basket.setTextureOffset(10, 21).addBox(-2.5F, -2.0F, -2.0F, 1.0F, 2.0F, 4.0F, 0.0F, false);
        basket.setTextureOffset(0, 21).addBox(2.5F, -2.0F, -2.0F, 1.0F, 2.0F, 4.0F, 0.0F, false);

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, 20.0F, -2.5F);
        head.setTextureOffset(0, 10).addBox(-2.5F, -2.0F, -4.0F, 5.0F, 4.0F, 4.0F, 0.0F, false);
        head.setTextureOffset(20, 8).addBox(-0.5F, 0.0F, -4.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        ear_right = new ModelRenderer(this);
        ear_right.setRotationPoint(-1.5F, -1.0F, -2.0F);
        head.addChild(ear_right);
        setRotationAngle(ear_right, -2.7489F, 0.7854F, -3.1416F);
        ear_right.setTextureOffset(20, 21).addBox(-0.5F, -6.0F, -0.5F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        ear_right.setTextureOffset(24, 24).addBox(-0.5F, -7.0F, -1.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        ear_right.setTextureOffset(0, 0).addBox(-0.5F, -6.0F, -2.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);

        ear_left = new ModelRenderer(this);
        ear_left.setRotationPoint(1.5F, -1.0F, -2.0F);
        head.addChild(ear_left);
        setRotationAngle(ear_left, -2.7489F, -0.7854F, -3.1416F);
        ear_left.setTextureOffset(20, 21).addBox(-0.5F, -6.0F, -0.5F, 1.0F, 6.0F, 1.0F, 0.0F, true);
        ear_left.setTextureOffset(24, 24).addBox(-0.5F, -7.0F, -1.5F, 1.0F, 5.0F, 1.0F, 0.0F, true);
        ear_left.setTextureOffset(0, 0).addBox(-0.5F, -6.0F, -2.5F, 1.0F, 3.0F, 1.0F, 0.0F, true);

        front_leg_right = new ModelRenderer(this);
        front_leg_right.setRotationPoint(-1.0F, 23.0F, -1.5F);
        front_leg_right.setTextureOffset(0, 21).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        front_leg_left = new ModelRenderer(this);
        front_leg_left.setRotationPoint(1.0F, 23.0F, -1.5F);
        front_leg_left.setTextureOffset(20, 5).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        back_leg_left = new ModelRenderer(this);
        back_leg_left.setRotationPoint(1.0F, 25.0F, 0.5F);
        back_leg_left.setTextureOffset(15, 0).addBox(-0.5F, -3.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        back_leg_right = new ModelRenderer(this);
        back_leg_right.setRotationPoint(-1.0F, 25.0F, 0.5F);
        back_leg_right.setTextureOffset(0, 10).addBox(-0.5F, -3.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        tail = new ModelRenderer(this);
        tail.setRotationPoint(0.0F, 20.5F, 1.5F);
        setRotationAngle(tail, 0.0F, 3.1416F, 0.0F);
        tail.setTextureOffset(0, 0).addBox(-2.5F, -4.5F, -5.0F, 5.0F, 5.0F, 5.0F, 0.0F, false);

        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 21.5F, -0.5F);
        setRotationAngle(body, 0.0F, 3.1416F, 0.0F);
        body.setTextureOffset(14, 14).addBox(-2.0F, -1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 0.0F, false);
    }


    @Override
    public void setRotationAngles(EntityCarbuncle entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        //previously the render function, render code was moved to a method below
        this.head.rotateAngleX = headPitch * 0.017453292F;
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.back_leg_right.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.back_leg_left.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.front_leg_right.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.front_leg_left.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        basket.render(matrixStack, buffer, packedLight, packedOverlay);
        head.render(matrixStack, buffer, packedLight, packedOverlay);
        front_leg_right.render(matrixStack, buffer, packedLight, packedOverlay);
        front_leg_left.render(matrixStack, buffer, packedLight, packedOverlay);
        back_leg_left.render(matrixStack, buffer, packedLight, packedOverlay);
        back_leg_right.render(matrixStack, buffer, packedLight, packedOverlay);
        tail.render(matrixStack, buffer, packedLight, packedOverlay);
        body.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
