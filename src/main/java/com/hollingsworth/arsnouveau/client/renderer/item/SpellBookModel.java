package com.hollingsworth.arsnouveau.client.renderer.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SpellBookModel extends Model {

    private final ModelRenderer left_cover;
    private final ModelRenderer right_cover;
    private final ModelRenderer bb_main;

    public SpellBookModel() {
        super(RenderType::getEntityCutout);
        textureWidth = 128;
        textureHeight = 128;

        left_cover = new ModelRenderer(this);
        left_cover.setRotationPoint(0.0F, 23.0F, 0.0F);
        setRotationAngle(left_cover, 0.0F, 0.0F, 0.3927F);
        left_cover.setTextureOffset(35, 0).addBox(-9.0F, 0.0F, -7.0F, 9.0F, 1.0F, 14.0F, 0.0F, false);
        left_cover.setTextureOffset(0, 15).addBox(-10.0F, 1.0F, -7.5F, 10.0F, 0.0F, 15.0F, 0.0F, false);

        right_cover = new ModelRenderer(this);
        right_cover.setRotationPoint(0.0F, 23.0F, 0.0F);
        setRotationAngle(right_cover, 0.0F, 0.0F, -0.3927F);
        right_cover.setTextureOffset(21, 21).addBox(0.0F, 0.0F, -7.0F, 9.0F, 1.0F, 14.0F, 0.0F, false);
        right_cover.setTextureOffset(0, 0).addBox(0.0F, 1.0F, -7.5F, 10.0F, 0.0F, 15.0F, 0.0F, false);

        bb_main = new ModelRenderer(this);
        bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
        bb_main.setTextureOffset(0, 30).addBox(-1.0F, -1.0F, -7.5F, 2.0F, 1.0F, 15.0F, 0.0F, false);
    }



    @Override
    public void render(MatrixStack ms, IVertexBuilder buffer, int light, int overlay, float r, float g, float b, float a) {
        render(ms, buffer, light, overlay, r, g, b, a, 1);
    }


    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float r, float g, float b, float alpha, float fract) {
        left_cover.render(matrixStack, buffer, packedLight, packedOverlay);
        right_cover.render(matrixStack, buffer, packedLight, packedOverlay);
        bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
