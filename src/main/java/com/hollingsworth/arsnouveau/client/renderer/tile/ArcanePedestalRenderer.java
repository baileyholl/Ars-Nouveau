package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class ArcanePedestalRenderer implements BlockEntityRenderer<ArcanePedestalTile> {

    public ArcanePedestalRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {

    }

    public void renderFloatingItem(ArcanePedestalTile tileEntityIn, ItemEntity entityItem, double x, double y, double z, PoseStack stack, MultiBufferSource iRenderTypeBuffer){
        stack.pushPose();
        tileEntityIn.frames += 1.5f * Minecraft.getInstance().getDeltaFrameTime();
        entityItem.setYHeadRot(tileEntityIn.frames);
        entityItem.age = (int) tileEntityIn.frames;
        Minecraft.getInstance().getEntityRenderDispatcher().render(entityItem, 0.5,1,0.5, entityItem.yRot, 2.0f,stack, iRenderTypeBuffer,15728880);

        stack.popPose();
    }

    @Override
    public void render(ArcanePedestalTile tileEntityIn, float v, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int i, int i1) {
        double x = tileEntityIn.getBlockPos().getX();
        double y = tileEntityIn.getBlockPos().getY();
        double z = tileEntityIn.getBlockPos().getZ();

        if(tileEntityIn.getStack() == null)
            return;

        if (tileEntityIn.entity == null || !ItemStack.matches(tileEntityIn.entity.getItem(), tileEntityIn.getStack())) {
            tileEntityIn.entity = new ItemEntity(tileEntityIn.getLevel(), x, y, z, tileEntityIn.getStack());
        }

        ItemEntity entityItem = tileEntityIn.entity;
        matrixStack.pushPose();
        tileEntityIn.frames += 1.5f * Minecraft.getInstance().getDeltaFrameTime();
        entityItem.setYHeadRot(tileEntityIn.frames);
        entityItem.age = (int) tileEntityIn.frames;
        Minecraft.getInstance().getEntityRenderDispatcher().render(entityItem, 0.5,1,0.5, entityItem.yRot, 2.0f,matrixStack, iRenderTypeBuffer,i);

        matrixStack.popPose();
    }
}
