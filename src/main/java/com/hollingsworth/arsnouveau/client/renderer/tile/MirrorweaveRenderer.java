package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.MirrorWeaveTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
// 1.21.11: ModelData removed from net.neoforged.neoforge.client.model.data

// MC 1.21.11: BlockEntityRenderer now requires 2 type params <T, S extends BlockEntityRenderState>
// render() replaced by createRenderState() + extractRenderState() + submit()
// TODO: Port block rendering. The entire tessellation API changed: BlockStateModel.collectParts() replaces
// getRenderTypes/getQuads, BakedQuad is now a record (direction()/shade() instead of getDirection()/isShade()),
// putQuadData signature changed (CommonRenderStorage replaces individual light/shade floats),
// calculateShape signature changed. Rewrite when porting resource is available.
public class MirrorweaveRenderer<T extends MirrorWeaveTile> implements BlockEntityRenderer<T, BlockEntityRenderState> {

    public MirrorweaveRenderer(BlockEntityRendererProvider.Context pContext) {
    }

    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }

    @Override
    public void submit(BlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        // TODO: Port mirror weave rendering to submit() / SubmitNodeCollector.
    }

    @Override
    public void extractRenderState(T tile, BlockEntityRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(tile, state, breakProgress);
    }

    static final Direction[] DIRECTIONS = Direction.values();

    // Legacy render logic - retained for porting reference.
    // TODO: Re-enable when tessellation API porting is complete.
    public void renderLegacy(MirrorWeaveTile tileEntityIn, float partialTick, PoseStack pPoseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        // Stub - see class-level TODO for porting notes
    }

    public void render(MirrorWeaveTile tileEntityIn, float partialTicks, PoseStack pPoseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        // Stub - called by SkyBlockRenderer
    }

    public void updateCulling(MirrorWeaveTile tileEntityIn) {
        boolean disableEntireRender = true;
        tileEntityIn.renderInvalid = false;
        for (Direction direction : DIRECTIONS) {
            BlockPos blockingPos = tileEntityIn.getBlockPos().relative(direction);
            BlockState blockingState = tileEntityIn.getLevel().getBlockState(blockingPos);
            tileEntityIn.setRenderDirection(direction, false);
            if (tileEntityIn.getLevel().getBlockEntity(blockingPos) instanceof MirrorWeaveTile neighborTile) {
                blockingState = neighborTile.getStateForCulling();
            }
            // 1.21.11: getOcclusionShape() takes no arguments now
            var blockingShape = blockingState.getOcclusionShape();
            if (!tileEntityIn.shouldRenderFace(blockingState, tileEntityIn.getLevel(), tileEntityIn.getBlockPos(), direction, blockingPos)) {
                continue;
            }
            if (!blockingState.canOcclude()) {
                tileEntityIn.setRenderDirection(direction, true);
                disableEntireRender = false;
                continue;
            }

            if (blockingState.canOcclude() && !Shapes.blockOccludes(Shapes.block(), blockingShape, direction)) {
                tileEntityIn.setRenderDirection(direction, true);
                disableEntireRender = false;
                continue;
            }

        }
        tileEntityIn.disableRender = disableEntireRender;
    }

    public int getViewDistance() {
        return 68;
    }
}
