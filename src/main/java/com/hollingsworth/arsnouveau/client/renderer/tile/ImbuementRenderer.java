package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class ImbuementRenderer extends ArsGeoBlockRenderer<ImbuementTile> {

    public ImbuementRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        super(p_i226006_1_, new GenericModel<>("imbuement_chamber"));
    }

    @Override
    public void actuallyRender(PoseStack matrixStack, ImbuementTile tileEntityIn, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        super.actuallyRender(matrixStack, tileEntityIn, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
        if (tileEntityIn.getStack() == null || tileEntityIn.getStack().isEmpty()) return;

        matrixStack.pushPose();
        matrixStack.translate(0.f, 0.5f, 0.f);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.mulPose(Axis.YP.rotationDegrees((partialTick + (float) ClientInfo.ticksInGame) * 3f));
        Minecraft.getInstance().getItemRenderer().renderStatic(tileEntityIn.getStack(),
                ItemDisplayContext.FIXED,
                packedLight,
                packedOverlay,
                matrixStack,
                bufferSource,
                tileEntityIn.getLevel(),
                (int) tileEntityIn.getBlockPos().asLong());
        matrixStack.popPose();

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
