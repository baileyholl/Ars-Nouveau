package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.Planarium;
import com.hollingsworth.arsnouveau.common.block.tile.PlanariumProjectorTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

// MC 1.21.11: BlockEntityRenderer now requires 2 type params <T, S extends BlockEntityRenderState>
// render() replaced by createRenderState() + submit()
// TODO: Port block rendering in submit() - renderSingleBlock needs MultiBufferSource which is not in SubmitNodeCollector.
// Use collector.submitBlock() or submitBlockModel() for block rendering.
public class PlanariumProjectorRenderer implements BlockEntityRenderer<PlanariumProjectorTile, BlockEntityRenderState> {
    public PlanariumProjectorRenderer(BlockEntityRendererProvider.Context blockRenderDispatcher) {
    }

    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }

    @Override
    public void submit(BlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0, -24, 0);
        poseStack.scale(32, 32, 32);
        // TODO: Port block rendering. Old: Minecraft.getInstance().getBlockRenderer().renderSingleBlock(...)
        // which needed MultiBufferSource. Now use collector.submitBlock() for simple cases,
        // or collector.submitBlockModel() for custom RenderType.
        // collector.submitBlock(poseStack, BlockRegistry.PLANARIUM.defaultBlockState().setValue(Planarium.INVERTED, true), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(PlanariumProjectorTile blockEntity, Vec3 cameraPos) {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen() {
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
