package com.hollingsworth.arsnouveau.common.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.Random;

public abstract class SingleBlockFeature extends Feature<BlockStateConfiguration> {
    public SingleBlockFeature(Codec<BlockStateConfiguration> p_i231953_1_) {
        super(p_i231953_1_);
    }
    //TODO: Restore place
    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> p_159749_) {
        return false;
    }

//    @Override
//    public boolean place(WorldGenLevel seed, ChunkGenerator chunkGenerator, Random rand, BlockPos pos, BlockStateConfiguration config) {
//        while(true) {
//            label46: {
//                if (pos.getY() > 3) {
//                    if (!seed.isEmptyBlock(pos) || seed.isEmptyBlock(pos.below()) || seed.isWaterAt(pos)) {
//                        break label46;
//                    }
//
//                }
//
//                if (pos.getY() <= 3) {
//                    return false;
//                }
//                seed.setBlock(pos, config.state, 4);
//                onStatePlace(seed, chunkGenerator, rand, pos, config);
//                return true;
//            }
//
//            pos = pos.below();
//        }
//    }

    public abstract void onStatePlace(WorldGenLevel seed, ChunkGenerator chunkGenerator, Random rand, BlockPos pos, BlockStateConfiguration config);
}
