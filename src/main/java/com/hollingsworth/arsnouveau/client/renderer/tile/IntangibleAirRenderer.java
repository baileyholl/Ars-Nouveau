package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.IntangibleAirBlock;
import com.hollingsworth.arsnouveau.common.block.tile.IntangibleAirTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Random;

public class IntangibleAirRenderer extends TileEntityRenderer<IntangibleAirTile> {
    public IntangibleAirRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }
    static class DummyRender extends net.minecraft.client.renderer.RenderType{
        public static final RenderType RenderBlock = create("MiningLaserRenderBlock",
                DefaultVertexFormats.BLOCK, GL11.GL_QUADS, 256,
                RenderType.State.builder()
                        .setShadeModelState(SMOOTH_SHADE)
                        .setLightmapState(LIGHTMAP)
                        .setTextureState(BLOCK_SHEET_MIPPED)
                        .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .setCullState(CULL)
                        .setWriteMaskState(COLOR_WRITE)
                        .createCompositeState(false));

        public DummyRender(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
            super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
        }
    }


    private void renderModelBrightnessColorQuads(MatrixStack.Entry matrixEntry, IVertexBuilder builder, float red, float green, float blue, float alpha, List<BakedQuad> listQuads, int combinedLightsIn, int combinedOverlayIn) {
        for(BakedQuad bakedquad : listQuads) {
            float f;
            float f1;
            float f2;

            if (bakedquad.isTinted()) {
                f = red * 1f;
                f1 = green * 1f;
                f2 = blue * 1f;
            } else {
                f = 1f;
                f1 = 1f;
                f2 = 1f;
            }

            builder.addVertexData(matrixEntry, bakedquad, f, f1, f2, alpha, combinedLightsIn, combinedOverlayIn);
        }
    }

    @Override
    public void render(IntangibleAirTile tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState renderState = Block.stateById(tileEntityIn.stateID);
        if(renderState == null)
            return;
        double scale = ((double)tileEntityIn.duration)/(double)tileEntityIn.maxLength;

        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
        Minecraft.getInstance().getTextureManager().bind(PlayerContainer.BLOCK_ATLAS);
        IBakedModel ibakedmodel = blockrendererdispatcher.getBlockModel(renderState);
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        int color = blockColors.getColor(renderState, tileEntityIn.getLevel(), tileEntityIn.getBlockPos(), 0);
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        matrixStackIn.pushPose();


        for (Direction direction : Direction.values()) {
            if (!(tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos().relative(direction)).getBlock() instanceof IntangibleAirBlock)) {
                renderModelBrightnessColorQuads(matrixStackIn.last(), bufferIn.getBuffer(DummyRender.RenderBlock), f, f1, f2, (float)scale, ibakedmodel.getQuads(renderState, direction, new Random(MathHelper.getSeed(tileEntityIn.getBlockPos())), EmptyModelData.INSTANCE), combinedLightIn, combinedOverlayIn);
            }
        }

        matrixStackIn.popPose();
    }
}
