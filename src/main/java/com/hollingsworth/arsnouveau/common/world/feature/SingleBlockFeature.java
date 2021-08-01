package com.hollingsworth.arsnouveau.common.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public abstract class SingleBlockFeature extends Feature<BlockStateFeatureConfig> {
    public SingleBlockFeature(Codec<BlockStateFeatureConfig> p_i231953_1_) {
        super(p_i231953_1_);
    }

    @Override
    public boolean place(ISeedReader seed, ChunkGenerator chunkGenerator, Random rand, BlockPos pos, BlockStateFeatureConfig config) {
        while(true) {
            label46: {
                if (pos.getY() > 3) {
                    if (!seed.isEmptyBlock(pos) || seed.isEmptyBlock(pos.below()) || seed.isWaterAt(pos)) {
                        break label46;
                    }

                }

                if (pos.getY() <= 3) {
                    return false;
                }
                seed.setBlock(pos, config.state, 4);
                onStatePlace(seed, chunkGenerator, rand, pos, config);
                return true;
            }

            pos = pos.below();
        }
    }

    public abstract void onStatePlace(ISeedReader seed, ChunkGenerator chunkGenerator, Random rand, BlockPos pos, BlockStateFeatureConfig config);
}
