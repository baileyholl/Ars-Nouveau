package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nullable;

public class CarbuncleRenderer extends GeoEntityRenderer<EntityCarbuncle> {
    private static final ResourceLocation ORANGE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_orange.png");
    private static final ResourceLocation PURPLE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_purple.png");
    private static final ResourceLocation GREEN = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_green.png");
    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_wild_orange.png");

    public CarbuncleRenderer(EntityRendererManager manager) {
        super(manager,new CarbuncleModel());
//        this.addLayer(new CarbuncleHeldItemLayer(this));
//        this.addLayer(new ModelLayerRenderer(this, new CarbuncleShadesModel(this.getGeoModelProvider())));
    }
    EntityCarbuncle carbuncle;
    IRenderTypeBuffer buffer;
    ResourceLocation text;
    @Override
    protected void applyRotations(EntityCarbuncle entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public void renderEarly(EntityCarbuncle animatable, MatrixStack stackIn, float ticks, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        this.carbuncle = animatable;
        this.buffer = renderTypeBuffer;
        this.text = this.getTextureLocation(animatable);
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
    }

    @Override
    public void render(EntityCarbuncle entity, float entityYaw, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int p_225623_6_) {
        super.render(entity, entityYaw, p_225623_3_, matrixStack, iRenderTypeBuffer, p_225623_6_);
    }

    @Override
    public void renderRecursively(GeoBone bone, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if(bone.getName().equals("item")){
//            System.out.println(bone);
            stack.pushPose();
            RenderUtils.moveToPivot(bone, stack);
            stack.translate(0, -0.10, 0);
            stack.scale(0.75f, 0.75f, 0.75f);
            ItemStack itemstack = carbuncle.getHeldStack();
            Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemCameraTransforms.TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, stack, this.buffer);
            stack.popPose();
            bufferIn = buffer.getBuffer(RenderType.entityCutoutNoCull(text));

        }

        super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    public ResourceLocation getColor(EntityCarbuncle e){
        String color = e.getEntityData().get(EntityCarbuncle.COLOR).toLowerCase();

        if(color.isEmpty())
            return ORANGE;

        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_" + color +".png");
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCarbuncle entity) {
        return entity.isTamed() ? getColor(entity) : WILD_TEXTURE;
    }

    @Override
    public RenderType getRenderType(EntityCarbuncle animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityCutoutNoCull(textureLocation);
    }
}