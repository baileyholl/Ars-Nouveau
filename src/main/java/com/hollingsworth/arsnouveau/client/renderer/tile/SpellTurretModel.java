package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SpellTurretModel extends Model {

    public final ModelRenderer back;
    private final ModelRenderer bone;
    private final ModelRenderer bone2;
    private final ModelRenderer bone3;
    private final ModelRenderer bone4;
    private final ModelRenderer bone5;
    private final ModelRenderer bb_main;

    public SpellTurretModel() {
        super(RenderType::entityCutout);
        texWidth = 32;
        texHeight = 32;

        back = new ModelRenderer(this);
        back.setPos(0.0F, 16.0F, 0.0F);


        bone = new ModelRenderer(this);
        bone.setPos(0.0F, 0.0F, 4.0F);
        back.addChild(bone);
        bone.texOffs(12, 22).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);
        bone.texOffs(0, 3).addBox(-1.0F, -6.0F, 2.0F, 2.0F, 3.0F, 1.0F, 0.0F, false);
        bone.texOffs(0, 0).addBox(-1.0F, -6.0F, 0.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        bone.texOffs(24, 4).addBox(-1.0F, -5.0F, 0.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);

        bone2 = new ModelRenderer(this);
        bone2.setPos(0.0F, 0.0F, 4.0F);
        back.addChild(bone2);
        setRotationAngle(bone2, 0.0F, 0.0F, -1.5708F);
        bone2.texOffs(12, 22).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);
        bone2.texOffs(0, 3).addBox(-1.0F, -6.0F, 2.0F, 2.0F, 3.0F, 1.0F, 0.0F, false);
        bone2.texOffs(0, 0).addBox(-1.0F, -6.0F, 0.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        bone2.texOffs(24, 4).addBox(-1.0F, -5.0F, 0.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);

        bone3 = new ModelRenderer(this);
        bone3.setPos(0.0F, 0.0F, 4.0F);
        back.addChild(bone3);
        setRotationAngle(bone3, 0.0F, 0.0F, -3.1416F);
        bone3.texOffs(12, 22).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);
        bone3.texOffs(0, 3).addBox(-1.0F, -6.0F, 2.0F, 2.0F, 3.0F, 1.0F, 0.0F, false);
        bone3.texOffs(0, 0).addBox(-1.0F, -6.0F, 0.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        bone3.texOffs(24, 4).addBox(-1.0F, -5.0F, 0.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);

        bone4 = new ModelRenderer(this);
        bone4.setPos(0.0F, 0.0F, 4.0F);
        back.addChild(bone4);
        setRotationAngle(bone4, 0.0F, 0.0F, 1.5708F);
        bone4.texOffs(12, 22).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);
        bone4.texOffs(0, 3).addBox(-1.0F, -6.0F, 2.0F, 2.0F, 3.0F, 1.0F, 0.0F, false);
        bone4.texOffs(0, 0).addBox(-1.0F, -6.0F, 0.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        bone4.texOffs(24, 4).addBox(-1.0F, -5.0F, 0.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);

        bone5 = new ModelRenderer(this);
        bone5.setPos(-1.5F, -0.5F, 4.5F);
        back.addChild(bone5);
        bone5.texOffs(16, 16).addBox(-1.0F, -2.0F, -1.5F, 5.0F, 5.0F, 1.0F, 0.0F, false);

        bb_main = new ModelRenderer(this);
        bb_main.setPos(0.0F, 24.0F, 0.0F);
        bb_main.texOffs(0, 0).addBox(-4.0F, -12.0F, -5.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
        bb_main.texOffs(0, 16).addBox(-3.0F, -11.0F, -6.0F, 6.0F, 6.0F, 2.0F, 0.0F, false);
        bb_main.texOffs(0, 24).addBox(-2.5F, -6.5F, -7.0F, 5.0F, 1.0F, 1.0F, 0.0F, false);
        bb_main.texOffs(24, 0).addBox(-2.5F, -9.5F, -7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        bb_main.texOffs(24, 24).addBox(1.5F, -9.5F, -7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        bb_main.texOffs(20, 22).addBox(-2.5F, -10.5F, -7.0F, 5.0F, 1.0F, 1.0F, 0.0F, false);
    }



    @Override
    public void renderToBuffer(MatrixStack ms, IVertexBuilder buffer, int light, int overlay, float r, float g, float b, float a) {
        back.render(ms, buffer, light, overlay);
        bb_main.render(ms, buffer, light, overlay);

    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
