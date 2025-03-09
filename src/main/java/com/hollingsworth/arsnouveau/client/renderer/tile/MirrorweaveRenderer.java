package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.MirrorWeaveTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.*;

import static net.minecraft.world.level.block.Block.OCCLUSION_CACHE;

public class MirrorweaveRenderer implements BlockEntityRenderer<MirrorWeaveTile> {
    private BlockRenderDispatcher blockRenderer;



    public MirrorweaveRenderer(BlockEntityRendererProvider.Context pContext) {
        this.blockRenderer = pContext.getBlockRenderDispatcher();
    }

    @Override
    public void render(MirrorWeaveTile tileEntityIn, float partialTick, PoseStack pPoseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState renderState = tileEntityIn.mimicState;
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(ModPotions.MAGIC_FIND_EFFECT)){
            renderState = BlockRegistry.MIRROR_WEAVE.defaultBlockState();
        }
        if (renderState == null)
            return;
        if(tileEntityIn.clientData == null){
            tileEntityIn.clientData = new MirrorWeaveTile.ClientData();
        }
        MirrorWeaveTile.ClientData clientData = tileEntityIn.clientData;
        if(clientData.renderInvalid || tileEntityIn.renderInvalid){
            System.out.println("set invalid");
            tileEntityIn.renderInvalid = false;
            clientData.renderInvalid = false;
            clientData.aoCalcMap = new HashMap<>();
            clientData.nullDirectionAO = null;
            clientData.renderDirections = new HashSet<>();
            for(Direction direction : Direction.values()){
                BlockPos blockingPos = tileEntityIn.getBlockPos().relative(direction);
                BlockState blockingState = tileEntityIn.getLevel().getBlockState(blockingPos);
                if(tileEntityIn.getLevel().getBlockEntity(blockingPos) instanceof MirrorWeaveTile neighborTile){
                    blockingState = neighborTile.mimicState;
                }
                var blockingShape = blockingState.getOcclusionShape(tileEntityIn.getLevel(), blockingPos);
                if(!shouldRenderFace(renderState, blockingState, tileEntityIn.getLevel(), tileEntityIn.getBlockPos(), direction, blockingPos)){
                    System.out.println("skipping " + direction);
                    continue;
                }
                if(!blockingState.canOcclude()) {
                    clientData.renderDirections.add(direction);
                    System.out.println("neightbor is not occluding" + blockingState);
                    continue;
                }

                if(blockingState.canOcclude() && Shapes.blockOccudes(Shapes.block(), blockingShape, direction)){
                    clientData.renderDirections.add(direction);
                    continue;
                }
                System.out.println("skipping " + direction);

            }
        }

        pPoseStack.pushPose();
        renderBlock(tileEntityIn, tileEntityIn.getBlockPos(), renderState, pPoseStack, bufferIn, tileEntityIn.getLevel(), true, combinedOverlayIn);
        pPoseStack.popPose();
    }

    public static boolean shouldRenderFace(BlockState state, BlockState blockstate, Level level, BlockPos offset, Direction face, BlockPos pos) {
        if (state.skipRendering(blockstate, face)) {
            return false;
        } else if (blockstate.hidesNeighborFace(level, pos, state, face.getOpposite()) && state.supportsExternalFaceHiding()) {
            return false;
        } else if (blockstate.canOcclude()) {
            Block.BlockStatePairKey block$blockstatepairkey = new Block.BlockStatePairKey(state, blockstate, face);
            Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> object2bytelinkedopenhashmap = OCCLUSION_CACHE.get();
            byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block$blockstatepairkey);
            if (b0 != 127) {
                return b0 != 0;
            } else {
                VoxelShape voxelshape = state.getFaceOcclusionShape(level, offset, face);
                if (voxelshape.isEmpty()) {
                    return true;
                } else {
                    VoxelShape voxelshape1 = blockstate.getFaceOcclusionShape(level, pos, face.getOpposite());
                    boolean flag = Shapes.joinIsNotEmpty(voxelshape, voxelshape1, BooleanOp.ONLY_FIRST);
                    if (object2bytelinkedopenhashmap.size() == 2048) {
                        object2bytelinkedopenhashmap.removeLastByte();
                    }

                    object2bytelinkedopenhashmap.putAndMoveToFirst(block$blockstatepairkey, (byte)(flag ? 1 : 0));
                    return flag;
                }
            }
        } else {
            return true;
        }
    }

    private void renderBlock(MirrorWeaveTile tile, BlockPos pPos, BlockState pState, PoseStack pPoseStack, MultiBufferSource pBufferSource, Level pLevel, boolean pExtended, int pPackedOverlay) {
        renderPistonMovedBlocks(tile, pPos, pState, pPoseStack, pBufferSource, pLevel, pExtended, pPackedOverlay, Minecraft.getInstance().getBlockRenderer());
    }
    static final Direction[] DIRECTIONS = Direction.values();

    public void renderPistonMovedBlocks(MirrorWeaveTile tile, BlockPos pos, BlockState state, PoseStack stack, MultiBufferSource bufferSource, Level level, boolean checkSides, int packedOverlay, BlockRenderDispatcher blockRenderer) {
        var model = blockRenderer.getBlockModel(state);
        for (var renderType : model.getRenderTypes(state, RandomSource.create(state.getSeed(pos)), ModelData.EMPTY)) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderTypeHelper.getMovingBlockRenderType(renderType));
            tesselateBlock(tile, blockRenderer.getModelRenderer(), level, model, state, pos, stack, vertexConsumer, checkSides, RandomSource.create(), state.getSeed(pos), packedOverlay, ModelData.EMPTY, renderType);
        }
    }

    /**
     * @param checkSides if {@code true}, only renders each side if {@link
     *                   net.minecraft.world.level.block.Block#shouldRenderFace(
     *                   net.minecraft.world.level.block.state.BlockState,
     *                   net.minecraft.world.level.BlockGetter,
     *                   net.minecraft.core.BlockPos, net.minecraft.core.Direction,
     *                   net.minecraft.core.BlockPos)} returns {@code true}
     */
    public void tesselateBlock(
            MirrorWeaveTile tile,
            ModelBlockRenderer blockRenderer,
            BlockAndTintGetter level,
            BakedModel model,
            BlockState state,
            BlockPos pos,
            PoseStack poseStack,
            VertexConsumer consumer,
            boolean checkSides,
            RandomSource random,
            long seed,
            int packedOverlay,
            net.neoforged.neoforge.client.model.data.ModelData modelData,
            net.minecraft.client.renderer.RenderType renderType
    ) {
        boolean flag = Minecraft.useAmbientOcclusion() && switch(model.useAmbientOcclusion(state, modelData, renderType)) {
            case TRUE -> true;
            case DEFAULT -> state.getLightEmission(level, pos) == 0;
            case FALSE -> false;
        };
        Vec3 vec3 = state.getOffset(level, pos);
        poseStack.translate(vec3.x, vec3.y, vec3.z);

        try {
            if (flag) {
                tesselateWithAO(tile, blockRenderer, level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay, modelData, renderType);
            } else {
                blockRenderer.tesselateWithoutAO(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay, modelData, renderType);
            }
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Tesselating block model");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block model being tesselated");
            CrashReportCategory.populateBlockDetails(crashreportcategory, level, pos, state);
            crashreportcategory.setDetail("Using AO", flag);
            throw new ReportedException(crashreport);
        }
    }

    /**
     * @param checkSides if {@code true}, only renders each side if {@link
     *                   net.minecraft.world.level.block.Block#shouldRenderFace(
     *                   net.minecraft.world.level.block.state.BlockState,
     *                   net.minecraft.world.level.BlockGetter,
     *                   net.minecraft.core.BlockPos, net.minecraft.core.Direction,
     *                   net.minecraft.core.BlockPos)} returns {@code true}
     */
    public void tesselateWithAO(
            MirrorWeaveTile tile,
            ModelBlockRenderer blockRenderer,
            BlockAndTintGetter level,
            BakedModel model,
            BlockState state,
            BlockPos pos,
            PoseStack poseStack,
            VertexConsumer consumer,
            boolean checkSides,
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
            random.setSeed(seed);
            List<BakedQuad> list = model.getQuads(state, direction, random, modelData, renderType);
            if (!list.isEmpty()) {
                blockpos$mutableblockpos.setWithOffset(pos, direction);
                if (!checkSides || tile.clientData.renderDirections.contains(direction)) {
                    this.renderModelFaceAO(
                            tile,
                            direction,
                            blockRenderer,
                            level, state, pos, poseStack, consumer, list, afloat, bitset, packedOverlay
                    );
                }else{
//                    System.out.println("skipping " + direction);
                }
            }
        }

        random.setSeed(seed);
        List<BakedQuad> list1 = model.getQuads(state, null, random, modelData, renderType);
        if (!list1.isEmpty()) {
            this.renderModelFaceAO(tile, null, blockRenderer,
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
            MirrorWeaveTile tile,
            Direction direction,
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
        MirrorWeaveTile.ClientData data = tile.clientData;
        var cachedAO = direction == null ? data.nullDirectionAO : data.aoCalcMap.get(direction);
        ModelBlockRenderer.AmbientOcclusionFace aoFace = new ModelBlockRenderer.AmbientOcclusionFace();
        if(cachedAO == null) {
            List<ModelBlockRenderer.AmbientOcclusionFace> faces = new ArrayList<>();
            System.out.println("cache miss");
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
                faces.add(aoFace);
            }
            if(direction == null) {
                data.nullDirectionAO = new MirrorWeaveTile.ClientData.CachedAO(quads, faces);
            }else{
                data.aoCalcMap.put(direction, new MirrorWeaveTile.ClientData.CachedAO(quads, faces));
            }
        }else{
            for (int i = 0; i < cachedAO.getQuads().size(); i++) {
                BakedQuad bakedquad = cachedAO.getQuads().get(i);
                ModelBlockRenderer.AmbientOcclusionFace face = cachedAO.getAoFace().get(i);
                blockRenderer.putQuadData(
                        level,
                        state,
                        pos,
                        consumer,
                        poseStack.last(),
                        bakedquad,
                        face.brightness[0],
                        face.brightness[1],
                        face.brightness[2],
                        face.brightness[3],
                        face.lightmap[0],
                        face.lightmap[1],
                        face.lightmap[2],
                        face.lightmap[3],
                        packedOverlay
                );
            }
        }
    }

    public int getViewDistance() {
        return 68;
    }
}
