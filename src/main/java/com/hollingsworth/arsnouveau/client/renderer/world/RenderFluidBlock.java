package com.hollingsworth.arsnouveau.client.renderer.world;


import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;

import java.util.List;

import static net.minecraft.client.renderer.LevelRenderer.getLightColor;

public class RenderFluidBlock {
    private static BakedQuad createQuad(List<Vec3> vectors, float[] cols, TextureAtlasSprite sprite, Direction face, float u1, float u2, float v1, float v2) {
        QuadBakingVertexConsumer quadBaker = new QuadBakingVertexConsumer();
        Vec3 normal = Vec3.atLowerCornerOf(face.getNormal());

        putVertex(quadBaker, normal, vectors.get(0).x, vectors.get(0).y, vectors.get(0).z, u1, v1, sprite, cols, face);
        putVertex(quadBaker, normal, vectors.get(1).x, vectors.get(1).y, vectors.get(1).z, u1, v2, sprite, cols, face);
        putVertex(quadBaker, normal, vectors.get(2).x, vectors.get(2).y, vectors.get(2).z, u2, v2, sprite, cols, face);
        putVertex(quadBaker, normal, vectors.get(3).x, vectors.get(3).y, vectors.get(3).z, u2, v1, sprite, cols, face);

        return quadBaker.bakeQuad();
    }

    private static void putVertex(QuadBakingVertexConsumer quadBaker, Vec3 normal,
                                  double x, double y, double z, float u, float v, TextureAtlasSprite sprite, float[] cols, Direction face) {
        quadBaker.addVertex((float) x, (float) y, (float) z);
        quadBaker.setNormal((float) normal.x, (float) normal.y, (float) normal.z);
        quadBaker.setColor(cols[0], cols[1], cols[2], cols[3]);
        quadBaker.setUv(u, v);
        quadBaker.setSprite(sprite);
        quadBaker.setDirection(face);
    }

    public static void renderFluidBlock(BlockState renderState, Level level, BlockPos pos, PoseStack matrixStackIn, VertexConsumer builder, boolean renderAdjacent) {
        if (renderState.getFluidState().isEmpty()) return;
        FluidState fluidState = renderState.getFluidState();
        Fluid fluid = fluidState.getType();
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.withDefaultNamespace("block/water_still"));
        int color = 0xFFFFFFFF;
        if (fluid == Fluids.WATER) {
            color = -12618012;
        } else if (fluid == Fluids.LAVA) {
            sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.withDefaultNamespace("block/lava_still"));
        }
        int brightness = getLightColor(level, pos);
        float a = (color >> 24 & 0xFF) / 255.0f;
        float r = (color >> 16 & 0xFF) / 255.0f;
        float g = (color >> 8 & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        float minU = sprite.getU0();
        float minV = sprite.getV0();

        float maxU = sprite.getU1();
        float maxV = sprite.getV1();

        float x = 0.0f;
        float y = 0.0f;
        float z = 0.0f;

        float x2 = 1.0f;
        float z2 = 1.0f;
        float height = 0.875f; //14/16

        float[] cols = new float[]{r, g, b, a};

        BakedQuad quad;
        if (!renderAdjacent) {
            return;
        }
        matrixStackIn.pushPose();

        //DOWN
        quad = createQuad(ImmutableList.of(new Vec3(x, y, z2), new Vec3(x, y, z), new Vec3(x2, y, z), new Vec3(x2, y, z2)), cols, sprite, Direction.DOWN, minU, maxU, minV, maxV);
        putBulkData(builder, matrixStackIn.last(), quad, r, g, b, a, brightness, 0, false);
        //UP
        quad = createQuad(ImmutableList.of(new Vec3(x, height, z), new Vec3(x, height, z2), new Vec3(x2, height, z2), new Vec3(x2, height, z)), cols, sprite, Direction.UP, minU, maxU, minV, maxV);
        putBulkData(builder, matrixStackIn.last(), quad, r, g, b, a, brightness, 0, false);
        //NORTH
        quad = createQuad(ImmutableList.of(new Vec3(x2, height, z), new Vec3(x2, y, z), new Vec3(x, y, z), new Vec3(x, height, z)), cols, sprite, Direction.NORTH, minU, maxU, minV, maxV);
        putBulkData(builder, matrixStackIn.last(), quad, r, g, b, a, brightness, 0, false);
        //SOUTH
        quad = createQuad(ImmutableList.of(new Vec3(x, height, z2), new Vec3(x, y, z2), new Vec3(x2, y, z2), new Vec3(x2, height, z2)), cols, sprite, Direction.SOUTH, minU, maxU, minV, maxV);
        putBulkData(builder, matrixStackIn.last(), quad, r, g, b, a, brightness, 0, false);
        //WEST
        quad = createQuad(ImmutableList.of(new Vec3(x, height, z), new Vec3(x, y, z), new Vec3(x, y, z2), new Vec3(x, height, z2)), cols, sprite, Direction.WEST, minU, maxU, minV, maxV);
        putBulkData(builder, matrixStackIn.last(), quad, r, g, b, a, brightness, 0, false);
        //EAST
        quad = createQuad(ImmutableList.of(new Vec3(x2, height, z2), new Vec3(x2, y, z2), new Vec3(x2, y, z), new Vec3(x2, height, z)), cols, sprite, Direction.EAST, minU, maxU, minV, maxV);
        putBulkData(builder, matrixStackIn.last(), quad, r, g, b, a, brightness, 0, false);

        matrixStackIn.popPose();

    }

    static void putBulkData(VertexConsumer consumer, PoseStack.Pose pose, BakedQuad bakedQuad, float red, float green, float blue, float alpha, int packedLight, int packedOverlay, boolean readExistingColor) {
        consumer.putBulkData(pose, bakedQuad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, alpha, new int[]{packedLight, packedLight, packedLight, packedLight}, packedOverlay, readExistingColor);
    }

}
