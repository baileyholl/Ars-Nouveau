package com.hollingsworth.arsnouveau.client.renderer.tile;


import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.common.block.tile.PortalTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@OnlyIn(Dist.CLIENT)
public class PortalTileRenderer<T extends PortalTile> extends TileEntityRenderer<T> {
    public static final ResourceLocation END_SKY_TEXTURE = new ResourceLocation("textures/environment/end_sky.png");
    public static final ResourceLocation END_PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");
    private static final Random RANDOM = new Random(31100L);
    private static final List<RenderType> RENDER_TYPES = IntStream.range(0, 16).mapToObj((p_228882_0_) -> {
        return RenderType.endPortal(p_228882_0_ + 1);
    }).collect(ImmutableList.toImmutableList());

    public PortalTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        RANDOM.setSeed(31100L);
        double d0 = tileEntityIn.getBlockPos().distSqr(this.renderer.camera.getPosition(), true);
        int i = this.getPasses(d0);
        float f = this.getOffset();
        Matrix4f matrix4f = matrixStackIn.last().pose();
        this.renderCube(tileEntityIn, f, tileEntityIn.getLevel().getSunAngle(partialTicks), matrix4f, bufferIn.getBuffer(RENDER_TYPES.get(0)));

        for(int j = 1; j < i; ++j) {
            this.renderCube(tileEntityIn, f, tileEntityIn.getLevel().getSunAngle(partialTicks) / (float)(12.0f  - j), matrix4f, bufferIn.getBuffer(RENDER_TYPES.get(j)));
        }

    }

    private void renderCube(T tileEntityIn, float p_228883_2_, float p_228883_3_, Matrix4f p_228883_4_, IVertexBuilder p_228883_5_) {
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

    private void renderFace(T tileEntityIn, Matrix4f matrix, IVertexBuilder iBuilder, float p_228884_4_, float p_228884_5_, float p_228884_6_, float p_228884_7_, float p_228884_8_, float p_228884_9_, float p_228884_10_, float p_228884_11_, float p_228884_12_, float p_228884_13_, float p_228884_14_, Direction direction) {
//        if (tileEntityIn.shouldRenderFace(p_228884_15_)) {
//            p_228884_3_.pos(p_228884_2_, p_228884_4_, p_228884_6_, p_228884_8_).color(p_228884_12_, p_228884_13_, p_228884_14_, 1.0F).endVertex();
//            p_228884_3_.pos(p_228884_2_, p_228884_5_, p_228884_6_, p_228884_9_).color(p_228884_12_, p_228884_13_, p_228884_14_, 1.0F).endVertex();
//            p_228884_3_.pos(p_228884_2_, p_228884_5_, p_228884_7_, p_228884_10_).color(p_228884_12_, p_228884_13_, p_228884_14_, 1.0F).endVertex();
//            p_228884_3_.pos(p_228884_2_, p_228884_4_, p_228884_7_, p_228884_11_).color(p_228884_12_, p_228884_13_, p_228884_14_, 1.0F).endVertex();
//        }
        if(direction == Direction.EAST || direction == Direction.WEST || direction == Direction.SOUTH || direction == Direction.NORTH){
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