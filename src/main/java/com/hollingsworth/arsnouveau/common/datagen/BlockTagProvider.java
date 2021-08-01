package com.hollingsworth.arsnouveau.common.datagen;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;

public class BlockTagProvider extends BlockTagsProvider {
    public BlockTagProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void addTags() {
//        getOrCreateBuilder(Tags.Blocks.ORES).add(BlockRegistry.ARCANE_ORE);
    }
}
