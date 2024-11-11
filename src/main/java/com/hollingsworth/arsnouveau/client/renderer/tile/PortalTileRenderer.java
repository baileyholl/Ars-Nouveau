package com.hollingsworth.arsnouveau.client.renderer.tile;


import com.hollingsworth.arsnouveau.common.block.PortalBlock;
import com.hollingsworth.arsnouveau.common.block.tile.PortalTile;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class PortalTileRenderer<T extends PortalTile> implements BlockEntityRenderer<T> {

    private static final Random RANDOM = new Random(31100L);

    public PortalTileRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {

    }

    public void render(PortalTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (Config.ALTERNATE_PORTAL_RENDER.get() || tileEntityIn.getBlockState().getValue(PortalBlock.ALTERNATE)) return;
        double d0 = 5d;
        int i = this.getPasses(d0);
        float f = this.getOffset();
        Matrix4f matrix4f = matrixStackIn.last().pose();
        this.renderCube(tileEntityIn, f, 0.10F, matrix4f, bufferIn.getBuffer(RenderType.endPortal()));

        for (int j = 1; j < i; ++j) {
            this.renderCube(tileEntityIn, f, 2.0F / (float) (35 - j), matrix4f, bufferIn.getBuffer(RenderType.endPortal()));
        }

    }

    private void renderCube(PortalTile tileEntityIn, float p_228883_2_, float p_228883_3_, Matrix4f p_228883_4_, VertexConsumer p_228883_5_) {
        float f = (RANDOM.nextFloat() * 0.5F + 0.1F) * p_228883_3_;
        float f1 = (RANDOM.nextFloat() * 0.5F + 0.4F) * p_228883_3_;
        float f2 = (RANDOM.nextFloat() * 0.5F + 0.5F) * p_228883_3_;
        this.renderFace(tileEntityIn, p_228883_4_, p_228883_5_, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, f, f1, f2, Direction.SOUTH);
        this.renderFace(tileEntityIn, p_228883_4_, p_228883_5_, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, f, f1, f2, Direction.NORTH);
        this.renderFace(tileEntityIn, p_228883_4_, p_228883_5_, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, f, f1, f2, Direction.EAST);
        this.renderFace(tileEntityIn, p_228883_4_, p_228883_5_, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, f, f1, f2, Direction.WEST);
        this.renderFace(tileEntityIn, p_228883_4_, p_228883_5_, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f, f1, f2, Direction.DOWN);
        this.renderFace(tileEntityIn, p_228883_4_, p_228883_5_, 0.0F, 1.0F, p_228883_2_, p_228883_2_, 1.0F, 1.0F, 0.0F, 0.0F, f, f1, f2, Direction.UP);
    }

    private void renderFace(PortalTile tileEntityIn, Matrix4f matrix, VertexConsumer iBuilder, float p_228884_4_, float p_228884_5_, float p_228884_6_, float p_228884_7_, float p_228884_8_, float p_228884_9_, float p_228884_10_, float p_228884_11_, float p_228884_12_, float p_228884_13_, float p_228884_14_, Direction direction) {
        var beAxis = tileEntityIn.getBlockState().getValue(PortalBlock.AXIS);
        var directionAxis = direction.getAxis();
        // what have I done...
        if (!tileEntityIn.isHorizontal && (beAxis == Direction.Axis.X && directionAxis == Direction.Axis.Z) || (beAxis == Direction.Axis.Z && directionAxis == Direction.Axis.X)) {
            iBuilder.vertex(matrix, p_228884_4_, p_228884_6_, p_228884_8_).color(p_228884_12_, p_228884_13_, p_228884_14_, 1.0F).endVertex();
            iBuilder.vertex(matrix, p_228884_5_, p_228884_6_, p_228884_9_).color(p_228884_12_, p_228884_13_, p_228884_14_, 1.0F).endVertex();
            iBuilder.vertex(matrix, p_228884_5_, p_228884_7_, p_228884_10_).color(p_228884_12_, p_228884_13_, p_228884_14_, 1.0F).endVertex();
            iBuilder.vertex(matrix, p_228884_4_, p_228884_7_, p_228884_11_).color(p_228884_12_, p_228884_13_, p_228884_14_, 1.0F).endVertex();

        } else if (tileEntityIn.isHorizontal && direction.getAxis() == Direction.Axis.Y) {
            iBuilder.vertex(matrix, p_228884_4_, p_228884_6_, p_228884_8_).color(p_228884_12_, p_228884_13_, p_228884_14_, 1.0F).endVertex();
            iBuilder.vertex(matrix, p_228884_5_, p_228884_6_, p_228884_9_).color(p_228884_12_, p_228884_13_, p_228884_14_, 1.0F).endVertex();
            iBuilder.vertex(matrix, p_228884_5_, p_228884_7_, p_228884_10_).color(p_228884_12_, p_228884_13_, p_228884_14_, 1.0F).endVertex();
            iBuilder.vertex(matrix, p_228884_4_, p_228884_7_, p_228884_11_).color(p_228884_12_, p_228884_13_, p_228884_14_, 1.0F).endVertex();
        }
    }

    protected int getPasses(double p_191286_1_) {
        if (p_191286_1_ > 36864.0D) {
            return 1;
        } else if (p_191286_1_ > 25600.0D) {
            return 3;
        } else if (p_191286_1_ > 16384.0D) {
            return 5;
        } else if (p_191286_1_ > 9216.0D) {
            return 7;
        } else if (p_191286_1_ > 4096.0D) {
            return 9;
        } else if (p_191286_1_ > 1024.0D) {
            return 11;
        } else if (p_191286_1_ > 576.0D) {
            return 13;
        } else {
            return p_191286_1_ > 256.0D ? 14 : 15;
        }
    }

    protected float getOffset() {
        return 0.75F;
    }
}