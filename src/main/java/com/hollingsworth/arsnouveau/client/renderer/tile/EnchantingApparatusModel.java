package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class EnchantingApparatusModel extends Model {
    private final ModelRenderer frame_all;
    private final ModelRenderer frame_top;
    private final ModelRenderer frame_bot;
    public EnchantingApparatusModel() {
        super(RenderType::getEntityCutout);
        textureWidth = 32;
        textureHeight = 32;

        frame_all = new ModelRenderer(this);
        frame_all.setRotationPoint(0.0F, 16.0F, 0.0F);
        frame_top = new ModelRenderer(this);
        frame_top.setRotationPoint(0.0F, -0.1923F, 0.0F);
        frame_all.addChild(frame_top);
        frame_top.setTextureOffset(15, 7).addBox(-6.5F, -1.3077F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_top.setTextureOffset(15, 15).addBox(3.5F, -1.3077F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_top.setTextureOffset(22, 1).addBox(-3.5F, -3.3077F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_top.setTextureOffset(0, 21).addBox(2.5F, -3.3077F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_top.setTextureOffset(19, 12).addBox(-4.5F, -3.3077F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        frame_top.setTextureOffset(18, 9).addBox(3.5F, -3.3077F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        frame_top.setTextureOffset(7, 20).addBox(-6.5F, -5.3077F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_top.setTextureOffset(19, 19).addBox(5.5F, -5.3077F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_top.setTextureOffset(13, 4).addBox(-5.5F, -5.3077F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        frame_top.setTextureOffset(13, 0).addBox(1.5F, -5.3077F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        frame_top.setTextureOffset(0, 4).addBox(-2.5F, -6.3077F, -1.5F, 5.0F, 1.0F, 3.0F, 0.0F, false);
        frame_top.setTextureOffset(22, 8).addBox(-0.5F, -5.3077F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        frame_top.setTextureOffset(0, 8).addBox(-1.5F, -3.3077F, -1.5F, 3.0F, 1.0F, 3.0F, 0.0F, false);

        frame_bot = new ModelRenderer(this);
        frame_bot.setRotationPoint(0.0F, 3.1923F, 0.0F);
        frame_all.addChild(frame_bot);
        frame_bot.setTextureOffset(7, 15).addBox(-6.5F, -2.6923F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_bot.setTextureOffset(0, 14).addBox(3.5F, -2.6923F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_bot.setTextureOffset(15, 20).addBox(-3.5F, -2.6923F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_bot.setTextureOffset(11, 20).addBox(2.5F, -2.6923F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_bot.setTextureOffset(14, 17).addBox(-4.5F, -0.6923F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        frame_bot.setTextureOffset(8, 17).addBox(3.5F, -0.6923F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        frame_bot.setTextureOffset(4, 16).addBox(-6.5F, -1.6923F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_bot.setTextureOffset(0, 16).addBox(5.5F, -1.6923F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_bot.setTextureOffset(9, 13).addBox(-5.5F, 1.3077F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        frame_bot.setTextureOffset(0, 12).addBox(1.5F, 1.3077F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        frame_bot.setTextureOffset(0, 0).addBox(-2.5F, 2.3077F, -1.5F, 5.0F, 1.0F, 3.0F, 0.0F, false);
        frame_bot.setTextureOffset(9, 9).addBox(-1.5F, -0.6923F, -1.5F, 3.0F, 1.0F, 3.0F, 0.0F, false);
        frame_bot.setTextureOffset(22, 5).addBox(-0.5F, 0.3077F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        frame_all.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
