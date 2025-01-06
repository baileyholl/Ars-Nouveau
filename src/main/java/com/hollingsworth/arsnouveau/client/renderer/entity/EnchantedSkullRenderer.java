package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.EnchantedSkull;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class EnchantedSkullRenderer extends EnchantedFallingBlockRenderer<EnchantedSkull> {
    public EnchantedSkullRenderer(EntityRendererProvider.Context p_174112_) {
        super(p_174112_);
    }

    @Override
    public void render(EnchantedSkull pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();
        renderSkull(pEntity.level, pEntity.getStack(), pMatrixStack, pBuffer, pPackedLight);

        pMatrixStack.popPose();
    }

    public static void renderSkull(Level level, ItemStack stack, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack,
                ItemDisplayContext.HEAD,
                pPackedLight,
                pPackedLight,
                pMatrixStack,
                pBuffer,
                level,
                0);
    }
}
