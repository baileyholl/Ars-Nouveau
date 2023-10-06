package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.CosmeticRenderUtil;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.client.ShaderRegistry;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nullable;

public class StarbuncleRenderer extends GeoEntityRenderer<Starbuncle> {

    public StarbuncleRenderer(EntityRendererProvider.Context manager) {
        super(manager, new StarbuncleModel());
    }

    Starbuncle starbuncle;
    MultiBufferSource buffer;
    ResourceLocation text;

    @Override
    protected void applyRotations(Starbuncle entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public void renderEarly(Starbuncle animatable, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        this.starbuncle = animatable;
        this.buffer = renderTypeBuffer;
        this.text = this.getTextureLocation(animatable);
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
    }

    @Override
    public void render(Starbuncle entity, float entityYaw, float p_225623_3_, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int p_225623_6_) {
        super.render(entity, entityYaw, p_225623_3_, matrixStack, iRenderTypeBuffer, p_225623_6_);
    }

    @Override
    public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("item")) {
            stack.pushPose();
            RenderUtils.translateToPivotPoint(stack, bone);
            stack.translate(0, -0.10, 0);
            stack.scale(0.75f, 0.75f, 0.75f);
            ItemStack itemstack = starbuncle.getHeldStack();
            if (starbuncle.dynamicBehavior != null) {
                itemstack = starbuncle.dynamicBehavior.getStackForRender();
            }
            Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemTransforms.TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, stack, this.buffer, (int) starbuncle.getOnPos().asLong());
            stack.popPose();
            bufferIn = buffer.getBuffer(RenderType.entityCutoutNoCull(text));
        }
        if (this.starbuncle.getCosmeticItem().getItem() instanceof ICosmeticItem cosmetic && cosmetic.getBone().equals(bone.getName())) {
            CosmeticRenderUtil.renderCosmetic(bone, stack, this.buffer, this.starbuncle, packedLightIn);
            bufferIn = buffer.getBuffer(RenderType.entityCutoutNoCull(text));
        }
        super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public ResourceLocation getTextureLocation(Starbuncle entity) {
        return entity.getTexture(entity);
    }

    @Override
    public RenderType getRenderType(Starbuncle animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        // Jared's special shader, because adopter details aren't synced.
        if(animatable.getName().getString().equals("Splonk")) {
            return ShaderRegistry.blamed(textureLocation, true);
        }else if(animatable.getName().getString().equals("Bailey")){
            return ShaderRegistry.rainbowEntity(textureLocation, new ResourceLocation(ArsNouveau.MODID, "textures/entity/starbuncle_mask.png"),true);
        }
        return RenderType.entityCutoutNoCull(textureLocation);
    }
}