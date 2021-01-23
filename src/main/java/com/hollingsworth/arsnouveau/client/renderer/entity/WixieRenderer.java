package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.geo.exception.GeoModelException;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class WixieRenderer extends GeoEntityRenderer<EntityWixie> {
    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/wixie.png");

    protected WixieRenderer(EntityRendererManager renderManager) {
        super(renderManager, new WixieModel());
    }

    @Override
    public void render(EntityWixie entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        try {
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        }catch (GeoModelException e){
            System.out.println("Missing model detected, restart client.");
            e.printStackTrace();
        }
    }

    @Override
    public ResourceLocation getEntityTexture(EntityWixie entity) {
        return WILD_TEXTURE;
    }
}
