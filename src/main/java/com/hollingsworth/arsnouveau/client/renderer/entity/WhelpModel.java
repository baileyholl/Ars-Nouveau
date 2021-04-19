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

	public WhelpModel() {
		texWidth = 32;
		texHeight = 32;




		kobold = new ModelRenderer(this);
		kobold.setPos(0.0F, 18.0F, -2.5F);
		kobold.texOffs(16, 3).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, 0.0F, false);

		head = new ModelRenderer(this);
		head.setPos(0.0F, 16.0F, -3.0F);
		head.texOffs(12, 0).addBox(-1.5F, 0.0F, -4.0F, 3.0F, 1.0F, 2.0F, 0.0F, false);
		head.texOffs(17, 17).addBox(-4.0F, -3.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		head.texOffs(17, 17).addBox(3.0F, -3.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		head.texOffs(16, 12).addBox(-4.0F, -3.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		head.texOffs(16, 12).addBox(3.0F, -3.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		head.texOffs(16, 12).addBox(-3.0F, -2.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		head.texOffs(16, 12).addBox(2.0F, -2.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		head.texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);
		head.texOffs(12, 15).addBox(-0.5F, -3.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		head.texOffs(4, 18).addBox(-0.5F, -1.0F, 2.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		lower_jaw = new ModelRenderer(this);
		lower_jaw.setPos(0.0F, -4.0F, -2.0F);
		head.addChild(lower_jaw);
		setRotationAngle(lower_jaw, 0.3491F, 0.0F, 0.0F);
		lower_jaw.texOffs(0, 14).addBox(-1.0F, 4.6984F, -3.7103F, 2.0F, 1.0F, 2.0F, 0.0F, false);

		body = new ModelRenderer(this);
		body.setPos(0.0F, 20.0F, -1.5F);
		setRotationAngle(body, 0.7854F, 0.0F, 0.0F);
		body.texOffs(0, 8).addBox(-2.0F, -1.7678F, -0.9393F, 4.0F, 4.0F, 2.0F, 0.0F, false);

		tail = new ModelRenderer(this);
		tail.setPos(0.0F, 21.0F, 0.0F);
		setRotationAngle(tail, -0.3927F, 0.0F, 0.0F);
		tail.texOffs(8, 10).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);
		tail.texOffs(16, 10).addBox(-1.0F, -1.5F, 3.5F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		tail.texOffs(10, 15).addBox(-0.5F, -3.0F, 1.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		tail.texOffs(0, 17).addBox(-0.5F, -3.5F, 4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		tail.texOffs(6, 15).addBox(-0.5F, -4.0F, 2.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

		wing1 = new ModelRenderer(this);
		wing1.setPos(1.0F, 19.0F, -1.0F);
		wing1.texOffs(0, 7).addBox(0.0F, -2.5F, 2.5F, 0.0F, 1.0F, 1.0F, 0.0F, false);
		wing1.texOffs(10, 6).addBox(0.0F, -2.5F, 0.5F, 0.0F, 2.0F, 2.0F, 0.0F, false);
		wing1.texOffs(12, 1).addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 2.0F, 0.0F, false);

		wing2 = new ModelRenderer(this);
		wing2.setPos(-1.0F, 19.0F, -1.0F);
		wing2.texOffs(0, 7).addBox(0.0F, -2.5F, 2.5F, 0.0F, 1.0F, 1.0F, 0.0F, false);
		wing2.texOffs(10, 6).addBox(0.0F, -2.5F, 0.5F, 0.0F, 2.0F, 2.0F, 0.0F, false);
		wing2.texOffs(12, 1).addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 2.0F, 0.0F, false);

		leg2 = new ModelRenderer(this);
		leg2.setPos(-2.0F, 20.5F, -0.5F);
		setRotationAngle(leg2, 1.1781F, 0.0F, 0.0F);
		leg2.texOffs(14, 6).addBox(-1.0F, -0.5F, -1.5F, 1.0F, 2.0F, 2.0F, 0.0F, false);
		leg2.texOffs(12, 15).addBox(-1.0F, 1.5F, -2.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);

		leg1 = new ModelRenderer(this);
		leg1.setPos(2.0F, 20.5F, -0.5F);
		setRotationAngle(leg1, 1.1781F, 0.0F, 0.0F);
		leg1.texOffs(14, 6).addBox(0.0F, -0.5F, -1.5F, 1.0F, 2.0F, 2.0F, 0.0F, false);
		leg1.texOffs(12, 15).addBox(0.0F, 1.5F, -2.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);

		arm_right = new ModelRenderer(this);
		arm_right.setPos(-2.0F, 19.5F, -2.0F);
		setRotationAngle(arm_right, -0.3927F, 0.0F, 0.0F);
		arm_right.texOffs(0, 0).addBox(-1.0F, -0.5F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);

		arm_left = new ModelRenderer(this);
		arm_left.setPos(2.0F, 19.5F, -2.0F);
		setRotationAngle(arm_left, -0.3927F, 0.0F, 0.0F);
		arm_left.texOffs(0, 0).addBox(0.0F, -0.5F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
	@Override
	public void setupAnim(EntityWhelp entityWhelp, float v, float v1, float v2, float v3, float v4) {
//		entityKobold.rotateAngleX = x;
//		entityKobold.rotateAngleY = y;
//		entityKobold.rotateAngleZ = z;
		this.head.xRot = v4 * 0.017453292F;
		this.head.yRot = v3 * 0.017453292F;
		float lvt_8_2_ = v2 * 1.3F;
		this.wing1.yRot = 0.0F;
		this.wing1.zRot = MathHelper.cos(lvt_8_2_) *3.1415927F * .5f;
		this.wing2.xRot = this.wing1.xRot;
		this.wing2.yRot = this.wing1.yRot;
		this.wing2.zRot = -this.wing1.zRot;
//		this.frontLegs.rotateAngleX = 0.7853982F;
//		this.middleLegs.rotateAngleX = 0.7853982F;
//		this.backLegs.rotateAngleX = 0.7853982F;
		this.body.xRot = 0.0F;
		this.body.yRot = 0.0F;
		this.body.zRot = 0.0F;
	}



	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		matrixStack.pushPose();
		matrixStack.scale(.85f, .85f, .85f);
        matrixStack.translate(0, -0.5, 0);
		matrixStack.popPose();
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

	}

	@Override
	public void prepareMobModel(EntityWhelp p_212843_1_, float x, float y, float z) {
		super.prepareMobModel(p_212843_1_, x, y, z);
//		this.head.rotateAngleX = z * 0.017453292F;
//		this.head.rotateAngleY = z * 0.017453292F;

	}



}