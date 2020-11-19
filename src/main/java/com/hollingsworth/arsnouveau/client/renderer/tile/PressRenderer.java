package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.api.util.MappingUtil;
import com.hollingsworth.arsnouveau.common.block.tile.GlyphPressTile;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class PressRenderer extends GeoBlockRenderer<GlyphPressTile> {

    public PressRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn, new PressModel());
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
        matrixStack.rotate(Vector3f.YP.rotationDegrees(-direction1.getHorizontalAngle()));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
        matrixStack.translate(0, 0D, -0.2d);
        matrixStack.scale(0.35f, 0.35f, 0.35F);
        Minecraft.getInstance().getItemRenderer().renderItem(new ItemStack(itemToRender), ItemCameraTransforms.TransformType.NONE, 150, il , matrixStack, iRenderTypeBuffer);
        matrixStack.pop();
    }

    @Override
    public void renderEarly(GlyphPressTile tileEntityIn, MatrixStack matrixStack, float ticks, IRenderTypeBuffer iRenderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        double x = tileEntityIn.getPos().getX();
        double y = tileEntityIn.getPos().getY();
        double z = tileEntityIn.getPos().getZ();
        if(tileEntityIn.baseMaterial == null || tileEntityIn.baseMaterial.isEmpty()){
            return;
        }
        if (tileEntityIn.entity == null || !ItemStack.areItemStacksEqual(tileEntityIn.entity.getItem(), tileEntityIn.reagentItem)) {
            tileEntityIn.entity = new ItemEntity(tileEntityIn.getWorld(), x, y, z, tileEntityIn.reagentItem);
        }

        x = x + .5;
        y = y + 0.9;
        z = z +.5;

        if (tileEntityIn.counter <= 40) {
            renderPressedItem(x, y, z, tileEntityIn.baseMaterial.getItem(), matrixStack, iRenderTypeBuffer,packedLightIn,  packedOverlayIn);
        }else if(tileEntityIn.counter <= 110){
            renderPressedItem(x, y, z, ItemsRegistry.blankGlyph, matrixStack, iRenderTypeBuffer,packedLightIn,  packedOverlayIn);
        }else{
            renderPressedItem(x, y, z, tileEntityIn.baseMaterial.getItem(), matrixStack, iRenderTypeBuffer,packedLightIn,  packedOverlayIn);
        }

        if(tileEntityIn.counter > 70 && tileEntityIn.counter < 120){
            BlockPos pos = tileEntityIn.getPos();
            World world = tileEntityIn.getWorld();
            if(world.getGameTime() % 3 != 0)
                return;
            for (int i = 0; i < 1; i++) {
                double posX = pos.getX();
                double posY = pos.getY();
                double posZ = pos.getZ();

                double randX = world.rand.nextFloat() > 0.5 ? world.rand.nextFloat() : -world.rand.nextFloat();
                double randZ = world.rand.nextFloat() > 0.5 ? world.rand.nextFloat() : -world.rand.nextFloat();

                double d0 = posX + 0.5 + randX * 0.2;
                double d1 = posY + 0.4;
                double d2 = posZ + 0.5 + randZ * 0.2;
                double spdX = world.rand.nextFloat() > 0.5 ? world.rand.nextFloat() : -world.rand.nextFloat();
                double spdZ = world.rand.nextFloat() > 0.5 ? world.rand.nextFloat() : -world.rand.nextFloat();

                world.addParticle(ParticleTypes.ENCHANTED_HIT, d0, d1, d2,  spdX * 0.05, 0.0,  spdZ * 0.05);
            }
        }
    }
}
