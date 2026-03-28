package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;

// MC 1.21.11: FallingBlockRenderer pattern — MovingBlockRenderState + submitMovingBlock
public class EnchantedFallingBlockRenderer<T extends EnchantedFallingBlock> extends EntityRenderer<T, EnchantedFallingBlockRenderer.State> {

    public static class State extends EntityRenderState {
        public final MovingBlockRenderState movingBlock = new MovingBlockRenderState();
    }

    public EnchantedFallingBlockRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.5F;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(T entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        BlockPos pos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
        state.movingBlock.blockState = entity.blockState;
        state.movingBlock.blockPos = pos;
        state.movingBlock.randomSeedPos = pos;
        state.movingBlock.level = entity.level();
        state.movingBlock.biome = entity.level().getBiome(pos);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.movingBlock.blockState.getRenderShape() == RenderShape.MODEL) {
            poseStack.pushPose();
            poseStack.translate(-0.5, 0.0, -0.5);
            collector.submitMovingBlock(poseStack, state.movingBlock);
            poseStack.popPose();
        }
        super.submit(state, poseStack, collector, cameraState);
    }
}
