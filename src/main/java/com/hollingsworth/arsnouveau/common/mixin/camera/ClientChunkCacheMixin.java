package com.hollingsworth.arsnouveau.common.mixin.camera;

import com.hollingsworth.arsnouveau.common.camera.ANIChunkStorageProvider;
import com.hollingsworth.arsnouveau.common.camera.CameraController;
import com.hollingsworth.arsnouveau.common.util.CameraUtil;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

// https://github.com/Geforce132/an/blob/1.18.2/src/main/java/net/geforcemods/an/mixin/camera/ClientChunkCacheMixin.java

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
    public void an$onInit(ClientLevel level, int viewDistance, CallbackInfo ci) {
        CameraController.setCameraStorage(ANnewStorage(Math.max(2, viewDistance) + 3));
    }

    /**
     * Updates the camera storage's view radius by creating a new Storage instance with the same view center and chunks as the
     * previous one
     */
    @Inject(method = "updateViewRadius", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientChunkCache$Storage;<init>(Lnet/minecraft/client/multiplayer/ClientChunkCache;I)V"))
    public void an$onUpdateViewRadius(int viewDistance, CallbackInfo ci) {
        ClientChunkCache.Storage oldStorage = CameraController.getCameraStorage();
        ClientChunkCache.Storage newStorage = ANnewStorage(Math.max(2, viewDistance) + 3);

        newStorage.viewCenterX = oldStorage.viewCenterX;
        newStorage.viewCenterZ = oldStorage.viewCenterZ;

        for (int i = 0; i < oldStorage.chunks.length(); ++i) {
            LevelChunk chunk = oldStorage.chunks.get(i);

            if (chunk != null) {
                ChunkPos pos = chunk.getPos();

                if (newStorage.inRange(pos.x, pos.z))
                    newStorage.replace(newStorage.getIndex(pos.x, pos.z), chunk);
            }
        }

        CameraController.setCameraStorage(newStorage);
    }

    /**
     * Handles chunks that are dropped in range of the camera storage
     */
    @Inject(method = "drop", at = @At(value = "HEAD"))
    public void an$onDrop(ChunkPos pos, CallbackInfo ci) {
        int x = pos.x;
        int z = pos.z;
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
     * checks if the camera storage should be used
     */
    @Inject(method = "replaceWithPacketData", at = @At("HEAD"))
    private void as$useStorage(int x, int z, FriendlyByteBuf buffer, CompoundTag tag, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> consumer, CallbackInfoReturnable<LevelChunk> cir, @Share("as$useCamera") LocalBooleanRef ref) {
        ref.set(CameraUtil.isPlayerMountedOnCamera(Minecraft.getInstance().player) && CameraController.getCameraStorage().inRange(x, z));
    }

    /**
     * Handles chunks that get sent to the client which are in range of the camera storage, i.e. place them into the storage for
     * them to be acquired afterwards
     */
    @Redirect(method = "replaceWithPacketData", at = @At(value = "FIELD", target = "Lnet/minecraft/client/multiplayer/ClientChunkCache;storage:Lnet/minecraft/client/multiplayer/ClientChunkCache$Storage;"))
    private ClientChunkCache.Storage an$redirectStorage(ClientChunkCache instance, int x, int z, @Share("as$useCamera") LocalBooleanRef ref) {
        if (ref.get()) {
            return CameraController.getCameraStorage();
        } else {
            return this.storage;
        }
    }

    /**
     * If chunks in range of a camera storage need to be acquired, ask the camera storage about these chunks
     */
    @Inject(method = "getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/LevelChunk;", at = @At("TAIL"), cancellable = true)
    private void an$onGetChunk(int x, int z, ChunkStatus requiredStatus, boolean load, CallbackInfoReturnable<LevelChunk> callback) {
        if (CameraUtil.isPlayerMountedOnCamera(Minecraft.getInstance().player) && CameraController.getCameraStorage().inRange(x, z)) {
            LevelChunk chunk = CameraController.getCameraStorage().getChunk(CameraController.getCameraStorage().getIndex(x, z));

            if (chunk != null && chunk.getPos().x == x && chunk.getPos().z == z)
                callback.setReturnValue(chunk);
        }
    }
}