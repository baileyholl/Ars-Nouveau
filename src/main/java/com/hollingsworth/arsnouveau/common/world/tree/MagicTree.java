package com.hollingsworth.arsnouveau.common.world.tree;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public class MagicTree extends AbstractTreeGrower {

    ResourceKey<ConfiguredFeature<?, ?>> configConfiguredFeature;

    public MagicTree(ResourceKey<ConfiguredFeature<?, ?>> configConfiguredFeature) {
        this.configConfiguredFeature = configConfiguredFeature;
    }


    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pHasFlowers) {
        return configConfiguredFeature;
    }
}
