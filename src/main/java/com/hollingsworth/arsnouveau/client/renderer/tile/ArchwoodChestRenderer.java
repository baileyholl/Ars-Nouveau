package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

// MC 1.21.11: BlockEntityRenderer with render-state pattern; uses submitModelPart
public class ArchwoodChestRenderer<T extends BlockEntity & LidBlockEntity> implements BlockEntityRenderer<T, ArchwoodChestRenderer.State> {

    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;
    private final ModelPart doubleLeftLid;
    private final ModelPart doubleLeftBottom;
    private final ModelPart doubleLeftLock;
    private final ModelPart doubleRightLid;
    private final ModelPart doubleRightBottom;
    private final ModelPart doubleRightLock;
    private final MaterialSet materials;

    public ArchwoodChestRenderer(BlockEntityRendererProvider.Context context) {
        this.materials = context.materials();
        ModelPart modelpart = context.bakeLayer(ModelLayers.CHEST);
        this.bottom = modelpart.getChild("bottom");
        this.lid = modelpart.getChild("lid");
        this.lock = modelpart.getChild("lock");
        ModelPart modelpart1 = context.bakeLayer(ModelLayers.DOUBLE_CHEST_LEFT);
        this.doubleLeftBottom = modelpart1.getChild("bottom");
        this.doubleLeftLid = modelpart1.getChild("lid");
        this.doubleLeftLock = modelpart1.getChild("lock");
        ModelPart modelpart2 = context.bakeLayer(ModelLayers.DOUBLE_CHEST_RIGHT);
        this.doubleRightBottom = modelpart2.getChild("bottom");
        this.doubleRightLid = modelpart2.getChild("lid");
        this.doubleRightLock = modelpart2.getChild("lock");
    }

    public static class State extends BlockEntityRenderState {
        public float lidAngle;
        public ChestType chestType;
        public Direction facing;
        public boolean isAbstractChest;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(T tile, State state, float partialTick,
                                   Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(tile, state, partialTick, cameraPos, crumbling);
        Level world = tile.getLevel();
        BlockState blockstate = (world != null)
                ? tile.getBlockState()
                : Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
        state.chestType = blockstate.hasProperty(ChestBlock.TYPE) ? blockstate.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
        state.facing = blockstate.getValue(ChestBlock.FACING);
        state.isAbstractChest = blockstate.getBlock() instanceof AbstractChestBlock;

        if (state.isAbstractChest && world != null) {
            AbstractChestBlock<?> abstractchestblock = (AbstractChestBlock<?>) blockstate.getBlock();
            DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combiner =
                    abstractchestblock.combine(blockstate, world, tile.getBlockPos(), true);
            float f1 = combiner.apply(ChestBlock.opennessCombiner(tile)).get(partialTick);
            f1 = 1.0F - f1;
            state.lidAngle = 1.0F - f1 * f1 * f1;
        } else {
            state.lidAngle = 0;
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (!state.isAbstractChest) return;
        poseStack.pushPose();
        float f = state.facing.toYRot();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-f));
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        Material rendermaterial = getMaterial(state.chestType);
        RenderType renderType = rendermaterial.renderType(RenderTypes::entityCutout);

        boolean flag1 = state.chestType != ChestType.SINGLE;
        ModelPart usedLid, usedLock, usedBottom;
        if (flag1) {
            if (state.chestType == ChestType.RIGHT) {
                usedLid = doubleRightLid; usedLock = doubleRightLock; usedBottom = doubleRightBottom;
            } else {
                usedLid = doubleLeftLid; usedLock = doubleLeftLock; usedBottom = doubleLeftBottom;
            }
        } else {
            usedLid = lid; usedLock = lock; usedBottom = bottom;
        }
        usedLid.xRot = -(state.lidAngle * ((float) Math.PI / 2F));
        usedLock.xRot = usedLid.xRot;
        collector.submitModelPart(usedLid, poseStack, renderType, state.lightCoords, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, null);
        collector.submitModelPart(usedLock, poseStack, renderType, state.lightCoords, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, null);
        collector.submitModelPart(usedBottom, poseStack, renderType, state.lightCoords, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, null);

        poseStack.popPose();
    }

    protected Material getMaterial(ChestType chestType) {
        return switch (chestType) {
            case LEFT -> net.minecraft.client.renderer.Sheets.CHEST_MAPPER.apply(ArsNouveau.prefix("archwood_left"));
            case RIGHT -> net.minecraft.client.renderer.Sheets.CHEST_MAPPER.apply(ArsNouveau.prefix("archwood_right"));
            default -> net.minecraft.client.renderer.Sheets.CHEST_MAPPER.apply(ArsNouveau.prefix("archwood"));
        };
    }
}
