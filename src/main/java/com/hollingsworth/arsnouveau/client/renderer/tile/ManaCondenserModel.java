package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.ClientHandler;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ManaCondenserModel extends Model {
    private final ModelRenderer arm1;
    private final ModelRenderer slant1;
    private final ModelRenderer slant2;
    private final ModelRenderer slant3;
    private final ModelRenderer slant4;
    private final ModelRenderer arm2;
    private final ModelRenderer arm3;
    private final ModelRenderer arm4;
    private final ModelRenderer gem;
    private final ModelRenderer bb_main;

    public ManaCondenserModel(){
        super(RenderType::getEntityCutout);
        textureWidth = 32;
        textureHeight = 32;

        arm1 = new ModelRenderer(this);
        arm1.setRotationPoint(0.0F, 24.0F, 0.0F);
        arm1.setTextureOffset(14, 8).addBox(-1.0F, -9.0F, 6.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        arm1.setTextureOffset(6, 13).addBox(-1.0F, -10.0F, 5.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        arm1.setTextureOffset(0, 12).addBox(-1.0F, -10.0F, 4.0F, 2.0F, 5.0F, 1.0F, 0.0F, false);
        arm1.setTextureOffset(6, 13).addBox(-1.0F, -5.0F, 3.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        arm1.setTextureOffset(14, 14).addBox(-1.0F, -5.0F, 2.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
        arm1.setTextureOffset(6, 13).addBox(-1.0F, -1.0F, 2.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        slant1 = new ModelRenderer(this);
        slant1.setRotationPoint(0.0F, 13.0F, 0.0F);
        setRotationAngle(slant1, -0.3927F, 0.0F, 0.0F);
        slant1.setTextureOffset(8, 8).addBox(-0.5F, -0.574F, 1.3858F, 1.0F, 1.0F, 4.0F, 0.0F, false);

        slant2 = new ModelRenderer(this);
        slant2.setRotationPoint(0.0F, 13.0F, 0.0F);
        setRotationAngle(slant2, 0.0F, -1.5708F, -0.3927F);
        slant2.setTextureOffset(8, 8).addBox(-0.5F, -0.574F, 1.3858F, 1.0F, 1.0F, 4.0F, 0.0F, false);

        slant3 = new ModelRenderer(this);
        slant3.setRotationPoint(0.0F, 13.0F, 0.0F);
        setRotationAngle(slant3, 2.7489F, 0.0F, -3.1416F);
        slant3.setTextureOffset(8, 8).addBox(-0.5F, -0.574F, 1.3858F, 1.0F, 1.0F, 4.0F, 0.0F, false);

        slant4 = new ModelRenderer(this);
        slant4.setRotationPoint(0.0F, 13.0F, 0.0F);
        setRotationAngle(slant4, 0.0F, 1.5708F, 0.3927F);
        slant4.setTextureOffset(8, 8).addBox(-0.5F, -0.574F, 1.3858F, 1.0F, 1.0F, 4.0F, 0.0F, false);

        arm2 = new ModelRenderer(this);
        arm2.setRotationPoint(0.0F, 24.0F, 0.0F);
        setRotationAngle(arm2, 0.0F, -1.5708F, 0.0F);
        arm2.setTextureOffset(14, 8).addBox(-1.0F, -9.0F, 6.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        arm2.setTextureOffset(6, 13).addBox(-1.0F, -10.0F, 5.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        arm2.setTextureOffset(0, 12).addBox(-1.0F, -10.0F, 4.0F, 2.0F, 5.0F, 1.0F, 0.0F, false);
        arm2.setTextureOffset(6, 13).addBox(-1.0F, -5.0F, 3.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        arm2.setTextureOffset(14, 14).addBox(-1.0F, -5.0F, 2.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
        arm2.setTextureOffset(6, 13).addBox(-1.0F, -1.0F, 2.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        arm3 = new ModelRenderer(this);
        arm3.setRotationPoint(0.0F, 24.0F, 0.0F);
        setRotationAngle(arm3, -3.1416F, 0.0F, 3.1416F);
        arm3.setTextureOffset(14, 8).addBox(-1.0F, -9.0F, 6.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        arm3.setTextureOffset(6, 13).addBox(-1.0F, -10.0F, 5.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        arm3.setTextureOffset(0, 12).addBox(-1.0F, -10.0F, 4.0F, 2.0F, 5.0F, 1.0F, 0.0F, false);
        arm3.setTextureOffset(6, 13).addBox(-1.0F, -5.0F, 3.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        arm3.setTextureOffset(14, 14).addBox(-1.0F, -5.0F, 2.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
        arm3.setTextureOffset(6, 13).addBox(-1.0F, -1.0F, 2.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        arm4 = new ModelRenderer(this);
        arm4.setRotationPoint(0.0F, 24.0F, 0.0F);
        setRotationAngle(arm4, 0.0F, 1.5708F, 0.0F);
        arm4.setTextureOffset(14, 8).addBox(-1.0F, -9.0F, 6.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        arm4.setTextureOffset(6, 13).addBox(-1.0F, -10.0F, 5.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        arm4.setTextureOffset(0, 12).addBox(-1.0F, -10.0F, 4.0F, 2.0F, 5.0F, 1.0F, 0.0F, false);
        arm4.setTextureOffset(6, 13).addBox(-1.0F, -5.0F, 3.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        arm4.setTextureOffset(14, 14).addBox(-1.0F, -5.0F, 2.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
        arm4.setTextureOffset(6, 13).addBox(-1.0F, -1.0F, 2.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        gem = new ModelRenderer(this);
        gem.setRotationPoint(0.0F, 10.0F, 0.0F);
        gem.setTextureOffset(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

        bb_main = new ModelRenderer(this);
        bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
        bb_main.setTextureOffset(0, 8).addBox(-1.5F, -11.0F, -1.5F, 3.0F, 1.0F, 3.0F, 0.0F, false);
        bb_main.setTextureOffset(12, 0).addBox(-1.0F, -10.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        bb_main.setTextureOffset(0, 0).addBox(-0.5F, -8.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float r, float g, float b, float a) {
        arm1.render(matrixStack, buffer, packedLight, packedOverlay);
        slant1.render(matrixStack, buffer, packedLight, packedOverlay);
        slant2.render(matrixStack, buffer, packedLight, packedOverlay);
        slant3.render(matrixStack, buffer, packedLight, packedOverlay);
        slant4.render(matrixStack, buffer, packedLight, packedOverlay);
        arm2.render(matrixStack, buffer, packedLight, packedOverlay);
        arm3.render(matrixStack, buffer, packedLight, packedOverlay);
        arm4.render(matrixStack, buffer, packedLight, packedOverlay);
        gem.render(matrixStack, buffer, packedLight, packedOverlay);
        bb_main.render(matrixStack, buffer, packedLight, packedOverlay);

        gem.rotateAngleY = (ClientInfo.ticksInGame /5.0f) % 360;
        gem.rotateAngleX = 0;
        gem.rotateAngleZ = 0;
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }


}
