package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ArcanePedestalRenderer implements BlockEntityRenderer<ArcanePedestalTile> {
    private final EntityRenderDispatcher entityRenderer;
    public ArcanePedestalRenderer(BlockEntityRendererProvider.Context pContext) {
        entityRenderer = pContext.getEntityRenderer();
    }

    @Override
    public void render(ArcanePedestalTile tileEntityIn, float pPartialTick, PoseStack matrixStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        double x = tileEntityIn.getBlockPos().getX();
        double y = tileEntityIn.getBlockPos().getY();
        double z = tileEntityIn.getBlockPos().getZ();

        if (tileEntityIn.getStack() == null || tileEntityIn.getStack().isEmpty())
            return;

        if (tileEntityIn.renderEntity == null || !ItemStack.matches(tileEntityIn.renderEntity.getItem(), tileEntityIn.getStack())) {
            tileEntityIn.renderEntity = new ItemEntity(tileEntityIn.getLevel(), x, y, z, tileEntityIn.getStack());
        }

        ItemEntity entityItem = tileEntityIn.renderEntity;
        matrixStack.pushPose();
        tileEntityIn.frames += 1.5f * Minecraft.getInstance().getDeltaFrameTime();
        entityItem.setYHeadRot(tileEntityIn.frames);
        entityItem.age = (int) tileEntityIn.frames;
        if(tileEntityIn.getBlockState().hasProperty(BlockStateProperties.FACING)){
            float yOffset = 0.5f;
            float xOffset = 0.5f;
            float zOffset = 0.5f;
            Direction facing = tileEntityIn.getBlockState().getValue(BlockStateProperties.FACING);
            if(facing == Direction.DOWN){
                yOffset = 0.4f;
            }else if(facing == Direction.WEST){
                xOffset = 0.45f;
            }else if(facing == Direction.EAST){
                xOffset = 0.55f;
            }else if(facing == Direction.SOUTH){
                zOffset = 0.55f;
            }else if(facing == Direction.NORTH){
                zOffset = 0.45f;
            }else if(facing == Direction.UP){
                entityRenderer.render(entityItem, xOffset, yOffset, zOffset, entityItem.yRot, 2.0f, matrixStack, pBufferSource, pPackedLight);
                matrixStack.popPose();
                return;
            }
            float ticks = (pPartialTick + (float) ClientInfo.ticksInGame);
            matrixStack.translate(xOffset, yOffset, zOffset);
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            matrixStack.mulPose( Vector3f.YP.rotationDegrees(ticks * 3f));
            Minecraft.getInstance().getItemRenderer().renderStatic(entityItem.getItem(), ItemTransforms.TransformType.FIXED, pPackedLight, pPackedOverlay, matrixStack, pBufferSource, (int) tileEntityIn.getBlockPos().asLong());
        }else {
            entityRenderer.render(entityItem, 0.5, 1, 0.5,
                    entityItem.yRot, 2.0f, matrixStack, pBufferSource, pPackedLight);
        }
        matrixStack.popPose();
    }
}
