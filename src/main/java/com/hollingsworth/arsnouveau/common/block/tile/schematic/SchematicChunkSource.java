package com.hollingsworth.arsnouveau.common.block.tile.schematic;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

public class SchematicChunkSource extends ChunkSource {
    private final SchematicLevel level;
    private final LevelChunk emptyChunk;

    public SchematicChunkSource(SchematicLevel level) {
        this.level = level;
        this.emptyChunk = new EmptyLevelChunk(level, ChunkPos.ZERO, level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.PLAINS));
    }

    @Override
    public ChunkAccess getChunk(int x, int z, ChunkStatus chunkStatus, boolean requireChunk) {
        return emptyChunk;
    }

    @Nullable
    @Override
    public LightChunk getChunkForLighting(int chunkX, int chunkZ) {
        return emptyChunk;
    }

    @Override
    public BlockGetter getLevel() {
        return level;
    }

    @Override
    public void tick(BooleanSupplier hasTimeLeft, boolean tickChunks) {
    }

    @Override
    public String gatherStats() {
        return "SchematicChunkSource";
    }

    @Override
    public int getLoadedChunksCount() {
        return 0;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return level.getLightEngine();
    }
}
