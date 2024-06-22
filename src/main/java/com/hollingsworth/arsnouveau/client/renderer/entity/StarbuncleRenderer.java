package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.CosmeticRenderUtil;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.client.registry.ShaderRegistry;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.mojang.blaze3d.vertex.BufferBuilder;
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
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtil;

public class StarbuncleRenderer extends GeoEntityRenderer<Starbuncle> {
    public static MultiBufferSource.BufferSource cosmeticBuffer = MultiBufferSource.immediate(new BufferBuilder(256));

    public StarbuncleRenderer(EntityRendererProvider.Context manager) {
        super(manager, new StarbuncleModel());
    }


    @Override
    public void renderFinal(PoseStack poseStack, Starbuncle animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void renderRecursively(PoseStack stack, Starbuncle animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderRecursively(stack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        if (bone.getName().equals("item")) {
            stack.pushPose();
            RenderUtil.translateToPivotPoint(stack, bone);
            stack.translate(0, -0.10, 0);
            stack.scale(0.75f, 0.75f, 0.75f);
            ItemStack itemstack = animatable.getHeldStack();
            if (animatable.dynamicBehavior != null) {
                itemstack = animatable.dynamicBehavior.getStackForRender();
            }
            if(!itemstack.isEmpty()) {
                Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, stack, bufferSource, animatable.level, (int) animatable.getOnPos().asLong());
            }
            stack.popPose();
        }
        if (animatable.getCosmeticItem().getItem() instanceof ICosmeticItem cosmetic && cosmetic.getBone().equals(bone.getName())) {
            CosmeticRenderUtil.renderCosmetic(bone, stack, cosmeticBuffer, animatable, packedLight);
            cosmeticBuffer.endBatch();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Starbuncle entity) {
        return entity.getTexture(entity);
    }

    @Override
    public RenderType getRenderType(Starbuncle animatable, ResourceLocation textureLocation, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
        // Jared's special shader, because adopter details aren't synced.
        if(animatable.getName().getString().equals("Splonk")) {
            return ShaderRegistry.blamed(textureLocation, true);
        }else if(animatable.getName().getString().equals("Bailey")){
            return ShaderRegistry.rainbowEntity(textureLocation, ArsNouveau.prefix( "textures/entity/starbuncle_mask.png"),true);
        }
        return RenderType.entityCutoutNoCull(textureLocation);
    }
}