package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ItemTagProvider extends ItemTagsProvider {
    public static TagKey<Item> SUMMON_BED_ITEMS =  ItemTags.create(new ResourceLocation(ArsNouveau.MODID, "summon_bed"));
    public static TagKey<Item> SOURCE_GEM_TAG = ItemTags.create(new ResourceLocation("forge:gems/source"));
    public static TagKey<Item> SOURCE_GEM_BLOCK_TAG = ItemTags.create(new ResourceLocation("forge:storage_blocks/source"));
    public static TagKey<Item> ARCHWOOD_LOG_TAG = ItemTags.create(new ResourceLocation("forge:logs/archwood"));
    public static TagKey<Item> MAGIC_FOOD = ItemTags.create(new ResourceLocation(ArsNouveau.MODID, "magic_food"));
    public static TagKey<Item> WILDEN_DROP_TAG = ItemTags.create(new ResourceLocation(ArsNouveau.MODID, "wilden_drop"));

    public ItemTagProvider(DataGenerator p_126530_, BlockTagsProvider p_126531_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126530_, p_126531_, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {


        this.tag(MAGIC_FOOD)
                .add(ItemsRegistry.SOURCE_BERRY_PIE.get(),
                        ItemsRegistry.SOURCE_BERRY_ROLL.get());
        this.tag(ItemTags.create(new ResourceLocation(ArsNouveau.MODID, "whirlisprig/denied_drop")))
                .add(Items.DIRT).addTag(Tags.Items.SEEDS);

        this.tag(Tags.Items.FENCES).add(BlockRegistry.ARCHWOOD_FENCE.asItem());
        this.tag(Tags.Items.FENCES_WOODEN).add(BlockRegistry.ARCHWOOD_FENCE.asItem());
        this.tag(Tags.Items.FENCE_GATES).add(BlockRegistry.ARCHWOOD_FENCE_GATE.asItem());
        this.tag(Tags.Items.FENCE_GATES_WOODEN).add(BlockRegistry.ARCHWOOD_FENCE_GATE.asItem());
        this.tag(SOURCE_GEM_TAG)
                .add(ItemsRegistry.SOURCE_GEM.get());

        this.tag(ARCHWOOD_LOG_TAG)
                .add(BlockRegistry.BLAZING_LOG.asItem(),
                        BlockRegistry.CASCADING_LOG.asItem(),
                        BlockRegistry.VEXING_LOG.asItem(),
                        BlockRegistry.FLOURISHING_LOG.asItem(),
                        BlockRegistry.BLAZING_WOOD.asItem(),
                        BlockRegistry.CASCADING_WOOD.asItem(),
                        BlockRegistry.FLOURISHING_WOOD.asItem(),
                        BlockRegistry.VEXING_WOOD.asItem(),
                        BlockRegistry.STRIPPED_AWLOG_BLUE.asItem(),
                        BlockRegistry.STRIPPED_AWWOOD_BLUE.asItem(),
                        BlockRegistry.STRIPPED_AWLOG_GREEN.asItem(),
                        BlockRegistry.STRIPPED_AWWOOD_GREEN.asItem(),
                        BlockRegistry.STRIPPED_AWLOG_RED.asItem(),
                        BlockRegistry.STRIPPED_AWWOOD_RED.asItem(),
                        BlockRegistry.STRIPPED_AWLOG_PURPLE.asItem(),
                        BlockRegistry.STRIPPED_AWWOOD_PURPLE.asItem());
        this.tag(ItemTags.LOGS).add(
                BlockRegistry.BLAZING_LOG.asItem(),
                BlockRegistry.CASCADING_LOG.asItem(),
                BlockRegistry.VEXING_LOG.asItem(),
                BlockRegistry.FLOURISHING_LOG.asItem(),
                BlockRegistry.BLAZING_WOOD.asItem(),
                BlockRegistry.CASCADING_WOOD.asItem(),
                BlockRegistry.FLOURISHING_WOOD.asItem(),
                BlockRegistry.VEXING_WOOD.asItem(), BlockRegistry.STRIPPED_AWLOG_BLUE.asItem(),
                BlockRegistry.STRIPPED_AWWOOD_BLUE.asItem(),
                BlockRegistry.STRIPPED_AWLOG_GREEN.asItem(),
                BlockRegistry.STRIPPED_AWWOOD_GREEN.asItem(),
                BlockRegistry.STRIPPED_AWLOG_RED.asItem(),
                BlockRegistry.STRIPPED_AWWOOD_RED.asItem(),
                BlockRegistry.STRIPPED_AWLOG_PURPLE.asItem(),
                BlockRegistry.STRIPPED_AWWOOD_PURPLE.asItem()
        );
        this.tag(ItemTags.LOGS_THAT_BURN).add(
                BlockRegistry.BLAZING_LOG.asItem(),
                BlockRegistry.CASCADING_LOG.asItem(),
                BlockRegistry.VEXING_LOG.asItem(),
                BlockRegistry.FLOURISHING_LOG.asItem(),
                BlockRegistry.BLAZING_WOOD.asItem(),
                BlockRegistry.CASCADING_WOOD.asItem(),
                BlockRegistry.FLOURISHING_WOOD.asItem(),
                BlockRegistry.VEXING_WOOD.asItem(),
                BlockRegistry.STRIPPED_AWLOG_BLUE.asItem(),
                BlockRegistry.STRIPPED_AWWOOD_BLUE.asItem(),
                BlockRegistry.STRIPPED_AWLOG_GREEN.asItem(),
                BlockRegistry.STRIPPED_AWWOOD_GREEN.asItem(),
                BlockRegistry.STRIPPED_AWLOG_RED.asItem(),
                BlockRegistry.STRIPPED_AWWOOD_RED.asItem(),
                BlockRegistry.STRIPPED_AWLOG_PURPLE.asItem(),
                BlockRegistry.STRIPPED_AWWOOD_PURPLE.asItem()


        );
        this.tag(ItemTags.create(new ResourceLocation("forge", "planks/archwood")))
                .add(BlockRegistry.ARCHWOOD_PLANK.asItem());
        this.tag(Tags.Items.SEEDS)
                .add(BlockRegistry.MAGE_BLOOM_CROP.asItem());
        this.tag(Tags.Items.STORAGE_BLOCKS).add(BlockRegistry.SOURCE_GEM_BLOCK.asItem());
        this.tag(SOURCE_GEM_BLOCK_TAG)
                .add(BlockRegistry.SOURCE_GEM_BLOCK.asItem());
        this.tag(Tags.Items.GEMS).add(ItemsRegistry.SOURCE_GEM.get());

        this.tag(ItemTags.PLANKS).add(BlockRegistry.ARCHWOOD_PLANK.asItem());
        this.tag(ItemTags.FENCES).add(BlockRegistry.ARCHWOOD_FENCE.asItem());
        this.tag(ItemTags.WOODEN_FENCES).add(BlockRegistry.ARCHWOOD_FENCE.asItem());
        this.tag(ItemTags.BEACON_PAYMENT_ITEMS).add(ItemsRegistry.SOURCE_GEM.get());
        this.tag(ItemTags.BUTTONS).add(BlockRegistry.ARCHWOOD_BUTTON.asItem());
        this.tag(ItemTags.WOODEN_BUTTONS).add(BlockRegistry.ARCHWOOD_BUTTON.asItem());
        this.tag(ItemTags.DOORS).add(BlockRegistry.ARCHWOOD_DOOR.asItem());
        this.tag(ItemTags.WOODEN_DOORS).add(BlockRegistry.ARCHWOOD_DOOR.asItem());
        this.tag(ItemTags.SAPLINGS).add( BlockRegistry.BLAZING_SAPLING.asItem(),
                BlockRegistry.CASCADING_SAPLING.asItem(),
                BlockRegistry.FLOURISHING_SAPLING.asItem(),
                BlockRegistry.VEXING_SAPLING.asItem());
        this.tag(ItemTags.SLABS).add(BlockRegistry.ARCHWOOD_SLABS.asItem());
        this.tag(ItemTags.WOODEN_SLABS).add(BlockRegistry.ARCHWOOD_SLABS.asItem());
        this.tag(ItemTags.STAIRS).add(BlockRegistry.ARCHWOOD_STAIRS.asItem());
        this.tag(ItemTags.WOODEN_STAIRS).add(BlockRegistry.ARCHWOOD_STAIRS.asItem());
        this.tag(ItemTags.TRAPDOORS).add(BlockRegistry.ARCHWOOD_TRAPDOOR.asItem());
        this.tag(ItemTags.WOODEN_TRAPDOORS).add(BlockRegistry.ARCHWOOD_TRAPDOOR.asItem());
        this.tag(ItemTags.WOODEN_PRESSURE_PLATES).add(BlockRegistry.ARCHWOOD_PPlate.asItem());

        this.tag(WILDEN_DROP_TAG).add(ItemsRegistry.WILDEN_HORN.get(),
                ItemsRegistry.WILDEN_SPIKE.get(),
                ItemsRegistry.WILDEN_WING.get());

        this.tag(SUMMON_BED_ITEMS).add( BlockRegistry.RED_SBED.asItem(),
                BlockRegistry.GREEN_SBED.asItem(),
                BlockRegistry.YELLOW_SBED.asItem(),
                BlockRegistry.BLUE_SBED.asItem(),
                BlockRegistry.ORANGE_SBED.asItem(),
                BlockRegistry.PURPLE_SBED.asItem());

    }
}
