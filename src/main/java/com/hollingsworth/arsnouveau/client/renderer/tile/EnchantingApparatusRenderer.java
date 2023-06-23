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
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.RenderUtils;


public class EnchantingApparatusRenderer extends ArsGeoBlockRenderer<EnchantingApparatusTile> {

    public EnchantingApparatusRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        super(p_i226006_1_, new GenericModel<>("enchanting_apparatus"));
    }

    @Override
    public void renderRecursively(PoseStack stack, EnchantingApparatusTile animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer bufferIn, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
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
            Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, stack, this.buffer, tile.getLevel(), (int) tile.getBlockPos().asLong());
            stack.popPose();
            bufferIn = buffer.getBuffer(RenderType.entityCutoutNoCull(text));
        }
        super.renderRecursively(stack, animatable, bone, renderType, bufferSource, bufferIn, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }


    MultiBufferSource buffer;
    EnchantingApparatusTile tile;
    ResourceLocation text;

    @Override
    public void preRender(PoseStack poseStack, EnchantingApparatusTile animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.tile = animatable;
        this.buffer = bufferSource;
        this.text = this.getTextureLocation(animatable);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
