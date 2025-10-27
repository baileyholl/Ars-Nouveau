package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.block.tile.ItemDetectorTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;

public class ItemDetectorRenderer implements BlockEntityRenderer<ItemDetectorTile> {
    private final EntityRenderDispatcher entityRenderer;

    public ItemDetectorRenderer(BlockEntityRendererProvider.Context pContext) {
        entityRenderer = pContext.getEntityRenderer();
    }

    @Override
    public void render(ItemDetectorTile tileEntityIn, float pPartialTick, PoseStack matrixStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if (tileEntityIn.filterStack == null || tileEntityIn.filterStack.isEmpty())
            return;

        float yOffset = 0.5f;
        float xOffset = 0.5f;
        float zOffset = 0.5f;
        float ticks = (pPartialTick + (float) ClientInfo.ticksInGame);
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, zOffset);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.mulPose(Axis.YP.rotationDegrees(ticks * 2f));
        Minecraft.getInstance().getItemRenderer().renderStatic(tileEntityIn.filterStack, ItemDisplayContext.FIXED, pPackedLight, pPackedOverlay, matrixStack, pBufferSource, tileEntityIn.getLevel(), (int) tileEntityIn.getBlockPos().asLong());

        matrixStack.popPose();
    }
}
