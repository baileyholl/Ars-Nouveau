package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.LiquidBlockVertexConsumer;
import com.hollingsworth.arsnouveau.client.renderer.PlanariumRenderingWorld;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.client.renderer.world.CulledStatePos;
import com.hollingsworth.arsnouveau.common.block.tile.PlanariumTile;
import com.hollingsworth.arsnouveau.common.mixin.structure.StructureTemplateAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;

public class PlanariumRenderer extends GeoBlockRenderer<PlanariumTile> {

    GeoModel dimModel = new PlanariumModel(true);

    public static List<WeakReference<PlanariumTile>> deferredRenders = new ArrayList<>();
    public static Map<ResourceKey<Level>, StructureRenderData> structureRenderData = new WeakHashMap<>();

    public PlanariumRenderer(BlockEntityRendererProvider.Context blockRenderDispatcher) {
        super(new PlanariumModel(false));

    }

    @Override
    public boolean shouldRenderOffScreen(PlanariumTile blockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(PlanariumTile blockEntity, Vec3 cameraPos) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 50;
    }


    @Override
    public void render(PlanariumTile blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        if (blockEntity.getTemplate() == null)
            return;
//        if (!LevelInLevelRenderer.renderers.containsKey(blockEntity.key)) {
//            BlockPos pos = blockEntity.getBlockPos();
//            LevelInLevelRenderer.createAndAddRenderer(blockEntity.key, blockEntity.getTemplate(), new Vector3f(pos.getX(), pos.getY() + 1, pos.getZ()));
//        }

        structureRenderData.computeIfAbsent(blockEntity.key, (be) -> {

            var data = new StructureRenderData(blockEntity.getTemplate(), blockEntity.lastUpdated);
            generateRender(data, blockEntity.getLevel(), blockEntity.getBlockPos());
            return data;
        });

        deferredRenders.add(new WeakReference<>(blockEntity));
        // old culled code
//        PlanariumTile.ClientDimEntry clientDim = PlanariumTile.clientTemplates.get(blockEntity.key);
//        PlanariumRenderingWorld fakeRenderingWorld = clientDim.fakeRenderingWorld();
//        List<CulledStatePos> statePosCache = clientDim.statePosList();
//
//        ModelBlockRenderer.enableCaching();
//        renderStateList(statePosCache, poseStack, bufferSource, fakeRenderingWorld, packedOverlay, fakeRenderingWorld);
//        if (clientDim.needsCulled()) {
//            clientDim.setCulledStates(statePosCache.stream().filter(c -> !c.shouldSkipRender()).toList());
//        }
//        ModelBlockRenderer.clearCache();
//        poseStack.popPose();
    }

//    public void renderStateList(List<CulledStatePos> statePosCache, PoseStack poseStack, MultiBufferSource bufferSource, PlanariumRenderingWorld pLevel, int pPackedOverlay, PlanariumRenderingWorld fakeRenderingWorld) {
//        float pad = 0.0025f;
//        float scale = (1.0f - 2.0f * pad) / (float) 32;
//        float offset = pad + ((1.0f - 2.0f * pad) - 32 * scale) * 0.5f;
//        for (CulledStatePos statePos : statePosCache) {
//            if (statePos.needsUpdate()) {
//                updateCulling(statePos, fakeRenderingWorld);
//                statePos.setNeedsUpdate(false);
//            }
//            if (statePos.shouldSkipRender()) {
//                continue;
//            }
//            poseStack.pushPose();
//            poseStack.translate(offset, offset, offset);
//
//            poseStack.scale(scale, scale, scale);
//            poseStack.translate(statePos.pos.getX(), 26 + statePos.pos.getY(), statePos.pos.getZ());
//            FluidState fluidState = statePos.state.getFluidState();
//            if (!fluidState.isEmpty()) {
//                final RenderType layer = ItemBlockRenderTypes.getRenderLayer(fluidState);
//                final VertexConsumer buffer = bufferSource.getBuffer(layer);
//                Minecraft.getInstance().getBlockRenderer().renderLiquid(statePos.pos, fakeRenderingWorld, new LiquidBlockVertexConsumer(buffer, poseStack, statePos.pos), statePos.state, fluidState);
//            }
//            renderBlock(statePos, poseStack, bufferSource, fakeRenderingWorld, OverlayTexture.NO_OVERLAY);
//            BlockEntity blockEntity = fakeRenderingWorld.getBlockEntity(statePos.pos);
//            if (blockEntity != null) {
//                BlockEntityRenderDispatcher blockEntityRenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher();
//                ItemStack stack = new ItemStack(statePos.state.getBlock());
//                BlockEntityWithoutLevelRenderer geckoRenderer = GeoRenderProvider.of(stack).getGeoItemRenderer();
//                if (geckoRenderer != null) {
//                    poseStack.translate(0, -0.5, 0);
//                }
//                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(statePos.state, poseStack, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
//            }
//            poseStack.popPose();
//        }
//    }
//
//    public void updateCulling(CulledStatePos culledStatePos, PlanariumRenderingWorld fakeRenderingWorld) {
//        boolean disableEntireRender = true;
//        culledStatePos.setSkipRender(false);
//        for (Direction direction : DIRECTIONS) {
//            BlockPos adjacentPos = culledStatePos.pos.relative(direction);
//            BlockState adjacentState = fakeRenderingWorld.getBlockState(adjacentPos);
//            culledStatePos.setRenderDirection(direction, false);
//            if (fakeRenderingWorld.getBlockEntity(adjacentPos) instanceof MirrorWeaveTile neighborTile) {
//                adjacentState = neighborTile.getStateForCulling();
//            }
//            var blockingShape = adjacentState.getOcclusionShape(fakeRenderingWorld, adjacentPos);
//            if (!Block.shouldRenderFace(culledStatePos.state, fakeRenderingWorld, culledStatePos.pos, direction, adjacentPos)) {
//                continue;
//            }
//            if (!adjacentState.canOcclude()) {
//                culledStatePos.setRenderDirection(direction, true);
//                disableEntireRender = false;
//                continue;
//            }
//
//            if (adjacentState.canOcclude() && !Shapes.blockOccudes(Shapes.block(), blockingShape, direction)) {
//                culledStatePos.setRenderDirection(direction, true);
//                disableEntireRender = false;
//                continue;
//            }
//
//        }
//        culledStatePos.setSkipRender(disableEntireRender);
//    }
//
//    private void renderBlock(CulledStatePos tile, PoseStack pPoseStack, MultiBufferSource pBufferSource, Level pLevel, int pPackedOverlay) {
//        renderPistonMovedBlocks(tile, tile.pos, tile.state, pPoseStack, pBufferSource, pLevel, pPackedOverlay, Minecraft.getInstance().getBlockRenderer());
//    }
//
//    public void renderPistonMovedBlocks(CulledStatePos tile, BlockPos pos, BlockState state, PoseStack stack, MultiBufferSource bufferSource, Level level, int packedOverlay, BlockRenderDispatcher blockRenderer) {
//        var model = blockRenderer.getBlockModel(state);
//        for (var renderType : model.getRenderTypes(state, RandomSource.create(state.getSeed(pos)), ModelData.EMPTY)) {
//            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderTypeHelper.getMovingBlockRenderType(renderType));
//            tesselateBlock(tile, blockRenderer.getModelRenderer(), level, model, state, pos, stack, vertexConsumer, RandomSource.create(), state.getSeed(pos), packedOverlay, ModelData.EMPTY, renderType);
//        }
//    }
//    // Taken from ModelBlockRenderer and modified to cache AO data and culled sides
//
//    public void tesselateBlock(
//            CulledStatePos tile,
//            ModelBlockRenderer blockRenderer,
//            BlockAndTintGetter level,
//            BakedModel model,
//            BlockState state,
//            BlockPos pos,
//            PoseStack poseStack,
//            VertexConsumer consumer,
//            RandomSource random,
//            long seed,
//            int packedOverlay,
//            net.neoforged.neoforge.client.model.data.ModelData modelData,
//            net.minecraft.client.renderer.RenderType renderType
//    ) {
//        boolean flag = Minecraft.useAmbientOcclusion() && switch (model.useAmbientOcclusion(state, modelData, renderType)) {
//            case TRUE -> true;
//            case DEFAULT -> state.getLightEmission(level, pos) == 0;
//            case FALSE -> false;
//        };
//        Vec3 vec3 = state.getOffset(level, pos);
//        poseStack.translate(vec3.x, vec3.y, vec3.z);
//
//        try {
//            if (flag) {
//                tesselateWithAO(tile, blockRenderer, level, model, state, pos, poseStack, consumer, random, seed, packedOverlay, modelData, renderType);
//            } else {
//                tesselateWithoutAO(tile, blockRenderer, level, model, state, pos, poseStack, consumer, random, seed, packedOverlay, modelData, renderType);
//            }
//        } catch (Throwable throwable) {
//            CrashReport crashreport = CrashReport.forThrowable(throwable, "Tesselating block model");
//            CrashReportCategory crashreportcategory = crashreport.addCategory("Block model being tesselated");
//            CrashReportCategory.populateBlockDetails(crashreportcategory, level, pos, state);
//            crashreportcategory.setDetail("Using AO", flag);
//            throw new ReportedException(crashreport);
//        }
//    }
//
//    public void tesselateWithoutAO(
//            CulledStatePos tile,
//            ModelBlockRenderer renderer,
//            BlockAndTintGetter level,
//            BakedModel model,
//            BlockState state,
//            BlockPos pos,
//            PoseStack poseStack,
//            VertexConsumer consumer,
//            RandomSource random,
//            long seed,
//            int packedOverlay,
//            net.neoforged.neoforge.client.model.data.ModelData modelData,
//            net.minecraft.client.renderer.RenderType renderType
//    ) {
//        BitSet bitset = new BitSet(3);
//        BlockPos.MutableBlockPos blockpos$mutableblockpos = pos.mutable();
//
//        for (Direction direction : DIRECTIONS) {
//            if (!tile.shouldRenderFace(direction)) {
//                continue;
//            }
//            random.setSeed(seed);
//            List<BakedQuad> list = model.getQuads(state, direction, random, modelData, renderType);
//            if (!list.isEmpty()) {
//                blockpos$mutableblockpos.setWithOffset(pos, direction);
//                int i = getLightColor(level, state, blockpos$mutableblockpos);
//                renderModelFaceFlat(renderer, level, state, pos, i, packedOverlay, false, poseStack, consumer, list, bitset);
//            }
//        }
//
//        random.setSeed(seed);
//        List<BakedQuad> list1 = model.getQuads(state, null, random, modelData, renderType);
//        if (!list1.isEmpty()) {
//            renderModelFaceFlat(renderer, level, state, pos, -1, packedOverlay, true, poseStack, consumer, list1, bitset);
//        }
//    }
//
//    // copy of getLightColor but prevents other mods from intersecting this (dynamic lights) and causing unneccessary performance hits
//    public int getLightColor(BlockAndTintGetter level, BlockState state, BlockPos pos) {
//        if (state.emissiveRendering(level, pos)) {
//            return 15728880;
//        } else {
//            int i = level.getBrightness(LightLayer.SKY, pos);
//            int j = level.getBrightness(LightLayer.BLOCK, pos);
//            int k = state.getLightEmission(level, pos);
//            if (j < k) {
//                j = k;
//            }
//
//            return i << 20 | j << 4;
//        }
//    }
//
//    /**
//     * @param repackLight {@code true} if packed light should be re-calculated
//     * @param shapeFlags  the bit set to store the shape flags in. The first bit will
//     *                    be {@code true} if the face should be offset, and the second
//     *                    if the face is less than a block in width and height.
//     */
//    private void renderModelFaceFlat(
//            ModelBlockRenderer renderer,
//            BlockAndTintGetter level,
//            BlockState state,
//            BlockPos pos,
//            int packedLight,
//            int packedOverlay,
//            boolean repackLight,
//            PoseStack poseStack,
//            VertexConsumer consumer,
//            List<BakedQuad> quads,
//            BitSet shapeFlags
//    ) {
//        for (BakedQuad bakedquad : quads) {
//            if (repackLight) {
//                renderer.calculateShape(level, state, pos, bakedquad.getVertices(), bakedquad.getDirection(), null, shapeFlags);
//                BlockPos blockpos = shapeFlags.get(0) ? pos.relative(bakedquad.getDirection()) : pos;
//                packedLight = LevelRenderer.getLightColor(level, state, blockpos);
//            }
//
//            float f = level.getShade(bakedquad.getDirection(), bakedquad.isShade());
//            renderer.putQuadData(
//                    level, state, pos, consumer, poseStack.last(), bakedquad, f, f, f, f, packedLight, packedLight, packedLight, packedLight, packedOverlay
//            );
//        }
//    }
//
//
//    public void tesselateWithAO(
//            CulledStatePos tile,
//            ModelBlockRenderer blockRenderer,
//            BlockAndTintGetter level,
//            BakedModel model,
//            BlockState state,
//            BlockPos pos,
//            PoseStack poseStack,
//            VertexConsumer consumer,
//            RandomSource random,
//            long seed,
//            int packedOverlay,
//            net.neoforged.neoforge.client.model.data.ModelData modelData,
//            net.minecraft.client.renderer.RenderType renderType
//    ) {
//        float[] afloat = new float[DIRECTIONS.length * 2];
//        BitSet bitset = new BitSet(3);
//        BlockPos.MutableBlockPos blockpos$mutableblockpos = pos.mutable();
//
//        for (Direction direction : DIRECTIONS) {
//            if (!tile.shouldRenderFace(direction)) {
//                continue;
//            }
//            random.setSeed(seed);
//            List<BakedQuad> list = model.getQuads(state, direction, random, modelData, renderType);
//            if (!list.isEmpty()) {
//                blockpos$mutableblockpos.setWithOffset(pos, direction);
//                this.renderModelFaceAO(
//                        blockRenderer,
//                        level, state, pos, poseStack, consumer, list, afloat, bitset, packedOverlay
//                );
//            }
//        }
//
//        random.setSeed(seed);
//        List<BakedQuad> list1 = model.getQuads(state, null, random, modelData, renderType);
//        if (!list1.isEmpty()) {
//            this.renderModelFaceAO(blockRenderer,
//                    level, state, pos, poseStack, consumer, list1, afloat, bitset, packedOverlay
//            );
//        }
//    }
//
//    /**
//     * @param shape      the array, of length 12, to store the shape bounds in
//     * @param shapeFlags the bit set to store the shape flags in. The first bit will
//     *                   be {@code true} if the face should be offset, and the second
//     *                   if the face is less than a block in width and height.
//     */
//    private void renderModelFaceAO(
//            ModelBlockRenderer blockRenderer,
//            BlockAndTintGetter level,
//            BlockState state,
//            BlockPos pos,
//            PoseStack poseStack,
//            VertexConsumer consumer,
//            List<BakedQuad> quads,
//            float[] shape,
//            BitSet shapeFlags,
//            int packedOverlay
//    ) {
//        ModelBlockRenderer.AmbientOcclusionFace aoFace = new ModelBlockRenderer.AmbientOcclusionFace();
//        for (BakedQuad bakedquad : quads) {
//            blockRenderer.calculateShape(level, state, pos, bakedquad.getVertices(), bakedquad.getDirection(), shape, shapeFlags);
//            if (!ClientHooks.calculateFaceWithoutAO(level, state, pos, bakedquad, shapeFlags.get(0), aoFace.brightness, aoFace.lightmap))
//                aoFace.calculate(level, state, pos, bakedquad.getDirection(), shape, shapeFlags, bakedquad.isShade());
//            blockRenderer.putQuadData(
//                    level,
//                    state,
//                    pos,
//                    consumer,
//                    poseStack.last(),
//                    bakedquad,
//                    aoFace.brightness[0],
//                    aoFace.brightness[1],
//                    aoFace.brightness[2],
//                    aoFace.brightness[3],
//                    aoFace.lightmap[0],
//                    aoFace.lightmap[1],
//                    aoFace.lightmap[2],
//                    aoFace.lightmap[3],
//                    packedOverlay
//            );
//        }
//    }

    @Override
    public GeoModel<PlanariumTile> getGeoModel() {
        return this.animatable.isDimModel ? dimModel : super.getGeoModel();
    }


    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(new PlanariumModel(false));
    }

    //    //Start rendering - this is the most expensive part, so we render it, then cache it, and draw it over and over (much cheaper)
    public static void buildRender(PlanariumTile tile, PoseStack poseStack, Player player) {
        if (tile == null) {
            return;
        }
        BlockPos renderPos = tile.getBlockPos();
        renderPos = renderPos.above();
        StructureRenderData data = PlanariumRenderer.structureRenderData.get(tile.key);
        //Start drawing the Render and cache it, used for both Building and Copy/Paste
        if (shouldUpdateRender(data, tile)) {
            clearByteBuffers(data);
            data = new StructureRenderData(tile.getTemplate(), tile.lastUpdated);
            PlanariumRenderer.structureRenderData.put(tile.key, data);
            generateRender(data, player.level(), renderPos);
            data.lastUpdatedAt = tile.lastUpdated;
        }
    }

    public static boolean shouldUpdateRender(StructureRenderData data, PlanariumTile planariumTile) {
        return data.lastUpdatedAt < planariumTile.lastUpdated;
    }

    public static void clearByteBuffers(StructureRenderData data) { //Prevents leaks - Unused?
        for (Map.Entry<RenderType, ByteBufferBuilder> entry : data.builders.entrySet()) {
            entry.getValue().clear();
        }
        data.bufferBuilders.clear();
        data.sortStates.clear();
        data.meshDatas.clear();
    }

    public static void generateRender(StructureRenderData data, Level level, BlockPos renderPos) {
        generateRender(data, level, renderPos, Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());
    }

    /**
     * This method creates a Map<RenderType, VertexBuffer> when given an ArrayList<StatePos> statePosCache - its used both here to draw in-game AND in the TemplateManagerGUI.java class
     */
    public static void generateRender(StructureRenderData data, Level level, BlockPos renderPos, Vec3 cameraPosition) {
        float pad = 0.0025f;
        float scale = (1.0f - 2.0f * pad) / (float) 32;
        float offset = pad + ((1.0f - 2.0f * pad) - 32 * scale) * 0.5f;

        List<CulledStatePos> statePosCache = data.statePosCache;
        Map<RenderType, VertexBuffer> vertexBuffers = data.vertexBuffers;
        if (statePosCache == null || statePosCache.isEmpty()) return;
        PoseStack matrix = new PoseStack();
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer modelBlockRenderer = dispatcher.getModelRenderer();
        final RandomSource random = RandomSource.create();
        clearByteBuffers(data);

        for (CulledStatePos pos : statePosCache) {
            BlockState renderState = data.fakeRenderingWorld.getBlockState(pos.pos);
            if (renderState.isAir() || !(isModelRender(pos.state) || !pos.state.getFluidState().isEmpty()))
                continue;

            BakedModel ibakedmodel = dispatcher.getBlockModel(renderState);
            matrix.pushPose();
            matrix.translate(offset, offset, offset);
            matrix.scale(scale, scale, scale);
            matrix.translate(pos.pos.getX(), pos.pos.getY() + 26, pos.pos.getZ());
            for (RenderType renderType : ibakedmodel.getRenderTypes(renderState, random, ModelData.EMPTY)) {
                //Flowers render weirdly so we use a custom renderer to make them look better. Glass and Flowers are both cutouts, so we only want this for non-cube blocks
                if (renderType.equals(RenderType.cutout()) && renderState.getShape(level, pos.pos.offset(renderPos)).equals(Shapes.block()))
                    renderType = RenderType.translucent();
                BufferBuilder builder = data.bufferBuilders.computeIfAbsent(renderType, rt -> new BufferBuilder(data.getByteBuffer(rt), rt.mode(), rt.format()));
                try {
                    FluidState fluidState = pos.state.getFluidState();
                    if (!fluidState.isEmpty()) {
                        Minecraft.getInstance().getBlockRenderer().renderLiquid(pos.pos, data.fakeRenderingWorld, new LiquidBlockVertexConsumer(builder, matrix, pos.pos), pos.state, fluidState);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (renderState.getRenderShape() != RenderShape.MODEL) {
                    continue;
                }
                //Use tesselateBlock to skip the block.isModel check - this helps render Create blocks that are both models AND animated
                if (renderState.getFluidState().isEmpty() && isModelRender(pos.state)) {
                    try {
                        modelBlockRenderer.tesselateBlock(data.fakeRenderingWorld, ibakedmodel, renderState, pos.pos.offset(renderPos), matrix, builder, true, random, renderState.getSeed(pos.pos.offset(renderPos)), OverlayTexture.NO_OVERLAY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            matrix.popPose();
        }

        Vec3 subtracted = cameraPosition.subtract(renderPos.getX(), renderPos.getY(), renderPos.getZ());
        Vector3f sortPos = new Vector3f((float) subtracted.x, (float) subtracted.y, (float) subtracted.z);
        for (Map.Entry<RenderType, BufferBuilder> entry : data.bufferBuilders.entrySet()) {
            RenderType renderType = entry.getKey();
            ByteBufferBuilder byteBufferBuilder = data.getByteBuffer(renderType);
            BufferBuilder builder = entry.getValue();
            var meshDatas = data.meshDatas;
            if (meshDatas.containsKey(renderType) && meshDatas.get(renderType) != null)
                meshDatas.get(renderType).close();
            meshDatas.put(renderType, builder.build());
            if (meshDatas.containsKey(renderType) && meshDatas.get(renderType) != null) {
                data.sortStates.put(renderType, meshDatas.get(renderType).sortQuads(byteBufferBuilder, VertexSorting.byDistance(v -> -sortPos.distanceSquared(v))));
                VertexBuffer vertexBuffer = vertexBuffers.get(entry.getKey());
                vertexBuffer.bind();
                vertexBuffer.upload(meshDatas.get(renderType));
                VertexBuffer.unbind();
            }
        }
    }

    public static boolean isModelRender(BlockState state) {
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        BakedModel ibakedmodel = dispatcher.getBlockModel(state);
        for (Direction direction : Direction.values()) {
            if (!ibakedmodel.getQuads(state, direction, RandomSource.create()).isEmpty()) {
                return true;
            }
            if (!ibakedmodel.getQuads(state, null, RandomSource.create()).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    //Draw what we've cached
    public static void drawRender(PlanariumTile tile, PoseStack poseStack, Matrix4f projectionMatrix, Matrix4f modelViewMatrix, Player player) {
        float pad = 0.0025f;
        float scale = (1.0f - 2.0f * pad) / (float) 32;
        float offset = pad + ((1.0f - 2.0f * pad) - 32 * scale) * 0.5f;


        if (tile == null) {
            return;
        }
        StructureRenderData data = PlanariumRenderer.structureRenderData.get(tile.key);
        if (data == null || data.vertexBuffers == null) {
            return;
        }
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        BlockPos renderPos = tile.getBlockPos();
        //Sort every <X> Frames to prevent screendoor effect
        if (data.sortCounter > 20) {
            sortAll(data, renderPos);
            data.sortCounter = 0;
        } else {
            data.sortCounter++;
        }

        poseStack.pushPose();

        poseStack.mulPose(modelViewMatrix);
        poseStack.translate(
                renderPos.getX() - projectedView.x(),
                renderPos.getY() - projectedView.y(),
                renderPos.getZ() - projectedView.z()
        );
        //Draw the renders in the specified order
        ArrayList<RenderType> drawSet = new ArrayList<>();
        drawSet.add(RenderType.solid());
        drawSet.add(RenderType.cutout());
        drawSet.add(RenderType.cutoutMipped());
        drawSet.add(RenderType.translucent());
        drawSet.add(RenderType.tripwire());
        try {
            for (RenderType renderType : drawSet) {
                RenderType drawRenderType = renderType;
                VertexBuffer vertexBuffer = data.vertexBuffers.get(renderType);
                if (vertexBuffer.getFormat() == null)
                    continue;
                drawRenderType.setupRenderState();
                vertexBuffer.bind();
                vertexBuffer.drawWithShader(poseStack.last().pose(), new Matrix4f(projectionMatrix), RenderSystem.getShader());
                VertexBuffer.unbind();
                drawRenderType.clearRenderState();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        poseStack.popPose();

        BlockEntityRenderDispatcher dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
        Level originalLevel = dispatcher.level;
        dispatcher.prepare(originalLevel, Minecraft.getInstance().getBlockEntityRenderDispatcher().camera, Minecraft.getInstance().hitResult);

        for (BlockEntity blockEntity : data.fakeRenderingWorld.blockEntityMap.values()) {
            BlockPos pos = blockEntity.getBlockPos();
            poseStack.pushPose();

            poseStack.translate(-projectedView.x(), -projectedView.y(), -projectedView.z());
            poseStack.translate(renderPos.getX(), renderPos.getY(), renderPos.getZ());
            poseStack.translate(offset, offset, offset);

            poseStack.scale(scale, scale, scale);
            poseStack.translate(pos.getX(), 26 + pos.getY(), pos.getZ());
            BlockEntityRenderer renderer = dispatcher.getRenderer(blockEntity);
            if (renderer != null) {
                renderer.render(blockEntity, 1.0f, poseStack, data.bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            }
            poseStack.popPose();
        }
    }

    //Sort all the RenderTypes
    public static void sortAll(StructureRenderData data, BlockPos lookingAt) {
        for (Map.Entry<RenderType, MeshData.SortState> entry : data.sortStates.entrySet()) {
            RenderType renderType = entry.getKey();
            var renderedBuffer = sort(data, lookingAt, renderType);
            VertexBuffer vertexBuffer = data.vertexBuffers.get(renderType);
            vertexBuffer.bind();
            vertexBuffer.uploadIndexBuffer(renderedBuffer);
            VertexBuffer.unbind();
        }
    }

    //Sort the render type we pass in - using DireBufferBuilder because we want to sort in the opposite direction from normal
    public static ByteBufferBuilder.Result sort(StructureRenderData data, BlockPos lookingAt, RenderType renderType) {
        // Move our projected view in the direction of 0,0,0 by a tiny amount, accounting for negative values
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();
        Vector3f sortPos = new Vector3f((float) camPos.x, (float) camPos.y, (float) camPos.z);

        return data.sortStates.get(renderType).buildSortedIndexBuffer(
                data.getByteBuffer(renderType),
                VertexSorting.byDistance(v -> -sortPos.distanceSquared(v))
        );
    }

    public static class StructureRenderData {
        public ArrayList<CulledStatePos> statePosCache;
        public Map<RenderType, MeshData.SortState> sortStates = new HashMap<>();
        public Map<RenderType, MeshData> meshDatas = new HashMap<>();
        public PlanariumRenderingWorld fakeRenderingWorld;
        public StructureTemplate structureTemplate;
        public int sortCounter;
        public final Map<RenderType, ByteBufferBuilder> builders = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((renderType) -> renderType, (type) -> new ByteBufferBuilder(type.bufferSize())));
        public Map<RenderType, VertexBuffer> vertexBuffers = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((renderType) -> renderType, (type) -> new VertexBuffer(VertexBuffer.Usage.STATIC)));
        public final Map<RenderType, BufferBuilder> bufferBuilders = new HashMap<>();
        public StructurePlaceSettings structurePlaceSettings;
        public long lastUpdatedAt;
        public MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new ByteBufferBuilder(1536));

        public StructureRenderData(StructureTemplate structureTemplate, long lastUpdatedAt) {
            var accessor = (StructureTemplateAccessor) structureTemplate;
            var palettes = accessor.getPalettes();
            if (palettes.isEmpty()) {
                return;
            }
            var palette = palettes.get(0);
            statePosCache = new ArrayList<>();
            this.structureTemplate = structureTemplate;
            for (StructureTemplate.StructureBlockInfo blockInfo : palette.blocks()) {
                statePosCache.add(new CulledStatePos(blockInfo.state(), blockInfo.pos(), blockInfo.nbt()));
            }
            structurePlaceSettings = new StructurePlaceSettings();
            fakeRenderingWorld = new PlanariumRenderingWorld(Minecraft.getInstance().level, statePosCache);
            this.lastUpdatedAt = lastUpdatedAt;
        }

        //Get the buffer from the map, and ensure its building
        public ByteBufferBuilder getByteBuffer(RenderType renderType) {
            return builders.get(renderType);
        }
    }
}
