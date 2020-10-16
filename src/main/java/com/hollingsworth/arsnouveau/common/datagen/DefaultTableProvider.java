package com.hollingsworth.arsnouveau.common.datagen;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DefaultTableProvider extends LootTableProvider {
    public DefaultTableProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    public static class BlockLootTable extends BlockLootTables {
        public List<Block> list = new ArrayList<>();
        @Override
        protected void addTables() {
            registerDropSelf(BlockRegistry.AB_ALTERNATE);
            registerDropSelf(BlockRegistry.ARCANE_BRICKS);

            registerDropSelf(BlockRegistry.ARCANE_PEDESTAL);
            registerDropSelf(BlockRegistry.ARCANE_STONE);
            registerDropSelf(BlockRegistry.AB_BASKET);
            registerDropSelf(BlockRegistry.AB_HERRING);
            registerDropSelf(BlockRegistry.AB_MOSAIC);
            registerDrop(BlockRegistry.ARCANE_ORE, ItemsRegistry.manaGem);
        }

        public void registerDropSelf(Block block){
            list.add(block);
            registerDropSelfLootTable(block);
        }

        public void registerDrop(Block input, IItemProvider output){
            list.add(input);
            registerDropping(input, output);
        }

        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            return list;
        }

    }

    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> tables = ImmutableList.of(
            Pair.of(BlockLootTable::new, LootParameterSets.BLOCK)
    );

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return tables;
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker)
    {
        map.forEach((p_218436_2_, p_218436_3_) -> {
            LootTableManager.func_227508_a_(validationtracker, p_218436_2_, p_218436_3_);
        });
    }

}
