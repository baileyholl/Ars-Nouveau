package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.api.util.MappingUtil;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ArcanePedestalRenderer extends TileEntityRenderer<ArcanePedestalTile> {

    public ArcanePedestalRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    public void renderFloatingItem(ArcanePedestalTile tileEntityIn, ItemEntity entityItem, double x, double y, double z, MatrixStack stack, IRenderTypeBuffer iRenderTypeBuffer){
        stack.push();

        RenderSystem.enableLighting();

        tileEntityIn.frames++;
//        Minecraft.getInstance().gameRenderer.getLightTexture().disableLightmap();

        entityItem.setRotationYawHead(tileEntityIn.frames);
        //Prevent 'jump' in the bobbing
        //Bobbing is calculated as the age plus the yaw
        ObfuscationReflectionHelper.setPrivateValue(ItemEntity.class, entityItem, (int) (800f - tileEntityIn.frames), MappingUtil.getItemEntityAge());

        Minecraft.getInstance().getRenderManager().renderEntityStatic(entityItem, 0.5,1,0.5, entityItem.rotationYaw, 2.0f,stack, iRenderTypeBuffer,15728880);

        Minecraft.getInstance().getRenderManager().getRenderer(entityItem);
        RenderSystem.disableLighting();
        stack.pop();
        Minecraft.getInstance().gameRenderer.getLightTexture().enableLightmap();

    }

    @Override
    public void render(ArcanePedestalTile tileEntityIn, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        //  ItemStack dirt = new ItemStack(Items.DIRT);
        //     System.out.println("rendering");
        double x = tileEntityIn.getPos().getX();
        double y = tileEntityIn.getPos().getY();
        double z = tileEntityIn.getPos().getZ();
        if(tileEntityIn.stack == null)
            return;

        if (tileEntityIn.entity == null || !ItemStack.areItemStacksEqual(tileEntityIn.entity.getItem(), tileEntityIn.stack)) {
            tileEntityIn.entity = new ItemEntity(tileEntityIn.getWorld(), x, y, z, tileEntityIn.stack);
        }


        ItemEntity entityItem = tileEntityIn.entity;
        // entityItem.getSize()
        x = x + .5;
        y = y + 0.9;
        z = z +.5;

        renderFloatingItem(tileEntityIn, entityItem, x, y , z, matrixStack, iRenderTypeBuffer);

    }
}
