package com.hollingsworth.arsnouveau.common.world.tree;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

import java.util.function.Supplier;

public class MagicTree extends AbstractTreeGrower {

    Supplier<Holder<ConfiguredFeature<TreeConfiguration, ?>>> configConfiguredFeature;
    public MagicTree(Supplier<Holder<ConfiguredFeature<TreeConfiguration, ?>>> configConfiguredFeature){
        this.configConfiguredFeature = configConfiguredFeature;
    }

    @Override
    protected Holder<ConfiguredFeature<TreeConfiguration, ?>> getConfiguredFeature(RandomSource randomIn, boolean largeHive) {
        return configConfiguredFeature.get();
    }

}
