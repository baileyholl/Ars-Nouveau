package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class EnchantingApparatusModel extends Model {
    public final ModelRenderer frame_all;
    public final ModelRenderer frame_top;
    public final ModelRenderer frame_top1;
    public final ModelRenderer frame_top2;
    public final ModelRenderer frame_top3;
    public final ModelRenderer frame_top4;
    public final ModelRenderer frame_bot;
    public final ModelRenderer frame_bot1;
    public final ModelRenderer frame_bot2;
    public final ModelRenderer frame_bot3;
    public final ModelRenderer frame_bot4;

    public EnchantingApparatusModel() {
        super(RenderType::getEntityCutout);
        textureWidth = 32;
        textureHeight = 32;

        frame_all = new ModelRenderer(this);
        frame_all.setRotationPoint(0.0F, 1.0F, 0.0F);


        frame_top = new ModelRenderer(this);
        frame_top.setRotationPoint(0.0F, -0.1923F, 0.0F);
        frame_all.addChild(frame_top);
        frame_top.setTextureOffset(0, 6).addBox(-2.5F, -6.3077F, -2.5F, 5.0F, 1.0F, 5.0F, 0.0F, false);
        frame_top.setTextureOffset(20, 23).addBox(-0.5F, -5.3077F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        frame_top.setTextureOffset(0, 12).addBox(-1.5F, -3.3077F, -1.5F, 3.0F, 1.0F, 3.0F, 0.0F, false);

        frame_top1 = new ModelRenderer(this);
        frame_top1.setRotationPoint(0.0F, 0.1923F, -0.5F);
        frame_top.addChild(frame_top1);
        frame_top1.setTextureOffset(15, 8).addBox(1.5F, -5.5F, 0.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        frame_top1.setTextureOffset(0, 20).addBox(5.5F, -5.5F, 0.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_top1.setTextureOffset(0, 18).addBox(3.5F, -1.5F, 0.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_top1.setTextureOffset(21, 10).addBox(2.5F, -3.5F, 0.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_top1.setTextureOffset(20, 20).addBox(3.5F, -3.5F, -0.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);

        frame_top2 = new ModelRenderer(this);
        frame_top2.setRotationPoint(0.0F, -2.3077F, 0.0F);
        frame_top.addChild(frame_top2);
        frame_top2.setTextureOffset(6, 18).addBox(-4.5F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        frame_top2.setTextureOffset(8, 21).addBox(-3.5F, -1.0F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_top2.setTextureOffset(8, 16).addBox(-6.5F, 1.0F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_top2.setTextureOffset(0, 6).addBox(-6.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_top2.setTextureOffset(15, 2).addBox(-5.5F, -3.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);

        frame_top3 = new ModelRenderer(this);
        frame_top3.setRotationPoint(0.0F, -2.3077F, 0.0F);
        frame_top.addChild(frame_top3);
        setRotationAngle(frame_top3, 0.0F, -1.5708F, 0.0F);
        frame_top3.setTextureOffset(6, 18).addBox(-4.5F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        frame_top3.setTextureOffset(8, 21).addBox(-3.5F, -1.0F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_top3.setTextureOffset(8, 16).addBox(-6.5F, 1.0F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_top3.setTextureOffset(0, 6).addBox(-6.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_top3.setTextureOffset(15, 2).addBox(-5.5F, -3.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);

        frame_top4 = new ModelRenderer(this);
        frame_top4.setRotationPoint(0.0F, -2.3077F, 0.0F);
        frame_top.addChild(frame_top4);
        setRotationAngle(frame_top4, 0.0F, 1.5708F, 0.0F);
        frame_top4.setTextureOffset(6, 18).addBox(-4.5F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        frame_top4.setTextureOffset(8, 21).addBox(-3.5F, -1.0F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_top4.setTextureOffset(8, 16).addBox(-6.5F, 1.0F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_top4.setTextureOffset(0, 6).addBox(-6.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_top4.setTextureOffset(15, 2).addBox(-5.5F, -3.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);

        frame_bot = new ModelRenderer(this);
        frame_bot.setRotationPoint(0.0F, 3.1923F, 0.0F);
        frame_all.addChild(frame_bot);
        frame_bot.setTextureOffset(0, 0).addBox(-2.5F, 2.3077F, -2.5F, 5.0F, 1.0F, 5.0F, 0.0F, false);
        frame_bot.setTextureOffset(12, 12).addBox(-1.5F, -0.6923F, -1.5F, 3.0F, 1.0F, 3.0F, 0.0F, false);
        frame_bot.setTextureOffset(12, 23).addBox(-0.5F, 0.3077F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        frame_bot1 = new ModelRenderer(this);
        frame_bot1.setRotationPoint(0.0F, -3.1923F, -0.5F);
        frame_bot.addChild(frame_bot1);
        frame_bot1.setTextureOffset(15, 6).addBox(-5.5F, 4.5F, 0.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        frame_bot1.setTextureOffset(12, 18).addBox(-6.5F, 1.5F, 0.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_bot1.setTextureOffset(16, 16).addBox(-6.5F, 0.5F, 0.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_bot1.setTextureOffset(16, 21).addBox(-3.5F, 0.5F, 0.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_bot1.setTextureOffset(16, 18).addBox(-4.5F, 2.5F, -0.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);

        frame_bot2 = new ModelRenderer(this);
        frame_bot2.setRotationPoint(0.0F, -0.6923F, 0.0F);
        frame_bot.addChild(frame_bot2);
        frame_bot2.setTextureOffset(15, 0).addBox(1.5F, 2.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        frame_bot2.setTextureOffset(0, 0).addBox(5.5F, -1.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_bot2.setTextureOffset(0, 16).addBox(3.5F, -2.0F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_bot2.setTextureOffset(4, 21).addBox(2.5F, -2.0F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_bot2.setTextureOffset(9, 12).addBox(3.5F, 0.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

        frame_bot3 = new ModelRenderer(this);
        frame_bot3.setRotationPoint(0.0F, -0.6923F, 0.0F);
        frame_bot.addChild(frame_bot3);
        setRotationAngle(frame_bot3, 0.0F, -1.5708F, 0.0F);
        frame_bot3.setTextureOffset(15, 0).addBox(1.5F, 2.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        frame_bot3.setTextureOffset(0, 0).addBox(5.5F, -1.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_bot3.setTextureOffset(0, 16).addBox(3.5F, -2.0F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_bot3.setTextureOffset(4, 21).addBox(2.5F, -2.0F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_bot3.setTextureOffset(9, 12).addBox(3.5F, 0.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

        frame_bot4 = new ModelRenderer(this);
        frame_bot4.setRotationPoint(0.0F, -0.6923F, 0.0F);
        frame_bot.addChild(frame_bot4);
        setRotationAngle(frame_bot4, 0.0F, 1.5708F, 0.0F);
        frame_bot4.setTextureOffset(15, 0).addBox(1.5F, 2.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        frame_bot4.setTextureOffset(0, 0).addBox(5.5F, -1.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_bot4.setTextureOffset(0, 16).addBox(3.5F, -2.0F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_bot4.setTextureOffset(4, 21).addBox(2.5F, -2.0F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_bot4.setTextureOffset(9, 12).addBox(3.5F, 0.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
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
