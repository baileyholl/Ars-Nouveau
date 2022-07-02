package com.hollingsworth.arsnouveau.client.renderer.tile;


import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class ImbuementRenderer extends GeoBlockRenderer<ImbuementTile> {

    public ImbuementRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        super(p_i226006_1_, new GenericModel<>("imbuement_chamber"));
    }

    MultiBufferSource buffer;
    ImbuementTile tile;
    ResourceLocation text;

    @Override
    public void renderEarly(ImbuementTile animatable, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        this.tile = animatable;
        this.buffer = renderTypeBuffer;
        this.text = this.getTextureLocation(animatable);
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
    }

    @Override
    public void render(BlockEntity tile, float v, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int lightIn, int overlayIn) {
        super.render(tile, v, matrixStack, iRenderTypeBuffer, lightIn, overlayIn);
        ImbuementTile tileEntityIn = (ImbuementTile) tile;
        this.tile = tileEntityIn;
        double x = tile.getBlockPos().getX();
        double y = tile.getBlockPos().getY();
        double z = tile.getBlockPos().getZ();
        if (tileEntityIn.entity == null || !ItemStack.matches(tileEntityIn.entity.getItem(), tileEntityIn.stack)) {
            tileEntityIn.entity = new ItemEntity(tile.getLevel(), x, y, z, tileEntityIn.stack);

        }
        if (tileEntityIn.entity != null) {
            ItemEntity entityItem = tileEntityIn.entity;
            tileEntityIn.frames += 1.5f * Minecraft.getInstance().getDeltaFrameTime();
            entityItem.setYHeadRot(tileEntityIn.frames);
            entityItem.age = (int) tileEntityIn.frames;
            matrixStack.pushPose();
            matrixStack.scale(0.75f, 0.75f, 0.75f);
            float offset = 0.5f * 0.75f + 0.31f;
            Minecraft.getInstance().getEntityRenderDispatcher().render(entityItem, offset, 0.3, offset, entityItem.yRot, 0, matrixStack, iRenderTypeBuffer, lightIn);
            matrixStack.popPose();
        }
    }
}
