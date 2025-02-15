package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.EnchantingApparatusBlock;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.RenderUtil;


public class EnchantingApparatusRenderer extends ArsGeoBlockRenderer<EnchantingApparatusTile> {

    public EnchantingApparatusRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        super(p_i226006_1_, new GenericModel<>("enchanting_apparatus"));
    }

    @Override
    public void renderFinal(PoseStack stack, EnchantingApparatusTile tile, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int color) {
        super.renderFinal(stack, tile, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, color);
        GeoBone frame = model.getBone("frame_all").orElse(null);
        if (frame != null && tile.getStack() != null) {
            double x = tile.getBlockPos().getX();
            double y = tile.getBlockPos().getY();
            double z = tile.getBlockPos().getZ();
            if (tile.renderEntity == null || !ItemStack.matches(tile.renderEntity.getItem(), tile.getStack())) {
                tile.renderEntity = new ItemEntity(tile.getLevel(), x, y, z, tile.getStack());
            }

            stack.pushPose();

            RenderUtil.translateMatrixToBone(stack, frame);
            var facing = tile.getBlockState().getValue(EnchantingApparatusBlock.FACING);
            stack.translate(0.5 - facing.getStepX() * 0.1, 0.5 - facing.getStepY() * 0.1, 0.5 - facing.getStepZ() * 0.1);
            stack.scale(0.75f, 0.75f, 0.75f);
            stack.mulPose(facing.getRotation());
            ItemStack itemstack = tile.renderEntity.getItem();
            Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, stack, bufferSource, tile.getLevel(), (int) tile.getBlockPos().asLong());
            stack.popPose();
        }
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        if (facing.getAxis().isHorizontal()) {
            var step = facing.step();
            poseStack.translate(-step.x * 0.5, 0.5, -step.z * 0.5);
        } else if (facing == Direction.DOWN) {
            poseStack.translate(0, 1.0, 0);
        }
        poseStack.mulPose(facing.getRotation());
    }
}
