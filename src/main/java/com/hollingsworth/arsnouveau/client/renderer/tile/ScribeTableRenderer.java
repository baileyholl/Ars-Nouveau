package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

public class ScribeTableRenderer extends TileEntityRenderer<ScribesTile> {
    public ScribeTableRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Override
    public void render(ScribesTile tileEntityIn, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
//  ItemStack dirt = new ItemStack(Items.DIRT);
        //     System.out.println("rendering");
        try {
            double x = tileEntityIn.getBlockPos().getX();
            double y = tileEntityIn.getBlockPos().getY();
            double z = tileEntityIn.getBlockPos().getZ();
            if (tileEntityIn.stack == null) {
                return;
            }

            if (tileEntityIn.entity == null || !ItemStack.matches(tileEntityIn.entity.getItem(), tileEntityIn.stack)) {
                tileEntityIn.entity = new ItemEntity(tileEntityIn.getLevel(), x, y, z, tileEntityIn.stack);
            }


            ItemEntity entityItem = tileEntityIn.entity;
            renderPressedItem(tileEntityIn, entityItem.getItem().getItem(), matrixStack, iRenderTypeBuffer, i, i1);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Mercy for optifine users.");
        }
    }
    public void renderPressedItem(ScribesTile tile, Item itemToRender, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int il){
        matrixStack.pushPose();
        matrixStack.translate(0.5D, .9D, 0.5D);
        BlockState state = tile.getLevel().getBlockState(tile.getBlockPos());
        if(!(state.getBlock() instanceof ScribesBlock))
            return;
        float y = ((Direction)state.getValue(ScribesBlock.FACING)).getClockWise().toYRot();
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-y + 90f));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(112.5f));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180F));

        matrixStack.scale(0.6f, 0.6f, 0.6f);

        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(itemToRender), ItemCameraTransforms.TransformType.FIXED, i, il, matrixStack, iRenderTypeBuffer);
        matrixStack.popPose();
    }

}
