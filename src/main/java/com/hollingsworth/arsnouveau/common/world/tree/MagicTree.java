package com.hollingsworth.arsnouveau.common.world.tree;

import com.hollingsworth.arsnouveau.common.event.WorldEvent;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.util.Random;

public class MagicTree extends Tree {

    @Override
    protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean largeHive) {
        return WorldEvent.MAGIC_TREE_CONFIG;
    }
}
