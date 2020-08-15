package com.hollingsworth.arsnouveau.client.renderer;

import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class EnchantingApparatusRenderer extends TileEntityRenderer<EnchantingApparatusTile> {

    public EnchantingApparatusRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }


    @Override
    public void render(EnchantingApparatusTile tileEntityIn, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int lightIn, int overlayIn) {
        double x = tileEntityIn.getPos().getX();
        double y = tileEntityIn.getPos().getY();
        double z = tileEntityIn.getPos().getZ();
        if(tileEntityIn.catalystItem == null)
            return;

        if (tileEntityIn.entity == null || !ItemStack.areItemStacksEqual(tileEntityIn.entity.getItem(), tileEntityIn.catalystItem)) {
            tileEntityIn.entity = new ItemEntity(tileEntityIn.getWorld(), x, y, z, tileEntityIn.catalystItem);
        }


        ItemEntity entityItem = tileEntityIn.entity;
        // entityItem.getSize()
        x = x + .5;
        y = y + 1.25;
        z = z +.5;

        matrixStack.push();
        RenderSystem.enableLighting();
        matrixStack.translate(0.5D, 0.6f, 0.5D);
        Direction direction1 = Direction.byHorizontalIndex((1 + Direction.NORTH.getHorizontalIndex()) % 4);
        //       GlStateManager.rotatef(-direction1.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
//        matrixStack.rotate(0.0F, 1.0F, 0.0F, 0.0F);
        //   GlStateManager.translatef(-0.3125F, -0.3125F, 0.0F);

        matrixStack.scale(0.35f, 0.35f, 0.35F);

        Minecraft.getInstance().getItemRenderer().renderItem(entityItem.getItem(), ItemCameraTransforms.TransformType.FIXED, 15728880, overlayIn, matrixStack, iRenderTypeBuffer);
        matrixStack.pop();
    }
}
