package com.hollingsworth.arsnouveau.common.world.tree;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.trunkplacer.AbstractTrunkPlacer;
import net.minecraft.world.gen.trunkplacer.TrunkPlacerType;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class MagicTrunkPlacer extends AbstractTrunkPlacer {
    public MagicTrunkPlacer(int baseHeight, int p_i232060_2_, int p_i232060_3_) {
        super(baseHeight, p_i232060_2_, p_i232060_3_);
    }

    @Override
    protected TrunkPlacerType<?> func_230381_a_() {
        return null;
    }

    @Override
    public List<FoliagePlacer.Foliage> func_230382_a_(IWorldGenerationReader generationReader, Random random, int p_230382_3_, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox boundingBox, BaseTreeFeatureConfig baseTreeFeatureConfig) {
        return null;
    }
}
