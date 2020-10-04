package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.Tags;

public class BlockTagProvider extends BlockTagsProvider {
    public BlockTagProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerTags() {
        getBuilder(Tags.Blocks.ORES).add(BlockRegistry.ARCANE_ORE);
    }
}
