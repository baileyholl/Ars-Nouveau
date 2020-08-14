package com.hollingsworth.arsnouveau.client.renderer;

import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
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


    public void render(EnchantingApparatusTile tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
        //  ItemStack dirt = new ItemStack(Items.DIRT);
        //     System.out.println("rendering");

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

        renderFloatingItem(tileEntityIn, entityItem, x, y , z);


    }

    public void renderFloatingItem(EnchantingApparatusTile tileEntityIn, ItemEntity entityItem, double x, double y, double z){
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)x , (float)y -0.7f, (float)z);
        Direction direction1 = Direction.byHorizontalIndex((1 + Direction.NORTH.getHorizontalIndex()) % 4);
        //       GlStateManager.rotatef(-direction1.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(0.0F, 1.0F, 0.0F, 0.0F);
        //   GlStateManager.translatef(-0.3125F, -0.3125F, 0.0F);

        GlStateManager.scalef(0.35f, 0.35f, 0.35F);

//        Minecraft.getInstance().getItemRenderer().renderItem(entityItem.getItem(), ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.popMatrix();

    }

    @Override
    public void render(EnchantingApparatusTile enchantingApparatusTile, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {

    }
}
