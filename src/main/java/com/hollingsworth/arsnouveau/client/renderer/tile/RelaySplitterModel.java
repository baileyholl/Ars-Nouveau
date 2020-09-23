package com.hollingsworth.arsnouveau.client.renderer.tile;// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class RelaySplitterModel extends Model  {
	public final ModelRenderer ring_inner;
	public final ModelRenderer ring_outer;
	public final ModelRenderer center;



	public RelaySplitterModel() {
		super(RenderType::getEntityCutout);
		textureWidth = 32;
		textureHeight = 32;
		ring_outer = new ModelRenderer(this);
		ring_outer.setRotationPoint(0.0F, 16.0F, 0.0F);
		ring_outer.setTextureOffset(7, 13).addBox(-2.0F, -1.5F, -7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
		ring_outer.setTextureOffset(12, 6).addBox(-2.0F, 0.5F, -7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
		ring_outer.setTextureOffset(23, 24).addBox(-4.0F, -1.5F, -6.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		ring_outer.setTextureOffset(6, 23).addBox(2.0F, 0.5F, -6.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		ring_outer.setTextureOffset(24, 12).addBox(2.0F, -1.5F, -6.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		ring_outer.setTextureOffset(22, 19).addBox(-4.0F, 0.5F, -6.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		ring_outer.setTextureOffset(22, 9).addBox(5.0F, -1.5F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		ring_outer.setTextureOffset(14, 21).addBox(-6.0F, 0.5F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		ring_outer.setTextureOffset(6, 1).addBox(6.0F, -1.5F, -2.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		ring_outer.setTextureOffset(0, 5).addBox(-7.0F, 0.5F, -2.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		ring_outer.setTextureOffset(18, 22).addBox(5.0F, -1.5F, 2.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		ring_outer.setTextureOffset(20, 16).addBox(-6.0F, 0.5F, 2.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		ring_outer.setTextureOffset(24, 2).addBox(2.0F, -1.5F, 5.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		ring_outer.setTextureOffset(22, 0).addBox(-4.0F, 0.5F, 5.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		ring_outer.setTextureOffset(12, 8).addBox(-2.0F, -1.5F, 6.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
		ring_outer.setTextureOffset(12, 2).addBox(-2.0F, 0.5F, 6.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
		ring_outer.setTextureOffset(11, 24).addBox(-4.0F, -1.5F, 5.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		ring_outer.setTextureOffset(22, 22).addBox(2.0F, 0.5F, 5.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		ring_outer.setTextureOffset(21, 6).addBox(-6.0F, -1.5F, 2.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		ring_outer.setTextureOffset(20, 13).addBox(5.0F, 0.5F, 2.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		ring_outer.setTextureOffset(6, 6).addBox(-7.0F, -1.5F, -2.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		ring_outer.setTextureOffset(0, 0).addBox(6.0F, 0.5F, -2.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		ring_outer.setTextureOffset(0, 22).addBox(-6.0F, -1.5F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		ring_outer.setTextureOffset(20, 2).addBox(5.0F, 0.5F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		ring_outer.setTextureOffset(8, 18).addBox(-5.0F, -2.0F, -5.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);
		ring_outer.setTextureOffset(4, 18).addBox(-5.0F, -2.0F, 4.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);
		ring_outer.setTextureOffset(16, 16).addBox(4.0F, -2.0F, 4.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);
		ring_outer.setTextureOffset(0, 17).addBox(4.0F, -2.0F, -5.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		ring_inner = new ModelRenderer(this);
		ring_inner.setRotationPoint(0.0F, 16.0F, 0.0F);
		ring_inner.setTextureOffset(18, 10).addBox(3.0F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
		ring_inner.setTextureOffset(12, 18).addBox(-4.0F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
		ring_inner.setTextureOffset(8, 11).addBox(-2.0F, 3.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);
		ring_inner.setTextureOffset(18, 19).addBox(-3.0F, -3.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		ring_inner.setTextureOffset(4, 15).addBox(-3.0F, 2.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		ring_inner.setTextureOffset(10, 15).addBox(2.0F, -3.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		ring_inner.setTextureOffset(0, 14).addBox(2.0F, 2.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		ring_inner.setTextureOffset(12, 0).addBox(-2.0F, -4.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);

		center = new ModelRenderer(this);
		center.setRotationPoint(0.0F, 16.0F, 0.0F);
		center.setTextureOffset(6, 0).addBox(-0.5F, 1.0F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		center.setTextureOffset(6, 6).addBox(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		center.setTextureOffset(0, 0).addBox(1.0F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		center.setTextureOffset(6, 2).addBox(-0.5F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		center.setTextureOffset(0, 2).addBox(-2.0F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		center.setTextureOffset(0, 5).addBox(-0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		center.setTextureOffset(0, 10).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);	}


	@Override
	public void render(MatrixStack ms, IVertexBuilder buffer, int light, int overlay, float r, float g, float b, float a) {
			render(ms, buffer, light, overlay, r, g, b, a, 1);
	}


	public void render(MatrixStack ms, IVertexBuilder buffer, int light, int overlay, float r, float g, float b, float alpha, float fract) {
		ring_inner.render(ms, buffer, light, overlay, r, g, b, alpha);
		center.render(ms, buffer, light, overlay, r, g, b, alpha);
		ring_outer.render(ms, buffer, light, overlay, r, g, b, alpha);

//		float lvt_8_2_ = 1.3F;
//		float angle = (Minecraft.getInstance().world.getGameTime()/10.0f) % 360;
//
//		float outerAngle = (Minecraft.getInstance().world.getGameTime()/20.0f) % 360;
////		ring_outer.rotateAngleZ =  MathHelper.cos(angle) *3.1415927F * 2;
//		ring_outer.rotateAngleX = outerAngle;
//		ring_outer.rotateAngleZ = outerAngle;
//
//		ring_inner.rotateAngleY = angle;
//		ring_inner.rotateAngleX = angle;
//		center.rotateAngleX = -angle;
//		center.rotateAngleY = angle;
	}
}