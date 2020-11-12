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
//        Minecraft.getInstance().gameRenderer.getLightTexture().disableLightmap();

        entityItem.setRotationYawHead(tileEntityIn.frames);
        //Prevent 'jump' in the bobbing
        //Bobbing is calculated as the age plus the yaw
        ObfuscationReflectionHelper.setPrivateValue(ItemEntity.class, entityItem, (int) (800f - tileEntityIn.frames), MappingUtil.getItemEntityAge());

        Minecraft.getInstance().getRenderManager().renderEntityStatic(entityItem, 0.5,1,0.5, entityItem.rotationYaw, 2.0f,stack, iRenderTypeBuffer,15728880);

        Minecraft.getInstance().getRenderManager().getRenderer(entityItem);
        stack.pop();
    }

    public void renderPressedItem(double x, double y, double z, Item itemToRender, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int il){
        matrixStack.push();
//        matrixStack.translate((float)x , (float)y -0.7f, (float)z);

        Direction direction1 = Direction.byHorizontalIndex((1 + Direction.NORTH.getHorizontalIndex()) % 4);
        matrixStack.translate(0.5D, 0D, 0.5D);
        float lvt_12_1_ = -direction1.getHorizontalAngle();
        matrixStack.rotate(Vector3f.YP.rotationDegrees(lvt_12_1_));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
        //       GlStateManager.rotatef(-direction1.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
//        RenderSystem.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
        //   GlStateManager.translatef(-0.3125F, -0.3125F, 0.0F);
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
            //   System.out.println(tileEntityIn.itemStack);
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

//    private static final ResourceLocation MAP_BACKGROUND_TEXTURES = new ResourceLocation("textures/map/map_background.png");
//    private void renderItem(ItemStack itemstack) {
//
//        if (!itemstack.isEmpty()) {
//            GlStateManager.pushMatrix();
//       //   MapData mapdata = FilledMapItem.getMapData(itemstack, itemFrame.world);
//            int i =  1;
//            GlStateManager.rotatef((float)i * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
//     //       if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderItemInFrameEvent(itemFrame, this))) {
//                if (false) {
//                    GlStateManager.disableLighting();
//                    Minecraft.getInstance().getRenderManager().textureManager.bindTexture(MAP_BACKGROUND_TEXTURES);
//                    GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
//                    float f = 0.0078125F;
//                    GlStateManager.scalef(0.0078125F, 0.0078125F, 0.0078125F);
//                    GlStateManager.translatef(-64.0F, -64.0F, 0.0F);
//                    GlStateManager.translatef(0.0F, 0.0F, -1.0F);
////                    if (mapdata != null) {
////                        Minecraft.getInstance().gameRenderer.getMapItemRenderer().renderMap(mapdata, true);
////                    }
//                } else {
//                    GlStateManager.scalef(0.5F, 0.5F, 0.5F);
//                    Minecraft.getInstance().getItemRenderer().renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED);
//                }
//            }
//
//            GlStateManager.popMatrix();
//
//
//    }
//
//    /**
//     * Render the mob inside the mob spawner.
//     */
//    public static void renderMob(World world, double posX, double posY, double posZ, float partialTicks) {
//        GlStateManager.pushMatrix();
//        GlStateManager.translatef((float)posX + 0.5F, (float)posY, (float)posZ + 0.5F);
//
//        CreeperEntity entity = new CreeperEntity(EntityType.CREEPER,world);
//        if (entity != null) {
//            float f = 0.53125F;
//            float f1 = Math.max(entity.getWidth(), entity.getHeight());
//            if ((double)f1 > 1.0D) {
//                f /= f1;
//            }
//
//            GlStateManager.translatef(0.0F, 0.4F, 0.0F);
//            GlStateManager.rotatef((float)MathHelper.lerp((double)partialTicks,0,0) * 10.0F, 0.0F, 1.0F, 0.0F);
//            GlStateManager.translatef(0.0F, -0.2F, 0.0F);
//            GlStateManager.rotatef(-30.0F, 1.0F, 0.0F, 0.0F);
//            GlStateManager.scalef(f, f, f);
//            entity.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
//            Minecraft.getInstance().getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, false);
//        }
//        GlStateManager.popMatrix();
//    }

}
