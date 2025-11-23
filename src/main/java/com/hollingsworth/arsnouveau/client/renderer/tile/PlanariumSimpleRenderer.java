package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.LiquidBlockVertexConsumer;
import com.hollingsworth.arsnouveau.client.renderer.PlanariumRenderingWorld;
import com.hollingsworth.arsnouveau.client.renderer.world.CulledStatePos;
import com.hollingsworth.arsnouveau.common.block.tile.MirrorWeaveTile;
import com.hollingsworth.arsnouveau.common.block.tile.PlanariumTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;
import java.util.List;

public class PlanariumSimpleRenderer implements BlockEntityRenderer<PlanariumTile> {
    static final Direction[] DIRECTIONS = Direction.values();

    public PlanariumSimpleRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(@NotNull PlanariumTile blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (blockEntity.getTemplate() == null)
            return;
        poseStack.pushPose();
        PlanariumTile.ClientDimEntry clientDim = PlanariumTile.clientTemplates.get(blockEntity.key);
        PlanariumRenderingWorld fakeRenderingWorld = clientDim.fakeRenderingWorld();
        List<CulledStatePos> statePosCache = clientDim.statePosList();

        ModelBlockRenderer.enableCaching();
        renderStateList(statePosCache, poseStack, bufferSource, fakeRenderingWorld, packedOverlay, fakeRenderingWorld);
        if (clientDim.needsCulled()) {
            clientDim.setCulledStates(statePosCache.stream().filter(c -> !c.shouldSkipRender()).toList());
        }
        ModelBlockRenderer.clearCache();
        poseStack.popPose();
    }

    public void renderStateList(List<CulledStatePos> statePosCache, PoseStack poseStack, MultiBufferSource bufferSource, PlanariumRenderingWorld pLevel, int pPackedOverlay, PlanariumRenderingWorld fakeRenderingWorld) {
        float pad = 0.0025f;
        float scale = (1.0f - 2.0f * pad) / (float) 32;
        float offset = pad + ((1.0f - 2.0f * pad) - 32 * scale) * 0.5f;
        for (CulledStatePos statePos : statePosCache) {
            if (statePos.needsUpdate()) {
                updateCulling(statePos, fakeRenderingWorld);
                statePos.setNeedsUpdate(false);
            }
            if (statePos.shouldSkipRender()) {
                continue;
            }
            poseStack.pushPose();
            poseStack.translate(offset, offset, offset);

            poseStack.scale(scale, scale, scale);
            poseStack.translate(statePos.pos.getX(), 26 + statePos.pos.getY(), statePos.pos.getZ());
            FluidState fluidState = statePos.state.getFluidState();
            if (!fluidState.isEmpty()) {
                final RenderType layer = ItemBlockRenderTypes.getRenderLayer(fluidState);
                final VertexConsumer buffer = bufferSource.getBuffer(layer);
                Minecraft.getInstance().getBlockRenderer().renderLiquid(statePos.pos, fakeRenderingWorld, new LiquidBlockVertexConsumer(buffer, poseStack, statePos.pos), statePos.state, fluidState);
            }
            renderBlock(statePos, poseStack, bufferSource, fakeRenderingWorld, OverlayTexture.NO_OVERLAY);
//            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(statePos.state, poseStack, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }

    public void updateCulling(CulledStatePos culledStatePos, PlanariumRenderingWorld fakeRenderingWorld) {
        boolean disableEntireRender = true;
        culledStatePos.setSkipRender(false);
        for (Direction direction : DIRECTIONS) {
            BlockPos adjacentPos = culledStatePos.pos.relative(direction);
            BlockState adjacentState = fakeRenderingWorld.getBlockState(adjacentPos);
            culledStatePos.setRenderDirection(direction, false);
            if (fakeRenderingWorld.getBlockEntity(adjacentPos) instanceof MirrorWeaveTile neighborTile) {
                adjacentState = neighborTile.getStateForCulling();
            }
            var blockingShape = adjacentState.getOcclusionShape(fakeRenderingWorld, adjacentPos);
            if (!Block.shouldRenderFace(culledStatePos.state, fakeRenderingWorld, culledStatePos.pos, direction, adjacentPos)) {
                continue;
            }
            if (!adjacentState.canOcclude()) {
                culledStatePos.setRenderDirection(direction, true);
                disableEntireRender = false;
                continue;
            }

            if (adjacentState.canOcclude() && !Shapes.blockOccudes(Shapes.block(), blockingShape, direction)) {
                culledStatePos.setRenderDirection(direction, true);
                disableEntireRender = false;
                continue;
            }

        }
        culledStatePos.setSkipRender(disableEntireRender);
    }

    private void renderBlock(CulledStatePos tile, PoseStack pPoseStack, MultiBufferSource pBufferSource, Level pLevel, int pPackedOverlay) {
        renderPistonMovedBlocks(tile, tile.pos, tile.state, pPoseStack, pBufferSource, pLevel, pPackedOverlay, Minecraft.getInstance().getBlockRenderer());
    }

    public void renderPistonMovedBlocks(CulledStatePos tile, BlockPos pos, BlockState state, PoseStack stack, MultiBufferSource bufferSource, Level level, int packedOverlay, BlockRenderDispatcher blockRenderer) {
        var model = blockRenderer.getBlockModel(state);
        for (var renderType : model.getRenderTypes(state, RandomSource.create(state.getSeed(pos)), ModelData.EMPTY)) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderTypeHelper.getMovingBlockRenderType(renderType));
            tesselateBlock(tile, blockRenderer.getModelRenderer(), level, model, state, pos, stack, vertexConsumer, RandomSource.create(), state.getSeed(pos), packedOverlay, ModelData.EMPTY, renderType);
        }
    }
    // Taken from ModelBlockRenderer and modified to cache AO data and culled sides

    public void tesselateBlock(
            CulledStatePos tile,
            ModelBlockRenderer blockRenderer,
            BlockAndTintGetter level,
            BakedModel model,
            BlockState state,
            BlockPos pos,
            PoseStack poseStack,
            VertexConsumer consumer,
            RandomSource random,
            long seed,
            int packedOverlay,
            net.neoforged.neoforge.client.model.data.ModelData modelData,
            net.minecraft.client.renderer.RenderType renderType
    ) {
        boolean flag = Minecraft.useAmbientOcclusion() && switch (model.useAmbientOcclusion(state, modelData, renderType)) {
            case TRUE -> true;
            case DEFAULT -> state.getLightEmission(level, pos) == 0;
            case FALSE -> false;
        };
        Vec3 vec3 = state.getOffset(level, pos);
        poseStack.translate(vec3.x, vec3.y, vec3.z);

        try {
            if (flag) {
                tesselateWithAO(tile, blockRenderer, level, model, state, pos, poseStack, consumer, random, seed, packedOverlay, modelData, renderType);
            } else {
                tesselateWithoutAO(tile, blockRenderer, level, model, state, pos, poseStack, consumer, random, seed, packedOverlay, modelData, renderType);
            }
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Tesselating block model");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block model being tesselated");
            CrashReportCategory.populateBlockDetails(crashreportcategory, level, pos, state);
            crashreportcategory.setDetail("Using AO", flag);
            throw new ReportedException(crashreport);
        }
    }

    public void tesselateWithoutAO(
            CulledStatePos tile,
            ModelBlockRenderer renderer,
            BlockAndTintGetter level,
            BakedModel model,
            BlockState state,
            BlockPos pos,
            PoseStack poseStack,
            VertexConsumer consumer,
            RandomSource random,
            long seed,
            int packedOverlay,
            net.neoforged.neoforge.client.model.data.ModelData modelData,
            net.minecraft.client.renderer.RenderType renderType
    ) {
        BitSet bitset = new BitSet(3);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = pos.mutable();

        for (Direction direction : DIRECTIONS) {
            if (!tile.shouldRenderFace(direction)) {
                continue;
            }
            random.setSeed(seed);
            List<BakedQuad> list = model.getQuads(state, direction, random, modelData, renderType);
            if (!list.isEmpty()) {
                blockpos$mutableblockpos.setWithOffset(pos, direction);
                int i = getLightColor(level, state, blockpos$mutableblockpos);
                renderModelFaceFlat(renderer, level, state, pos, i, packedOverlay, false, poseStack, consumer, list, bitset);
            }
        }

        random.setSeed(seed);
        List<BakedQuad> list1 = model.getQuads(state, null, random, modelData, renderType);
        if (!list1.isEmpty()) {
            renderModelFaceFlat(renderer, level, state, pos, -1, packedOverlay, true, poseStack, consumer, list1, bitset);
        }
    }

    // copy of getLightColor but prevents other mods from intersecting this (dynamic lights) and causing unneccessary performance hits
    public int getLightColor(BlockAndTintGetter level, BlockState state, BlockPos pos) {
        if (state.emissiveRendering(level, pos)) {
            return 15728880;
        } else {
            int i = level.getBrightness(LightLayer.SKY, pos);
            int j = level.getBrightness(LightLayer.BLOCK, pos);
            int k = state.getLightEmission(level, pos);
            if (j < k) {
                j = k;
            }

            return i << 20 | j << 4;
        }
    }

    /**
     * @param repackLight {@code true} if packed light should be re-calculated
     * @param shapeFlags  the bit set to store the shape flags in. The first bit will
     *                    be {@code true} if the face should be offset, and the second
     *                    if the face is less than a block in width and height.
     */
    private void renderModelFaceFlat(
            ModelBlockRenderer renderer,
            BlockAndTintGetter level,
            BlockState state,
            BlockPos pos,
            int packedLight,
            int packedOverlay,
            boolean repackLight,
            PoseStack poseStack,
            VertexConsumer consumer,
            List<BakedQuad> quads,
            BitSet shapeFlags
    ) {
        for (BakedQuad bakedquad : quads) {
            if (repackLight) {
                renderer.calculateShape(level, state, pos, bakedquad.getVertices(), bakedquad.getDirection(), null, shapeFlags);
                BlockPos blockpos = shapeFlags.get(0) ? pos.relative(bakedquad.getDirection()) : pos;
                packedLight = LevelRenderer.getLightColor(level, state, blockpos);
            }

            float f = level.getShade(bakedquad.getDirection(), bakedquad.isShade());
            renderer.putQuadData(
                    level, state, pos, consumer, poseStack.last(), bakedquad, f, f, f, f, packedLight, packedLight, packedLight, packedLight, packedOverlay
            );
        }
    }


    public void tesselateWithAO(
            CulledStatePos tile,
            ModelBlockRenderer blockRenderer,
            BlockAndTintGetter level,
            BakedModel model,
            BlockState state,
            BlockPos pos,
            PoseStack poseStack,
            VertexConsumer consumer,
            RandomSource random,
            long seed,
            int packedOverlay,
            net.neoforged.neoforge.client.model.data.ModelData modelData,
            net.minecraft.client.renderer.RenderType renderType
    ) {
        float[] afloat = new float[DIRECTIONS.length * 2];
        BitSet bitset = new BitSet(3);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = pos.mutable();

        for (Direction direction : DIRECTIONS) {
            if (!tile.shouldRenderFace(direction)) {
                continue;
            }
            random.setSeed(seed);
            List<BakedQuad> list = model.getQuads(state, direction, random, modelData, renderType);
            if (!list.isEmpty()) {
                blockpos$mutableblockpos.setWithOffset(pos, direction);
                this.renderModelFaceAO(
                        blockRenderer,
                        level, state, pos, poseStack, consumer, list, afloat, bitset, packedOverlay
                );
            }
        }

        random.setSeed(seed);
        List<BakedQuad> list1 = model.getQuads(state, null, random, modelData, renderType);
        if (!list1.isEmpty()) {
            this.renderModelFaceAO(blockRenderer,
                    level, state, pos, poseStack, consumer, list1, afloat, bitset, packedOverlay
            );
        }
    }

    /**
     * @param shape      the array, of length 12, to store the shape bounds in
     * @param shapeFlags the bit set to store the shape flags in. The first bit will
     *                   be {@code true} if the face should be offset, and the second
     *                   if the face is less than a block in width and height.
     */
    private void renderModelFaceAO(
            ModelBlockRenderer blockRenderer,
            BlockAndTintGetter level,
            BlockState state,
            BlockPos pos,
            PoseStack poseStack,
            VertexConsumer consumer,
            List<BakedQuad> quads,
            float[] shape,
            BitSet shapeFlags,
            int packedOverlay
    ) {
        ModelBlockRenderer.AmbientOcclusionFace aoFace = new ModelBlockRenderer.AmbientOcclusionFace();
        for (BakedQuad bakedquad : quads) {
            blockRenderer.calculateShape(level, state, pos, bakedquad.getVertices(), bakedquad.getDirection(), shape, shapeFlags);
            if (!ClientHooks.calculateFaceWithoutAO(level, state, pos, bakedquad, shapeFlags.get(0), aoFace.brightness, aoFace.lightmap))
                aoFace.calculate(level, state, pos, bakedquad.getDirection(), shape, shapeFlags, bakedquad.isShade());
            blockRenderer.putQuadData(
                    level,
                    state,
                    pos,
                    consumer,
                    poseStack.last(),
                    bakedquad,
                    aoFace.brightness[0],
                    aoFace.brightness[1],
                    aoFace.brightness[2],
                    aoFace.brightness[3],
                    aoFace.lightmap[0],
                    aoFace.lightmap[1],
                    aoFace.lightmap[2],
                    aoFace.lightmap[3],
                    packedOverlay
            );
        }
    }


}
