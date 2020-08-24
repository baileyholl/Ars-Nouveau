package com.hollingsworth.arsnouveau.client.renderer;

import com.hollingsworth.arsnouveau.api.util.MappingUtil;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.items.ItemsRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ScribeTableRenderer extends TileEntityRenderer<ScribesTile> {
    public ScribeTableRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Override
    public void render(ScribesTile tileEntityIn, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
//  ItemStack dirt = new ItemStack(Items.DIRT);
        //     System.out.println("rendering");
        double x = tileEntityIn.getPos().getX();
        double y = tileEntityIn.getPos().getY();
        double z = tileEntityIn.getPos().getZ();
        if(tileEntityIn.stack == null){
            return;
        }

        if (tileEntityIn.entity == null || !ItemStack.areItemStacksEqual(tileEntityIn.entity.getItem(), tileEntityIn.stack)) {
            tileEntityIn.entity = new ItemEntity(tileEntityIn.getWorld(), x, y, z, tileEntityIn.stack);
        }


        ItemEntity entityItem = tileEntityIn.entity;
        // entityItem.getSize()
        x = x + .5;
        y = y + 0.9;
        z = z +.5;
        renderPressedItem(tileEntityIn,entityItem.getItem().getItem(),matrixStack, iRenderTypeBuffer, i, i1);

    }
    public void renderPressedItem(ScribesTile tile, Item itemToRender, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int il){
        matrixStack.push();
//        matrixStack.translate((float)x , (float)y -0.7f, (float)z);

        Direction direction1 = Direction.byHorizontalIndex((1 + Direction.NORTH.getHorizontalIndex()) % 4);
//        matrixStack.translate(0.5D, 0.75D, 0.5D);
        matrixStack.translate(0.5D, .9D, 0.5D);
//        float lvt_12_1_ = -direction1.getHorizontalAngle();
//        matrixStack.rotate(Vector3f.YP.rotationDegrees(lvt_12_1_));
//        matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
        BlockState state = tile.getWorld().getBlockState(tile.getPos());
        float y = ((Direction)state.get(ScribesBlock.FACING)).rotateY().getHorizontalAngle();
        matrixStack.rotate(Vector3f.YP.rotationDegrees(-y + 90f));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(112.5f));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(180F));

//        matrixStack.rotate(Vector3f.YP.rotationDegrees(-22.5f));
//
//        matrixStack.rotate(Vector3f.ZP.rotationDegrees(45f));
        //       GlStateManager.rotatef(-direction1.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
//        RenderSystem.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
        //   GlStateManager.translatef(-0.3125F, -0.3125F, 0.0F);

        matrixStack.scale(0.6f, 0.6f, 0.6f);

        Minecraft.getInstance().getItemRenderer().renderItem(new ItemStack(itemToRender), ItemCameraTransforms.TransformType.FIXED, 150, il , matrixStack, iRenderTypeBuffer);
        matrixStack.pop();
    }


    public void renderFloatingItem(ScribesTile tileEntityIn, ItemEntity entityItem, double x, double y, double z, MatrixStack stack, IRenderTypeBuffer iRenderTypeBuffer){
        stack.push();

        RenderSystem.enableLighting();


//        Minecraft.getInstance().gameRenderer.getLightTexture().disableLightmap();


        //Prevent 'jump' in the bobbing
        //Bobbing is calculated as the age plus the yaw
        ObfuscationReflectionHelper.setPrivateValue(ItemEntity.class, entityItem, (int) (800f - tileEntityIn.frames), MappingUtil.getItemEntityAge());

        Minecraft.getInstance().getRenderManager().renderEntityStatic(entityItem, 0.5,1,0.5, entityItem.rotationYaw, 2.0f,stack, iRenderTypeBuffer,15728880);

        Minecraft.getInstance().getRenderManager().getRenderer(entityItem);
        RenderSystem.disableLighting();
        stack.pop();
        Minecraft.getInstance().gameRenderer.getLightTexture().enableLightmap();

    }
}
