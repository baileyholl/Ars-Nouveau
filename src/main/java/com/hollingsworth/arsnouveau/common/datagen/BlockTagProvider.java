package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends IntrinsicHolderTagsProvider<Block> {

    public static TagKey<Block> IGNORE_TILE = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "ignore_tile"));
    public static TagKey<Block> SUMMON_BED = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "summon_bed"));
    public static TagKey<Block> SUMMON_SLEEPABLE = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "summon_sleepable"));
    public static TagKey<Block> DECORATIVE_AN = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "an_decorative"));
    public static TagKey<Block> MAGIC_SAPLINGS = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "magic_saplings"));
    public static TagKey<Block> MAGIC_PLANTS = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "magic_plants"));
    public static TagKey<Block> HARVEST_FOLIAGE = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "harvest/foliage"));
    public static TagKey<Block> HARVEST_STEMS = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "harvest/stems"));
    public static TagKey<Block> BREAK_BLACKLIST = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "break_blacklist"));
    public static TagKey<Block> GRAVITY_BLACKLIST = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "gravity_blacklist"));
    public static TagKey<Block> NO_BREAK_DROP = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "no_break_drop"));
    public static TagKey<Block> FELLABLE = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "harvest/fellable"));
    public static TagKey<Block> BUDDING_BLOCKS = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "golem/budding"));
    public static TagKey<Block> CLUSTER_BLOCKS = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "golem/cluster"));
    public static TagKey<Block> BREAK_WITH_PICKAXE = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "break_with_pickaxe"));
    public static TagKey<Block> RELOCATION_NOT_SUPPORTED = BlockTags.create(new ResourceLocation("forge", "relocation_not_supported"));

    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper helper) {
        super(output, Registries.BLOCK, future, block -> block.builtInRegistryHolder().key(), ArsNouveau.MODID, helper);
    }

    public String getName() {
        return "AN tags";
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(RELOCATION_NOT_SUPPORTED);
        this.tag(BUDDING_BLOCKS).add(Blocks.BUDDING_AMETHYST);
        this.tag(CLUSTER_BLOCKS).add(Blocks.AMETHYST_CLUSTER);
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                BlockRegistry.RELAY,
                BlockRegistry.ARCANE_CORE_BLOCK,
                BlockRegistry.ENCHANTING_APP_BLOCK,
                BlockRegistry.ARCANE_PEDESTAL.get(),
                BlockRegistry.ARCANE_PLATFORM.get(),
                BlockRegistry.MAGELIGHT_TORCH.get(),
                BlockRegistry.CREATIVE_SOURCE_JAR,
                BlockRegistry.RUNE_BLOCK,
                BlockRegistry.IMBUEMENT_BLOCK,
                BlockRegistry.SOURCE_JAR,
                BlockRegistry.RELAY_SPLITTER,
                BlockRegistry.ENCHANTED_SPELL_TURRET,
                BlockRegistry.VOLCANIC_BLOCK,
                BlockRegistry.LAVA_LILY,
                BlockRegistry.WIXIE_CAULDRON,
                BlockRegistry.SOURCE_GEM_BLOCK,
                BlockRegistry.RITUAL_BLOCK.get(),
                BlockRegistry.POTION_JAR,
                BlockRegistry.POTION_MELDER,
                BlockRegistry.SCONCE_BLOCK,
                BlockRegistry.DRYGMY_BLOCK,
                BlockRegistry.ALCHEMICAL_BLOCK,
                BlockRegistry.VITALIC_BLOCK,
                BlockRegistry.MYCELIAL_BLOCK,
                BlockRegistry.RELAY_DEPOSIT,
                BlockRegistry.RELAY_WARP,
                BlockRegistry.BASIC_SPELL_TURRET,
                BlockRegistry.TIMER_SPELL_TURRET,
                BlockRegistry.SPELL_PRISM,
                BlockRegistry.SCRYERS_CRYSTAL,
                BlockRegistry.SCRYERS_OCULUS,
                BlockRegistry.POTION_DIFFUSER,
                BlockRegistry.MOB_JAR,
                BlockRegistry.VOID_PRISM,
                BlockRegistry.BRAZIER_RELAY.get()
        );

        this.tag(BlockTags.MINEABLE_WITH_AXE).add(
                BlockRegistry.SCRIBES_BLOCK,
                BlockRegistry.CASCADING_LOG,
                BlockRegistry.CASCADING_WOOD,
                BlockRegistry.BLAZING_LOG,
                BlockRegistry.BLAZING_WOOD,
                BlockRegistry.VEXING_LOG,
                BlockRegistry.VEXING_WOOD,
                BlockRegistry.FLOURISHING_LOG,
                BlockRegistry.FLOURISHING_WOOD,
                BlockRegistry.ARCHWOOD_PLANK,
                BlockRegistry.ARCHWOOD_BUTTON,
                BlockRegistry.ARCHWOOD_STAIRS,
                BlockRegistry.ARCHWOOD_SLABS,
                BlockRegistry.ARCHWOOD_FENCE_GATE,
                BlockRegistry.ARCHWOOD_TRAPDOOR,
                BlockRegistry.ARCHWOOD_PPlate,
                BlockRegistry.ARCHWOOD_FENCE,
                BlockRegistry.ARCHWOOD_DOOR,
                BlockRegistry.STRIPPED_AWLOG_BLUE,
                BlockRegistry.STRIPPED_AWWOOD_BLUE,
                BlockRegistry.STRIPPED_AWLOG_GREEN,
                BlockRegistry.STRIPPED_AWWOOD_GREEN,
                BlockRegistry.STRIPPED_AWLOG_RED,
                BlockRegistry.STRIPPED_AWWOOD_RED,
                BlockRegistry.STRIPPED_AWLOG_PURPLE,
                BlockRegistry.STRIPPED_AWWOOD_PURPLE,
                BlockRegistry.CRAFTING_LECTERN.get(),
                BlockRegistry.ARCHWOOD_CHEST,
                BlockRegistry.ALTERATION_TABLE,
                BlockRegistry.ITEM_DETECTOR.get(),
                BlockRegistry.REPOSITORY
        );
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(
                BlockRegistry.CASCADING_LEAVE,
                BlockRegistry.BLAZING_LEAVES,
                BlockRegistry.FLOURISHING_LEAVES,
                BlockRegistry.VEXING_LEAVES
        );
        this.tag(Tags.Blocks.CHESTS).add(BlockRegistry.ARCHWOOD_CHEST);
        this.tag(Tags.Blocks.CHESTS_WOODEN).add(BlockRegistry.ARCHWOOD_CHEST);
        for (String s : LibBlockNames.DECORATIVE_SOURCESTONE) {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, s));
            this.tag(DECORATIVE_AN).add(block);
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
        }

        this.tag(DECORATIVE_AN).add(BlockRegistry.FALSE_WEAVE, BlockRegistry.MIRROR_WEAVE, BlockRegistry.GHOST_WEAVE, BlockRegistry.MAGEBLOOM_BLOCK);

        this.tag(HARVEST_FOLIAGE).addTag(BlockTags.LEAVES).add(
                Blocks.BROWN_MUSHROOM_BLOCK,
                Blocks.RED_MUSHROOM_BLOCK,
                Blocks.NETHER_WART_BLOCK,
                Blocks.WARPED_WART_BLOCK,
                Blocks.SHROOMLIGHT,
                Blocks.VINE,
                Blocks.CAVE_VINES,
                Blocks.TWISTING_VINES,
                Blocks.PUMPKIN,
                Blocks.MELON,
                Blocks.WEEPING_VINES,
                Blocks.MANGROVE_ROOTS);

        this.tag(HARVEST_STEMS).add(
                Blocks.BAMBOO,
                Blocks.SUGAR_CANE,
                Blocks.CACTUS);


        this.tag(FELLABLE).add(Blocks.MUSHROOM_STEM).addTags(BlockTags.LOGS, HARVEST_FOLIAGE, HARVEST_STEMS);

        TagKey<Block> WHIRLISPRIG_KINDA_LIKES = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "whirlisprig/kinda_likes"));
        TagKey<Block> WHIRLISPRIG_GREATLY_LIKES = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "whirlisprig/greatly_likes"));
        this.tag(WHIRLISPRIG_GREATLY_LIKES).add(Blocks.MUSHROOM_STEM,
                Blocks.BROWN_MUSHROOM_BLOCK,
                Blocks.RED_MUSHROOM_BLOCK,
                Blocks.SHROOMLIGHT,
                Blocks.WARPED_WART_BLOCK, Blocks.NETHER_WART_BLOCK);
        this.tag(WHIRLISPRIG_KINDA_LIKES);

        this.tag(MAGIC_SAPLINGS).add(
                BlockRegistry.BLAZING_SAPLING,
                BlockRegistry.CASCADING_SAPLING,
                BlockRegistry.FLOURISHING_SAPLING,
                BlockRegistry.VEXING_SAPLING
        );
        this.tag(BlockTags.SAPLINGS).add(
                BlockRegistry.BLAZING_SAPLING,
                BlockRegistry.CASCADING_SAPLING,
                BlockRegistry.FLOURISHING_SAPLING,
                BlockRegistry.VEXING_SAPLING
        );

        this.tag(MAGIC_PLANTS).addTag(MAGIC_SAPLINGS).add(
                BlockRegistry.SOURCEBERRY_BUSH,
                BlockRegistry.MAGE_BLOOM_CROP,
                BlockRegistry.FROSTAYA_POD,
                BlockRegistry.MENDOSTEEN_POD,
                BlockRegistry.BASTION_POD,
                BlockRegistry.BOMBEGRANTE_POD
        );

        this.tag(Tags.Blocks.FENCES).add(BlockRegistry.ARCHWOOD_FENCE);
        this.tag(Tags.Blocks.FENCES_WOODEN).add(BlockRegistry.ARCHWOOD_FENCE);

        this.tag(Tags.Blocks.FENCE_GATES).add(BlockRegistry.ARCHWOOD_FENCE_GATE);
        this.tag(Tags.Blocks.FENCE_GATES_WOODEN).add(BlockRegistry.ARCHWOOD_FENCE_GATE);

        this.tag(BlockTags.LOGS).add(
                BlockRegistry.VEXING_LOG,
                BlockRegistry.CASCADING_LOG,
                BlockRegistry.FLOURISHING_LOG,
                BlockRegistry.BLAZING_LOG,
                BlockRegistry.STRIPPED_AWLOG_BLUE,
                BlockRegistry.STRIPPED_AWWOOD_BLUE,
                BlockRegistry.STRIPPED_AWLOG_GREEN,
                BlockRegistry.STRIPPED_AWWOOD_GREEN,
                BlockRegistry.STRIPPED_AWLOG_RED,
                BlockRegistry.STRIPPED_AWWOOD_RED,
                BlockRegistry.STRIPPED_AWLOG_PURPLE,
                BlockRegistry.STRIPPED_AWWOOD_PURPLE);
        this.tag(BlockTags.LOGS_THAT_BURN).add(BlockRegistry.VEXING_LOG,
                BlockRegistry.CASCADING_LOG,
                BlockRegistry.FLOURISHING_LOG,
                BlockRegistry.BLAZING_LOG,
                BlockRegistry.STRIPPED_AWLOG_BLUE,
                BlockRegistry.STRIPPED_AWWOOD_BLUE,
                BlockRegistry.STRIPPED_AWLOG_GREEN,
                BlockRegistry.STRIPPED_AWWOOD_GREEN,
                BlockRegistry.STRIPPED_AWLOG_RED,
                BlockRegistry.STRIPPED_AWWOOD_RED,
                BlockRegistry.STRIPPED_AWLOG_PURPLE,
                BlockRegistry.STRIPPED_AWWOOD_PURPLE);
        this.tag(BlockTags.PLANKS).add(BlockRegistry.ARCHWOOD_PLANK);
        this.tag(BlockTags.FENCE_GATES).add(BlockRegistry.ARCHWOOD_FENCE_GATE);
        this.tag(BlockTags.FENCES).add(BlockRegistry.ARCHWOOD_FENCE);
        this.tag(BlockTags.WOODEN_FENCES).add(BlockRegistry.ARCHWOOD_FENCE);
        TagKey<Block> ARCHWOOD_LEAVES = BlockTags.create(new ResourceLocation("minecraft", "leaves/archwood_leaves"));
        this.tag(ARCHWOOD_LEAVES)
                .add(BlockRegistry.VEXING_LEAVES,
                        BlockRegistry.CASCADING_LEAVE,
                        BlockRegistry.BLAZING_LEAVES,
                        BlockRegistry.FLOURISHING_LEAVES);
        this.tag(BlockTags.LEAVES).add(BlockRegistry.VEXING_LEAVES,
                BlockRegistry.CASCADING_LEAVE,
                BlockRegistry.BLAZING_LEAVES,
                BlockRegistry.FLOURISHING_LEAVES);

        this.tag(BlockTags.BEE_GROWABLES).add(BlockRegistry.MAGE_BLOOM_CROP);
        this.tag(BlockTags.BUTTONS).add(BlockRegistry.ARCHWOOD_BUTTON);
        this.tag(BlockTags.CROPS).add(BlockRegistry.MAGE_BLOOM_CROP);
        this.tag(BlockTags.SLABS).add(BlockRegistry.ARCHWOOD_SLABS);
        this.tag(BlockTags.STAIRS).add(BlockRegistry.ARCHWOOD_STAIRS);
        this.tag(BlockTags.TRAPDOORS).add(BlockRegistry.ARCHWOOD_TRAPDOOR);
        this.tag(BlockTags.WOODEN_BUTTONS).add(BlockRegistry.ARCHWOOD_BUTTON);
        this.tag(BlockTags.WOODEN_DOORS).add(BlockRegistry.ARCHWOOD_DOOR);
        this.tag(BlockTags.DOORS).add(BlockRegistry.ARCHWOOD_DOOR);
        this.tag(BlockTags.WOODEN_SLABS).add(BlockRegistry.ARCHWOOD_SLABS);
        this.tag(BlockTags.WOODEN_STAIRS).add(BlockRegistry.ARCHWOOD_STAIRS);
        this.tag(BlockTags.WOODEN_TRAPDOORS).add(BlockRegistry.ARCHWOOD_TRAPDOOR);

        this.tag(IGNORE_TILE).add(
                BlockRegistry.INTANGIBLE_AIR,
                BlockRegistry.REDSTONE_AIR,
                BlockRegistry.MAGE_BLOCK,
                BlockRegistry.SCONCE_BLOCK,
                BlockRegistry.LIGHT_BLOCK,
                BlockRegistry.GHOST_WEAVE,
                BlockRegistry.SKY_WEAVE.get()
        );

        this.tag(SUMMON_BED).add(
                BlockRegistry.RED_SBED,
                BlockRegistry.GREEN_SBED,
                BlockRegistry.YELLOW_SBED,
                BlockRegistry.BLUE_SBED,
                BlockRegistry.ORANGE_SBED,
                BlockRegistry.PURPLE_SBED
        );
        this.tag(SUMMON_SLEEPABLE).addTag(SUMMON_BED).addTag(BlockTags.BEDS);
        this.tag(BREAK_BLACKLIST);
        this.tag(NO_BREAK_DROP).add(Blocks.TURTLE_EGG);
        this.tag(GRAVITY_BLACKLIST).add(Blocks.BEDROCK, BlockRegistry.MAGE_BLOCK).addTag(RELOCATION_NOT_SUPPORTED);
        this.tag(BREAK_WITH_PICKAXE).add(Blocks.AMETHYST_CLUSTER);
        this.tag(BlockTags.PORTALS).add(BlockRegistry.PORTAL_BLOCK);
    }
}
