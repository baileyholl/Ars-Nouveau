package com.hollingsworth.arsnouveau.common.mixin.camera;


import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;

// https://github.com/Geforce132/SecurityCraft/blob/1.18.2/src/main/java/net/geforcemods/securitycraft/mixin/camera/ChunkMapMixin.java
@Mixin(
        value = {ChunkMap.class},
        priority = 1100
)
public abstract class ChunkMapMixin {
    //todo: reenable scryer camera
//    @Shadow
//    int viewDistance;
//
//    public ChunkMapMixin() {
//    }
//
//    @Shadow
//    protected abstract void updateChunkTracking(ServerPlayer var1, ChunkPos var2, MutableObject<ClientboundLevelChunkWithLightPacket> var3, boolean var4, boolean var5);
//
//    @Shadow
//    public abstract List<ServerPlayer> getPlayers(ChunkPos var1, boolean var2);

//    @Inject(
//            method = {"setViewDistance"},
//            at = {@At(
//                    value = "NEW",
//                    target = "org/apache/commons/lang3/mutable/MutableObject",
//                    shift = At.Shift.AFTER
//            )},
//            locals = LocalCapture.CAPTURE_FAILSOFT,
//            cancellable = true,
//            remap = false
//    )
//    private void updateAccordingToCamera(int viewDistance, CallbackInfo callback, int i, int j, ObjectIterator<?> objectIterator, ChunkHolder chunkHolder, ChunkPos chunkPos) {
//        MutableObject<ClientboundLevelChunkWithLightPacket> mutableObject = new MutableObject();
//        this.getPlayers(chunkPos, false).forEach((player) -> {
//            SectionPos sectionPos;
//            if (CameraUtil.isPlayerMountedOnCamera(player)) {
//                sectionPos = SectionPos.of(player.getCamera());
//            } else {
//                sectionPos = player.getLastSectionPos();
//            }
//
//            boolean flag = ChunkMap.isChunkInRange(chunkPos.x, chunkPos.z, sectionPos.x(), sectionPos.z(), j);
//            boolean flag1 = ChunkMap.isChunkInRange(chunkPos.x, chunkPos.z, sectionPos.x(), sectionPos.z(), viewDistance);
//            this.updateChunkTracking(player, chunkPos, mutableObject, flag, flag1);
//        });
//        callback.cancel();
//    }

//    @Inject(
//            method = {"move"},
//            at = {@At("TAIL")}
//    )
//    private void trackCameraLoadedChunks(ServerPlayer player, CallbackInfo callback) {
//        if (CameraUtil.isPlayerMountedOnCamera(player)) {
//            SectionPos pos = SectionPos.of(player.getCamera());
//            ScryerCamera camera = (ScryerCamera) player.getCamera();
//
//            for (int i = pos.x() - this.viewDistance; i <= pos.x() + this.viewDistance; ++i) {
//                for (int j = pos.z() - this.viewDistance; j <= pos.z() + this.viewDistance; ++j) {
//                    this.updateChunkTracking(player, new ChunkPos(i, j), new MutableObject(), camera.hasLoadedChunks(), true);
//                }
//            }
//
//            camera.setHasLoadedChunks(this.viewDistance);
//        }
//
//    }
}
