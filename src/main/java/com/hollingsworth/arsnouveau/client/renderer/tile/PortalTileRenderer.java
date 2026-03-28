package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.PortalBlock;
import com.hollingsworth.arsnouveau.common.block.tile.PortalTile;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

// MC 1.21.11: BlockEntityRenderer uses render-state pattern; custom geometry via submitCustomGeometry
public class PortalTileRenderer<T extends PortalTile> implements BlockEntityRenderer<T, PortalTileRenderer.State> {

    public PortalTileRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    public static class State extends BlockEntityRenderState {
        public boolean skip;
        public boolean isHorizontal;
        public Direction.Axis beAxis;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(T tile, State state, float partialTick,
                                   Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(tile, state, partialTick, cameraPos, crumbling);
        BlockState bs = tile.getBlockState();
        state.skip = Config.ALTERNATE_PORTAL_RENDER.get() || bs.getValue(PortalBlock.ALTERNATE);
        state.isHorizontal = tile.isHorizontal;
        state.beAxis = bs.getValue(PortalBlock.AXIS);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.skip) return;
        float f = getOffset();
        collector.submitCustomGeometry(poseStack, RenderTypes.endPortal(), (pose, buffer) -> {
            Matrix4f m4f = pose.pose();
            renderCube(state, f, m4f, buffer);
        });
    }

    private void renderCube(State state, float offset, Matrix4f pose, VertexConsumer vc) {
        renderFace(state, pose, vc, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
        renderFace(state, pose, vc, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
        renderFace(state, pose, vc, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
        renderFace(state, pose, vc, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
        renderFace(state, pose, vc, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
        renderFace(state, pose, vc, 0.0F, 1.0F, offset, offset, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
    }

    private void renderFace(State state, Matrix4f pose, VertexConsumer vc,
                             float x0, float x1, float y0, float y1,
                             float z0, float z1, float z2, float z3,
                             Direction direction) {
        Direction.Axis dirAxis = direction.getAxis();
        Direction.Axis beAxis = state.beAxis;
        if (!state.isHorizontal
                && ((beAxis == Direction.Axis.X && dirAxis == Direction.Axis.Z)
                || (beAxis == Direction.Axis.Z && dirAxis == Direction.Axis.X))) {
            vc.addVertex(pose, x0, y0, z0);
            vc.addVertex(pose, x1, y0, z1);
            vc.addVertex(pose, x1, y1, z2);
            vc.addVertex(pose, x0, y1, z3);
        } else if (state.isHorizontal && dirAxis == Direction.Axis.Y) {
            vc.addVertex(pose, x0, y0, z0);
            vc.addVertex(pose, x1, y0, z1);
            vc.addVertex(pose, x1, y1, z2);
            vc.addVertex(pose, x0, y1, z3);
        }
    }

    protected float getOffset() {
        return 0.75F;
    }
}
