package com.hollingsworth.arsnouveau.common.datagen;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

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
    private static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
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
            registerDropSelf(BlockRegistry.WARD_BLOCK);
            registerDropSelf(BlockRegistry.SPELL_TURRET);
            registerDrop(BlockRegistry.ARCANE_ORE, ItemsRegistry.manaGem);
            registerDropSelf(BlockRegistry.AB_CLOVER);
            registerDropSelf(BlockRegistry.AB_SMOOTH_SLAB);
            registerDropSelf(BlockRegistry.AB_SMOOTH);

            registerDropSelf(BlockRegistry.BLAZING_LOG);
            registerDropSelf(BlockRegistry.VEXING_LOG);
            registerDropSelf(BlockRegistry.CASCADING_LOG);
            registerDropSelf(BlockRegistry.FLOURISHING_LOG);

            registerDropSelf(BlockRegistry.BLAZING_SAPLING);
            registerDropSelf(BlockRegistry.VEXING_SAPLING);
            registerDropSelf(BlockRegistry.CASCADING_SAPLING);
            registerDropSelf(BlockRegistry.FLOURISHING_SAPLING);
            registerDropSelf(BlockRegistry.ARCHWOOD_PLANK);

            registerDrop(BlockRegistry.WIXIE_CAULDRON, Items.CAULDRON);

            registerLeavesAndSticks(BlockRegistry.BLAZING_LEAVES, BlockRegistry.BLAZING_SAPLING);
            registerLeavesAndSticks(BlockRegistry.CASCADING_LEAVE, BlockRegistry.CASCADING_SAPLING);
            registerLeavesAndSticks(BlockRegistry.FLOURISHING_LEAVES, BlockRegistry.FLOURISHING_SAPLING);
            registerLeavesAndSticks(BlockRegistry.VEXING_LEAVES, BlockRegistry.VEXING_SAPLING);


            registerDropSelf(BlockRegistry.BLAZING_WOOD);
            registerDropSelf(BlockRegistry.VEXING_WOOD);
            registerDropSelf(BlockRegistry.CASCADING_WOOD);
            registerDropSelf(BlockRegistry.FLOURISHING_WOOD);

            registerDropSelf(BlockRegistry.ARCHWOOD_BUTTON);
            registerDropSelf(BlockRegistry.ARCHWOOD_STAIRS);
            registerDropSelf(BlockRegistry.ARCHWOOD_SLABS);
           // registerDropSelf(BlockRegistry.ARCHWOOD_SIGN);
            registerDropSelf(BlockRegistry.ARCHWOOD_FENCE_GATE);
            registerDropSelf(BlockRegistry.ARCHWOOD_TRAPDOOR);
            registerDropSelf(BlockRegistry.ARCHWOOD_PPlate);
            registerDropSelf(BlockRegistry.ARCHWOOD_FENCE);
            registerDropSelf(BlockRegistry.STRIPPED_AWLOG_BLUE);
            registerDropSelf(BlockRegistry.STRIPPED_AWWOOD_BLUE);
            registerDropSelf(BlockRegistry.STRIPPED_AWLOG_GREEN);
            registerDropSelf(BlockRegistry.STRIPPED_AWWOOD_GREEN);
            registerDropSelf(BlockRegistry.STRIPPED_AWLOG_RED);
            registerDropSelf(BlockRegistry.STRIPPED_AWWOOD_RED);
            registerDropSelf(BlockRegistry.STRIPPED_AWLOG_PURPLE);
            registerDropSelf(BlockRegistry.STRIPPED_AWWOOD_PURPLE);
            registerDropDoor(BlockRegistry.ARCHWOOD_DOOR);
            registerDropSelf(BlockRegistry.MANA_GEM_BLOCK);

            registerDropSelf(BlockRegistry.AB_SMOOTH_BASKET);
            registerDropSelf(BlockRegistry.AB_SMOOTH_CLOVER);
            registerDropSelf(BlockRegistry.AB_SMOOTH_HERRING);
            registerDropSelf(BlockRegistry.AB_SMOOTH_MOSAIC);
            registerDropSelf(BlockRegistry.AB_SMOOTH_ALTERNATING);
            registerDropSelf(BlockRegistry.AB_SMOOTH_ASHLAR);
            registerDropSelf(BlockRegistry.POTION_MELDER);

        }

        public void registerLeavesAndSticks(Block leaves, Block sapling){
            list.add(leaves);
            this.registerLootTable(leaves, l_state -> droppingWithChancesAndSticks(l_state, sapling, DEFAULT_SAPLING_DROP_RATES));
        }

        public void registerDropDoor(Block block){
            list.add(block);
            this.registerLootTable(block, BlockLootTables::registerDoor);
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
            LootTableManager.validateLootTable(validationtracker, p_218436_2_, p_218436_3_);
        });
    }

}
