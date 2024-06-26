package com.hollingsworth.arsnouveau.common.world.structure;

import com.hollingsworth.arsnouveau.setup.registry.StructureRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVinesPlantBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.Optional;

public class WildenDen extends Structure {

    public static final MapCodec<WildenDen> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(WildenDen.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
            ).apply(instance, WildenDen::new));

    public final Holder<StructureTemplatePool> startPool;
    public final Optional<ResourceLocation> startJigsawName;
    public final int size;
    public final HeightProvider startHeight;
    public final Optional<Heightmap.Types> projectStartToHeightmap;
    public final int maxDistanceFromCenter;
    public Optional<Integer> terrainHeightCheckRadius = Optional.empty();
    public Optional<Integer> allowedTerrainHeightRange = Optional.empty();
    public boolean cannotSpawnInLiquid = true;
    public Optional<Integer> minYAllowed = Optional.empty();
    public Optional<Integer> maxYAllowed = Optional.empty();
    public WildenDen(Structure.StructureSettings config,
                         Holder<StructureTemplatePool> startPool,
                         Optional<ResourceLocation> startJigsawName,
                         int size,
                         HeightProvider startHeight,
                         Optional<Heightmap.Types> projectStartToHeightmap,
                         int maxDistanceFromCenter)
    {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        terrainHeightCheckRadius = Optional.of(1);
        allowedTerrainHeightRange = Optional.of(6);

    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        var chunkPos = context.chunkPos();
        if (this.cannotSpawnInLiquid) {
            BlockPos centerOfChunk = context.chunkPos().getMiddleBlockPosition(0);
            int landHeight = context.chunkGenerator().getFirstOccupiedHeight(centerOfChunk.getX(), centerOfChunk.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
            NoiseColumn columnOfBlocks = context.chunkGenerator().getBaseColumn(centerOfChunk.getX(), centerOfChunk.getZ(), context.heightAccessor(), context.randomState());
            BlockState topBlock = columnOfBlocks.getBlock(centerOfChunk.getY() + landHeight);

            if(!topBlock.getFluidState().isEmpty()) {
                return Optional.empty();
            }
        }

        if (this.terrainHeightCheckRadius.isPresent() &&
                (this.allowedTerrainHeightRange.isPresent() || this.minYAllowed.isPresent()))
        {
            int maxTerrainHeight = Integer.MIN_VALUE;
            int minTerrainHeight = Integer.MAX_VALUE;
            int terrainCheckRange = this.terrainHeightCheckRadius.get();

            for (int curChunkX = chunkPos.x - terrainCheckRange; curChunkX <= chunkPos.x + terrainCheckRange; curChunkX++) {
                for (int curChunkZ = chunkPos.z - terrainCheckRange; curChunkZ <= chunkPos.z + terrainCheckRange; curChunkZ++) {
                    int height = context.chunkGenerator().getBaseHeight((curChunkX << 4) + 7, (curChunkZ << 4) + 7, this.projectStartToHeightmap.orElse(Heightmap.Types.WORLD_SURFACE_WG), context.heightAccessor(), context.randomState());
                    maxTerrainHeight = Math.max(maxTerrainHeight, height);
                    minTerrainHeight = Math.min(minTerrainHeight, height);

                    if (this.minYAllowed.isPresent() && minTerrainHeight < this.minYAllowed.get()) {
                        return Optional.empty();
                    }

                    if (this.maxYAllowed.isPresent() && minTerrainHeight > this.maxYAllowed.get()) {
                        return Optional.empty();
                    }
                }
            }

            if(this.allowedTerrainHeightRange.isPresent() &&
                    maxTerrainHeight - minTerrainHeight > this.allowedTerrainHeightRange.get())
            {
                return Optional.empty();
            }
        }


        // Set's our spawning blockpos's y offset to be 60 blocks up.
        // Since we are going to have heightmap/terrain height spawning set to true further down, this will make it so we spawn 60 blocks above terrain.
        // If we wanted to spawn on ocean floor, we would set heightmap/terrain height spawning to false and the grab the y value of the terrain with OCEAN_FLOOR_WG heightmap.
        int startY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));

        // Turns the chunk coordinates into actual coordinates we can use. (Gets corner of that chunk)
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());

        Optional<Structure.GenerationStub> structurePiecesGenerator =
                JigsawPlacement.addPieces(
                        context, // Used for JigsawPlacement to get all the proper behaviors done.
                        this.startPool, // The starting pool to use to create the structure layout from
                        this.startJigsawName, // Can be used to only spawn from one Jigsaw block. But we don't need to worry about this.
                        this.size, // How deep a branch of pieces can go away from center piece. (5 means branches cannot be longer than 5 pieces from center piece)
                        blockPos, // Where to spawn the structure.
                        false, // "useExpansionHack" This is for legacy villages to generate properly. You should keep this false always.
                        this.projectStartToHeightmap, // Adds the terrain height's y value to the passed in blockpos's y value. (This uses WORLD_SURFACE_WG heightmap which stops at top water too)
                        // Here, blockpos's y value is 60 which means the structure spawn 60 blocks above terrain height.
                        // Set this to false for structure to be place only at the passed in blockpos's Y value instead.
                        // Definitely keep this false when placing structures in the nether as otherwise, heightmap placing will put the structure on the Bedrock roof.
                        this.maxDistanceFromCenter, PoolAliasLookup.EMPTY, DimensionPadding.ZERO, LiquidSettings.APPLY_WATERLOGGING); // Maximum limit for how far pieces can spawn from center. You cannot set this bigger than 128 or else pieces gets cutoff.

        return structurePiecesGenerator;
    }

    @Override
    public void afterPlace(WorldGenLevel level, StructureManager manager, ChunkGenerator p_226562_, RandomSource p_226563_, BoundingBox p_226564_, ChunkPos p_226565_, PiecesContainer p_226566_) {
        super.afterPlace(level, manager, p_226562_, p_226563_, p_226564_, p_226565_, p_226566_);
        // Replace all berries with vines
        BlockPos.betweenClosed(p_226564_.minX(), p_226564_.minY(), p_226564_.minZ(), p_226564_.maxX(), p_226564_.maxY(), p_226564_.maxZ())
                .forEach(pos -> {
                    if (level.getBlockState(pos).is(Blocks.CAVE_VINES_PLANT)
                            && level.getBlockState(pos).hasProperty(CaveVinesPlantBlock.BERRIES)
                            && level.getBlockState(pos).getValue(CaveVinesPlantBlock.BERRIES)){
                        level.setBlock(pos, Blocks.CAVE_VINES_PLANT.defaultBlockState().setValue(CaveVinesPlantBlock.BERRIES, false), 2);
                    }
                    if (level.getBlockState(pos).is(Blocks.CAVE_VINES)){
                        level.setBlock(pos, Blocks.CAVE_VINES.defaultBlockState().setValue(CaveVinesPlantBlock.BERRIES, false).setValue(GrowingPlantHeadBlock.AGE, 25), 2);
                    }
                });
    }

    @Override
    public StructureType<?> type() {
        return StructureRegistry.WILDEN_DEN.get();
    }
}
