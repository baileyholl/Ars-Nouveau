package com.hollingsworth.arsnouveau.common.world.tree;

import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

import java.util.Random;

public class MagicTree extends AbstractTreeGrower {

    ConfiguredFeature<TreeConfiguration, ?> configConfiguredFeature;
    public MagicTree(ConfiguredFeature<TreeConfiguration, ?> configConfiguredFeature){
        this.configConfiguredFeature = configConfiguredFeature;
    }

    @Override
    protected ConfiguredFeature<TreeConfiguration, ?> getConfiguredFeature(Random randomIn, boolean largeHive) {
        return configConfiguredFeature;
    }

}
