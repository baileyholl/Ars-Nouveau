package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.EnchantedSkull;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.Level;

// 1.21.11: render(T, float, float, PoseStack, MultiBufferSource, int) removed from EntityRenderer.
// TODO: Port skull rendering to submit() pipeline using custom render state that captures getStack().
public class EnchantedSkullRenderer extends EnchantedFallingBlockRenderer<EnchantedSkull> {
    public EnchantedSkullRenderer(EntityRendererProvider.Context p_174112_) {
        super(p_174112_);
    }

    public static void renderSkull(Level level, ItemStack stack, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        // TODO: 1.21.11 - ItemRenderer.renderStatic() completely removed.
        // Rendering now requires ItemModelResolver + ItemStackRenderState.submit() pipeline.
        // Skull rendering is broken until fully ported to the new item render state pipeline.
        // See: Minecraft.getInstance().getItemModelResolver() for the new entry point.
    }
}
