package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class CarbuncleRenderer  extends MobRenderer<EntityCarbuncle, CarbuncleModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_orange.png");
    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_wild_orange.png");

    public CarbuncleRenderer(EntityRendererManager manager) {
        super(manager, new CarbuncleModel(), 0.2f);
        this.addLayer(new CarbuncleHeldItemLayer(this));
    }

    @Override
    public void render(EntityCarbuncle entityCarbuncle, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int p_225623_6_) {
        super.render(entityCarbuncle, p_225623_2_, p_225623_3_, matrixStack, iRenderTypeBuffer, p_225623_6_);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityCarbuncle entity) {
        return entity.isTamed() ? TEXTURE : WILD_TEXTURE;
    }


}
