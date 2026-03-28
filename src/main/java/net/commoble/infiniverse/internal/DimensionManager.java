package net.commoble.infiniverse.internal;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Stub for infiniverse DimensionManager which has no 1.21.11 version.
 * TODO: Replace with actual implementation or alternative library when available.
 * Tracked issue: infiniverse library not yet ported to 1.21.11.
 */
public class DimensionManager {
    public static final DimensionManager INSTANCE = new DimensionManager();
    private static final Logger LOGGER = LoggerFactory.getLogger(DimensionManager.class);

    public ServerLevel getOrCreateLevel(MinecraftServer server, ResourceKey<Level> key, Supplier<LevelStem> dimensionFactory) {
        LOGGER.error("[ERROR] DimensionManager.getOrCreateLevel is not implemented - infiniverse library not available for 1.21.11. Planarium dimension creation will fail.");
        throw new UnsupportedOperationException("infiniverse DimensionManager not available for 1.21.11 - Planarium feature disabled");
    }
}
