package com.hollingsworth.arsnouveau.common.entity;// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class kobold extends EntityModel<Entity> {
	private final ModelRenderer kobold;
	private final ModelRenderer head;
	private final ModelRenderer lower_jaw;
	private final ModelRenderer body;
	private final ModelRenderer leg1;
	private final ModelRenderer leg2;
	private final ModelRenderer tail;
	private final ModelRenderer wing1;
	private final ModelRenderer wing2;
	private final ModelRenderer arm1;
	private final ModelRenderer arm2;

	public kobold() {
		textureWidth = 32;
		textureHeight = 32;

		kobold = new ModelRenderer(this);
		kobold.setRotationPoint(0.0F, 14.0F, 3.0F);
		kobold.setTextureOffset(16, 10).addBox(-1.0F, -2.0F, -6.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 11.0F, 0.0F);
		setRotationAngle(head, 0.0F, 0.0F, 0.0F);
		head.setTextureOffset(12, 0).addBox(-1.0F, 0.0F, -7.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		head.setTextureOffset(4, 18).addBox(1.0F, -4.0F, -3.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		head.setTextureOffset(0, 18).addBox(-2.0F, -4.0F, -3.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		head.setTextureOffset(8, 18).addBox(1.0F, -4.0F, -2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		head.setTextureOffset(10, 15).addBox(-2.0F, -4.0F, -2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		head.setTextureOffset(0, 0).addBox(-2.0F, -2.0F, -5.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

		lower_jaw = new ModelRenderer(this);
		lower_jaw.setRotationPoint(0.0F, 1.0F, 0.0F);
		head.addChild(lower_jaw);
		setRotationAngle(lower_jaw, 0.3491F, 0.0F, 0.0F);
		lower_jaw.setTextureOffset(16, 3).addBox(-0.5F, -1.7101F, -6.6985F, 1.0F, 1.0F, 2.0F, 0.0F, false);

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 13.0F, 3.0F);
		setRotationAngle(body, 0.7854F, 0.0F, 0.0F);
		body.setTextureOffset(0, 8).addBox(-2.0F, -3.5355F, -5.5355F, 4.0F, 4.0F, 2.0F, 0.0F, false);

		leg1 = new ModelRenderer(this);
		leg1.setRotationPoint(2.0F, 15.5F, 2.5F);
		setRotationAngle(leg1, 1.3526F, 0.0F, 0.0F);
		leg1.setTextureOffset(14, 6).addBox(0.0F, -3.2124F, -2.1256F, 1.0F, 2.0F, 2.0F, 0.0F, false);
		leg1.setTextureOffset(16, 16).addBox(0.0F, -1.2124F, -3.1256F, 1.0F, 1.0F, 2.0F, 0.0F, false);

		leg2 = new ModelRenderer(this);
		leg2.setRotationPoint(-2.0F, 15.5F, 2.5F);
		setRotationAngle(leg2, 1.3526F, 0.0F, 0.0F);
		leg2.setTextureOffset(14, 6).addBox(-1.0F, -3.2124F, -2.1256F, 1.0F, 2.0F, 2.0F, 0.0F, false);
		leg2.setTextureOffset(16, 16).addBox(-1.0F, -1.2124F, -3.1256F, 1.0F, 1.0F, 2.0F, 0.0F, false);

		tail = new ModelRenderer(this);
		tail.setRotationPoint(0.0F, 16.5F, 3.0F);
		tail.setTextureOffset(9, 11).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 1.0F, 3.0F, 0.0F, false);
		tail.setTextureOffset(12, 15).addBox(-0.5F, -2.0F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		tail.setTextureOffset(4, 14).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		wing1 = new ModelRenderer(this);
		wing1.setRotationPoint(1.0F, 14.5F, 1.5F);
		setRotationAngle(wing1, 0.0F, 0.0F, 0.3927F);
		wing1.setTextureOffset(6, 15).addBox(-0.5F, -1.0F, -3.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		wing1.setTextureOffset(0, 14).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
		wing1.setTextureOffset(10, 8).addBox(-0.5F, -2.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		wing2 = new ModelRenderer(this);
		wing2.setRotationPoint(-1.0F, 14.5F, 1.5F);
		setRotationAngle(wing2, 0.0F, 0.0F, -0.3927F);
		wing2.setTextureOffset(6, 15).addBox(-0.5F, -1.0F, -3.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		wing2.setTextureOffset(0, 14).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
		wing2.setTextureOffset(10, 8).addBox(-0.5F, -2.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		arm1 = new ModelRenderer(this);
		arm1.setRotationPoint(-2.0F, 14.0F, 0.5F);
		setRotationAngle(arm1, -0.3927F, 0.0F, 0.0F);
		arm1.setTextureOffset(0, 0).addBox(-1.0F, 1.1481F, -2.7716F, 1.0F, 3.0F, 1.0F, 0.0F, false);

		arm2 = new ModelRenderer(this);
		arm2.setRotationPoint(3.0F, 14.0F, 0.5F);
		setRotationAngle(arm2, -0.3927F, 0.0F, 0.0F);
		arm2.setTextureOffset(0, 0).addBox(-1.0F, 1.1481F, -2.7716F, 1.0F, 3.0F, 1.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		kobold.render(matrixStack, buffer, packedLight, packedOverlay);
		head.render(matrixStack, buffer, packedLight, packedOverlay);
		body.render(matrixStack, buffer, packedLight, packedOverlay);
		leg1.render(matrixStack, buffer, packedLight, packedOverlay);
		leg2.render(matrixStack, buffer, packedLight, packedOverlay);
		tail.render(matrixStack, buffer, packedLight, packedOverlay);
		wing1.render(matrixStack, buffer, packedLight, packedOverlay);
		wing2.render(matrixStack, buffer, packedLight, packedOverlay);
		arm1.render(matrixStack, buffer, packedLight, packedOverlay);
		arm2.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}