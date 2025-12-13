package com.hollingsworth.arsnouveau.client.renderer.world;

//
//public record LevelInLevelRenderer(UUID id, PipelineState state) {
//    public static Map<ResourceKey<Level>, LevelInLevelRenderer> renderers = new HashMap<>();
//
//    public static void createAndAddRenderer(ResourceKey<Level> key, StructureTemplate data, Vector3f renderLocation) {
//
//        var bounds = AABB.of(data.getBoundingBox(new StructurePlaceSettings(), BlockPos.ZERO));
//        var virtualLevel = new ExtendedVirtualLevel(Minecraft.getInstance().level.registryAccess(), true);
//
//        virtualLevel.setBounds(bounds);
//        data.placeInWorld(virtualLevel, BlockPos.ZERO, BlockPos.ZERO, new StructurePlaceSettings().setKnownShape(true), RandomSource.create(), Block.UPDATE_CLIENTS);
//
//        var bakedLevel = LevelBakery.bakeVertices(virtualLevel, bounds, new Vector3f());
//        final var newRenderer = LevelInLevelRenderer.create(bakedLevel, virtualLevel, renderLocation);
//
//        renderers.putIfAbsent(key, newRenderer);
//    }
//
//    public static LevelInLevelRenderer create(BakedLevel level, VirtualLevel virtualLevel) {
//        var bounds = virtualLevel.getBounds();
//        final var centerBlock = BlockPos.containing(bounds.getCenter())
//                .mutable()
//                .setY(Mth.floor(bounds.minY))
//                .immutable();
//
//        final var centerVector = Vec3.atLowerCornerOf(centerBlock).toVector3f();
//
//        return create(level, virtualLevel, centerVector);
//    }
//
//    public static LevelInLevelRenderer create(BakedLevel level, VirtualLevel virtualLevel, Vector3f renderLocation) {
//        final var initialState = LevelPipeline.INSTANCE.setup((state) -> {
//            final var blockEntityPos = virtualLevel.blockSystem()
//                    .blockAndFluidStorage()
//                    .blockEntityPositions()
//                    .toArray(BlockPos[]::new);
//
//            state.set(GanderRenderToolkit.BAKED_LEVEL, level);
//            state.set(GanderRenderToolkit.BLOCK_ENTITY_POSITIONS, blockEntityPos);
//            state.set(GanderRenderToolkit.RENDER_ORIGIN, renderLocation);
//            state.set(GanderRenderToolkit.CAMERA, new SceneCamera());
//        });
//
//        return new LevelInLevelRenderer(UUID.randomUUID(), initialState);
//    }
//
//    public void onRenderStage(RenderLevelStageEvent evt) {
//        final var graphics = new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource());
//
//        final var renderTypeForStage = RenderTypes.GEOMETRY_STAGES.get(evt.getStage());
//        final var partialTick = evt.getPartialTick().getGameTimeDeltaPartialTick(true);
//
//        if (renderTypeForStage != null) {
//            evt.getPoseStack().pushPose();
//            state.set(GanderRenderToolkit.PROJECTION_MATRIX, evt.getProjectionMatrix());
//            state.set(GanderRenderToolkit.MODEL_VIEW_MATRIX, evt.getModelViewMatrix());
//
//            LevelPipeline.INSTANCE.renderPass(state, renderTypeForStage, graphics,
//                    evt.getFrustum(), partialTick);
//            evt.getPoseStack().popPose();
//        }
//    }
//
//    public void onClientTick(ClientTickEvent.Post event) {
//        final var level = state.get(GanderRenderToolkit.BAKED_LEVEL);
//        if (level.originalLevel() instanceof TickingLevel vl)
//            vl.tick(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true));
//    }
//}