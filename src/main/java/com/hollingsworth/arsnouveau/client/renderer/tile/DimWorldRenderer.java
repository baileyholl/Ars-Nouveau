package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.DimTile;
import com.hollingsworth.arsnouveau.common.mixin.structure.StructureTemplateAccessor;
import com.hollingsworth.nuggets.client.rendering.FakeRenderingWorld;
import com.hollingsworth.nuggets.client.rendering.StatePos;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DimWorldRenderer implements BlockEntityRenderer<DimTile> {
    StructureRenderData renderData;

    public DimWorldRenderer(BlockEntityRendererProvider.Context blockRenderDispatcher) {

    }

    @Override
    public void render(DimTile blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if(blockEntity.template == null)
            return;
        StructureTemplate structureTemplate = blockEntity.template;
        BlockPos pos = BlockPos.ZERO;
        Vec3i size = new Vec3i(16, 16, 16);

        List<StatePos> statePosCache = new ArrayList<>();
        for (StructureTemplate.StructureBlockInfo blockInfo : structureTemplate.palettes.getFirst().blocks()) {
            statePosCache.add(new StatePos(blockInfo.state(), blockInfo.pos()));
        }
        poseStack.pushPose();
        // Shrink the entire structure to render above this cube as a model
        poseStack.translate(0, 1, 0);
        poseStack.scale(1.0f / size.getX(), 1.0f / size.getY(), 1.0f / size.getZ());

        for (StatePos statePos : statePosCache) {
            poseStack.pushPose();
            poseStack.translate(statePos.pos.getX(), statePos.pos.getY(), statePos.pos.getZ());
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(statePos.state, poseStack, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
        StructureTemplateAccessor accessor = (StructureTemplateAccessor) structureTemplate;
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        for (StructureTemplate.StructureEntityInfo entityInfo : accessor.getEntityInfoList()) {
            Entity entity = EntityType.loadEntityRecursive(entityInfo.nbt, blockEntity.getLevel(), (entityx) -> entityx);
            if(entity == null){
                continue;
            }
            entity.setDeltaMovement(0, 0, 0);
            entity.xo = entity.getX();
            entity.yo = entity.getY();
            entity.zo = entity.getZ();
            entity.xRotO = entity.xRot;
            entity.yRotO = entity.yRot;
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.yBodyRotO = livingEntity.yBodyRot;
                livingEntity.yHeadRotO = livingEntity.yHeadRot;
            }
            entityRenderDispatcher.render(entity, entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), partialTick, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
            for (Entity entity1 : entity.getPassengers()) {
                entityRenderDispatcher.render(entity1, 0.0D, 0.0D, 0.0D, 0.0F, partialTick, poseStack, bufferSource, packedLight);
            }
        }
        poseStack.popPose();
    }

    //Start rendering - this is the most expensive part, so we render it, then cache it, and draw it over and over (much cheaper)
    public void buildRender(DimTile blockEntity, StructureRenderData data, PoseStack poseStack, Player player) {
        BlockPos renderPos = blockEntity.getBlockPos();
        renderPos = renderPos.above();
        //Start drawing the Render and cache it, used for both Building and Copy/Paste
        if (shouldUpdateRender(data, renderPos)) {
            generateRender(data, player.level(), renderPos);
            data.lastRenderPos = renderPos;
        }
    }

    public boolean shouldUpdateRender(StructureRenderData data, BlockPos renderPos) {
        return data.lastRenderPos == null || !data.lastRenderPos.equals(renderPos);
    }

    public void clearByteBuffers(StructureRenderData data) { //Prevents leaks - Unused?
        for (Map.Entry<RenderType, ByteBufferBuilder> entry : data.builders.entrySet()) {
            entry.getValue().clear();
        }
        data.bufferBuilders.clear();
        data.sortStates.clear();
        data.meshDatas.clear();
    }

    public void generateRender(StructureRenderData data, Level level, BlockPos renderPos) {
        generateRender(data, level, renderPos, Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());
    }

    /**
     * This method creates a Map<RenderType, VertexBuffer> when given an ArrayList<StatePos> statePosCache - its used both here to draw in-game AND in the TemplateManagerGUI.java class
     */
    public void generateRender(StructureRenderData data, Level level, BlockPos renderPos, Vec3 cameraPosition) {
        ArrayList<StatePos> statePosCache = data.statePosCache;
        Map<RenderType, VertexBuffer> vertexBuffers = data.vertexBuffers;
        if (statePosCache == null || statePosCache.isEmpty()) return;
        data.fakeRenderingWorld = new FakeRenderingWorld(level, statePosCache, renderPos);
        PoseStack matrix = new PoseStack(); //Create a new matrix stack for use in the buffer building process
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer modelBlockRenderer = dispatcher.getModelRenderer();
        final RandomSource random = RandomSource.create();
        clearByteBuffers(data);
        //Iterate through the state pos cache and start drawing to the VertexBuffers - skip modelRenders(like chests) - include fluids (even though they don't work yet)
        for (StatePos pos : statePosCache) {
            BlockState renderState = data.fakeRenderingWorld.getBlockStateWithoutReal(pos.pos);
            if (renderState.isAir() || !(isModelRender(pos.state) || !pos.state.getFluidState().isEmpty()))
                continue;

            BakedModel ibakedmodel = dispatcher.getBlockModel(renderState);
            matrix.pushPose();
            matrix.translate(pos.pos.getX(), pos.pos.getY(), pos.pos.getZ());

            for (RenderType renderType : ibakedmodel.getRenderTypes(renderState, random, ModelData.EMPTY)) {
                //Flowers render weirdly so we use a custom renderer to make them look better. Glass and Flowers are both cutouts, so we only want this for non-cube blocks
                if (renderType.equals(RenderType.cutout()) && renderState.getShape(level, pos.pos.offset(renderPos)).equals(Shapes.block()))
                    renderType = RenderType.translucent();
                BufferBuilder builder = data.bufferBuilders.computeIfAbsent(renderType, rt -> new BufferBuilder(data.getByteBuffer(rt), rt.mode(), rt.format()));
                //Use tesselateBlock to skip the block.isModel check - this helps render Create blocks that are both models AND animated
                if (renderState.getFluidState().isEmpty()) {
                    try {
                        modelBlockRenderer.tesselateBlock(data.fakeRenderingWorld, ibakedmodel, renderState, pos.pos.offset(renderPos), matrix, builder, false, random, renderState.getSeed(pos.pos.offset(renderPos)), OverlayTexture.NO_OVERLAY);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //System.out.println(e);
                    }
                } else {
                    try {
                        //RenderFluidBlock.renderFluidBlock(renderState, level, pos.pos.offset(renderPos).above(255), matrix, direVertexConsumer, true);
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
    public static void drawRender(DimTile dimTile, StructureRenderData data, PoseStack poseStack, Matrix4f projectionMatrix, Matrix4f modelViewMatrix, Player player) {
        if (data.vertexBuffers == null) {
            return;
        }
        BlockPos anchorPos = data.anchorPos;
        MultiBufferSource.BufferSource buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        BlockPos renderPos = dimTile.getBlockPos().above();
        //Sort every <X> Frames to prevent screendoor effect
        if (data.sortCounter > 20) {
            sortAll(data, renderPos);
            data.sortCounter = 0;
        } else {
            data.sortCounter++;
        }

        PoseStack matrix = poseStack;
        matrix.pushPose();
        matrix.mulPose(modelViewMatrix);
        matrix.translate(
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
//                if (renderType.equals(RenderType.cutout()))
//                    drawRenderType = RenderType.cutout();
//                else
//                    drawRenderType = RenderType.translucent();
                VertexBuffer vertexBuffer = data.vertexBuffers.get(renderType);
                if (vertexBuffer.getFormat() == null)
                    continue; //IDE says this is never null, but if we remove this check we crash because its null so....
                drawRenderType.setupRenderState();
                vertexBuffer.bind();
                vertexBuffer.drawWithShader(matrix.last().pose(), new Matrix4f(projectionMatrix), RenderSystem.getShader());
                VertexBuffer.unbind();
                drawRenderType.clearRenderState();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        matrix.popPose();

        //If any of the blocks in the render didn't have a model (like chests) we draw them here. This renders AND draws them, so more expensive than caching, but I don't think we have a choice
        data.fakeRenderingWorld = new FakeRenderingWorld(player.level(), data.statePosCache, renderPos);
        for (StatePos pos : data.statePosCache) {
            if (pos.state.isAir() || isModelRender(pos.state))
                continue;
            matrix.pushPose();
            matrix.translate(-projectedView.x(), -projectedView.y(), -projectedView.z());
            matrix.translate(renderPos.getX(), renderPos.getY(), renderPos.getZ());
            matrix.translate(pos.pos.getX(), pos.pos.getY(), pos.pos.getZ());
            //MyRenderMethods.renderBETransparent(mockBuilderWorld.getBlockState(pos.pos), matrix, buffersource, 15728640, 655360, 0.5f);
            BlockEntityRenderDispatcher blockEntityRenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher();
            BlockEntity blockEntity = data.fakeRenderingWorld.getBlockEntity(pos.pos);
            if (blockEntity != null)
                blockEntityRenderer.render(blockEntity, 0, matrix, buffersource);
//            else
//                DireRenderMethods.renderBETransparent(data.fakeRenderingWorld.getBlockState(pos.pos), matrix, buffersource, 15728640, 655360, 0.5f);
            matrix.popPose();
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
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 subtracted = projectedView.subtract(lookingAt.getX(), lookingAt.getY(), lookingAt.getZ());
        Vector3f sortPos = new Vector3f((float) subtracted.x, (float) subtracted.y, (float) subtracted.z);
        return data.sortStates.get(renderType).buildSortedIndexBuffer(data.getByteBuffer(renderType), VertexSorting.byDistance(v -> -sortPos.distanceSquared(v)));
    }

    public static class StructureRenderData {
        public ArrayList<StatePos> statePosCache;
        public BoundingBox boundingBox;
        public BlockPos anchorPos;
        public Map<RenderType, MeshData.SortState> sortStates = new HashMap<>();
        public Map<RenderType, MeshData> meshDatas = new HashMap<>();
        public String name;
        public String blockprintsId;
        public FakeRenderingWorld fakeRenderingWorld;
        public StructureTemplate structureTemplate;
        public Rotation rotation;
        public Mirror mirror;
        public boolean flipped = false;
        public BlockPos lastRenderPos = null;
        public int sortCounter;
        //A map of RenderType -> DireBufferBuilder, so we can draw the different render types in proper order later
        public final Map<RenderType, ByteBufferBuilder> builders = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((renderType) -> renderType, (type) -> new ByteBufferBuilder(type.bufferSize())));
        //A map of RenderType -> Vertex Buffer to buffer the different render types.
        public Map<RenderType, VertexBuffer> vertexBuffers = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((renderType) -> renderType, (type) -> new VertexBuffer(VertexBuffer.Usage.STATIC)));
        public final Map<RenderType, BufferBuilder> bufferBuilders = new HashMap<>();
        public StructurePlaceSettings structurePlaceSettings;
        public double distanceFromCameraCast = 25;

        public StructureRenderData(StructureTemplate structureTemplate, String name, String blockprintsId) {
            var accessor = (StructureTemplateAccessor) structureTemplate;
            var palettes = accessor.getPalettes();
            if (palettes.isEmpty()) {
                return;
            }
            var palette = palettes.get(0);
            statePosCache = new ArrayList<>();
            this.structureTemplate = structureTemplate;
            for (StructureTemplate.StructureBlockInfo blockInfo : palette.blocks()) {
                statePosCache.add(new StatePos(blockInfo.state(), blockInfo.pos()));
            }
            structurePlaceSettings = new StructurePlaceSettings();
            boundingBox = structureTemplate.getBoundingBox(structurePlaceSettings, new BlockPos(0, 0, 0));
            this.name = name;
            this.blockprintsId = blockprintsId;
            rotation = Rotation.NONE;
            mirror = Mirror.NONE;
        }

        public void rotate(Rotation rotateBy) {
            rotation = rotation.getRotated(rotateBy);
            statePosCache = StatePos.rotate(statePosCache, new ArrayList<>(), rotateBy);
            boundingBox = structureTemplate.getBoundingBox(structurePlaceSettings.setRotation(rotation), new BlockPos(0, 0, 0));
        }

        public void mirror(boolean mirror) {
            this.mirror = mirror ? Mirror.FRONT_BACK : Mirror.NONE;

            boundingBox = structureTemplate.getBoundingBox(structurePlaceSettings.setMirror(this.mirror), new BlockPos(0, 0, 0));
        }

        public void flip() {
            flipped = !flipped;
            this.mirror(flipped);
        }

        //Get the buffer from the map, and ensure its building
        public ByteBufferBuilder getByteBuffer(RenderType renderType) {
            return builders.get(renderType);
        }
    }
}
