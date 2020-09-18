package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animation.model.AnimatedEntityModel;
import software.bernie.geckolib.animation.render.AnimatedModelRenderer;

public class CarbuncleModel extends AnimatedEntityModel<EntityCarbuncle> {
    public final AnimatedModelRenderer carbuncle;
    public final AnimatedModelRenderer head;
    public final AnimatedModelRenderer ear_right;
    public final AnimatedModelRenderer ear_left;
    public final AnimatedModelRenderer front_leg_right;
    public final AnimatedModelRenderer front_leg_left;
    public final AnimatedModelRenderer back_leg_left;
    public final AnimatedModelRenderer back_leg_right;
    public final AnimatedModelRenderer tail;
    public final AnimatedModelRenderer basket;
    public final AnimatedModelRenderer body;

    @Override
    public ResourceLocation getAnimationFileLocation() {
        return new ResourceLocation(ArsNouveau.MODID + ":animations/carbuncle_animations.json");
    }

    public CarbuncleModel() {
        textureWidth = 32;
        textureHeight = 32;
        carbuncle = new AnimatedModelRenderer(this);
        carbuncle.setRotationPoint(0.0F, 24.0F, 0.0F);

        carbuncle.setModelRendererName("carbuncle");
        this.registerModelRenderer(carbuncle);

        head = new AnimatedModelRenderer(this);
        head.setRotationPoint(0.0F, -4.0F, -2.5F);
        carbuncle.addChild(head);
        head.setTextureOffset(0, 10).addBox(-2.5F, -2.0F, -4.0F, 5.0F, 4.0F, 4.0F, 0.0F, false);
        head.setTextureOffset(20, 8).addBox(-0.5F, 0.0F, -4.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        head.setModelRendererName("head");
        this.registerModelRenderer(head);

        ear_right = new AnimatedModelRenderer(this);
        ear_right.setRotationPoint(-1.5F, -1.0F, -2.0F);
        head.addChild(ear_right);
        setRotationAngle(ear_right, -2.7489F, 0.7854F, -3.1416F);
        ear_right.setTextureOffset(20, 21).addBox(-0.5F, -6.0F, -0.5F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        ear_right.setTextureOffset(24, 24).addBox(-0.5F, -7.0F, -1.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        ear_right.setTextureOffset(0, 0).addBox(-0.5F, -6.0F, -2.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        ear_right.setModelRendererName("ear_right");
        this.registerModelRenderer(ear_right);

        ear_left = new AnimatedModelRenderer(this);
        ear_left.setRotationPoint(1.5F, -1.0F, -2.0F);
        head.addChild(ear_left);
        setRotationAngle(ear_left, -2.7489F, -0.7854F, -3.1416F);
        ear_left.setTextureOffset(20, 21).addBox(-0.5F, -6.0F, -0.5F, 1.0F, 6.0F, 1.0F, 0.0F, true);
        ear_left.setTextureOffset(24, 24).addBox(-0.5F, -7.0F, -1.5F, 1.0F, 5.0F, 1.0F, 0.0F, true);
        ear_left.setTextureOffset(0, 0).addBox(-0.5F, -6.0F, -2.5F, 1.0F, 3.0F, 1.0F, 0.0F, true);
        ear_left.setModelRendererName("ear_left");
        this.registerModelRenderer(ear_left);

        front_leg_right = new AnimatedModelRenderer(this);
        front_leg_right.setRotationPoint(-1.0F, -1.0F, -1.5F);
        carbuncle.addChild(front_leg_right);
        front_leg_right.setTextureOffset(0, 21).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        front_leg_right.setModelRendererName("front_leg_right");
        this.registerModelRenderer(front_leg_right);

        front_leg_left = new AnimatedModelRenderer(this);
        front_leg_left.setRotationPoint(1.0F, -1.0F, -1.5F);
        carbuncle.addChild(front_leg_left);
        front_leg_left.setTextureOffset(20, 5).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        front_leg_left.setModelRendererName("front_leg_left");
        this.registerModelRenderer(front_leg_left);

        back_leg_left = new AnimatedModelRenderer(this);
        back_leg_left.setRotationPoint(1.0F, -1.0F, 0.5F);
        carbuncle.addChild(back_leg_left);
        back_leg_left.setTextureOffset(15, 0).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        back_leg_left.setModelRendererName("back_leg_left");
        this.registerModelRenderer(back_leg_left);

        back_leg_right = new AnimatedModelRenderer(this);
        back_leg_right.setRotationPoint(-1.0F, -1.0F, 0.5F);
        carbuncle.addChild(back_leg_right);
        back_leg_right.setTextureOffset(0, 10).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        back_leg_right.setModelRendererName("back_leg_right");
        this.registerModelRenderer(back_leg_right);

        tail = new AnimatedModelRenderer(this);
        tail.setRotationPoint(0.0F, -9.0F, 4.0F);
        carbuncle.addChild(tail);
        setRotationAngle(tail, 0.0F, 3.1416F, 0.0F);
        tail.setTextureOffset(0, 0).addBox(-2.5F, 1.0F, -2.5F, 5.0F, 5.0F, 5.0F, 0.0F, false);
        tail.setModelRendererName("tail");
        this.registerModelRenderer(tail);

        basket = new AnimatedModelRenderer(this);
        basket.setRotationPoint(-0.5F, 1.0F, 8.0F);
        tail.addChild(basket);
        basket.setTextureOffset(15, 0).addBox(-1.5F, -1.0F, -10.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        basket.setTextureOffset(0, 18).addBox(-2.5F, -2.0F, -6.0F, 6.0F, 2.0F, 1.0F, 0.0F, false);
        basket.setTextureOffset(14, 10).addBox(-2.5F, -2.0F, -11.0F, 6.0F, 2.0F, 1.0F, 0.0F, false);
        basket.setTextureOffset(10, 21).addBox(-2.5F, -2.0F, -10.0F, 1.0F, 2.0F, 4.0F, 0.0F, false);
        basket.setTextureOffset(0, 21).addBox(2.5F, -2.0F, -10.0F, 1.0F, 2.0F, 4.0F, 0.0F, false);
        basket.setModelRendererName("basket");
        this.registerModelRenderer(basket);

        body = new AnimatedModelRenderer(this);
        body.setRotationPoint(0.0F, -2.5F, -0.5F);
        carbuncle.addChild(body);
        setRotationAngle(body, 0.0F, 3.1416F, 0.0F);
        body.setTextureOffset(14, 14).addBox(-2.0F, -1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 0.0F, false);
        body.setModelRendererName("body");
        this.registerModelRenderer(body);

        this.rootBones.add(carbuncle);
    }


    @Override
    public void setRotationAngles(EntityCarbuncle entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
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
        super.render(matrixStack, buffer, packedLight, packedOverlay, red, green,blue,alpha);
//        basket.render(matrixStack, buffer, packedLight, packedOverlay);
//        head.render(matrixStack, buffer, packedLight, packedOverlay);
//        front_leg_right.render(matrixStack, buffer, packedLight, packedOverlay);
//        front_leg_left.render(matrixStack, buffer, packedLight, packedOverlay);
//        back_leg_left.render(matrixStack, buffer, packedLight, packedOverlay);
//        back_leg_right.render(matrixStack, buffer, packedLight, packedOverlay);
//        tail.render(matrixStack, buffer, packedLight, packedOverlay);
//        body.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
