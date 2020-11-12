package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.api.util.MappingUtil;
import com.hollingsworth.arsnouveau.common.block.tile.GlyphPressTile;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class GlyphPressRenderer extends TileEntityRenderer<GlyphPressTile> {

    public GlyphPressRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }



    public void renderFloatingItem(GlyphPressTile tileEntityIn, ItemEntity entityItem, double x, double y, double z, MatrixStack stack, IRenderTypeBuffer iRenderTypeBuffer){
        stack.push();
        tileEntityIn.frames++;
        entityItem.setRotationYawHead(tileEntityIn.frames);
        ObfuscationReflectionHelper.setPrivateValue(ItemEntity.class, entityItem, (int) (800f - tileEntityIn.frames), MappingUtil.getItemEntityAge());
        Minecraft.getInstance().getRenderManager().renderEntityStatic(entityItem, 0.5,1,0.5, entityItem.rotationYaw, 2.0f,stack, iRenderTypeBuffer,15728880);
        Minecraft.getInstance().getRenderManager().getRenderer(entityItem);
        stack.pop();
    }

    public void renderPressedItem(double x, double y, double z, Item itemToRender, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int il){
        matrixStack.push();
        Direction direction1 = Direction.byHorizontalIndex((1 + Direction.NORTH.getHorizontalIndex()) % 4);
        matrixStack.translate(0.5D, 0D, 0.5D);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(-direction1.getHorizontalAngle()));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
        matrixStack.translate(-0, -0D, -0.2D);
        matrixStack.scale(0.35f, 0.35f, 0.35F);
        Minecraft.getInstance().getItemRenderer().renderItem(new ItemStack(itemToRender), ItemCameraTransforms.TransformType.FIXED, 150, il , matrixStack, iRenderTypeBuffer);
        matrixStack.pop();
    }

    @Override
    public void render(GlyphPressTile tileEntityIn, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        //  ItemStack dirt = new ItemStack(Items.DIRT);
        //     System.out.println("rendering");
        double x = tileEntityIn.getPos().getX();
        double y = tileEntityIn.getPos().getY();
        double z = tileEntityIn.getPos().getZ();
        if(tileEntityIn.baseMaterial == null || tileEntityIn.baseMaterial.isEmpty()){
            return;
        }
        if (tileEntityIn.entity == null || !ItemStack.areItemStacksEqual(tileEntityIn.entity.getItem(), tileEntityIn.reagentItem)) {
            tileEntityIn.entity = new ItemEntity(tileEntityIn.getWorld(), x, y, z, tileEntityIn.reagentItem);
        }


        ItemEntity entityItem = tileEntityIn.entity;
        // entityItem.getSize()
        x = x + .5;
        y = y + 0.9;
        z = z +.5;

        if(tileEntityIn.counter == 20){

            renderFloatingItem(tileEntityIn, entityItem, x, y + .1, z, matrixStack, iRenderTypeBuffer);
        }
        if (tileEntityIn.counter < 5) {
            renderPressedItem(x, y, z, tileEntityIn.baseMaterial.getItem(), matrixStack, iRenderTypeBuffer,i,  i1);
        }else if(tileEntityIn.counter < 21){
            renderPressedItem(x, y, z, ItemsRegistry.blankGlyph, matrixStack, iRenderTypeBuffer,i,  i1);
        }else{
            renderPressedItem(x, y, z, tileEntityIn.baseMaterial.getItem(), matrixStack, iRenderTypeBuffer,i,  i1);
        }
    }
}
