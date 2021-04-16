package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

public class CarbuncleRenderer extends GeoEntityRenderer<EntityCarbuncle> {
    private static final ResourceLocation ORANGE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_orange.png");
    private static final ResourceLocation PURPLE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_purple.png");
    private static final ResourceLocation GREEN = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_green.png");
    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_wild_orange.png");

    public CarbuncleRenderer(EntityRendererManager manager) {
        super(manager,new CarbuncleModel());
        this.addLayer(new CarbuncleHeldItemLayer(this));
        //this.addLayer(new ModelLayerRenderer(this, new CarbuncleShadesModel()));
    }

    @Override
    protected void applyRotations(EntityCarbuncle entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public void render(EntityCarbuncle entity, float entityYaw, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int p_225623_6_) {
        super.render(entity, entityYaw, p_225623_3_, matrixStack, iRenderTypeBuffer, p_225623_6_);
    }


    public ResourceLocation getColor(EntityCarbuncle e){
        String color = e.getDataManager().get(EntityCarbuncle.COLOR).toLowerCase();

        if(color.isEmpty())
            return ORANGE;

        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_" + color +".png");
    }

    @Override
    public ResourceLocation getEntityTexture(EntityCarbuncle entity) {
        return entity.isTamed() ? getColor(entity) : WILD_TEXTURE;
    }

    @Override
    public RenderType getRenderType(EntityCarbuncle animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.getEntityCutoutNoCull(textureLocation);
    }
}