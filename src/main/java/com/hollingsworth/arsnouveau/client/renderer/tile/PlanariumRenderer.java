package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.LiquidBlockVertexConsumer;
import com.hollingsworth.arsnouveau.client.renderer.PlanariumRenderingWorld;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.client.renderer.world.CulledStatePos;
import com.hollingsworth.arsnouveau.common.block.tile.MirrorWeaveTile;
import com.hollingsworth.arsnouveau.common.block.tile.PlanariumTile;
import com.hollingsworth.arsnouveau.common.mixin.structure.StructureTemplateAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

// MC 1.21.11: BlockEntityRenderer now requires 2 type params <T, S extends BlockEntityRenderState>
// render() replaced by createRenderState() + extractRenderState() + submit()
// BakedModel replaced by BlockStateModel with collectParts() API
// ModelData moved from net.neoforged.neoforge.client.model.data to net.neoforged.neoforge.model.data
// TODO: Port render() to submit(). The renderer.render(fakeTile, ...) call inside render() no longer exists;
// use blockEntityRenderDispatcher.extractRenderState(fakeTile, ...) + submit().
public class PlanariumRenderer implements BlockEntityRenderer<PlanariumTile, BlockEntityRenderState> {

    public static List<WeakReference<PlanariumTile>> deferredRenders = new ArrayList<>();
    public static Map<ResourceKey<Level>, StructureRenderData> structureRenderData = new WeakHashMap<>();
    public static Direction[] DIRECTIONS = Direction.values();
    public static float pad = 0.0025f;
    public static float scale = (1.0f - 2.0f * pad) / (float) 32;
    public static float offset = pad + ((1.0f - 2.0f * pad) - 32 * scale) * 0.5f;

    public PlanariumRenderer(BlockEntityRendererProvider.Context blockRenderDispatcher) {

    }

    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }

    @Override
    public void submit(BlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        // TODO: Port planarium rendering to submit(). The old render() logic below uses MultiBufferSource
        // and renderer.render() which no longer exist. Needs full porting to extract entity states
        // and use BlockEntityRenderDispatcher.submit() or direct rendering via SubmitNodeCollector.
    }

    // Legacy render logic retained for porting reference
    // TODO: dispatcher.level, dispatcher.camera, renderer.render() all removed in 1.21.11.
    // Re-enable when full BlockEntityRenderDispatcher migration is done.
    @SuppressWarnings("unchecked")
    public void renderLegacy(PlanariumTile blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // Stub - see class-level TODO
    }

    public static void updateCulling(CulledStatePos culledStatePos, PlanariumRenderingWorld fakeRenderingWorld) {
        boolean disableEntireRender = true;
        culledStatePos.setSkipRender(false);
        for (Direction direction : DIRECTIONS) {
            BlockPos adjacentPos = culledStatePos.pos.relative(direction);
            BlockState adjacentState = fakeRenderingWorld.getBlockState(adjacentPos);
            culledStatePos.setRenderDirection(direction, false);
            if (fakeRenderingWorld.getBlockEntity(adjacentPos) instanceof MirrorWeaveTile neighborTile) {
                adjacentState = neighborTile.getStateForCulling();
            }
            // 1.21.11: getOcclusionShape() takes no arguments now
            var blockingShape = adjacentState.getOcclusionShape();
            // 1.21.11: Block.shouldRenderFace removed, use blockstate.shouldRenderFace or skip check
            if (false) { // TODO: restore shouldRenderFace logic when API is located
                continue;
            }
            if (!adjacentState.canOcclude()) {
                culledStatePos.setRenderDirection(direction, true);
                disableEntireRender = false;
                continue;
            }

            if (adjacentState.canOcclude() && !Shapes.blockOccludes(Shapes.block(), blockingShape, direction)) {
                culledStatePos.setRenderDirection(direction, true);
                disableEntireRender = false;
                continue;
            }

        }
        culledStatePos.setSkipRender(disableEntireRender);
    }


    // Taken from ModelBlockRenderer and modified to cache AO data and culled sides
    // TODO: 1.21.11 - BlockStateModel.useAmbientOcclusion(state,modelData,renderType) removed.
    // AO is now per-BlockModelPart via collectParts(). Stub until ported.
    public static void tesselateBlock(
            CulledStatePos tile,
            ModelBlockRenderer blockRenderer,
            BlockAndTintGetter level,
            BlockStateModel model,
            BlockState state,
            BlockPos pos,
            PoseStack poseStack,
            VertexConsumer consumer,
            RandomSource random,
            long seed,
            int packedOverlay,
            ModelData modelData,
            RenderType renderType
    ) {
        // Stub - see class-level TODO for porting notes
    }

    // TODO: 1.21.11 - model.getQuads() removed; use collectParts() + blockModelPart.getQuads(direction). Stub until ported.
    public static void tesselateWithoutAO(
            CulledStatePos tile,
            ModelBlockRenderer renderer,
            BlockAndTintGetter level,
            BlockStateModel model,
            BlockState state,
            BlockPos pos,
            PoseStack poseStack,
            VertexConsumer consumer,
            RandomSource random,
            long seed,
            int packedOverlay,
            net.neoforged.neoforge.model.data.ModelData modelData,
            net.minecraft.client.renderer.rendertype.RenderType renderType
    ) {
        // Stub - see class-level TODO
    }

    // copy of getLightColor but prevents other mods from intersecting this (dynamic lights) and causing unneccessary performance hits
    public static int getLightColor(BlockAndTintGetter level, BlockState state, BlockPos pos) {
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

    // TODO: 1.21.11 - BakedQuad.getVertices()/getDirection() removed (record fields); LevelRenderer.getLightColor(level,state,pos)
    // removed; ModelBlockRenderer.putQuadData() signature changed (CommonRenderStorage); calculateShape changed.
    // Stub until ported.
    private static void renderModelFaceFlat(
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
        // Stub - see class-level TODO
    }


    // TODO: 1.21.11 - model.getQuads() removed, AmbientOcclusionFace removed, calculateShape/putQuadData changed.
    // Stub until ported.
    public static void tesselateWithAO(
            CulledStatePos tile,
            ModelBlockRenderer blockRenderer,
            BlockAndTintGetter level,
            BlockStateModel model,
            BlockState state,
            BlockPos pos,
            PoseStack poseStack,
            VertexConsumer consumer,
            RandomSource random,
            long seed,
            int packedOverlay,
            net.neoforged.neoforge.model.data.ModelData modelData,
            net.minecraft.client.renderer.rendertype.RenderType renderType
    ) {
        // Stub - see class-level TODO
    }

    private static void renderModelFaceAO(
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
        // Stub - see class-level TODO
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
            generateRender(data, player.level(), renderPos, Minecraft.getInstance().gameRenderer.getMainCamera().position());
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


    /**
     * This method creates a Map<RenderType, VertexBuffer> when given an ArrayList<StatePos> statePosCache - its used both here to draw in-game AND in the TemplateManagerGUI.java class
     */
    public static void generateRender(StructureRenderData data, Level level, BlockPos renderPos, Vec3 cameraPosition) {
        float pad = 0.0025f;
        float scale = (1.0f - 2.0f * pad) / (float) 32;
        float offset = pad + ((1.0f - 2.0f * pad) - 32 * scale) * 0.5f;

        List<CulledStatePos> statePosCache = data.statePosCache;
        // TODO: VertexBuffer removed in 1.21.11 — vertexBuffers usage below is stubbed out.
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
            if (pos.needsUpdate()) {
                updateCulling(pos, data.fakeRenderingWorld);
                pos.setNeedsUpdate(false);
            }

            if (pos.shouldSkipRender()) {
                continue;
            }

            BlockStateModel ibakedmodel = dispatcher.getBlockModel(renderState);
            matrix.pushPose();
            matrix.translate(offset, offset, offset);
            matrix.scale(scale, scale, scale);
            matrix.translate(pos.pos.getX(), pos.pos.getY() + 26, pos.pos.getZ());
            // TODO: RenderType.solid()/cutout()/translucent() removed in 1.21.11. Use collectParts() + part.getRenderType().
            // Block rendering loop is stubbed out until the chunk render type API is ported.
            // for (RenderType renderType : <collectParts render types>) { ... }
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
                // TODO: VertexBuffer removed in 1.21.11 — upload stubbed out until render pipeline is ported.
                // VertexBuffer vertexBuffer = vertexBuffers.get(entry.getKey());
                // vertexBuffer.bind();
                // vertexBuffer.upload(meshDatas.get(renderType));
                // VertexBuffer.unbind();
            }
        }
    }

    public static boolean isModelRender(BlockState state) {
        // TODO: BlockStateModel.getQuads() removed in 1.21.11. Use collectParts() to check if model has geometry.
        // In 1.21.11, a model has geometry if collectParts() returns non-empty parts with quads.
        // For now, treat all non-air blocks as having models.
        return !state.isAir();
    }

    //Draw what we've cached
    public static void drawRender(PlanariumTile tile, PoseStack poseStack, Matrix4f projectionMatrix, Matrix4f modelViewMatrix, Player player) {


        if (tile == null) {
            return;
        }
        StructureRenderData data = PlanariumRenderer.structureRenderData.get(tile.key);
        if (data == null || data.vertexBuffers == null) {
            return;
        }
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().position();
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
        // TODO: RenderType.solid()/cutout()/translucent()/tripwire() and VertexBuffer removed in 1.21.11.
        // drawRender is stubbed out until the chunk render type and VertexBuffer APIs are ported.
        // ArrayList<RenderType> drawSet = new ArrayList<>();
        // drawSet.add(RenderType.solid()); drawSet.add(RenderType.cutout());
        // drawSet.add(RenderType.cutoutMipped()); drawSet.add(RenderType.translucent());
        // drawSet.add(RenderType.tripwire());
        // try {
        //     for (RenderType renderType : drawSet) {
        //         RenderType drawRenderType = renderType;
        //         VertexBuffer vertexBuffer = data.vertexBuffers.get(renderType);
        //         if (vertexBuffer.getFormat() == null) continue;
        //         drawRenderType.setupRenderState();
        //         vertexBuffer.bind();
        //         vertexBuffer.drawWithShader(poseStack.last().pose(), new Matrix4f(projectionMatrix), RenderSystem.getShader());
        //         VertexBuffer.unbind();
        //         drawRenderType.clearRenderState();
        //     }
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        poseStack.popPose();

        // TODO: 1.21.11 - dispatcher.level and dispatcher.camera fields removed. Use prepare() when API is available.
    }

    //Sort all the RenderTypes
    // TODO: VertexBuffer removed in 1.21.11 — sortAll stubbed out until render pipeline is ported.
    public static void sortAll(StructureRenderData data, BlockPos lookingAt) {
        // for (Map.Entry<RenderType, MeshData.SortState> entry : data.sortStates.entrySet()) {
        //     RenderType renderType = entry.getKey();
        //     var renderedBuffer = sort(data, lookingAt, renderType);
        //     VertexBuffer vertexBuffer = data.vertexBuffers.get(renderType);
        //     vertexBuffer.bind();
        //     vertexBuffer.uploadIndexBuffer(renderedBuffer);
        //     VertexBuffer.unbind();
        // }
    }

    //Sort the render type we pass in - using DireBufferBuilder because we want to sort in the opposite direction from normal
    public static ByteBufferBuilder.Result sort(StructureRenderData data, BlockPos lookingAt, RenderType renderType) {
        // Move our projected view in the direction of 0,0,0 by a tiny amount, accounting for negative values
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        // 1.21.11: Camera.position() → Camera.position()
        Vec3 camPos = camera.position();
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
        public int sortCounter;
        // TODO: RenderType.chunkBufferLayers() was removed in 1.21.11. Needs porting to new chunk buffer API.
        // Initialise with an empty map until the render pipeline is fully ported.
        public final Map<RenderType, ByteBufferBuilder> builders = new HashMap<>();
        // TODO: VertexBuffer was removed in 1.21.11. vertexBuffers stubbed as empty map until ported.
        @SuppressWarnings("rawtypes")
        public Map<RenderType, Object> vertexBuffers = new HashMap<>();
        public final Map<RenderType, BufferBuilder> bufferBuilders = new HashMap<>();
        public long lastUpdatedAt;

        public StructureRenderData(StructureTemplate structureTemplate, long lastUpdatedAt) {
            var accessor = (StructureTemplateAccessor) structureTemplate;
            var palettes = accessor.getPalettes();
            if (palettes.isEmpty()) {
                return;
            }
            var palette = palettes.get(0);
            statePosCache = new ArrayList<>();
            for (StructureTemplate.StructureBlockInfo blockInfo : palette.blocks()) {
                statePosCache.add(new CulledStatePos(blockInfo.state(), blockInfo.pos(), blockInfo.nbt()));
            }
            fakeRenderingWorld = new PlanariumRenderingWorld(Minecraft.getInstance().level, statePosCache);
            this.lastUpdatedAt = lastUpdatedAt;
        }

        //Get the buffer from the map, and ensure its building
        public ByteBufferBuilder getByteBuffer(RenderType renderType) {
            return builders.get(renderType);
        }
    }
}
