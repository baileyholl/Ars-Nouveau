package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.common.entity.EntityWhelp;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class CarbuncleRenderer  extends MobRenderer<EntityCarbuncle, CarbuncleModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_orange.png");

    public CarbuncleRenderer(EntityRendererManager manager) {
        super(manager, new CarbuncleModel(), 0.2f);
    }

    @Override
    public void render(EntityCarbuncle p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int p_225623_6_) {
/*        matrixStack.push();
        matrixStack.translate(0, -0.5, 0);
        matrixStack.pop();*/
        super.render(p_225623_1_, p_225623_2_, p_225623_3_, matrixStack, iRenderTypeBuffer, p_225623_6_);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityCarbuncle entity) {
        return TEXTURE;
    }


}
