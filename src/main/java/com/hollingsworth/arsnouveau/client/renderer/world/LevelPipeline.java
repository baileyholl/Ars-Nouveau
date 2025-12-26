package com.hollingsworth.arsnouveau.client.renderer.world;

//
//public final class LevelPipeline {
//    private static final Predicate<RenderType> IS_TRANSLUCENT = (renderType) -> renderType == RenderType.TRANSLUCENT;
//    private static final Set<RenderType> STATIC_GEOMETRY = Set.of(RenderType.solid(), RenderType.cutoutMipped(), RenderType.cutout());
//    public static MultiPassRenderPipeline INSTANCE;
//
//    private static void staticGeometryPass(PipelineState state, GuiGraphics graphics, float partialTicks) {
//        Vector3f renderOrigin = getCorrectedRenderOrigin(state, partialTicks);
//        Camera camera = (Camera) state.get(GanderRenderToolkit.CAMERA);
//        Vector3f camPos = camera.getPosition().toVector3f();
//        BakedLevel bakedLevel = (BakedLevel) state.get(GanderRenderToolkit.BAKED_LEVEL);
//        Matrix4f projectionMatrix = (Matrix4f) state.get(GanderRenderToolkit.PROJECTION_MATRIX);
//        Matrix4f modelViewMatrix = (Matrix4f) state.get(GanderRenderToolkit.MODEL_VIEW_MATRIX);
//        PoseStack poseStack = graphics.pose();
//        poseStack.pushPose();
//        poseStack.mulPose(modelViewMatrix);
//
//        for (RenderType renderType : STATIC_GEOMETRY) {
//            for (BakedLevelSection section : bakedLevel.sections().values()) {
//                LevelPipeline.renderSectionLayer(section.blockBuffers(), Function.identity(), renderType, poseStack, camPos, renderOrigin, projectionMatrix);
//                LevelPipeline.renderSectionLayer(section.fluidBuffers(), Function.identity(), renderType, poseStack, camPos, renderOrigin, projectionMatrix);
//            }
//        }
//
//        poseStack.popPose();
//    }
//
//    private static Vector3f getCorrectedRenderOrigin(PipelineState state, float partialTicks) {
//        Vector3f origin = new Vector3f((Vector3fc) state.getOrDefault(GanderRenderToolkit.RENDER_ORIGIN, new Vector3f()));
//        LocalPlayer player = (LocalPlayer) Objects.requireNonNull(Minecraft.getInstance().player);
//        return new Vector3f(Mth.lerp(partialTicks, (float) ((double) origin.x() - player.xOld), (float) ((double) origin.x() - player.getX())), Mth.lerp(partialTicks, (float) ((double) origin.y() - player.yOld), (float) ((double) origin.y() - player.getY())), Mth.lerp(partialTicks, (float) ((double) origin.z() - player.zOld), (float) ((double) origin.z() - player.getZ())));
//    }
//
//    public static void blockEntitiesPass(PipelineState state, GuiGraphics graphics, float partialTicks) {
//
//        final var camera = state.get(GanderRenderToolkit.CAMERA);
//        final var camPos = camera.getPosition().toVector3f();
//        final var bakedLevel = state.get(GanderRenderToolkit.BAKED_LEVEL);
//        final var blockEntities = state.get(GanderRenderToolkit.BLOCK_ENTITY_POSITIONS);
//
//        var renderOrigin = getCorrectedRenderOrigin(state, partialTicks);
//
//        final var mc = Minecraft.getInstance();
//        final var bufferSource = mc.renderBuffers().bufferSource();
//        final var blockEntityRenderDispatcher = mc.getBlockEntityRenderDispatcher();
//
//        // TODO: maybe we should raycast in the virtual level for these, rather than pulling from the real level?
//        blockEntityRenderDispatcher.prepare(bakedLevel.originalLevel(), camera, mc.hitResult);
//
//        final var renderOffset = new Vector3f(renderOrigin).sub(camPos);
//
//        final var poseStack = graphics.pose();
//        poseStack.pushPose();
//        poseStack.translate(renderOffset.x, renderOffset.y, renderOffset.z);
//        float pad = 0.0025f;
//        float scale = (1.0f - 2.0f * pad) / (float) 32;
//        poseStack.scale(scale, scale, scale);
//        Arrays.stream(blockEntities)
//                .map(bakedLevel.originalLevel()::getBlockEntity)
//                .forEach(blockEnt -> {
//                    renderSingleBlockEntity(partialTicks, poseStack, bufferSource, blockEnt, blockEntityRenderDispatcher);
//                });
//
//        poseStack.popPose();
//    }
//
//
//    private static void renderSingleBlockEntity(float partialTick, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, BlockEntity blockEnt, BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
//        poseStack.pushPose();
//        Vec3 offset = Vec3.atLowerCornerOf(blockEnt.getBlockPos());
//        poseStack.translate(offset.x, offset.y, offset.z);
//        blockEntityRenderDispatcher.render(blockEnt, partialTick, poseStack, bufferSource);
//        poseStack.popPose();
//    }
//
//    public static void translucentGeometryPass(PipelineState state, GuiGraphics graphics, float partialTicks) {
//        Camera camera = (Camera) state.get(GanderRenderToolkit.CAMERA);
//        Vector3f renderOrigin = getCorrectedRenderOrigin(state, partialTicks);
//        Vector3f camPos = camera.getPosition().toVector3f();
//        BakedLevel bakedLevel = (BakedLevel) state.get(GanderRenderToolkit.BAKED_LEVEL);
//        Matrix4f projectionMatrix = (Matrix4f) state.get(GanderRenderToolkit.PROJECTION_MATRIX);
//        Matrix4f modelViewMatrix = (Matrix4f) state.get(GanderRenderToolkit.MODEL_VIEW_MATRIX);
//        PoseStack poseStack = graphics.pose();
//        poseStack.pushPose();
//        poseStack.mulPose(modelViewMatrix);
//
//        bakedLevel.sections().forEach((chunkPos, section) -> {
//            LevelPipeline.renderSectionLayer(section.fluidBuffers(), Function.identity(), RenderType.translucent(), poseStack, camPos, renderOrigin, projectionMatrix);
//            LevelPipeline.renderSectionLayer(section.blockBuffers(), Function.identity(), RenderType.translucent(), poseStack, camPos, renderOrigin, projectionMatrix);
//        });
//        poseStack.pushPose();
//    }
//
//    static {
//        RenderPipelineBuilder builder = new RenderPipelineBuilder();
//        IPipelinePhaseCollectionBuilder var10000 = builder.phases();
//        Set var10001 = STATIC_GEOMETRY;
//        Objects.requireNonNull(var10001);
//        var10000.addGeometryUploadPhase(var10001::contains, LevelPipeline::staticGeometryPass).addGeometryUploadPhase(IS_TRANSLUCENT, LevelPipeline::blockEntitiesPass).addGeometryUploadPhase(IS_TRANSLUCENT, LevelPipeline::translucentGeometryPass);
//        INSTANCE = builder.stagedMultiPass();
//    }
//
//    public static void renderSectionBlocks(BakedLevelSection section, RenderTypeStore renderTypeStore, RenderType renderType, PoseStack poseStack, Vector3fc camera, Vector3fc renderOrigin, Matrix4f pProjectionMatrix) {
//        Map var10000 = section.blockBuffers();
//        Objects.requireNonNull(renderTypeStore);
//        renderSectionLayer(var10000, renderTypeStore::redirectedBlockRenderType, renderType, poseStack, camera, renderOrigin, pProjectionMatrix);
//    }
//
//    public static void renderSectionFluids(BakedLevelSection section, RenderTypeStore renderTypeStore, RenderType renderType, PoseStack poseStack, Vector3fc camera, Vector3fc renderOrigin, Matrix4f pProjectionMatrix) {
//        Map var10000 = section.fluidBuffers();
//        Objects.requireNonNull(renderTypeStore);
//        renderSectionLayer(var10000, renderTypeStore::redirectedFluidRenderType, renderType, poseStack, camera, renderOrigin, pProjectionMatrix);
//    }
//
//    public static void renderSectionLayer(Map<RenderType, VertexBuffer> renderBuffers, Function<RenderType, RenderType> redirector, RenderType renderType, PoseStack poseStack, Vector3fc cameraPosition, Vector3fc renderOrigin, Matrix4f pProjectionMatrix) {
//        Minecraft mc = Minecraft.getInstance();
//        RenderType retargetedRenderType = (RenderType) redirector.apply(renderType);
//        RenderSystem.assertOnRenderThread();
//        retargetedRenderType.setupRenderState();
//        mc.getProfiler().popPush(() -> "render_" + String.valueOf(renderType));
//        ShaderInstance shaderinstance = RenderSystem.getShader();
//        Uniform uniform = shaderinstance.CHUNK_OFFSET;
//        VertexBuffer vertexbuffer = (VertexBuffer) renderBuffers.get(renderType);
//        Vector3f renderAt = new Vector3f();
//        renderOrigin.sub(cameraPosition, renderAt);
//        if (vertexbuffer != null) {
//            if (uniform != null) {
//                shaderinstance.apply();
//                uniform.set(renderAt.x(), renderAt.y(), renderAt.z());
//                uniform.upload();
//            }
//
//            vertexbuffer.bind();
//            vertexbuffer.drawWithShader(poseStack.last().pose(), pProjectionMatrix, shaderinstance);
//        }
//
//        if (uniform != null) {
//            uniform.set(0.0F, 0.0F, 0.0F);
//        }
//
//        mc.getProfiler().pop();
//        retargetedRenderType.clearRenderState();
//    }
//}
