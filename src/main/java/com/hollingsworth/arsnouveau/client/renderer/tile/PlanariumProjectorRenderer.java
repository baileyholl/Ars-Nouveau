package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.PlanariumProjectorTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class PlanariumProjectorRenderer implements BlockEntityRenderer<PlanariumProjectorTile> {
    public PlanariumProjectorRenderer(BlockEntityRendererProvider.Context blockRenderDispatcher) {

    }

    @Override
    public void render(PlanariumProjectorTile planariumProjectorTile, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {
        poseStack.pushPose();
        poseStack.translate(0, -24, 0);
        poseStack.scale(32, 32, 32);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(BlockRegistry.PLANARIUM.defaultBlockState(), poseStack, multiBufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(PlanariumProjectorTile blockEntity, Vec3 cameraPos) {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen(PlanariumProjectorTile blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull PlanariumProjectorTile blockEntity) {
        return AABB.INFINITE;
    }


}
