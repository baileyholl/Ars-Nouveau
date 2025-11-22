package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.LiquidBlockVertexConsumer;
import com.hollingsworth.arsnouveau.client.renderer.PlanariumRenderingWorld;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
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
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.BitSet;
import java.util.List;

public class PlanariumRenderer extends GeoBlockRenderer<PlanariumTile> {

    GeoModel dimModel = new PlanariumModel(true);
    static final Direction[] DIRECTIONS = Direction.values();

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

    @Override
    public GeoModel<PlanariumTile> getGeoModel() {
        return this.animatable.isDimModel ? dimModel : super.getGeoModel();
    }


    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(new PlanariumModel(false));
    }

    //    //Start rendering - this is the most expensive part, so we render it, then cache it, and draw it over and over (much cheaper)
//    public void buildRender(PlanariumTile blockEntity, StructureRenderData data, PoseStack poseStack, Player player) {
//        BlockPos renderPos = blockEntity.getBlockPos();
//        renderPos = renderPos.above();
//        //Start drawing the Render and cache it, used for both Building and Copy/Paste
//        if (shouldUpdateRender(data, renderPos)) {
//            generateRender(data, player.level(), renderPos);
//            data.lastRenderPos = renderPos;
//        }
//    }


//    public boolean shouldUpdateRender(StructureRenderData data, BlockPos renderPos) {
//        return data.lastRenderPos == null || !data.lastRenderPos.equals(renderPos);
//    }
//
//    public void clearByteBuffers(StructureRenderData data) { //Prevents leaks - Unused?
//        for (Map.Entry<RenderType, ByteBufferBuilder> entry : data.builders.entrySet()) {
//            entry.getValue().clear();
//        }
//        data.bufferBuilders.clear();
//        data.sortStates.clear();
//        data.meshDatas.clear();
//    }
//
//    public void generateRender(StructureRenderData data, Level level, BlockPos renderPos) {
//        generateRender(data, level, renderPos, Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());
//    }
//
//    /**
//     * This method creates a Map<RenderType, VertexBuffer> when given an ArrayList<StatePos> statePosCache - its used both here to draw in-game AND in the TemplateManagerGUI.java class
//     */
//    public void generateRender(StructureRenderData data, Level level, BlockPos renderPos, Vec3 cameraPosition) {
//        ArrayList<StatePos> statePosCache = data.statePosCache;
//        Map<RenderType, VertexBuffer> vertexBuffers = data.vertexBuffers;
//        if (statePosCache == null || statePosCache.isEmpty()) return;
//        data.fakeRenderingWorld = new FakeRenderingWorld(level, statePosCache, renderPos);
//        PoseStack matrix = new PoseStack(); //Create a new matrix stack for use in the buffer building process
//        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
//        ModelBlockRenderer modelBlockRenderer = dispatcher.getModelRenderer();
//        final RandomSource random = RandomSource.create();
//        clearByteBuffers(data);
//        //Iterate through the state pos cache and start drawing to the VertexBuffers - skip modelRenders(like chests) - include fluids (even though they don't work yet)
//        for (StatePos pos : statePosCache) {
//            BlockState renderState = data.fakeRenderingWorld.getBlockStateWithoutReal(pos.pos);
//            if (renderState.isAir() || !(isModelRender(pos.state) || !pos.state.getFluidState().isEmpty()))
//                continue;
//
//            BakedModel ibakedmodel = dispatcher.getBlockModel(renderState);
//            matrix.pushPose();
//            matrix.translate(pos.pos.getX(), pos.pos.getY(), pos.pos.getZ());
//
//            for (RenderType renderType : ibakedmodel.getRenderTypes(renderState, random, ModelData.EMPTY)) {
//                //Flowers render weirdly so we use a custom renderer to make them look better. Glass and Flowers are both cutouts, so we only want this for non-cube blocks
//                if (renderType.equals(RenderType.cutout()) && renderState.getShape(level, pos.pos.offset(renderPos)).equals(Shapes.block()))
//                    renderType = RenderType.translucent();
//                BufferBuilder builder = data.bufferBuilders.computeIfAbsent(renderType, rt -> new BufferBuilder(data.getByteBuffer(rt), rt.mode(), rt.format()));
//                //Use tesselateBlock to skip the block.isModel check - this helps render Create blocks that are both models AND animated
//                if (renderState.getFluidState().isEmpty()) {
//                    try {
//                        modelBlockRenderer.tesselateBlock(data.fakeRenderingWorld, ibakedmodel, renderState, pos.pos.offset(renderPos), matrix, builder, false, random, renderState.getSeed(pos.pos.offset(renderPos)), OverlayTexture.NO_OVERLAY);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        //System.out.println(e);
//                    }
//                } else {
//                    try {
//                        //RenderFluidBlock.renderFluidBlock(renderState, level, pos.pos.offset(renderPos).above(255), matrix, direVertexConsumer, true);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            matrix.popPose();
//        }
//
//        Vec3 subtracted = cameraPosition.subtract(renderPos.getX(), renderPos.getY(), renderPos.getZ());
//        Vector3f sortPos = new Vector3f((float) subtracted.x, (float) subtracted.y, (float) subtracted.z);
//        for (Map.Entry<RenderType, BufferBuilder> entry : data.bufferBuilders.entrySet()) {
//            RenderType renderType = entry.getKey();
//            ByteBufferBuilder byteBufferBuilder = data.getByteBuffer(renderType);
//            BufferBuilder builder = entry.getValue();
//            var meshDatas = data.meshDatas;
//            if (meshDatas.containsKey(renderType) && meshDatas.get(renderType) != null)
//                meshDatas.get(renderType).close();
//            meshDatas.put(renderType, builder.build());
//            if (meshDatas.containsKey(renderType) && meshDatas.get(renderType) != null) {
//                data.sortStates.put(renderType, meshDatas.get(renderType).sortQuads(byteBufferBuilder, VertexSorting.byDistance(v -> -sortPos.distanceSquared(v))));
//                VertexBuffer vertexBuffer = vertexBuffers.get(entry.getKey());
//                vertexBuffer.bind();
//                vertexBuffer.upload(meshDatas.get(renderType));
//                VertexBuffer.unbind();
//            }
//        }
//    }
//
//    public static boolean isModelRender(BlockState state) {
//        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
//        BakedModel ibakedmodel = dispatcher.getBlockModel(state);
//        for (Direction direction : Direction.values()) {
//            if (!ibakedmodel.getQuads(state, direction, RandomSource.create()).isEmpty()) {
//                return true;
//            }
//            if (!ibakedmodel.getQuads(state, null, RandomSource.create()).isEmpty()) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    //Draw what we've cached
//    public static void drawRender(PlanariumTile planariumTile, StructureRenderData data, PoseStack poseStack, Matrix4f projectionMatrix, Matrix4f modelViewMatrix, Player player) {
//        if (data.vertexBuffers == null) {
//            return;
//        }
//        BlockPos anchorPos = data.anchorPos;
//        MultiBufferSource.BufferSource buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
//        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
//        BlockPos renderPos = planariumTile.getBlockPos().above();
//        //Sort every <X> Frames to prevent screendoor effect
//        if (data.sortCounter > 20) {
//            sortAll(data, renderPos);
//            data.sortCounter = 0;
//        } else {
//            data.sortCounter++;
//        }
//
//        PoseStack matrix = poseStack;
//        matrix.pushPose();
//        matrix.mulPose(modelViewMatrix);
//        matrix.translate(
//                renderPos.getX() - projectedView.x(),
//                renderPos.getY() - projectedView.y(),
//                renderPos.getZ() - projectedView.z()
//        );
//        //Draw the renders in the specified order
//        ArrayList<RenderType> drawSet = new ArrayList<>();
//        drawSet.add(RenderType.solid());
//        drawSet.add(RenderType.cutout());
//        drawSet.add(RenderType.cutoutMipped());
//        drawSet.add(RenderType.translucent());
//        drawSet.add(RenderType.tripwire());
//        try {
//            for (RenderType renderType : drawSet) {
//                RenderType drawRenderType = renderType;
////                if (renderType.equals(RenderType.cutout()))
////                    drawRenderType = RenderType.cutout();
////                else
////                    drawRenderType = RenderType.translucent();
//                VertexBuffer vertexBuffer = data.vertexBuffers.get(renderType);
//                if (vertexBuffer.getFormat() == null)
//                    continue; //IDE says this is never null, but if we remove this check we crash because its null so....
//                drawRenderType.setupRenderState();
//                vertexBuffer.bind();
//                vertexBuffer.drawWithShader(matrix.last().pose(), new Matrix4f(projectionMatrix), RenderSystem.getShader());
//                VertexBuffer.unbind();
//                drawRenderType.clearRenderState();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        matrix.popPose();
//
//        //If any of the blocks in the render didn't have a model (like chests) we draw them here. This renders AND draws them, so more expensive than caching, but I don't think we have a choice
//        data.fakeRenderingWorld = new FakeRenderingWorld(player.level(), data.statePosCache, renderPos);
//        for (StatePos pos : data.statePosCache) {
//            if (pos.state.isAir() || isModelRender(pos.state))
//                continue;
//            matrix.pushPose();
//            matrix.translate(-projectedView.x(), -projectedView.y(), -projectedView.z());
//            matrix.translate(renderPos.getX(), renderPos.getY(), renderPos.getZ());
//            matrix.translate(pos.pos.getX(), pos.pos.getY(), pos.pos.getZ());
//            //MyRenderMethods.renderBETransparent(mockBuilderWorld.getBlockState(pos.pos), matrix, buffersource, 15728640, 655360, 0.5f);
//            BlockEntityRenderDispatcher blockEntityRenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher();
//            BlockEntity blockEntity = data.fakeRenderingWorld.getBlockEntity(pos.pos);
//            if (blockEntity != null)
//                blockEntityRenderer.render(blockEntity, 0, matrix, buffersource);

    /// /            else
    /// /                DireRenderMethods.renderBETransparent(data.fakeRenderingWorld.getBlockState(pos.pos), matrix, buffersource, 15728640, 655360, 0.5f);
//            matrix.popPose();
//        }
//    }
//
//    //Sort all the RenderTypes
//    public static void sortAll(StructureRenderData data, BlockPos lookingAt) {
//        for (Map.Entry<RenderType, MeshData.SortState> entry : data.sortStates.entrySet()) {
//            RenderType renderType = entry.getKey();
//            var renderedBuffer = sort(data, lookingAt, renderType);
//            VertexBuffer vertexBuffer = data.vertexBuffers.get(renderType);
//            vertexBuffer.bind();
//            vertexBuffer.uploadIndexBuffer(renderedBuffer);
//            VertexBuffer.unbind();
//        }
//    }
//
//    //Sort the render type we pass in - using DireBufferBuilder because we want to sort in the opposite direction from normal
//    public static ByteBufferBuilder.Result sort(StructureRenderData data, BlockPos lookingAt, RenderType renderType) {
//        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
//        Vec3 subtracted = projectedView.subtract(lookingAt.getX(), lookingAt.getY(), lookingAt.getZ());
//        Vector3f sortPos = new Vector3f((float) subtracted.x, (float) subtracted.y, (float) subtracted.z);
//        return data.sortStates.get(renderType).buildSortedIndexBuffer(data.getByteBuffer(renderType), VertexSorting.byDistance(v -> -sortPos.distanceSquared(v)));
//    }
//
//    public static class StructureRenderData {
//        public ArrayList<StatePos> statePosCache;
//        public BoundingBox boundingBox;
//        public BlockPos anchorPos;
//        public Map<RenderType, MeshData.SortState> sortStates = new HashMap<>();
//        public Map<RenderType, MeshData> meshDatas = new HashMap<>();
//        public String name;
//        public String blockprintsId;
//        public FakeRenderingWorld fakeRenderingWorld;
//        public StructureTemplate structureTemplate;
//        public Rotation rotation;
//        public Mirror mirror;
//        public boolean flipped = false;
//        public BlockPos lastRenderPos = null;
//        public int sortCounter;
//        //A map of RenderType -> DireBufferBuilder, so we can draw the different render types in proper order later
//        public final Map<RenderType, ByteBufferBuilder> builders = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((renderType) -> renderType, (type) -> new ByteBufferBuilder(type.bufferSize())));
//        //A map of RenderType -> Vertex Buffer to buffer the different render types.
//        public Map<RenderType, VertexBuffer> vertexBuffers = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((renderType) -> renderType, (type) -> new VertexBuffer(VertexBuffer.Usage.STATIC)));
//        public final Map<RenderType, BufferBuilder> bufferBuilders = new HashMap<>();
//        public StructurePlaceSettings structurePlaceSettings;
//        public double distanceFromCameraCast = 25;
//
//        public StructureRenderData(StructureTemplate structureTemplate) {
//            var accessor = (StructureTemplateAccessor) structureTemplate;
//            var palettes = accessor.getPalettes();
//            if (palettes.isEmpty()) {
//                return;
//            }
//            var palette = palettes.get(0);
//            statePosCache = new ArrayList<>();
//            this.structureTemplate = structureTemplate;
//            for (StructureTemplate.StructureBlockInfo blockInfo : palette.blocks()) {
//                statePosCache.add(new StatePos(blockInfo.state(), blockInfo.pos()));
//            }
//            structurePlaceSettings = new StructurePlaceSettings();
//            boundingBox = structureTemplate.getBoundingBox(structurePlaceSettings, new BlockPos(0, 0, 0));
//            rotation = Rotation.NONE;
//            mirror = Mirror.NONE;
//        }
//
//        public void rotate(Rotation rotateBy) {
//            rotation = rotation.getRotated(rotateBy);
//            statePosCache = StatePos.rotate(statePosCache, new ArrayList<>(), rotateBy);
//            boundingBox = structureTemplate.getBoundingBox(structurePlaceSettings.setRotation(rotation), new BlockPos(0, 0, 0));
//        }
//
//        public void mirror(boolean mirror) {
//            this.mirror = mirror ? Mirror.FRONT_BACK : Mirror.NONE;
//
//            boundingBox = structureTemplate.getBoundingBox(structurePlaceSettings.setMirror(this.mirror), new BlockPos(0, 0, 0));
//        }
//
//        public void flip() {
//            flipped = !flipped;
//            this.mirror(flipped);
//        }
//
//        //Get the buffer from the map, and ensure its building
//        public ByteBufferBuilder getByteBuffer(RenderType renderType) {
//            return builders.get(renderType);
//        }
//    }
}
