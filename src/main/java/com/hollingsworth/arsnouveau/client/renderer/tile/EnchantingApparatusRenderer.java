package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.RenderUtils;


public class EnchantingApparatusRenderer extends ArsGeoBlockRenderer<EnchantingApparatusTile> {

    public EnchantingApparatusRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        super(p_i226006_1_, new GenericModel<>("enchanting_apparatus"));
    }

    MultiBufferSource buffer;
    EnchantingApparatusTile tile;
    ResourceLocation text;

    @Override
    public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("frame_all") && tile.getStack() != null) {

            double x = tile.getBlockPos().getX();
            double y = tile.getBlockPos().getY();
            double z = tile.getBlockPos().getZ();
            if (tile.renderEntity == null || !ItemStack.matches(tile.renderEntity.getItem(), tile.getStack())) {
                tile.renderEntity = new ItemEntity(tile.getLevel(), x, y, z, tile.getStack());
            }
            stack.pushPose();
            RenderUtils.translateMatrixToBone(stack, bone);
            stack.translate(0, +0.35, 0);
            stack.scale(0.75f, 0.75f, 0.75f);
            ItemStack itemstack = tile.renderEntity.getItem();
            Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemDisplayContext.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, stack, this.buffer, (int) tile.getBlockPos().asLong());
            stack.popPose();
            bufferIn = buffer.getBuffer(RenderType.entityCutoutNoCull(text));
        }
        super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void renderEarly(EnchantingApparatusTile animatable, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        this.tile = animatable;
        this.buffer = renderTypeBuffer;
        this.text = this.getTextureLocation(animatable);
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
    }

    @Override
    public void render(BlockEntity tile, float v, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int lightIn, int overlayIn) {
        try {
            super.render(tile, v, matrixStack, iRenderTypeBuffer, lightIn, overlayIn);
            EnchantingApparatusTile tileEntityIn = (EnchantingApparatusTile) tile;
            this.tile = tileEntityIn;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
