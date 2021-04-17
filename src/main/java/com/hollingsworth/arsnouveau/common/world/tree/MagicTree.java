package com.hollingsworth.arsnouveau.common.world.tree;

import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.util.Random;

public class MagicTree extends Tree {

    ConfiguredFeature<BaseTreeFeatureConfig, ?> configConfiguredFeature;
    public MagicTree(ConfiguredFeature<BaseTreeFeatureConfig, ?> configConfiguredFeature){
        this.configConfiguredFeature = configConfiguredFeature;
    }

    @Override
    protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredFeature(Random randomIn, boolean largeHive) {
        return configConfiguredFeature;
    }
}
