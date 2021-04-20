package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ArcaneCoreModel extends Model {


    private final ModelRenderer bone3;
    private final ModelRenderer gem;
    private final ModelRenderer segment1a;
    private final ModelRenderer segment1a2;
    private final ModelRenderer segment1a3;
    private final ModelRenderer segment1a4;
    private final ModelRenderer gem2;
    private final ModelRenderer gem3;
    private final ModelRenderer gem4;

    public ArcaneCoreModel() {
        super(RenderType::entityCutout);
        texWidth = 32;
        texHeight = 32;

        bone3 = new ModelRenderer(this);
        bone3.setPos(0.0F, 24.0F, 0.0F);
        setRotationAngle(bone3, 0.0F, 1.5708F, 0.0F);
        bone3.texOffs(18, 10).addBox(-2.0F, -15.5F, 1.0F, 4.0F, 2.0F, 1.0F, 0.0F, false);
        bone3.texOffs(18, 7).addBox(-2.0F, -2.5F, 1.0F, 4.0F, 2.0F, 1.0F, 0.0F, false);
        bone3.texOffs(20, 22).addBox(-1.0F, -15.0F, 2.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        bone3.texOffs(8, 22).addBox(-1.0F, -2.0F, 2.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        bone3.texOffs(0, 22).addBox(-1.0F, -15.0F, -4.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        bone3.texOffs(21, 4).addBox(-1.0F, -2.0F, -4.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        bone3.texOffs(14, 21).addBox(-4.0F, -15.0F, -1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        bone3.texOffs(19, 1).addBox(-4.0F, -2.0F, -1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        bone3.texOffs(8, 19).addBox(2.0F, -15.0F, -1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        bone3.texOffs(0, 19).addBox(2.0F, -2.0F, -1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        bone3.texOffs(20, 25).addBox(1.0F, -15.5F, -1.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        bone3.texOffs(6, 25).addBox(1.0F, -2.5F, -1.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        bone3.texOffs(0, 25).addBox(-2.0F, -15.5F, -1.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        bone3.texOffs(14, 24).addBox(-2.0F, -2.5F, -1.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        bone3.texOffs(17, 18).addBox(-2.0F, -15.5F, -2.0F, 4.0F, 2.0F, 1.0F, 0.0F, false);
        bone3.texOffs(8, 16).addBox(-2.0F, -2.5F, -2.0F, 4.0F, 2.0F, 1.0F, 0.0F, false);

        gem = new ModelRenderer(this);
        gem.setPos(-6.0F, 16.0F, -6.0F);


        segment1a = new ModelRenderer(this);
        segment1a.setPos(0.0F, 16.0F, 0.0F);
        segment1a.texOffs(18, 13).addBox(-2.0F, -6.0F, -6.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        segment1a.texOffs(11, 4).addBox(-2.0F, -7.0F, -5.0F, 4.0F, 2.0F, 1.0F, 0.0F, false);
        segment1a.texOffs(0, 4).addBox(-2.0F, -8.0F, -7.0F, 4.0F, 1.0F, 3.0F, 0.0F, false);
        segment1a.texOffs(8, 12).addBox(-2.0F, -8.0F, -8.0F, 4.0F, 3.0F, 1.0F, 0.0F, false);
        segment1a.texOffs(8, 8).addBox(-2.0F, 5.0F, -8.0F, 4.0F, 3.0F, 1.0F, 0.0F, false);
        segment1a.texOffs(4, 8).addBox(-2.0F, -5.0F, -8.0F, 1.0F, 10.0F, 1.0F, 0.0F, false);
        segment1a.texOffs(0, 8).addBox(1.0F, -5.0F, -8.0F, 1.0F, 10.0F, 1.0F, 0.0F, false);
        segment1a.texOffs(0, 0).addBox(-2.0F, 7.0F, -7.0F, 4.0F, 1.0F, 3.0F, 0.0F, false);
        segment1a.texOffs(11, 0).addBox(-2.0F, 5.0F, -5.0F, 4.0F, 2.0F, 1.0F, 0.0F, false);
        segment1a.texOffs(17, 15).addBox(-2.0F, 5.0F, -6.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);

        segment1a2 = new ModelRenderer(this);
        segment1a2.setPos(0.0F, 16.0F, 0.0F);
        setRotationAngle(segment1a2, 0.0F, -1.5708F, 0.0F);
        segment1a2.texOffs(18, 13).addBox(-2.0F, -6.0F, -6.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        segment1a2.texOffs(11, 4).addBox(-2.0F, -7.0F, -5.0F, 4.0F, 2.0F, 1.0F, 0.0F, false);
        segment1a2.texOffs(0, 4).addBox(-2.0F, -8.0F, -7.0F, 4.0F, 1.0F, 3.0F, 0.0F, false);
        segment1a2.texOffs(8, 12).addBox(-2.0F, -8.0F, -8.0F, 4.0F, 3.0F, 1.0F, 0.0F, false);
        segment1a2.texOffs(8, 8).addBox(-2.0F, 5.0F, -8.0F, 4.0F, 3.0F, 1.0F, 0.0F, false);
        segment1a2.texOffs(4, 8).addBox(-2.0F, -5.0F, -8.0F, 1.0F, 10.0F, 1.0F, 0.0F, false);
        segment1a2.texOffs(0, 8).addBox(1.0F, -5.0F, -8.0F, 1.0F, 10.0F, 1.0F, 0.0F, false);
        segment1a2.texOffs(0, 0).addBox(-2.0F, 7.0F, -7.0F, 4.0F, 1.0F, 3.0F, 0.0F, false);
        segment1a2.texOffs(11, 0).addBox(-2.0F, 5.0F, -5.0F, 4.0F, 2.0F, 1.0F, 0.0F, false);
        segment1a2.texOffs(17, 15).addBox(-2.0F, 5.0F, -6.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);

        segment1a3 = new ModelRenderer(this);
        segment1a3.setPos(0.0F, 16.0F, 0.0F);
        setRotationAngle(segment1a3, 0.0F, 3.1416F, 0.0F);
        segment1a3.texOffs(18, 13).addBox(-2.0F, -6.0F, -6.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        segment1a3.texOffs(11, 4).addBox(-2.0F, -7.0F, -5.0F, 4.0F, 2.0F, 1.0F, 0.0F, false);
        segment1a3.texOffs(0, 4).addBox(-2.0F, -8.0F, -7.0F, 4.0F, 1.0F, 3.0F, 0.0F, false);
        segment1a3.texOffs(8, 12).addBox(-2.0F, -8.0F, -8.0F, 4.0F, 3.0F, 1.0F, 0.0F, false);
        segment1a3.texOffs(8, 8).addBox(-2.0F, 5.0F, -8.0F, 4.0F, 3.0F, 1.0F, 0.0F, false);
        segment1a3.texOffs(4, 8).addBox(-2.0F, -5.0F, -8.0F, 1.0F, 10.0F, 1.0F, 0.0F, false);
        segment1a3.texOffs(0, 8).addBox(1.0F, -5.0F, -8.0F, 1.0F, 10.0F, 1.0F, 0.0F, false);
        segment1a3.texOffs(0, 0).addBox(-2.0F, 7.0F, -7.0F, 4.0F, 1.0F, 3.0F, 0.0F, false);
        segment1a3.texOffs(11, 0).addBox(-2.0F, 5.0F, -5.0F, 4.0F, 2.0F, 1.0F, 0.0F, false);
        segment1a3.texOffs(17, 15).addBox(-2.0F, 5.0F, -6.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);

        segment1a4 = new ModelRenderer(this);
        segment1a4.setPos(0.0F, 16.0F, 0.0F);
        setRotationAngle(segment1a4, 0.0F, 1.5708F, 0.0F);
        segment1a4.texOffs(18, 13).addBox(-2.0F, -6.0F, -6.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        segment1a4.texOffs(11, 4).addBox(-2.0F, -7.0F, -5.0F, 4.0F, 2.0F, 1.0F, 0.0F, false);
        segment1a4.texOffs(0, 4).addBox(-2.0F, -8.0F, -7.0F, 4.0F, 1.0F, 3.0F, 0.0F, false);
        segment1a4.texOffs(8, 12).addBox(-2.0F, -8.0F, -8.0F, 4.0F, 3.0F, 1.0F, 0.0F, false);
        segment1a4.texOffs(8, 8).addBox(-2.0F, 5.0F, -8.0F, 4.0F, 3.0F, 1.0F, 0.0F, false);
        segment1a4.texOffs(4, 8).addBox(-2.0F, -5.0F, -8.0F, 1.0F, 10.0F, 1.0F, 0.0F, false);
        segment1a4.texOffs(0, 8).addBox(1.0F, -5.0F, -8.0F, 1.0F, 10.0F, 1.0F, 0.0F, false);
        segment1a4.texOffs(0, 0).addBox(-2.0F, 7.0F, -7.0F, 4.0F, 1.0F, 3.0F, 0.0F, false);
        segment1a4.texOffs(11, 0).addBox(-2.0F, 5.0F, -5.0F, 4.0F, 2.0F, 1.0F, 0.0F, false);
        segment1a4.texOffs(17, 15).addBox(-2.0F, 5.0F, -6.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);

        gem2 = new ModelRenderer(this);
        gem2.setPos(6.0F, 16.0F, -6.0F);


        gem3 = new ModelRenderer(this);
        gem3.setPos(6.0F, 16.0F, 6.0F);


        gem4 = new ModelRenderer(this);
        gem4.setPos(-6.0F, 16.0F, 6.0F);

    }


    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone3.render(matrixStack, buffer, packedLight, packedOverlay);
        gem.render(matrixStack, buffer, packedLight, packedOverlay);
        segment1a.render(matrixStack, buffer, packedLight, packedOverlay);
        segment1a2.render(matrixStack, buffer, packedLight, packedOverlay);
        segment1a3.render(matrixStack, buffer, packedLight, packedOverlay);
        segment1a4.render(matrixStack, buffer, packedLight, packedOverlay);
        gem2.render(matrixStack, buffer, packedLight, packedOverlay);
        gem3.render(matrixStack, buffer, packedLight, packedOverlay);
        gem4.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}