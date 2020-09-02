package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class CarbuncleModel extends EntityModel<EntityCarbuncle> {
    private final ModelRenderer basket;
    private final ModelRenderer head;
    private final ModelRenderer ear_left;
    private final ModelRenderer ear_right;
    private final  ModelRenderer front_leg_right;
    private final ModelRenderer front_leg_left;
    private final ModelRenderer back_leg_left;
    private final ModelRenderer back_leg_right;
    private final ModelRenderer tail;
    private final ModelRenderer body;

    public CarbuncleModel() {
        textureWidth = 64;
        textureHeight = 64;

        basket = new ModelRenderer(this);
        basket.setRotationPoint(1.5F, 16.0F, -0.5F);
        basket.setTextureOffset(0, 0).addBox(-4.5F, -0.1F, -6.0F, 6.0F, 0.0F, 6.0F, 0.0F, false);
        basket.setTextureOffset(12, 17).addBox(-4.5F, -1.1F, 0.0F, 6.0F, 1.0F, 0.0F, 0.0F, false);
        basket.setTextureOffset(15, 10).addBox(-4.5F, -1.1F, -6.0F, 6.0F, 1.0F, 0.0F, 0.0F, false);
        basket.setTextureOffset(12, 10).addBox(-4.5F, -1.1F, -6.0F, 0.0F, 1.0F, 6.0F, 0.0F, false);
        basket.setTextureOffset(12, 12).addBox(1.5F, -1.1F, -6.0F, 0.0F, 1.0F, 6.0F, 0.0F, false);

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, 20.0F, 3.0F);
        head.setTextureOffset(15, 2).addBox(-2.5F, -2.0F, 0.0F, 5.0F, 4.0F, 4.0F, 0.0F, false);
        head.setTextureOffset(0, 9).addBox(-0.5F, 0.0F, 3.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        ear_left = new ModelRenderer(this);
        ear_left.setRotationPoint(-1.0F, -2.0F, 1.5F);
        head.addChild(ear_left);
        setRotationAngle(ear_left, 0.3927F, 0.7854F, 0.0F);
        ear_left.setTextureOffset(0, 0).addBox(0.0F, -5.5412F, -1.8066F, 0.0F, 6.0F, 1.0F, 0.0F, false);
        ear_left.setTextureOffset(4, 0).addBox(0.0F, -6.5412F, -2.8066F, 0.0F, 5.0F, 1.0F, 0.0F, false);
        ear_left.setTextureOffset(18, 21).addBox(0.0F, -5.5412F, -3.8066F, 0.0F, 3.0F, 1.0F, 0.0F, false);

        ear_right = new ModelRenderer(this);
        ear_right.setRotationPoint(3.0F, -2.0F, 1.5F);
        head.addChild(ear_right);
        setRotationAngle(ear_right, 0.3927F, -0.7854F, 0.0F);
        ear_right.setTextureOffset(2, 2).addBox(-1.4142F, -5.0F, -0.5F, 0.0F, 6.0F, 1.0F, 0.0F, false);
        ear_right.setTextureOffset(16, 18).addBox(-1.4142F, -6.0F, -1.5F, 0.0F, 5.0F, 1.0F, 0.0F, false);
        ear_right.setTextureOffset(0, 22).addBox(-1.4142F, -5.0F, -2.5F, 0.0F, 3.0F, 1.0F, 0.0F, false);

        front_leg_right = new ModelRenderer(this);
        front_leg_right.setRotationPoint(2.0F, 23.0F, 3.0F);
        front_leg_right.setTextureOffset(21, 21).addBox(-1.5F, -1.0F, -1.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        front_leg_left = new ModelRenderer(this);
        front_leg_left.setRotationPoint(0.0F, 23.0F, 3.0F);
        front_leg_left.setTextureOffset(20, 11).addBox(-1.5F, -1.0F, -1.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        back_leg_left = new ModelRenderer(this);
        back_leg_left.setRotationPoint(2.5F, 25.0F, -0.5F);
        back_leg_left.setTextureOffset(18, 19).addBox(-4.0F, -3.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        back_leg_right = new ModelRenderer(this);
        back_leg_right.setRotationPoint(2.5F, 25.0F, -0.5F);
        back_leg_right.setTextureOffset(0, 16).addBox(-2.0F, -3.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        tail = new ModelRenderer(this);
        tail.setRotationPoint(-1.0F, 21.0F, -1.0F);
        tail.setTextureOffset(0, 6).addBox(-1.5F, -5.0F, -5.0F, 5.0F, 5.0F, 5.0F, 0.0F, false);

        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 24.0F, 0.0F);
        body.setTextureOffset(0, 16).addBox(-2.0F, -4.0F, -1.0F, 4.0F, 3.0F, 4.0F, 0.0F, false);
    }


    @Override
    public void setRotationAngles(EntityCarbuncle entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        //previously the render function, render code was moved to a method below
        this.head.rotateAngleX = headPitch * 0.017453292F;
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
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
