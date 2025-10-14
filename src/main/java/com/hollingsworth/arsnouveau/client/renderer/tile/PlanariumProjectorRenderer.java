package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.PlanariumProjectorTile;
import com.hollingsworth.arsnouveau.common.block.tile.PlanariumTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.nuggets.client.rendering.FakeRenderingWorld;
import com.hollingsworth.nuggets.client.rendering.StatePos;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PlanariumProjectorRenderer implements BlockEntityRenderer<PlanariumProjectorTile> {
    FakeRenderingWorld fakeRenderingWorld;

    public PlanariumProjectorRenderer(BlockEntityRendererProvider.Context blockRenderDispatcher) {

    }

    @Override
    public void render(PlanariumProjectorTile planariumProjectorTile, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {
        if (fakeRenderingWorld == null) {
            ArrayList<StatePos> statePos = new ArrayList<>();
            statePos.add(new StatePos(BlockRegistry.PLANARIUM.defaultBlockState(), BlockPos.ZERO));
            fakeRenderingWorld = new FakeRenderingWorld(planariumProjectorTile.getLevel(), statePos, BlockPos.ZERO);
        }
        BlockState state = BlockRegistry.PLANARIUM.defaultBlockState();
        poseStack.pushPose();
        poseStack.translate(0, -25, 0);
        poseStack.scale(32, 32, 32);
//        Minecraft.getInstance().getBlockEntityRenderDispatcher().en
//        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, multiBufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        BlockEntityRenderDispatcher blockEntityRenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher();
        BlockEntity blockEntity = fakeRenderingWorld.getBlockEntity(BlockPos.ZERO);
        if (blockEntity instanceof PlanariumTile planariumTile) {
            planariumTile.isDimModel = true;
            blockEntityRenderer.render(blockEntity, 0, poseStack, multiBufferSource);
        }
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
