package com.hollingsworth.arsnouveau.common.mixin.camera;

import com.hollingsworth.arsnouveau.common.camera.ANIChunkStorageProvider;
import com.hollingsworth.arsnouveau.common.camera.CameraController;
import com.hollingsworth.arsnouveau.common.util.CameraUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

// https://github.com/Geforce132/SecurityCraft/blob/1.18.2/src/main/java/net/geforcemods/securitycraft/mixin/camera/ClientChunkCacheMixin.java

/**
 * These mixins aim at implementing the camera chunk storage from CameraController into all the places
 * ClientChunkCache#storage is used
 */
@Mixin(value = ClientChunkCache.class, priority = 1100)
public abstract class ClientChunkCacheMixin implements ANIChunkStorageProvider {
    @Shadow
    volatile ClientChunkCache.Storage storage;
    @Shadow
    @Final
    ClientLevel level;

    @Shadow
    private static boolean isValidChunk(LevelChunk chunk, int x, int z) {
        throw new IllegalStateException("Shadowing isValidChunk did not work!");
    }

    /**
     * Initializes the camera storage
     */
    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void onInit(ClientLevel level, int viewDistance, CallbackInfo ci) {
        CameraController.setCameraStorage(ANnewStorage(Math.max(2, viewDistance) + 3));
    }

    /**
     * Updates the camera storage with the new view radius
     */
    @Inject(method = "updateViewRadius", at = @At(value = "HEAD"))
    public void onUpdateViewRadius(int viewDistance, CallbackInfo ci) {
        CameraController.setCameraStorage(ANnewStorage(Math.max(2, viewDistance) + 3));
    }

    /**
     * Handles chunks that are dropped in range of the camera storage
     */
    @Inject(method = "drop", at = @At(value = "HEAD"))
    public void onDrop(ChunkPos chunkPos, CallbackInfo ci) {
        int x = chunkPos.x;
        int z = chunkPos.z;
        ClientChunkCache.Storage cameraStorage = CameraController.getCameraStorage();

        if (cameraStorage.inRange(x, z)) {
            int i = cameraStorage.getIndex(x, z);
            LevelChunk chunk = cameraStorage.getChunk(i);

            if (chunk != null && chunk.getPos().x == x && chunk.getPos().z == z) {
                NeoForge.EVENT_BUS.post(new ChunkEvent.Unload(chunk));
                cameraStorage.replace(i, chunk, null);
            }
        }
    }

    /**
     * Handles chunks that get sent to the client which are in range of the camera storage, i.e. place them into the storage
     * for them to be acquired afterwards
     */
    @Inject(method = "replaceWithPacketData", at = @At(value = "HEAD"), cancellable = true)
    private void onReplace(int x, int z, FriendlyByteBuf buffer, CompoundTag chunkTag, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> tagOutputConsumer, CallbackInfoReturnable<LevelChunk> callback) {
        ClientChunkCache.Storage cameraStorage = CameraController.getCameraStorage();

        if (CameraUtil.isPlayerMountedOnCamera(Minecraft.getInstance().player) && !storage.inRange(x, z) && cameraStorage.inRange(x, z)) {
            int index = cameraStorage.getIndex(x, z);
            LevelChunk chunk = cameraStorage.getChunk(index);
            ChunkPos chunkPos = new ChunkPos(x, z);

            if (!isValidChunk(chunk, x, z)) {
                chunk = new LevelChunk(level, chunkPos);
                chunk.replaceWithPacketData(buffer, chunkTag, tagOutputConsumer);
                cameraStorage.replace(index, chunk);
            } else
                chunk.replaceWithPacketData(buffer, chunkTag, tagOutputConsumer);

            level.onChunkLoaded(chunkPos);
            NeoForge.EVENT_BUS.post(new ChunkEvent.Load(chunk, false));
            callback.setReturnValue(chunk);
        }
    }

    /**
     * If chunks in range of a camera storage need to be acquired, ask the camera storage about these chunks
     */
    @Inject(method = "getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/ChunkAccess;", at = @At("TAIL"), cancellable = true)
    private void onGetChunk(int x, int z, ChunkStatus requiredStatus, boolean load, CallbackInfoReturnable<LevelChunk> callback) {
        if (CameraUtil.isPlayerMountedOnCamera(Minecraft.getInstance().player) && !storage.inRange(x, z) && CameraController.getCameraStorage().inRange(x, z)) {
            LevelChunk chunk = CameraController.getCameraStorage().getChunk(CameraController.getCameraStorage().getIndex(x, z));

            if (chunk != null && chunk.getPos().x == x && chunk.getPos().z == z)
                callback.setReturnValue(chunk);
        }
    }
}