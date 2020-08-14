package com.hollingsworth.arsnouveau.client.renderer;

import com.hollingsworth.arsnouveau.api.util.MappingUtil;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.block.tile.GlyphPressTile;
import com.hollingsworth.arsnouveau.common.items.ItemsRegistry;
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

public class ArcanePedestalRenderer extends TileEntityRenderer<ArcanePedestalTile> {

    public ArcanePedestalRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }


    public void render(ArcanePedestalTile tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
        //  ItemStack dirt = new ItemStack(Items.DIRT);
        //     System.out.println("rendering");

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

//        renderFloatingItem(tileEntityIn, entityItem, x, y , z, );


    }

    public void renderFloatingItem(ArcanePedestalTile tileEntityIn, ItemEntity entityItem, double x, double y, double z, MatrixStack stack){
        GlStateManager.pushMatrix();
        GlStateManager.enableLighting();

        tileEntityIn.frames++;
        Minecraft.getInstance().gameRenderer.getLightTexture().disableLightmap();

        entityItem.setRotationYawHead(tileEntityIn.frames);
        //Prevent 'jump' in the bobbing
        //Bobbing is calculated as the age plus the yaw
        ObfuscationReflectionHelper.setPrivateValue(ItemEntity.class, entityItem, (int) (800f - tileEntityIn.frames), MappingUtil.getItemEntityAge());
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        Minecraft.getInstance().getRenderManager().renderEntityStatic(entityItem, x, y, z,entityItem.rotationYaw, 0.0f,stack, irendertypebuffer$impl,10000);
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        Minecraft.getInstance().gameRenderer.getLightTexture().enableLightmap();

    }

    @Override
    public void render(ArcanePedestalTile arcanePedestalTile, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {

    }
}
