package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityWhelp;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class WhelpRenderer extends MobRenderer<EntityWhelp, WhelpModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/kobold_texture.png");

    public WhelpRenderer(EntityRendererManager manager) {
        super(manager, new WhelpModel(), 0.2f);
    }

    @Override
    public void render(EntityWhelp p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int p_225623_6_) {
/*        matrixStack.push();
        matrixStack.translate(0, -0.5, 0);
        matrixStack.pop();*/
        super.render(p_225623_1_, p_225623_2_, p_225623_3_, matrixStack, iRenderTypeBuffer, p_225623_6_);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityWhelp entity) {
        return TEXTURE;
    }


}
