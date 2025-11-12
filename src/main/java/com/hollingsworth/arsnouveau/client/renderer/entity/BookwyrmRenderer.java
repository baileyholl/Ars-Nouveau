package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
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
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.RenderUtil;


public class BookwyrmRenderer extends DecoratableEntityRenderer<EntityBookwyrm> {

    public static ResourceLocation BLUE = ArsNouveau.prefix("textures/entity/book_wyrm_blue.png");

    public BookwyrmRenderer(EntityRendererProvider.Context manager) {
        super(manager, new BookwyrmModel<>());
        scaleWidth = 0.6f;
        scaleHeight = 0.6f;
    }

    @Override
    public void renderRecursively(PoseStack stack, EntityBookwyrm animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferIn, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        super.renderRecursively(stack, animatable, bone, renderType, bufferIn, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
        if (bone.getName().equals("item")) {
            stack.pushPose();
            RenderUtil.translateToPivotPoint(stack, bone);
            stack.translate(0, -0.10, 0);
            stack.scale(0.75f, 0.75f, 0.75f);
            ItemStack itemstack = animatable.getHeldStack();
            Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, stack, bufferIn, animatable.level, (int) animatable.getOnPos().asLong());
            stack.popPose();
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(EntityBookwyrm animatable) {
        return animatable.getTexture();
    }
}