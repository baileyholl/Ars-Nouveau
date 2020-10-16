package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class CrystallizerModel extends Model {
    private final ModelRenderer bone;
    private final ModelRenderer bone2;
    private final ModelRenderer bone3;
    private final ModelRenderer bone4;
    private final ModelRenderer bone8;
    private final ModelRenderer bone5;
    private final ModelRenderer bone6;
    private final ModelRenderer bone7;
    private final ModelRenderer bb_main;

    public CrystallizerModel() {
        super(RenderType::getEntityCutout);
        textureWidth = 64;
        textureHeight = 64;

        bone = new ModelRenderer(this);
        bone.setRotationPoint(0.0F, 16.0F, -6.0F);
        setRotationAngle(bone, 0.0F, 0.0F, -1.5708F);


        bone2 = new ModelRenderer(this);
        bone2.setRotationPoint(-6.0F, 16.0F, 0.0F);


        bone3 = new ModelRenderer(this);
        bone3.setRotationPoint(0.0F, 10.5F, 0.0F);


        bone4 = new ModelRenderer(this);
        bone4.setRotationPoint(0.0F, 21.5F, 0.0F);
        setRotationAngle(bone4, 0.0F, -1.5708F, 0.0F);


        bone8 = new ModelRenderer(this);
        bone8.setRotationPoint(-1.0F, 16.0F, 0.0F);
        setRotationAngle(bone8, 0.0F, 1.5708F, 0.0F);
        bone8.setTextureOffset(24, 34).addBox(-1.0F, -7.0F, 7.0F, 2.0F, 6.0F, 1.0F, 0.0F, false);
        bone8.setTextureOffset(32, 20).addBox(-1.0F, 1.0F, 7.0F, 2.0F, 6.0F, 1.0F, 0.0F, false);
        bone8.setTextureOffset(37, 19).addBox(-5.0F, -6.0F, 7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        bone8.setTextureOffset(10, 46).addBox(-5.0F, -5.0F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        bone8.setTextureOffset(4, 46).addBox(-4.0F, -3.0F, 7.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        bone8.setTextureOffset(42, 31).addBox(-3.0F, -4.0F, 6.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone8.setTextureOffset(10, 36).addBox(1.0F, -6.0F, 7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        bone8.setTextureOffset(0, 46).addBox(4.0F, -5.0F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        bone8.setTextureOffset(45, 11).addBox(2.0F, -3.0F, 7.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        bone8.setTextureOffset(42, 15).addBox(2.0F, -4.0F, 6.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone8.setTextureOffset(44, 24).addBox(2.0F, 2.0F, 7.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        bone8.setTextureOffset(42, 42).addBox(2.0F, 3.0F, 6.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone8.setTextureOffset(0, 36).addBox(1.0F, 5.0F, 7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        bone8.setTextureOffset(45, 26).addBox(4.0F, 2.0F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        bone8.setTextureOffset(35, 11).addBox(-5.0F, 5.0F, 7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        bone8.setTextureOffset(44, 21).addBox(-4.0F, 2.0F, 7.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        bone8.setTextureOffset(22, 42).addBox(-3.0F, 3.0F, 6.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone8.setTextureOffset(42, 45).addBox(-5.0F, 2.0F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);

        bone5 = new ModelRenderer(this);
        bone5.setRotationPoint(0.0F, 16.0F, -1.0F);
        bone5.setTextureOffset(32, 32).addBox(-1.0F, -7.0F, 7.0F, 2.0F, 6.0F, 1.0F, 0.0F, false);
        bone5.setTextureOffset(18, 28).addBox(-1.0F, 1.0F, 7.0F, 2.0F, 6.0F, 1.0F, 0.0F, false);
        bone5.setTextureOffset(32, 30).addBox(-5.0F, -6.0F, 7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        bone5.setTextureOffset(22, 45).addBox(-5.0F, -5.0F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        bone5.setTextureOffset(44, 6).addBox(-4.0F, -3.0F, 7.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        bone5.setTextureOffset(41, 28).addBox(-3.0F, -4.0F, 6.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone5.setTextureOffset(32, 28).addBox(1.0F, -6.0F, 7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        bone5.setTextureOffset(38, 44).addBox(4.0F, -5.0F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        bone5.setTextureOffset(44, 2).addBox(2.0F, -3.0F, 7.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        bone5.setTextureOffset(38, 41).addBox(2.0F, -4.0F, 6.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone5.setTextureOffset(44, 0).addBox(2.0F, 2.0F, 7.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        bone5.setTextureOffset(32, 41).addBox(2.0F, 3.0F, 6.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone5.setTextureOffset(32, 17).addBox(1.0F, 5.0F, 7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        bone5.setTextureOffset(18, 44).addBox(4.0F, 2.0F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        bone5.setTextureOffset(32, 9).addBox(-5.0F, 5.0F, 7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        bone5.setTextureOffset(32, 44).addBox(-4.0F, 2.0F, 7.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        bone5.setTextureOffset(18, 41).addBox(-3.0F, 3.0F, 6.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone5.setTextureOffset(28, 43).addBox(-5.0F, 2.0F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);

        bone6 = new ModelRenderer(this);
        bone6.setRotationPoint(1.0F, 16.0F, 0.0F);
        setRotationAngle(bone6, 0.0F, -1.5708F, 0.0F);
        bone6.setTextureOffset(12, 28).addBox(-1.0F, -7.0F, 7.0F, 2.0F, 6.0F, 1.0F, 0.0F, false);
        bone6.setTextureOffset(26, 26).addBox(-1.0F, 1.0F, 7.0F, 2.0F, 6.0F, 1.0F, 0.0F, false);
        bone6.setTextureOffset(32, 6).addBox(-5.0F, -6.0F, 7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        bone6.setTextureOffset(8, 19).addBox(-5.0F, -5.0F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        bone6.setTextureOffset(12, 44).addBox(-4.0F, -3.0F, 7.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        bone6.setTextureOffset(12, 41).addBox(-3.0F, -4.0F, 6.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone6.setTextureOffset(32, 3).addBox(1.0F, -6.0F, 7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        bone6.setTextureOffset(0, 19).addBox(4.0F, -5.0F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        bone6.setTextureOffset(6, 44).addBox(2.0F, -3.0F, 7.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        bone6.setTextureOffset(6, 41).addBox(2.0F, -4.0F, 6.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone6.setTextureOffset(0, 44).addBox(2.0F, 2.0F, 7.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        bone6.setTextureOffset(0, 41).addBox(2.0F, 3.0F, 6.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone6.setTextureOffset(26, 12).addBox(1.0F, 5.0F, 7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        bone6.setTextureOffset(8, 14).addBox(4.0F, 2.0F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        bone6.setTextureOffset(14, 24).addBox(-5.0F, 5.0F, 7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        bone6.setTextureOffset(43, 40).addBox(-4.0F, 2.0F, 7.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        bone6.setTextureOffset(40, 6).addBox(-3.0F, 3.0F, 6.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone6.setTextureOffset(0, 14).addBox(-5.0F, 2.0F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);

        bone7 = new ModelRenderer(this);
        bone7.setRotationPoint(0.0F, 16.0F, 1.0F);
        setRotationAngle(bone7, 0.0F, 3.1416F, 0.0F);
        bone7.setTextureOffset(6, 28).addBox(-1.0F, -7.0F, 7.0F, 2.0F, 6.0F, 1.0F, 0.0F, false);
        bone7.setTextureOffset(0, 28).addBox(-1.0F, 1.0F, 7.0F, 2.0F, 6.0F, 1.0F, 0.0F, false);
        bone7.setTextureOffset(0, 24).addBox(-5.0F, -6.0F, 7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        bone7.setTextureOffset(8, 5).addBox(-5.0F, -5.0F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        bone7.setTextureOffset(43, 13).addBox(-4.0F, -3.0F, 7.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        bone7.setTextureOffset(40, 3).addBox(-3.0F, -4.0F, 6.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone7.setTextureOffset(14, 22).addBox(1.0F, -6.0F, 7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        bone7.setTextureOffset(8, 0).addBox(4.0F, -5.0F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        bone7.setTextureOffset(42, 38).addBox(2.0F, -3.0F, 7.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        bone7.setTextureOffset(28, 40).addBox(2.0F, -4.0F, 6.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone7.setTextureOffset(42, 35).addBox(2.0F, 2.0F, 7.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        bone7.setTextureOffset(38, 35).addBox(2.0F, 3.0F, 6.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone7.setTextureOffset(14, 20).addBox(1.0F, 5.0F, 7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        bone7.setTextureOffset(0, 5).addBox(4.0F, 2.0F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        bone7.setTextureOffset(0, 10).addBox(-5.0F, 5.0F, 7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        bone7.setTextureOffset(42, 9).addBox(-4.0F, 2.0F, 7.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        bone7.setTextureOffset(38, 32).addBox(-3.0F, 3.0F, 6.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone7.setTextureOffset(0, 0).addBox(-5.0F, 2.0F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);

        bb_main = new ModelRenderer(this);
        bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
        bb_main.setTextureOffset(38, 24).addBox(-1.0F, -16.0F, -7.0F, 2.0F, 1.0F, 3.0F, 0.0F, false);
        bb_main.setTextureOffset(38, 21).addBox(-7.0F, -16.0F, -1.0F, 3.0F, 1.0F, 2.0F, 0.0F, false);
        bb_main.setTextureOffset(36, 38).addBox(4.0F, -16.0F, -1.0F, 3.0F, 1.0F, 2.0F, 0.0F, false);
        bb_main.setTextureOffset(14, 0).addBox(-7.0F, -9.0F, 6.0F, 14.0F, 2.0F, 1.0F, 0.0F, false);
        bb_main.setTextureOffset(0, 14).addBox(-7.0F, -9.0F, -6.0F, 1.0F, 2.0F, 12.0F, 0.0F, false);
        bb_main.setTextureOffset(14, 14).addBox(-7.0F, -9.0F, -7.0F, 14.0F, 2.0F, 1.0F, 0.0F, false);
        bb_main.setTextureOffset(0, 0).addBox(6.0F, -9.0F, -6.0F, 1.0F, 2.0F, 12.0F, 0.0F, false);
        bb_main.setTextureOffset(16, 38).addBox(-1.0F, -16.0F, 4.0F, 2.0F, 1.0F, 3.0F, 0.0F, false);
        bb_main.setTextureOffset(14, 17).addBox(-4.0F, -16.0F, 2.0F, 8.0F, 1.0F, 2.0F, 0.0F, false);
        bb_main.setTextureOffset(14, 9).addBox(-4.0F, -16.0F, -4.0F, 8.0F, 1.0F, 2.0F, 0.0F, false);
        bb_main.setTextureOffset(0, 19).addBox(2.0F, -16.0F, -2.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);
        bb_main.setTextureOffset(0, 14).addBox(-4.0F, -16.0F, -2.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);
        bb_main.setTextureOffset(8, 38).addBox(-1.0F, -1.0F, 4.0F, 2.0F, 1.0F, 3.0F, 0.0F, false);
        bb_main.setTextureOffset(0, 38).addBox(-7.0F, -1.0F, -1.0F, 3.0F, 1.0F, 2.0F, 0.0F, false);
        bb_main.setTextureOffset(0, 5).addBox(-4.0F, -1.0F, -2.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);
        bb_main.setTextureOffset(14, 6).addBox(-4.0F, -1.0F, -4.0F, 8.0F, 1.0F, 2.0F, 0.0F, false);
        bb_main.setTextureOffset(0, 0).addBox(2.0F, -1.0F, -2.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);
        bb_main.setTextureOffset(14, 3).addBox(-4.0F, -1.0F, 2.0F, 8.0F, 1.0F, 2.0F, 0.0F, false);
        bb_main.setTextureOffset(24, 23).addBox(4.0F, -1.0F, -1.0F, 3.0F, 1.0F, 2.0F, 0.0F, false);
        bb_main.setTextureOffset(23, 20).addBox(-1.0F, -1.0F, -7.0F, 2.0F, 1.0F, 3.0F, 0.0F, false);
    }


    @Override
    public void render(MatrixStack ms, IVertexBuilder buffer, int light, int overlay, float r, float g, float b, float a) {
        render(ms, buffer, light, overlay, r, g, b, a, 1);
    }
    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float r, float g, float b, float alpha, float fract) {
        bone.render(matrixStack, buffer, packedLight, packedOverlay);
        bone2.render(matrixStack, buffer, packedLight, packedOverlay);
        bone3.render(matrixStack, buffer, packedLight, packedOverlay);
        bone4.render(matrixStack, buffer, packedLight, packedOverlay);
        bone8.render(matrixStack, buffer, packedLight, packedOverlay);
        bone5.render(matrixStack, buffer, packedLight, packedOverlay);
        bone6.render(matrixStack, buffer, packedLight, packedOverlay);
        bone7.render(matrixStack, buffer, packedLight, packedOverlay);
        bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}