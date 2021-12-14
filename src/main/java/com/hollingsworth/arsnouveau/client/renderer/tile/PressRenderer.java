package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.GlyphPressTile;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class PressRenderer extends GeoBlockRenderer<GlyphPressTile> {

    public PressRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, new PressModel());
    }

    public void renderFloatingItem(GlyphPressTile tileEntityIn, ItemEntity entityItem, double x, double y, double z, PoseStack stack, MultiBufferSource iRenderTypeBuffer){
        stack.pushPose();
        tileEntityIn.frames++;
        entityItem.setYHeadRot(tileEntityIn.frames);
        Minecraft.getInstance().getEntityRenderDispatcher().render(entityItem, 0.5,1,0.5, entityItem.yRot, 2.0f,stack, iRenderTypeBuffer,15728880);
        Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entityItem);
        stack.popPose();
    }

    public void renderPressedItem(double x, double y, double z, Item itemToRender, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int i, int il){
        matrixStack.pushPose();
        Direction direction1 = Direction.from2DDataValue((1 + Direction.NORTH.get2DDataValue()) % 4);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-direction1.toYRot()));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        matrixStack.translate(0, 0D, -0.2d);
        matrixStack.scale(0.35f, 0.35f, 0.35F);
        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(itemToRender), ItemTransforms.TransformType.NONE, 150, il , matrixStack, iRenderTypeBuffer, 1);
        matrixStack.popPose();
    }

    @Override
    public void renderEarly(GlyphPressTile tileEntityIn, PoseStack matrixStack, float ticks, MultiBufferSource iRenderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        double x = tileEntityIn.getBlockPos().getX();
        double y = tileEntityIn.getBlockPos().getY();
        double z = tileEntityIn.getBlockPos().getZ();
        if(tileEntityIn.baseMaterial == null || tileEntityIn.baseMaterial.isEmpty()){
            return;
        }
        if (tileEntityIn.entity == null || !ItemStack.matches(tileEntityIn.entity.getItem(), tileEntityIn.reagentItem)) {
            tileEntityIn.entity = new ItemEntity(tileEntityIn.getLevel(), x, y, z, tileEntityIn.reagentItem);
        }

        x = x + .5;
        y = y + 0.9;
        z = z +.5;

        if (tileEntityIn.counter <= 40) {
            renderPressedItem(x, y, z, tileEntityIn.baseMaterial.getItem(), matrixStack, iRenderTypeBuffer,packedLightIn,  packedOverlayIn);
        }else if(tileEntityIn.counter <= 110){
            renderPressedItem(x, y, z, ItemsRegistry.BLANK_GLYPH, matrixStack, iRenderTypeBuffer,packedLightIn,  packedOverlayIn);
        }else{
            renderPressedItem(x, y, z, tileEntityIn.baseMaterial.getItem(), matrixStack, iRenderTypeBuffer,packedLightIn,  packedOverlayIn);
        }

        if(tileEntityIn.counter > 70 && tileEntityIn.counter < 120){
            BlockPos pos = tileEntityIn.getBlockPos();
            Level world = tileEntityIn.getLevel();
            if(world.getGameTime() % 3 != 0)
                return;
            for (int i = 0; i < 1; i++) {
                double posX = pos.getX();
                double posY = pos.getY();
                double posZ = pos.getZ();

                double randX = world.random.nextFloat() > 0.5 ? world.random.nextFloat() : -world.random.nextFloat();
                double randZ = world.random.nextFloat() > 0.5 ? world.random.nextFloat() : -world.random.nextFloat();

                double d0 = posX + 0.5 + randX * 0.2;
                double d1 = posY + 0.4;
                double d2 = posZ + 0.5 + randZ * 0.2;
                double spdX = world.random.nextFloat() > 0.5 ? world.random.nextFloat() : -world.random.nextFloat();
                double spdZ = world.random.nextFloat() > 0.5 ? world.random.nextFloat() : -world.random.nextFloat();

                world.addParticle(ParticleTypes.ENCHANTED_HIT, d0, d1, d2,  spdX * 0.05, 0.0,  spdZ * 0.05);
            }
        }
    }

    public static GenericItemRenderer getISTER(){
        return new GenericItemRenderer(new PressModel());
    }
}
