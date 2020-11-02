package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.renderers.geo.GeoEntityRenderer;

public class CarbuncleRenderer  extends GeoEntityRenderer<EntityCarbuncle> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_orange.png");
    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_wild_orange.png");

    public CarbuncleRenderer(EntityRendererManager manager) {
        super(manager,new CarbuncleModel());
       // this.addLayer(new CarbuncleHeldItemLayer(this));
    }

    @Override
    protected void applyRotations(EntityCarbuncle entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public void render(EntityCarbuncle entity, float entityYaw, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int p_225623_6_) {
        super.render(entity, entityYaw, p_225623_3_, matrixStack, iRenderTypeBuffer, p_225623_6_);

//
//        if(entity != null && head != null){
//            //     entity.setHeadRotation(entity.rotationYaw * 0.017453292F, (int) (entity.rotationPitch * 0.017453292F));
//            //   System.out.println(entity.ro);
//            System.out.println(entity.rotationYaw);
//            head.setRotationX((entity.rotationPitch * 0.0055F));
//            head.setRotationY((entity.rotationYaw * 0.0055F));
//        }
    }


    @Override
    public ResourceLocation getEntityTexture(EntityCarbuncle entity) {
        return entity.isTamed() ? TEXTURE : WILD_TEXTURE;
    }




}
