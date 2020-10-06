package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;

public class BlockTagProvider extends BlockTagsProvider {
    public BlockTagProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerTags() {
        getOrCreateBuilder(Tags.Blocks.ORES).add(BlockRegistry.ARCANE_ORE);
    }
}
