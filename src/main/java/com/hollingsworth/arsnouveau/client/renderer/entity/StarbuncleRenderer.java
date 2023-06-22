package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.api.client.CosmeticRenderUtil;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

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
    public void preRender(PoseStack poseStack, Starbuncle animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.starbuncle = animatable;
        this.buffer = bufferSource;
        this.text = this.getTextureLocation(animatable);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void render(Starbuncle entity, float entityYaw, float p_225623_3_, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int p_225623_6_) {
        super.render(entity, entityYaw, p_225623_3_, matrixStack, iRenderTypeBuffer, p_225623_6_);
    }

    @Override
    public void renderRecursively(PoseStack stack, Starbuncle animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("item")) {
            stack.pushPose();
            RenderUtils.translateToPivotPoint(stack, bone);
            stack.translate(0, -0.10, 0);
            stack.scale(0.75f, 0.75f, 0.75f);
            ItemStack itemstack = starbuncle.getHeldStack();
            if (starbuncle.dynamicBehavior != null) {
                itemstack = starbuncle.dynamicBehavior.getStackForRender();
            }
            Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, stack, this.buffer, animatable.level, (int) starbuncle.getOnPos().asLong());
            stack.popPose();
            buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(text));
        }
        if (this.starbuncle.getCosmeticItem().getItem() instanceof ICosmeticItem cosmetic && cosmetic.getBone().equals(bone.getName())) {
            CosmeticRenderUtil.renderCosmetic(bone, stack, this.buffer, this.starbuncle, packedLight);
            buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(text));
        }
        super.renderRecursively(stack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ResourceLocation getTextureLocation(Starbuncle entity) {
        return entity.getTexture(entity);
    }

    @Override
    public RenderType getRenderType(Starbuncle animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}