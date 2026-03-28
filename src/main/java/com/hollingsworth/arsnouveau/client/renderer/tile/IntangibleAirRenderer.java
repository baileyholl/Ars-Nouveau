package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.IntangibleAirTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;

// MC 1.21.11: BlockEntityRenderer now requires 2 type params <T, S extends BlockEntityRenderState>
// render() replaced by createRenderState() + submit()
//
// TODO: Port block transparency rendering:
// - Old: extended RenderType, used BakedModel.getQuads(BlockState, Direction, RandomSource, ModelData, null)
//   to render a partially-transparent block face-by-face.
// - New: Use BlockStateModel.collectParts() + ModelBlockRenderer.tesselateBlock(List<BlockModelPart>)
// - The custom RenderType (DummyRender with TRANSLUCENT_TRANSPARENCY + BLOCK_SHEET_MIPPED + VIEW_OFFSET_Z_LAYERING)
//   needs to be created via RenderType.create(String, RenderSetup) with a new CompositeState builder.
// - Since block rendering now goes through SubmitNodeCollector, use collector.submitBlockModel() with the appropriate RenderType.
// - ModelData removed from net.neoforged.neoforge.client.model.data (moved to net.neoforged.neoforge.model.data.ModelData)
public class IntangibleAirRenderer implements BlockEntityRenderer<IntangibleAirTile, BlockEntityRenderState> {

    public IntangibleAirRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }

    @Override
    public void submit(BlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        // TODO: Port transparent block-face rendering.
        // Data needed: stateID, duration, maxLength - must be captured from IntangibleAirTile
        // in extractRenderState into a custom BlockEntityRenderState subclass.
    }
}
