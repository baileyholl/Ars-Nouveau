package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import software.bernie.geckolib.util.RenderUtil;


public class EnchantingApparatusRenderer extends ArsGeoBlockRenderer<EnchantingApparatusTile> {

    public EnchantingApparatusRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        super(p_i226006_1_, new GenericModel<>("enchanting_apparatus"));
    }

    @Override
    public void renderFinal(PoseStack stack, EnchantingApparatusTile tile, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderFinal(stack, tile, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
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
            stack.translate(0.5, +0.5, 0.5);
            stack.scale(0.75f, 0.75f, 0.75f);
            ItemStack itemstack = tile.renderEntity.getItem();
            Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, stack, bufferSource, tile.getLevel(), (int) tile.getBlockPos().asLong());
            stack.popPose();
        }
    }
}
