package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.block.ArcanePedestal;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

public class ArcanePedestalRenderer implements BlockEntityRenderer<ArcanePedestalTile> {
    private final EntityRenderDispatcher entityRenderer;

    public ArcanePedestalRenderer(BlockEntityRendererProvider.Context pContext) {
        entityRenderer = pContext.getEntityRenderer();
    }

    @Override
    public void render(ArcanePedestalTile tileEntityIn, float pPartialTick, PoseStack matrixStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        if (tileEntityIn.getStack() == null || tileEntityIn.getStack().isEmpty()) return;
        if (!(tileEntityIn.getBlockState().getBlock() instanceof ArcanePedestal pedestal)) {
            return;
        }
        Vector3f offsetVec = pedestal.getItemOffset(tileEntityIn.getBlockState(), tileEntityIn.getBlockPos());
        float yOffset = offsetVec.y - tileEntityIn.getBlockPos().getY();
        float xOffset = offsetVec.x - tileEntityIn.getBlockPos().getX();
        float zOffset = offsetVec.z - tileEntityIn.getBlockPos().getZ();

        matrixStack.pushPose();
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
