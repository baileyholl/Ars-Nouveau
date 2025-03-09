package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends IntrinsicHolderTagsProvider<Item> {
    public static TagKey<Item> SUMMON_BED_ITEMS = ItemTags.create(ArsNouveau.prefix( "summon_bed"));
    public static TagKey<Item> SOURCE_GEM_TAG = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "gems/source"));
    public static TagKey<Item> SOURCE_GEM_BLOCK_TAG = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/source"));
    public static TagKey<Item> ARCHWOOD_LOG_TAG = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "logs/archwood"));
    public static TagKey<Item> MAGIC_FOOD = ItemTags.create(ArsNouveau.prefix( "magic_food"));
    public static TagKey<Item> WILDEN_DROP_TAG = ItemTags.create(ArsNouveau.prefix( "wilden_drop"));
    public static TagKey<Item> SHARD_TAG = ItemTags.create(ArsNouveau.prefix( "golem/shard"));
    public static TagKey<Item> BERRY_TAG = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "fruits/berry"));
    public static final TagKey<Item> SUMMON_SHARDS_TAG = ItemTags.create(ArsNouveau.prefix( "magic_shards"));
    public static TagKey<Item> JAR_ITEM_BLACKLIST = ItemTags.create(ArsNouveau.prefix( "interact_jar_blacklist"));
    public static TagKey<Item> RITUAL_LOOT_BLACKLIST = ItemTags.create(ArsNouveau.prefix( "ritual_loot_blacklist"));
    public static TagKey<Item> RITUAL_TRADE_BLACKLIST = ItemTags.create(ArsNouveau.prefix( "ritual_trade_blacklist"));
    public static TagKey<Item> STORAGE_BLOCKS_QUARTZ = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/quartz"));
    public static TagKey<Item> SHADY_WIZARD_FRUITS = ItemTags.create(ArsNouveau.prefix( "shady_wizard_fruits"));

    public static TagKey<Item> MAGIC_ARMOR = ItemTags.create(ArsNouveau.prefix("magic_armor"));
    public static TagKey<Item> MAGIC_HOOD = ItemTags.create(ArsNouveau.prefix("magic_hood"));
    public static TagKey<Item> MAGIC_ROBE = ItemTags.create(ArsNouveau.prefix("magic_robe"));
    public static TagKey<Item> MAGIC_LEGS = ItemTags.create(ArsNouveau.prefix("magic_legs"));
    public static TagKey<Item> MAGIC_BOOT = ItemTags.create(ArsNouveau.prefix("magic_boots"));


    public ItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper helper) {
        super(output, Registries.ITEM, future, item -> item.builtInRegistryHolder().key(), ArsNouveau.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(STORAGE_BLOCKS_QUARTZ).add(Items.QUARTZ_BLOCK);
        this.tag(SUMMON_SHARDS_TAG)
                .add(ItemsRegistry.DRYGMY_SHARD.get(),
                        ItemsRegistry.STARBUNCLE_SHARD.get(),
                        ItemsRegistry.WIXIE_SHARD.get(),
                        ItemsRegistry.WHIRLISPRIG_SHARDS.get(),
                        ItemsRegistry.ALAKARKINOS_SHARD.get());

        this.tag(BERRY_TAG).add(BlockRegistry.SOURCEBERRY_BUSH.asItem());

        this.tag(Tags.Items.MUSIC_DISCS).add(ItemsRegistry.FIREL_DISC.get(), ItemsRegistry.WILD_HUNT.get(), ItemsRegistry.SOUND_OF_GLASS.get());
        this.tag(MAGIC_FOOD)
                .add(ItemsRegistry.SOURCE_BERRY_PIE.get(),
                        ItemsRegistry.SOURCE_BERRY_ROLL.get());
        this.tag(Tags.Items.FOODS).addTag(MAGIC_FOOD);
        this.tag(Tags.Items.FOODS_BERRY).add(BlockRegistry.SOURCEBERRY_BUSH.asItem());
        this.tag(Tags.Items.FOODS_FRUIT).add(BlockRegistry.FROSTAYA_POD.asItem(),
                BlockRegistry.BOMBEGRANTE_POD.asItem(),
                BlockRegistry.MENDOSTEEN_POD.asItem(),
                BlockRegistry.BASTION_POD.asItem());
        this.tag(ItemTags.create(ArsNouveau.prefix( "whirlisprig/denied_drop")))
                .add(Items.DIRT).addTag(Tags.Items.SEEDS);

        this.tag(Tags.Items.RANGED_WEAPON_TOOLS).add(ItemsRegistry.SPELL_BOW.get(), ItemsRegistry.SPELL_CROSSBOW.get());

        this.tag(Tags.Items.FENCES).add(BlockRegistry.ARCHWOOD_FENCE.asItem());
        this.tag(Tags.Items.FENCES_WOODEN).add(BlockRegistry.ARCHWOOD_FENCE.asItem());
        this.tag(Tags.Items.FENCE_GATES).add(BlockRegistry.ARCHWOOD_FENCE_GATE.asItem());
        this.tag(Tags.Items.FENCE_GATES_WOODEN).add(BlockRegistry.ARCHWOOD_FENCE_GATE.asItem());
        this.tag(SOURCE_GEM_TAG)
                .add(ItemsRegistry.SOURCE_GEM.get());
        this.tag(SHARD_TAG).add(Items.AMETHYST_SHARD);
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
        this.tag(ItemTags.LEAVES).add(BlockRegistry.VEXING_LEAVES.asItem(),
                BlockRegistry.CASCADING_LEAVE.asItem(),
                BlockRegistry.BLAZING_LEAVES.asItem(),
                BlockRegistry.FLOURISHING_LEAVES.asItem());
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
        this.tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "planks/archwood")))
                .add(BlockRegistry.ARCHWOOD_PLANK.asItem());
        this.tag(Tags.Items.SEEDS)
                .add(BlockRegistry.MAGE_BLOOM_CROP.asItem());
        this.tag(Tags.Items.CROPS).add(ItemsRegistry.MAGE_BLOOM.asItem());
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
        this.tag(ItemTags.SAPLINGS).add(BlockRegistry.BLAZING_SAPLING.asItem(),
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

        this.tag(SUMMON_BED_ITEMS).add(BlockRegistry.RED_SBED.asItem(),
                BlockRegistry.GREEN_SBED.asItem(),
                BlockRegistry.YELLOW_SBED.asItem(),
                BlockRegistry.BLUE_SBED.asItem(),
                BlockRegistry.ORANGE_SBED.asItem(),
                BlockRegistry.PURPLE_SBED.asItem());

        Item[] books = new Item[]{ItemsRegistry.WORN_NOTEBOOK.asItem(),
                ItemsRegistry.CASTER_TOME.asItem(),
                ItemsRegistry.NOVICE_SPELLBOOK.asItem(),
                ItemsRegistry.ARCHMAGE_SPELLBOOK.asItem(),
                ItemsRegistry.APPRENTICE_SPELLBOOK.asItem(),
                ItemsRegistry.CREATIVE_SPELLBOOK.asItem()};

        this.tag(ItemTags.LECTERN_BOOKS).add(books);

        this.tag(ItemTags.BOOKSHELF_BOOKS).add(books);

        this.tag(ItemTags.SWORDS).add(ItemsRegistry.ENCHANTERS_SWORD.get());
        this.tag(Tags.Items.TOOLS_SHIELD).add(ItemsRegistry.ENCHANTERS_SHIELD.get());

        this.tag(MAGIC_ARMOR)
                .addTag(MAGIC_HOOD)
                .addTag(MAGIC_ROBE)
                .addTag(MAGIC_LEGS)
                .addTag(MAGIC_BOOT);

        this.tag(Tags.Items.ARMORS)
                .addTag(MAGIC_ARMOR);

        this.tag(MAGIC_HOOD)
                .add(ItemsRegistry.SORCERER_HOOD.asItem(), ItemsRegistry.ARCANIST_HOOD.asItem(), ItemsRegistry.BATTLEMAGE_HOOD.asItem());
        this.tag(MAGIC_ROBE)
                .add(ItemsRegistry.SORCERER_ROBES.asItem(), ItemsRegistry.ARCANIST_ROBES.asItem(), ItemsRegistry.BATTLEMAGE_ROBES.asItem());
        this.tag(MAGIC_LEGS)
                .add(ItemsRegistry.SORCERER_LEGGINGS.asItem(), ItemsRegistry.ARCANIST_LEGGINGS.asItem(), ItemsRegistry.BATTLEMAGE_LEGGINGS.asItem());
        this.tag(MAGIC_BOOT)
                .add(ItemsRegistry.SORCERER_BOOTS.asItem(), ItemsRegistry.ARCANIST_BOOTS.asItem(), ItemsRegistry.BATTLEMAGE_BOOTS.asItem());

        this.tag(ItemTags.FOOT_ARMOR)
                .addTag(MAGIC_BOOT);
        this.tag(ItemTags.CHEST_ARMOR)
                .addTag(MAGIC_ROBE);
        this.tag(ItemTags.HEAD_ARMOR)
                .addTag(MAGIC_HOOD);
        this.tag(ItemTags.LEG_ARMOR)
                .addTag(MAGIC_LEGS);

        this.tag(Tags.Items.CHESTS).add(BlockRegistry.ARCHWOOD_CHEST.asItem());
        this.tag(Tags.Items.CHESTS_WOODEN).add(BlockRegistry.ARCHWOOD_CHEST.asItem());
        this.tag(JAR_ITEM_BLACKLIST);
        this.tag(RITUAL_LOOT_BLACKLIST);
        this.tag(RITUAL_TRADE_BLACKLIST);

        this.tag(SHADY_WIZARD_FRUITS).add(
                BlockRegistry.BOMBEGRANTE_POD.asItem(),
                BlockRegistry.BASTION_POD.asItem(),
                BlockRegistry.FROSTAYA_POD.asItem(),
                BlockRegistry.MENDOSTEEN_POD.asItem()
        );

        this.tag(Tags.Items.TOOLS_BOW).add(ItemsRegistry.SPELL_BOW.get());
        this.tag(Tags.Items.TOOLS_CROSSBOW).add(ItemsRegistry.SPELL_CROSSBOW.get());
        this.tag(ItemTags.BOW_ENCHANTABLE).add(ItemsRegistry.SPELL_BOW.get());
        this.tag(ItemTags.CROSSBOW_ENCHANTABLE).add(ItemsRegistry.SPELL_CROSSBOW.get());
        this.tag(ItemTags.DURABILITY_ENCHANTABLE).add(ItemsRegistry.ENCHANTERS_SHIELD.get());
        this.tag(Tags.Items.TOOLS_FISHING_ROD).add(ItemsRegistry.ENCHANTERS_FISHING_ROD.get());
        this.tag(ItemTags.FISHING_ENCHANTABLE).add(ItemsRegistry.ENCHANTERS_FISHING_ROD.get());
        this.tag(Tags.Items.MINING_TOOL_TOOLS).add(ItemsRegistry.ENCHANTERS_GAUNTLET.get());
        this.tag(ItemTags.MINING_ENCHANTABLE).add(ItemsRegistry.ENCHANTERS_GAUNTLET.get());
        this.tag(ItemTags.MINING_LOOT_ENCHANTABLE).add(ItemsRegistry.ENCHANTERS_GAUNTLET.get());
    }
}
