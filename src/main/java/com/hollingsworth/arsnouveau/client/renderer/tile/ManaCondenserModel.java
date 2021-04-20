package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ManaCondenserModel extends Model {
    private final ModelRenderer gem;
    private final ModelRenderer bone;
    private final ModelRenderer slant3;
    private final ModelRenderer arm3;
    private final ModelRenderer bone2;
    private final ModelRenderer slant2;
    private final ModelRenderer arm2;
    private final ModelRenderer bone3;
    private final ModelRenderer slant4;
    private final ModelRenderer arm4;
    private final ModelRenderer bone4;
    private final ModelRenderer slant5;
    private final ModelRenderer arm5;
    private final ModelRenderer bb_main;

    public ManaCondenserModel(){
        super(RenderType::entityCutout);
        texWidth = 32;
        texHeight = 32;

        gem = new ModelRenderer(this);
        gem.setPos(0.0F, 10.0F, 0.0F);
        gem.texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

        bone = new ModelRenderer(this);
        bone.setPos(0.0F, 24.0F, 0.0F);


        slant3 = new ModelRenderer(this);
        slant3.setPos(0.0F, -11.0F, 0.0F);
        bone.addChild(slant3);
        setRotationAngle(slant3, 2.7489F, 0.0F, -3.1416F);
        slant3.texOffs(8, 8).addBox(-0.5F, -0.574F, 1.3858F, 1.0F, 1.0F, 4.0F, 0.0F, false);

        arm3 = new ModelRenderer(this);
        arm3.setPos(0.0F, 0.0F, 0.0F);
        bone.addChild(arm3);
        setRotationAngle(arm3, -3.1416F, 0.0F, 3.1416F);
        arm3.texOffs(6, 16).addBox(-1.0F, -9.0F, 6.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        arm3.texOffs(14, 9).addBox(-1.0F, -10.0F, 5.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        arm3.texOffs(0, 12).addBox(-1.0F, -10.0F, 4.0F, 2.0F, 5.0F, 1.0F, 0.0F, false);
        arm3.texOffs(14, 6).addBox(-1.0F, -5.0F, 3.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        arm3.texOffs(14, 14).addBox(-1.0F, -5.0F, 2.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
        arm3.texOffs(6, 13).addBox(-1.0F, -1.0F, 2.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        bone2 = new ModelRenderer(this);
        bone2.setPos(0.0F, 24.0F, 0.0F);
        setRotationAngle(bone2, 0.0F, -1.5708F, 0.0F);


        slant2 = new ModelRenderer(this);
        slant2.setPos(0.0F, -11.0F, 0.0F);
        bone2.addChild(slant2);
        setRotationAngle(slant2, 2.7489F, 0.0F, -3.1416F);
        slant2.texOffs(8, 8).addBox(-0.5F, -0.574F, 1.3858F, 1.0F, 1.0F, 4.0F, 0.0F, false);

        arm2 = new ModelRenderer(this);
        arm2.setPos(0.0F, 0.0F, 0.0F);
        bone2.addChild(arm2);
        setRotationAngle(arm2, -3.1416F, 0.0F, 3.1416F);
        arm2.texOffs(6, 16).addBox(-1.0F, -9.0F, 6.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        arm2.texOffs(14, 9).addBox(-1.0F, -10.0F, 5.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        arm2.texOffs(0, 12).addBox(-1.0F, -10.0F, 4.0F, 2.0F, 5.0F, 1.0F, 0.0F, false);
        arm2.texOffs(14, 6).addBox(-1.0F, -5.0F, 3.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        arm2.texOffs(14, 14).addBox(-1.0F, -5.0F, 2.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
        arm2.texOffs(6, 13).addBox(-1.0F, -1.0F, 2.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        bone3 = new ModelRenderer(this);
        bone3.setPos(0.0F, 24.0F, 0.0F);
        setRotationAngle(bone3, 0.0F, 3.1416F, 0.0F);


        slant4 = new ModelRenderer(this);
        slant4.setPos(0.0F, -11.0F, 0.0F);
        bone3.addChild(slant4);
        setRotationAngle(slant4, 2.7489F, 0.0F, -3.1416F);
        slant4.texOffs(8, 8).addBox(-0.5F, -0.574F, 1.3858F, 1.0F, 1.0F, 4.0F, 0.0F, false);

        arm4 = new ModelRenderer(this);
        arm4.setPos(0.0F, 0.0F, 0.0F);
        bone3.addChild(arm4);
        setRotationAngle(arm4, -3.1416F, 0.0F, 3.1416F);
        arm4.texOffs(6, 16).addBox(-1.0F, -9.0F, 6.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        arm4.texOffs(14, 9).addBox(-1.0F, -10.0F, 5.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        arm4.texOffs(0, 12).addBox(-1.0F, -10.0F, 4.0F, 2.0F, 5.0F, 1.0F, 0.0F, false);
        arm4.texOffs(14, 6).addBox(-1.0F, -5.0F, 3.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        arm4.texOffs(14, 14).addBox(-1.0F, -5.0F, 2.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
        arm4.texOffs(6, 13).addBox(-1.0F, -1.0F, 2.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        bone4 = new ModelRenderer(this);
        bone4.setPos(0.0F, 24.0F, 0.0F);
        setRotationAngle(bone4, 0.0F, 1.5708F, 0.0F);


        slant5 = new ModelRenderer(this);
        slant5.setPos(0.0F, -11.0F, 0.0F);
        bone4.addChild(slant5);
        setRotationAngle(slant5, 2.7489F, 0.0F, -3.1416F);
        slant5.texOffs(8, 8).addBox(-0.5F, -0.574F, 1.3858F, 1.0F, 1.0F, 4.0F, 0.0F, false);

        arm5 = new ModelRenderer(this);
        arm5.setPos(0.0F, 0.0F, 0.0F);
        bone4.addChild(arm5);
        setRotationAngle(arm5, -3.1416F, 0.0F, 3.1416F);
        arm5.texOffs(6, 16).addBox(-1.0F, -9.0F, 6.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        arm5.texOffs(14, 9).addBox(-1.0F, -10.0F, 5.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        arm5.texOffs(0, 12).addBox(-1.0F, -10.0F, 4.0F, 2.0F, 5.0F, 1.0F, 0.0F, false);
        arm5.texOffs(14, 6).addBox(-1.0F, -5.0F, 3.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        arm5.texOffs(14, 14).addBox(-1.0F, -5.0F, 2.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
        arm5.texOffs(6, 13).addBox(-1.0F, -1.0F, 2.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        bb_main = new ModelRenderer(this);
        bb_main.setPos(0.0F, 24.0F, 0.0F);
        bb_main.texOffs(0, 8).addBox(-1.5F, -11.0F, -1.5F, 3.0F, 1.0F, 3.0F, 0.0F, false);
        bb_main.texOffs(12, 0).addBox(-1.0F, -10.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        bb_main.texOffs(0, 0).addBox(-0.5F, -8.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float r, float g, float b, float a) {
        gem.render(matrixStack, buffer, packedLight, packedOverlay);
        bone.render(matrixStack, buffer, packedLight, packedOverlay);
        bone2.render(matrixStack, buffer, packedLight, packedOverlay);
        bone3.render(matrixStack, buffer, packedLight, packedOverlay);
        bone4.render(matrixStack, buffer, packedLight, packedOverlay);
        bb_main.render(matrixStack, buffer, packedLight, packedOverlay);

        gem.yRot = (ClientInfo.ticksInGame /5.0f) % 360;
        gem.xRot = 0;
        gem.zRot = 0;
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }


}
