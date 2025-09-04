package com.hollingsworth.arsnouveau.common.world.feature;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

public class LightFeature extends SingleBlockFeature {
    public LightFeature(Codec<BlockStateConfiguration> p_i231953_1_) {
        super(p_i231953_1_);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> pContext) {
        return false;
    }

    @Override
    public void onStatePlace(WorldGenLevel seed, ChunkGenerator chunkGenerator, RandomSource rand, BlockPos pos, BlockStateConfiguration config) {
        if (seed instanceof WorldGenRegion world) {
            RandomSource random = world.getRandom();
            if (world.getBlockEntity(pos) instanceof LightTile tile) {
                tile.color = new ParticleColor(
                        Math.max(10, random.nextInt(255)),
                        Math.max(10, random.nextInt(255)),
                        Math.max(10, random.nextInt(255))
                );
            }
        }
    }
}
