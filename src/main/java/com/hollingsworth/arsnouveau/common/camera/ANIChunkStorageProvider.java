package com.hollingsworth.arsnouveau.common.camera;

import net.minecraft.client.multiplayer.ClientChunkCache;

/**
 * https://github.com/Geforce132/SecurityCraft/blob/34699802f2ab334ab3e3f5e6412a5d74879b8ba8/src/main/java/net/geforcemods/securitycraft/misc/IChunkStorageProvider.java
 * Helper interface for creating new Storages, as these are inner classes and have to be created from ClientChunkCaches
 */
public interface ANIChunkStorageProvider {
    default ClientChunkCache.Storage ANnewStorage(int viewDistance) {
        if (this instanceof ClientChunkCache cache)
            return cache.new Storage(viewDistance);

        return null;
    }
}
