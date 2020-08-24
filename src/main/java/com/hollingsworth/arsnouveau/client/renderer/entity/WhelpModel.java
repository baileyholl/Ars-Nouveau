package com.hollingsworth.arsnouveau.client.renderer.entity;// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.hollingsworth.arsnouveau.common.entity.EntityWhelp;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class WhelpModel extends EntityModel<EntityWhelp> {
	private final ModelRenderer kobold;
	private final ModelRenderer head;
	private final ModelRenderer lower_jaw;
	private final ModelRenderer body;
	private final ModelRenderer tail;
	private final ModelRenderer wing1;
	private final ModelRenderer wing2;
	private final ModelRenderer leg2;
	private final ModelRenderer leg1;
	private final ModelRenderer arm_right;
	private final ModelRenderer arm_left;
	private final ModelRenderer ridges;


	public WhelpModel() {
		textureWidth = 32;
		textureHeight = 32;


		kobold = new ModelRenderer(this);
		kobold.setRotationPoint(0.0F, 14.0F, -2.0F);
		kobold.setTextureOffset(16, 3).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 13.0F, -2.5F);
		setRotationAngle(head, 0.0F, 0.0F, 0.0F);
		head.setTextureOffset(12, 0).addBox(-1.5F, -2.0F, -4.5F, 3.0F, 1.0F, 2.0F, 0.0F, false);
		head.setTextureOffset(17, 17).addBox(-4.0F, -5.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		head.setTextureOffset(17, 17).addBox(3.0F, -5.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		head.setTextureOffset(16, 12).addBox(-4.0F, -5.0F, 0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		head.setTextureOffset(16, 12).addBox(3.0F, -5.0F, 0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		head.setTextureOffset(16, 12).addBox(-3.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		head.setTextureOffset(16, 12).addBox(2.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		head.setTextureOffset(0, 0).addBox(-2.0F, -4.0F, -2.5F, 4.0F, 4.0F, 4.0F, 0.0F, false);
		head.setTextureOffset(12, 15).addBox(-0.5F, -5.0F, -1.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		head.setTextureOffset(4, 18).addBox(-0.5F, -3.0F, 1.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		lower_jaw = new ModelRenderer(this);
		lower_jaw.setRotationPoint(0.0F, -1.0F, -2.5F);
		head.addChild(lower_jaw);
		setRotationAngle(lower_jaw, 0.3491F, 0.0F, 0.0F);
		lower_jaw.setTextureOffset(0, 14).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 15.0F, -1.0F);
		setRotationAngle(body, 0.7854F, 0.0F, 0.0F);
		body.setTextureOffset(0, 8).addBox(-2.0F, -2.1213F, -1.2929F, 4.0F, 4.0F, 2.0F, 0.0F, false);

		tail = new ModelRenderer(this);
		tail.setRotationPoint(0.0F, 16.0F, 0.0F);
		setRotationAngle(tail, -0.3927F, 0.0F, 0.0F);
		tail.setTextureOffset(8, 10).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);
		tail.setTextureOffset(16, 10).addBox(-1.0F, -1.5F, 3.5F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		tail.setTextureOffset(10, 15).addBox(-0.5F, -3.0F, 1.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		tail.setTextureOffset(0, 17).addBox(-0.5F, -3.5F, 4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		tail.setTextureOffset(6, 15).addBox(-0.5F, -4.0F, 2.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

		wing1 = new ModelRenderer(this);
		wing1.setRotationPoint(0.5F, 14.5F, -1.5F);
		wing1.setTextureOffset(0, 7).addBox(0.5F, -3.0F, 3.0F, 0.0F, 1.0F, 1.0F, 0.0F, false);
		wing1.setTextureOffset(10, 6).addBox(0.5F, -3.0F, 1.0F, 0.0F, 2.0F, 2.0F, 0.0F, false);
		wing1.setTextureOffset(12, 1).addBox(0.5F, -1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, false);

		wing2 = new ModelRenderer(this);
		wing2.setRotationPoint(-1.5F, 14.5F, -1.5F);
		wing2.setTextureOffset(0, 7).addBox(0.5F, -3.0F, 3.0F, 0.0F, 1.0F, 1.0F, 0.0F, false);
		wing2.setTextureOffset(10, 6).addBox(0.5F, -3.0F, 1.0F, 0.0F, 2.0F, 2.0F, 0.0F, false);
		wing2.setTextureOffset(12, 1).addBox(0.5F, -1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, false);

		leg2 = new ModelRenderer(this);
		leg2.setRotationPoint(-2.0F, 15.5F, -0.5F);
		setRotationAngle(leg2, 1.1781F, 0.0F, 0.0F);
		leg2.setTextureOffset(14, 6).addBox(-1.0F, -0.5F, -1.5F, 1.0F, 2.0F, 2.0F, 0.0F, false);
		leg2.setTextureOffset(12, 15).addBox(-1.0F, 1.5F, -2.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);

		leg1 = new ModelRenderer(this);
		leg1.setRotationPoint(2.0F, 15.5F, -0.5F);
		setRotationAngle(leg1, 1.1781F, 0.0F, 0.0F);
		leg1.setTextureOffset(14, 6).addBox(0.0F, -0.5F, -1.5F, 1.0F, 2.0F, 2.0F, 0.0F, false);
		leg1.setTextureOffset(12, 15).addBox(0.0F, 1.5F, -2.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);

		arm_right = new ModelRenderer(this);
		arm_right.setRotationPoint(-2.0F, 14.5F, -2.0F);
		setRotationAngle(arm_right, -0.3927F, 0.0F, 0.0F);
		arm_right.setTextureOffset(0, 0).addBox(-1.0F, -0.5F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);

		arm_left = new ModelRenderer(this);
		arm_left.setRotationPoint(2.0F, 14.5F, -2.0F);
		setRotationAngle(arm_left, -0.3927F, 0.0F, 0.0F);
		arm_left.setTextureOffset(0, 0).addBox(0.0F, -0.5F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);

		ridges = new ModelRenderer(this);
		ridges.setRotationPoint(0.0F, 24.0F, 0.0F);

	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	@Override
	public void setRotationAngles(EntityWhelp entityWhelp, float v, float v1, float v2, float v3, float v4) {
//		entityKobold.rotateAngleX = x;
//		entityKobold.rotateAngleY = y;
//		entityKobold.rotateAngleZ = z;
		this.head.rotateAngleX = v4 * 0.017453292F;
		this.head.rotateAngleY = v3 * 0.017453292F;
		float lvt_8_2_ = v2 * 1.3F;
		this.wing1.rotateAngleY = 0.0F;
		this.wing1.rotateAngleZ = MathHelper.cos(lvt_8_2_) *3.1415927F * .5f;
		this.wing2.rotateAngleX = this.wing1.rotateAngleX;
		this.wing2.rotateAngleY = this.wing1.rotateAngleY;
		this.wing2.rotateAngleZ = -this.wing1.rotateAngleZ;
//		this.frontLegs.rotateAngleX = 0.7853982F;
//		this.middleLegs.rotateAngleX = 0.7853982F;
//		this.backLegs.rotateAngleX = 0.7853982F;
		this.body.rotateAngleX = 0.0F;
		this.body.rotateAngleY = 0.0F;
		this.body.rotateAngleZ = 0.0F;
	}



	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		matrixStack.scale(.85f, .85f, .85f);
		kobold.render(matrixStack, buffer, packedLight, packedOverlay);
		head.render(matrixStack, buffer, packedLight, packedOverlay);
		body.render(matrixStack, buffer, packedLight, packedOverlay);
		tail.render(matrixStack, buffer, packedLight, packedOverlay);
		wing1.render(matrixStack, buffer, packedLight, packedOverlay);
		wing2.render(matrixStack, buffer, packedLight, packedOverlay);
		leg2.render(matrixStack, buffer, packedLight, packedOverlay);
		leg1.render(matrixStack, buffer, packedLight, packedOverlay);
		arm_right.render(matrixStack, buffer, packedLight, packedOverlay);
		arm_left.render(matrixStack, buffer, packedLight, packedOverlay);
		ridges.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	@Override
	public void setLivingAnimations(EntityWhelp p_212843_1_, float x, float y, float z) {
		super.setLivingAnimations(p_212843_1_, x, y, z);
//		this.head.rotateAngleX = z * 0.017453292F;
//		this.head.rotateAngleY = z * 0.017453292F;

	}



}