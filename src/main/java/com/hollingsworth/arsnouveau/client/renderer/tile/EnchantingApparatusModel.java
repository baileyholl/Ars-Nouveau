package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class EnchantingApparatusModel extends Model {
    public final ModelRenderer frame_all;
    private final ModelRenderer cap_top;
    public final ModelRenderer cap_bot;
    public final ModelRenderer frame_top;
    private final ModelRenderer frame_top1;
    private final ModelRenderer frame_top2;
    private final ModelRenderer frame_top3;
    private final ModelRenderer frame_top4;
    public final ModelRenderer frame_bot;
    private final ModelRenderer frame_bot1;
    private final ModelRenderer frame_bot2;
    private final ModelRenderer frame_bot3;
    private final ModelRenderer frame_bot4;

    public EnchantingApparatusModel() {
        super(RenderType::entityCutout);
        texWidth = 32;
        texHeight = 32;

        frame_all = new ModelRenderer(this);
        frame_all.setPos(0.0F, 1.0F, 0.0F);


        cap_top = new ModelRenderer(this);
        cap_top.setPos(0.0F, 0.0F, 0.0F);
        frame_all.addChild(cap_top);
        cap_top.texOffs(0, 0).addBox(-2.5F, -6.5F, -2.5F, 5.0F, 1.0F, 5.0F, 0.0F, false);
        cap_top.texOffs(0, 6).addBox(-1.5F, -5.5F, -1.5F, 3.0F, 2.0F, 3.0F, 0.0F, false);

        cap_bot = new ModelRenderer(this);
        cap_bot.setPos(0.0F, 0.0F, 0.0F);
        frame_all.addChild(cap_bot);
        setRotationAngle(cap_bot, 0.0F, 0.0F, -3.1416F);
        cap_bot.texOffs(0, 0).addBox(-2.5F, -6.5F, -2.5F, 5.0F, 1.0F, 5.0F, 0.0F, false);
        cap_bot.texOffs(0, 6).addBox(-1.5F, -5.5F, -1.5F, 3.0F, 2.0F, 3.0F, 0.0F, false);

        frame_top = new ModelRenderer(this);
        frame_top.setPos(0.0F, 0.0F, 0.0F);
        frame_all.addChild(frame_top);


        frame_top1 = new ModelRenderer(this);
        frame_top1.setPos(0.0F, -2.5F, 0.0F);
        frame_top.addChild(frame_top1);
        setRotationAngle(frame_top1, 0.0F, -1.5708F, 0.0F);
        frame_top1.texOffs(10, 13).addBox(-4.5F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        frame_top1.texOffs(15, 0).addBox(-3.5F, -1.0F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_top1.texOffs(12, 8).addBox(-6.5F, 1.0F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_top1.texOffs(6, 13).addBox(-6.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_top1.texOffs(0, 11).addBox(-5.5F, -3.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);

        frame_top2 = new ModelRenderer(this);
        frame_top2.setPos(0.0F, -2.5F, 0.0F);
        frame_top.addChild(frame_top2);
        setRotationAngle(frame_top2, 0.0F, 3.1416F, 0.0F);
        frame_top2.texOffs(10, 13).addBox(-4.5F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        frame_top2.texOffs(15, 0).addBox(-3.5F, -1.0F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_top2.texOffs(12, 8).addBox(-6.5F, 1.0F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_top2.texOffs(6, 13).addBox(-6.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_top2.texOffs(0, 11).addBox(-5.5F, -3.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);

        frame_top3 = new ModelRenderer(this);
        frame_top3.setPos(0.0F, -2.5F, 0.0F);
        frame_top.addChild(frame_top3);
        setRotationAngle(frame_top3, 0.0F, 1.5708F, 0.0F);
        frame_top3.texOffs(10, 13).addBox(-4.5F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        frame_top3.texOffs(15, 0).addBox(-3.5F, -1.0F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_top3.texOffs(12, 8).addBox(-6.5F, 1.0F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_top3.texOffs(6, 13).addBox(-6.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_top3.texOffs(0, 11).addBox(-5.5F, -3.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);

        frame_top4 = new ModelRenderer(this);
        frame_top4.setPos(0.0F, -2.5F, 0.0F);
        frame_top.addChild(frame_top4);
        setRotationAngle(frame_top4, 0.0F, 0.0F, 0.0F);
        frame_top4.texOffs(10, 13).addBox(-4.5F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        frame_top4.texOffs(15, 0).addBox(-3.5F, -1.0F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_top4.texOffs(12, 8).addBox(-6.5F, 1.0F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_top4.texOffs(6, 13).addBox(-6.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_top4.texOffs(0, 11).addBox(-5.5F, -3.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);

        frame_bot = new ModelRenderer(this);
        frame_bot.setPos(0.0F, 0.0F, 0.0F);
        frame_all.addChild(frame_bot);
        setRotationAngle(frame_bot, 3.1416F, 0.0F, 0.0F);


        frame_bot1 = new ModelRenderer(this);
        frame_bot1.setPos(0.0F, -2.5F, 0.0F);
        frame_bot.addChild(frame_bot1);
        setRotationAngle(frame_bot1, 0.0F, 1.5708F, 0.0F);
        frame_bot1.texOffs(0, 13).addBox(-4.5F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        frame_bot1.texOffs(15, 15).addBox(-3.5F, -1.0F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_bot1.texOffs(10, 11).addBox(-6.5F, 1.0F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_bot1.texOffs(0, 0).addBox(-6.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_bot1.texOffs(9, 6).addBox(-5.5F, -3.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);

        frame_bot2 = new ModelRenderer(this);
        frame_bot2.setPos(0.0F, -2.5F, 0.0F);
        frame_bot.addChild(frame_bot2);
        setRotationAngle(frame_bot2, 0.0F, -3.1416F, 0.0F);
        frame_bot2.texOffs(0, 13).addBox(-4.5F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        frame_bot2.texOffs(15, 15).addBox(-3.5F, -1.0F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_bot2.texOffs(10, 11).addBox(-6.5F, 1.0F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_bot2.texOffs(0, 0).addBox(-6.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_bot2.texOffs(9, 6).addBox(-5.5F, -3.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);

        frame_bot3 = new ModelRenderer(this);
        frame_bot3.setPos(0.0F, -2.5F, 0.0F);
        frame_bot.addChild(frame_bot3);
        setRotationAngle(frame_bot3, 0.0F, -1.5708F, 0.0F);
        frame_bot3.texOffs(0, 13).addBox(-4.5F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        frame_bot3.texOffs(15, 15).addBox(-3.5F, -1.0F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_bot3.texOffs(10, 11).addBox(-6.5F, 1.0F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_bot3.texOffs(0, 0).addBox(-6.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_bot3.texOffs(9, 6).addBox(-5.5F, -3.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);

        frame_bot4 = new ModelRenderer(this);
        frame_bot4.setPos(0.0F, -2.5F, 0.0F);
        frame_bot.addChild(frame_bot4);
        setRotationAngle(frame_bot4, 0.0F, 0.0F, 0.0F);
        frame_bot4.texOffs(0, 13).addBox(-4.5F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        frame_bot4.texOffs(15, 15).addBox(-3.5F, -1.0F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        frame_bot4.texOffs(10, 11).addBox(-6.5F, 1.0F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        frame_bot4.texOffs(0, 0).addBox(-6.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        frame_bot4.texOffs(9, 6).addBox(-5.5F, -3.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        frame_all.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
