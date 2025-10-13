package com.hollingsworth.arsnouveau.common.world.dimension;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.ModSetup;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.Structure;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VoidChunkGenerator extends ChunkGenerator {
    public static final ChunkPos CHUNKPOS = new ChunkPos(0, 0);
    public static final long CHUNKID = CHUNKPOS.toLong();
    public static final BlockPos CORNER = CHUNKPOS.getWorldPosition();
    public static final BlockPos CENTER = CORNER.offset(7, 7, 7);
    public static final BlockPos MIN_SPAWN_CORNER = CORNER.offset(1, 1, 1);
    // don't want to spawn with head in the ceiling
    public static final BlockPos MAX_SPAWN_CORNER = CORNER.offset(13, 12, 13);

    private final Holder<Biome> biome;

    public Holder<Biome> biome() {
        return biome;
    }

    /**
     * get from Hyperbox.INSTANCE.hyperboxChunkGeneratorCodec.get();
     **/
    public static MapCodec<VoidChunkGenerator> makeCodec() {
        return Biome.CODEC.fieldOf("biome")
                .xmap(VoidChunkGenerator::new, VoidChunkGenerator::biome);
    }

    // hardcoding this for now, may reconsider later
    public int getHeight() {
        return 31;
    }

    // create chunk generator at runtime when dynamic dimension is created
    public VoidChunkGenerator(MinecraftServer server) {
        this(server.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(ArsNouveau.BIOME_KEY));
    }

    // create chunk generator when dimension is loaded from the dimension registry on server init
    public VoidChunkGenerator(Holder<Biome> biome) {
        super(new FixedBiomeSource(biome));
        this.biome = biome;
    }

    // get codec
    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return ModSetup.VOID_CHUNK_GENERATOR_CODEC.get();
    }

    // apply carvers
    @Override
    public void applyCarvers(WorldGenRegion world, long seed, RandomState random, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunkAccess, GenerationStep.Carving carvingStep) {
        // noop
    }

    @Override
    public void buildSurface(WorldGenRegion worldGenRegion, StructureManager structureFeatureManager, RandomState random, ChunkAccess chunk) {
        int chunkX = chunk.getPos().x;
        int chunkZ = chunk.getPos().z;
        int worldX = chunkX * 16;
        int worldZ = chunkZ * 16;
        
        int minX = -1;
        int maxX = 32;
        int minY = 0;
        int maxY = 32;
        int minZ = -1;
        int maxZ = 32;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldPosX = worldX + x;
                int worldPosZ = worldZ + z;

                if (worldPosX >= minX && worldPosX <= maxX &&
                        worldPosZ >= minZ && worldPosZ <= maxZ) {

                    for (int y = minY; y <= maxY; y++) {
                        boolean isEdge = (worldPosX == minX || worldPosX == maxX) ||
                                (y == minY || y == maxY) ||
                                (worldPosZ == minZ || worldPosZ == maxZ);

                        if (isEdge) {
                            BlockPos pos = new BlockPos(x, y, z);
                            chunk.setBlockState(pos, BlockRegistry.DIM_BOUNDARY.defaultBlockState(), false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
        // NOOP
    }

    @Override
    public int getGenDepth() // total number of available y-levels (between bottom and top)
    {
        return 33;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState random, StructureManager structures, ChunkAccess chunk) {
        // this is where the flat chunk generator generates flat chunks
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getSeaLevel() {
        // only used by features' generate methods
        return 0;
    }

    @Override
    public int getMinY() {
        // the lowest y-level in the dimension
        // debug -> 0
        // flat -> 0
        // noise -> NoiseSettings#minY
        // overworld -> -64
        // nether -> 0
        return -64;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types heightmapType, LevelHeightAccessor level, RandomState random) {
        // flat chunk generator counts the solid blockstates in its list
        // debug chunk generator returns 0
        // the "normal" chunk generator generates a height via noise
        // we can assume that this is what is used to define the "initial" heightmap
        return 0;
    }

    // get base column
    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState random) {
        // flat chunk generator returns a reader over its blockstate list
        // debug chunk generator returns a reader over an empty array
        // normal chunk generator returns a column whose contents are either default block, default fluid, or air

        return new NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(List<String> stringsToRender, RandomState random, BlockPos pos) {
        // no info to add
    }

    // let's make sure some of the default chunk generator methods aren't doing
    // anything we don't want them to either

    // get structure position
    @Nullable
    @Override
    public Pair<BlockPos, Holder<Structure>> findNearestMapStructure(ServerLevel level, HolderSet<Structure> structures, BlockPos pos, int range, boolean skipKnownStructures) {
        return null;
    }

    // decorate biomes with features
    @Override
    public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunkAccess, StructureManager structures) {
        // noop
    }

    @Override
    public int getSpawnHeight(LevelHeightAccessor level) {
        return 1;
    }

    // create structure references
    @Override
    public void createReferences(WorldGenLevel world, StructureManager structures, ChunkAccess chunk) {
        // no structures
    }
}