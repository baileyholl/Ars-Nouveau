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
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;

public class WildenGuardianDen extends WildenDen{

    public static final MapCodec<WildenGuardianDen> CODEC = RecordCodecBuilder.<WildenGuardianDen>mapCodec(instance ->
            instance.group(WildenGuardianDen.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
            ).apply(instance, WildenGuardianDen::new));

    public WildenGuardianDen(StructureSettings config, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName, int size, HeightProvider startHeight, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter) {
        super(config, startPool, startJigsawName, size, startHeight, projectStartToHeightmap, maxDistanceFromCenter);
    }

    @Override
    public void afterPlace(WorldGenLevel level, StructureManager manager, ChunkGenerator p_226562_, RandomSource p_226563_, BoundingBox p_226564_, ChunkPos p_226565_, PiecesContainer p_226566_) {
        super.afterPlace(level, manager, p_226562_, p_226563_, p_226564_, p_226565_, p_226566_);
        // Replace all mud blocks with water
        BlockPos.betweenClosed(p_226564_.minX(), p_226564_.minY(), p_226564_.minZ(), p_226564_.maxX(), p_226564_.maxY(), p_226564_.maxZ())
                .forEach(pos -> {
                    if (level.getBlockState(pos).is(Blocks.MUD)) {
                        level.setBlock(pos, Blocks.WATER.defaultBlockState(), 2);
                    }
                });
    }

    @Override
    public StructureType<?> type() {
        return StructureRegistry.WILDEN_GUARDIAN_DEN.get();
    }
}
