package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.model.data.ModelData;


// MC 1.21.11: EntityRenderer requires 2 type params <T, S extends EntityRenderState>
// render() replaced by createRenderState() + extractRenderState() + submit()
// ModelData moved from net.neoforged.neoforge.client.model.data to net.neoforged.neoforge.model.data
// BakedModel replaced by BlockStateModel; tesselateBlock signature changed
// TODO: Port falling block rendering to submit():
// - BlockState must be captured from entity during extractRenderState into a custom render state.
// - In submit(), use collector.submitBlock(poseStack, blockState, packedLight, packedOverlay, seed)
//   or collector.submitBlockModel() for custom rendering.
// - Old API: dispatcher.getModelRenderer().tesselateBlock(level, model, blockState, blockPos, poseStack, vertexConsumer, ...)
// - New API: dispatcher.getModelRenderer().tesselateBlock(level, List<BlockModelPart>, blockState, blockPos, poseStack, vertexConsumer, false, packedOverlay)
//   where parts come from: dispatcher.getBlockModel(blockState).collectParts(random, parts)
public class EnchantedFallingBlockRenderer<T extends EnchantedFallingBlock> extends EntityRenderer<T, EntityRenderState> {
    private final BlockRenderDispatcher dispatcher;

    public EnchantedFallingBlockRenderer(EntityRendererProvider.Context p_174112_) {
        super(p_174112_);
        this.shadowRadius = 0.5F;
        dispatcher = p_174112_.getBlockRenderDispatcher();
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(state, poseStack, collector, cameraState);
        // TODO: Port falling block rendering. BlockState must come from custom render state.
        // Use collector.submitBlock() or tesselateBlock with BlockModelParts.
    }

    // 1.21.11: getTextureLocation(T entity) removed from EntityRenderer; no replacement needed here
}
