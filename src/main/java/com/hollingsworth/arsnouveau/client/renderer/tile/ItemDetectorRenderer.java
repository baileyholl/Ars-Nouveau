package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.block.tile.ItemDetectorTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

// MC 1.21.11: BlockEntityRenderer now requires 2 type params + render-state pattern
public class ItemDetectorRenderer implements BlockEntityRenderer<ItemDetectorTile, ItemDetectorRenderer.State> {

    private final ItemModelResolver itemModelResolver;
    private final RandomSource random = RandomSource.create();

    public ItemDetectorRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    public static class State extends BlockEntityRenderState {
        public @Nullable ItemClusterRenderState displayItem;
        public float ticks;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ItemDetectorTile tile, State state, float partialTick,
                                   Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(tile, state, partialTick, cameraPos, crumbling);
        if (tile.filterStack == null || tile.filterStack.isEmpty()) {
            state.displayItem = null;
            return;
        }
        state.ticks = partialTick + (float) ClientInfo.ticksInGame;
        state.displayItem = new ItemClusterRenderState();
        itemModelResolver.updateForTopItem(state.displayItem.item, tile.filterStack, ItemDisplayContext.FIXED,
                tile.getLevel(), null, (int) tile.getBlockPos().asLong());
        state.displayItem.count = 1;
        state.displayItem.seed = 0;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.displayItem == null) return;
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.ticks * 2f));
        ItemEntityRenderer.submitMultipleFromCount(poseStack, collector, state.lightCoords, state.displayItem, random);
        poseStack.popPose();
    }
}
