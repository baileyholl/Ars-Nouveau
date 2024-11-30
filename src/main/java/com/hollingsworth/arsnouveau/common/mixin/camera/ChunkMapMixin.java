package com.hollingsworth.arsnouveau.common.mixin.camera;


import com.hollingsworth.arsnouveau.common.entity.ICameraCallback;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// https://github.com/Geforce132/an/blob/1.18.2/src/main/java/net/geforcemods/an/mixin/camera/ChunkMapMixin.java
@Mixin(
        value = {ChunkMap.class},
        priority = 1100
)
public abstract class ChunkMapMixin {

    @Shadow
    protected abstract void markChunkPendingToSend(ServerPlayer player, ChunkPos pos);

    @Shadow
    private static void markChunkPendingToSend(ServerPlayer player, LevelChunk chunk) {}

    /**
     * Sends chunks loaded by cameras to the client, and re-sends chunks around the player when they stop viewing a camera to
     * preemptively prevent missing chunks when exiting the camera
     */
    @Inject(method = "updateChunkTracking", at = @At(value = "HEAD"))
    private void an$onUpdateChunkTracking(ServerPlayer player, CallbackInfo callback) {
        if (player.getCamera() instanceof ICameraCallback camera) {
            if (camera.shouldUpdateChunkTracking(player)) {
                ChunkTrackingView.difference(player.getChunkTrackingView(), camera.getCameraChunks(), chunkPos -> markChunkPendingToSend(player, chunkPos), chunkPos -> {});
            }
        }
        else if (ICameraCallback.hasRecentlyDismounted(player))
            player.getChunkTrackingView().forEach(chunkPos -> markChunkPendingToSend(player, chunkPos));
    }

    /**
     * Allows chunks that are loaded near a currently active camera (for example by ForcedChunkManager) to be sent to the player
     * viewing the camera
     */
    @Inject(method = "onChunkReadyToSend", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getChunkTrackingView()Lnet/minecraft/server/level/ChunkTrackingView;"))
    private void an$sendChunksToCameras(LevelChunk chunk, CallbackInfo callback, @Local ServerPlayer player) {
        if (player.getCamera() instanceof ICameraCallback camera && camera.getCameraChunks().contains(chunk.getPos()))
            markChunkPendingToSend(player, chunk);
    }

    /**
     * Fixes block updates not getting sent to chunks around cameras by marking all nearby chunks as tracked
     */
    @Inject(method = "isChunkTracked", at = @At("HEAD"), cancellable = true)
    private void an$onIsChunkTracked(ServerPlayer player, int x, int z, CallbackInfoReturnable<Boolean> callback) {
        if (player.getCamera() instanceof ICameraCallback camera && camera.getCameraChunks().contains(x, z) && !player.connection.chunkSender.isPending(ChunkPos.asLong(x, z)))
            callback.setReturnValue(true);
    }
}
