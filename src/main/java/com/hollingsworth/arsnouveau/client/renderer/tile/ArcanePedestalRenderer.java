package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.block.ArcanePlatform;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ArcanePedestalRenderer implements BlockEntityRenderer<ArcanePedestalTile> {
    private final EntityRenderDispatcher entityRenderer;

    public ArcanePedestalRenderer(BlockEntityRendererProvider.Context pContext) {
        entityRenderer = pContext.getEntityRenderer();
    }

    @Override
    public void render(ArcanePedestalTile tileEntityIn, float pPartialTick, PoseStack matrixStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        if (tileEntityIn.getStack() == null || tileEntityIn.getStack().isEmpty()) return;

        float yOffset = 0.5f;
        float xOffset = 0.5f;
        float zOffset = 0.5f;

        matrixStack.pushPose();

        if (tileEntityIn.getBlockState().getBlock() instanceof ArcanePlatform) {
            switch (tileEntityIn.getBlockState().getValue(BlockStateProperties.FACING)) {
                case DOWN -> yOffset = 0.4f;
                case UP -> yOffset = 0.6f;
                case NORTH -> zOffset = 0.45f;
                case SOUTH -> zOffset = 0.55f;
                case WEST -> xOffset = 0.45f;
                case EAST -> xOffset = 0.55f;
            }
        } else {
            switch (tileEntityIn.getBlockState().getValue(BlockStateProperties.FACING)) {
                case DOWN -> yOffset = -0.1f;
                case UP -> yOffset = 1.1f;
                case NORTH -> zOffset = -0.05f;
                case SOUTH -> zOffset = 1.05f;
                case WEST -> xOffset = -0.05f;
                case EAST -> xOffset = 1.05f;
            }
        }
        matrixStack.translate(xOffset, yOffset, zOffset);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.mulPose(Axis.YP.rotationDegrees((pPartialTick + (float) ClientInfo.ticksInGame) * 3f));
        Minecraft.getInstance().getItemRenderer().renderStatic(tileEntityIn.getStack(),
                ItemDisplayContext.FIXED,
                pPackedLight,
                pPackedOverlay,
                matrixStack,
                pBufferSource,
                tileEntityIn.getLevel(),
                (int) tileEntityIn.getBlockPos().asLong());

        matrixStack.popPose();
    }
}
