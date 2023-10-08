package com.hollingsworth.arsnouveau.common.datagen;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.AlterationTable;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.block.ThreePartBlock;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.util.RegistryWrapper;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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

    public static class BlockLootTable extends BlockLoot {
        public List<Block> list = new ArrayList<>();

        @Override
        protected void addTables() {
            registerDropSelf(BlockRegistry.ENCHANTED_SPELL_TURRET);

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
            for (FlowerPotBlock pot : BlockRegistry.flowerPots.values()){
                list.add(pot);
                dropPottedContents(pot);
            }

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
            registerSlabItemTable(BlockRegistry.ARCHWOOD_SLABS);
            registerDropSelf(BlockRegistry.MAGELIGHT_TORCH);

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
            registerDropSelf(BlockRegistry.SOURCE_GEM_BLOCK);

            registerDropSelf(BlockRegistry.POTION_MELDER);
            registerDropSelf(BlockRegistry.RITUAL_BLOCK);
            registerDropSelf(BlockRegistry.SCONCE_BLOCK);
            registerBedCondition(BlockRegistry.SCRIBES_BLOCK, ScribesBlock.PART, ThreePartBlock.HEAD);
            registerDrop(BlockRegistry.DRYGMY_BLOCK, Items.MOSSY_COBBLESTONE);

            registerDropSelf(BlockRegistry.VITALIC_BLOCK);
            registerDropSelf(BlockRegistry.ALCHEMICAL_BLOCK);
            registerDropSelf(BlockRegistry.MYCELIAL_BLOCK);
            registerDropSelf(BlockRegistry.TIMER_SPELL_TURRET);
            registerDropSelf(BlockRegistry.BASIC_SPELL_TURRET);

            registerDropSelf(BlockRegistry.ARCHWOOD_CHEST);
            registerDropSelf(BlockRegistry.SPELL_PRISM);
            registerDropSelf(BlockRegistry.LAVA_LILY);

            registerDropSelf(BlockRegistry.AGRONOMIC_SOURCELINK);
            registerDropSelf(BlockRegistry.ENCHANTING_APP_BLOCK);
            registerDropSelf(BlockRegistry.ARCANE_PEDESTAL);
            registerDropSelf(BlockRegistry.ARCANE_PLATFORM);
            registerDropSelf(BlockRegistry.RELAY);
            registerDropSelf(BlockRegistry.RELAY_SPLITTER);
            registerDropSelf(BlockRegistry.ARCANE_CORE_BLOCK);
            registerDropSelf(BlockRegistry.IMBUEMENT_BLOCK);
            registerDropSelf(BlockRegistry.VOLCANIC_BLOCK);
            registerDropSelf(BlockRegistry.LAVA_LILY);
            registerDropSelf(BlockRegistry.BRAZIER_RELAY);

            registerDropSelf(BlockRegistry.RELAY_WARP);
            registerDropSelf(BlockRegistry.RELAY_DEPOSIT);
            registerDropSelf(BlockRegistry.RELAY_COLLECTOR);
            registerDropSelf(BlockRegistry.CRAFTING_LECTERN.get());
            registerDropSelf(BlockRegistry.RED_SBED);
            registerDropSelf(BlockRegistry.YELLOW_SBED);
            registerDropSelf(BlockRegistry.GREEN_SBED);
            registerDropSelf(BlockRegistry.PURPLE_SBED);
            registerDropSelf(BlockRegistry.BLUE_SBED);
            registerDropSelf(BlockRegistry.ORANGE_SBED);
            registerDropSelf(BlockRegistry.SCRYERS_CRYSTAL);
            registerDropSelf(BlockRegistry.SCRYERS_OCULUS);
            registerDropSelf(BlockRegistry.POTION_DIFFUSER);
            for(String s : LibBlockNames.DECORATIVE_SOURCESTONE){
                registerDropSelf(BlockRegistry.getBlock(s));
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, s + "_stairs"));
                registerDropSelf(block);
                Block slab = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, s + "_slab"));
                registerDropSelf(slab);
            }
            registerBedCondition(BlockRegistry.ALTERATION_TABLE, AlterationTable.PART, ThreePartBlock.HEAD);
            registerDropSelf(BlockRegistry.VOID_PRISM);
            registerDropSelf(BlockRegistry.MAGEBLOOM_BLOCK);
            registerDropSelf(BlockRegistry.GHOST_WEAVE);
            registerDropSelf(BlockRegistry.FALSE_WEAVE);
            registerDropSelf(BlockRegistry.MIRROR_WEAVE);
            registerDropSelf(BlockRegistry.ITEM_DETECTOR);
            registerDropSelf(BlockRegistry.SKY_WEAVE);
            registerDropSelf(BlockRegistry.ROTATING_TURRET);
            registerDropSelf(BlockRegistry.SPELL_SENSOR);
        }

        protected void registerSlabItemTable(Block p_124291_) {
            list.add(p_124291_);
            this.add(p_124291_,LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                    .add(applyExplosionDecay(p_124291_, LootItem.lootTableItem(p_124291_).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))
                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_124291_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE))))))));

        }

        protected <T extends Comparable<T> & StringRepresentable> void registerBedCondition(Block block, Property<T> prop, T isValue) {
            list.add(block);
            this.add(block, LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(block).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(prop, isValue)))))));
        }

        public void registerLeavesAndSticks(Block leaves, Block sapling) {
            list.add(leaves);
            this.add(leaves, l_state -> createLeavesDrops(l_state, sapling, DEFAULT_SAPLING_DROP_RATES));
        }

        public void registerDropDoor(Block block) {
            list.add(block);
            this.add(block, BlockLoot::createDoorTable);
        }

        public void registerDropSelf(RegistryWrapper block) {
            list.add((Block) block.get());
            dropSelf((Block) block.get());
        }

        public void registerDropSelf(Block block) {
            list.add(block);
            dropSelf(block);
        }

        public void registerDropSelf(RegistryObject<Block> block) {
            list.add(block.get());
            dropSelf(block.get());
        }
        public void registerDrop(Block input, ItemLike output) {
            list.add(input);
            dropOther(input, output);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return list;
        }

    }

    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> tables = ImmutableList.of(
            Pair.of(BlockLootTable::new, LootContextParamSets.BLOCK)
    );

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return tables;
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        map.forEach((p_218436_2_, p_218436_3_) -> {
            LootTables.validate(validationtracker, p_218436_2_, p_218436_3_);
        });
    }

}
