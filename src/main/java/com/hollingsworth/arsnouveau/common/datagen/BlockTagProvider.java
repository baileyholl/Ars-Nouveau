package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
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
    public static TagKey<Block> AUTOPULL_DISABLED = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "storage/autopull_disabled"));
    public static TagKey<Block> RELOCATION_NOT_SUPPORTED = BlockTags.create(new ResourceLocation("forge", "relocation_not_supported"));
    public static TagKey<Block> OCCLUDES_SPELL_SENSOR = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "occludes_spell_sensor"));
    public static TagKey<Block> INTERACT_BLACKLIST = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "interact_blacklist"));
    public static TagKey<Block> CASCADING_LOGS = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "cascading_logs"));
    public static TagKey<Block> FLOURISHING_LOGS = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "flourishing_logs"));
    public static TagKey<Block> VEXING_LOGS = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "vexing_logs"));
    public static TagKey<Block> BLAZING_LOGS = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "blazing_logs"));
    public static TagKey<Block> DOWSING_ROD = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "dowsing_rod"));
    public static TagKey<Block> BUSHES = BlockTags.create(new ResourceLocation("forge", "bushes"));

    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper helper) {
        super(output, Registries.BLOCK, future, block -> block.builtInRegistryHolder().key(), ArsNouveau.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(INTERACT_BLACKLIST);
        this.tag(BlockTags.FIRE).add(BlockRegistry.MAGIC_FIRE.get());
        this.tag(OCCLUDES_SPELL_SENSOR).add(BlockRegistry.MAGEBLOOM_BLOCK.get());
        this.tag(RELOCATION_NOT_SUPPORTED);
        this.tag(BUDDING_BLOCKS).add(Blocks.BUDDING_AMETHYST);
        this.tag(CLUSTER_BLOCKS).add(Blocks.AMETHYST_CLUSTER);
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                BlockRegistry.RELAY.get(),
                BlockRegistry.ARCANE_CORE_BLOCK.get(),
                BlockRegistry.ENCHANTING_APP_BLOCK.get(),
                BlockRegistry.ARCANE_PEDESTAL.get(),
                BlockRegistry.ARCANE_PLATFORM.get(),
                BlockRegistry.MAGELIGHT_TORCH.get(),
                BlockRegistry.CREATIVE_SOURCE_JAR.get(),
                BlockRegistry.RUNE_BLOCK.get(),
                BlockRegistry.IMBUEMENT_BLOCK.get(),
                BlockRegistry.SOURCE_JAR.get(),
                BlockRegistry.RELAY_SPLITTER.get(),
                BlockRegistry.ENCHANTED_SPELL_TURRET.get(),
                BlockRegistry.VOLCANIC_BLOCK.get(),
                BlockRegistry.WIXIE_CAULDRON.get(),
                BlockRegistry.SOURCE_GEM_BLOCK.get(),
                BlockRegistry.RITUAL_BLOCK.get(),
                BlockRegistry.POTION_JAR.get(),
                BlockRegistry.POTION_MELDER.get(),
                BlockRegistry.GOLD_SCONCE_BLOCK.get(),
                BlockRegistry.SOURCESTONE_SCONCE_BLOCK.get(),
                BlockRegistry.POLISHED_SCONCE_BLOCK.get(),
                BlockRegistry.ARCHWOOD_SCONCE_BLOCK.get(),
                BlockRegistry.DRYGMY_BLOCK.get(),
                BlockRegistry.ALCHEMICAL_BLOCK.get(),
                BlockRegistry.VITALIC_BLOCK.get(),
                BlockRegistry.MYCELIAL_BLOCK.get(),
                BlockRegistry.RELAY_DEPOSIT.get(),
                BlockRegistry.RELAY_WARP.get(),
                BlockRegistry.BASIC_SPELL_TURRET.get(),
                BlockRegistry.TIMER_SPELL_TURRET.get(),
                BlockRegistry.SPELL_PRISM.get(),
                BlockRegistry.SCRYERS_CRYSTAL.get(),
                BlockRegistry.SCRYERS_OCULUS.get(),
                BlockRegistry.POTION_DIFFUSER.get(),
                BlockRegistry.MOB_JAR.get(),
                BlockRegistry.VOID_PRISM.get(),
                BlockRegistry.BRAZIER_RELAY.get(),
                BlockRegistry.REDSTONE_RELAY.get()
        );

        this.tag(BlockTags.MINEABLE_WITH_AXE).add(
                BlockRegistry.SCRIBES_BLOCK.get(),
                BlockRegistry.CASCADING_LOG.get(),
                BlockRegistry.CASCADING_WOOD.get(),
                BlockRegistry.BLAZING_LOG.get(),
                BlockRegistry.BLAZING_WOOD.get(),
                BlockRegistry.VEXING_LOG.get(),
                BlockRegistry.VEXING_WOOD.get(),
                BlockRegistry.FLOURISHING_LOG.get(),
                BlockRegistry.FLOURISHING_WOOD.get(),
                BlockRegistry.ARCHWOOD_PLANK.get(),
                BlockRegistry.ARCHWOOD_BUTTON.get(),
                BlockRegistry.ARCHWOOD_STAIRS.get(),
                BlockRegistry.ARCHWOOD_SLABS.get(),
                BlockRegistry.ARCHWOOD_FENCE_GATE.get(),
                BlockRegistry.ARCHWOOD_TRAPDOOR.get(),
                BlockRegistry.ARCHWOOD_PPlate.get(),
                BlockRegistry.ARCHWOOD_FENCE.get(),
                BlockRegistry.ARCHWOOD_DOOR.get(),
                BlockRegistry.STRIPPED_AWLOG_BLUE.get(),
                BlockRegistry.STRIPPED_AWWOOD_BLUE.get(),
                BlockRegistry.STRIPPED_AWLOG_GREEN.get(),
                BlockRegistry.STRIPPED_AWWOOD_GREEN.get(),
                BlockRegistry.STRIPPED_AWLOG_RED.get(),
                BlockRegistry.STRIPPED_AWWOOD_RED.get(),
                BlockRegistry.STRIPPED_AWLOG_PURPLE.get(),
                BlockRegistry.STRIPPED_AWWOOD_PURPLE.get(),
                BlockRegistry.CRAFTING_LECTERN.get(),
                BlockRegistry.ARCHWOOD_CHEST.get(),
                BlockRegistry.ALTERATION_TABLE.get(),
                BlockRegistry.ITEM_DETECTOR.get(),
                BlockRegistry.REPOSITORY.get()
        );
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(
                BlockRegistry.CASCADING_LEAVE.get(),
                BlockRegistry.BLAZING_LEAVES.get(),
                BlockRegistry.FLOURISHING_LEAVES.get(),
                BlockRegistry.VEXING_LEAVES.get()
        );
        this.tag(Tags.Blocks.CHESTS).add(BlockRegistry.ARCHWOOD_CHEST.get());
        this.tag(Tags.Blocks.CHESTS_WOODEN).add(BlockRegistry.ARCHWOOD_CHEST.get());
        for (String s : LibBlockNames.DECORATIVE_SOURCESTONE) {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, s));
            Block stair = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, s + "_stairs"));
            Block slab = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, s + "_slab"));
            this.tag(DECORATIVE_AN).add(block, stair, slab);
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block, stair, slab);
        }

        this.tag(DECORATIVE_AN).add(BlockRegistry.FALSE_WEAVE.get(), BlockRegistry.MIRROR_WEAVE.get(), BlockRegistry.GHOST_WEAVE.get(), BlockRegistry.MAGEBLOOM_BLOCK.get());

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
                Blocks.WARPED_WART_BLOCK, Blocks.NETHER_WART_BLOCK,
                Blocks.CACTUS,
                Blocks.SUGAR_CANE,
                Blocks.CHORUS_FLOWER,
                Blocks.CHORUS_PLANT);
        this.tag(WHIRLISPRIG_KINDA_LIKES);

        this.tag(MAGIC_SAPLINGS).add(
                BlockRegistry.BLAZING_SAPLING.get(),
                BlockRegistry.CASCADING_SAPLING.get(),
                BlockRegistry.FLOURISHING_SAPLING.get(),
                BlockRegistry.VEXING_SAPLING.get()
        );
        this.tag(BlockTags.SAPLINGS).add(
                BlockRegistry.BLAZING_SAPLING.get(),
                BlockRegistry.CASCADING_SAPLING.get(),
                BlockRegistry.FLOURISHING_SAPLING.get(),
                BlockRegistry.VEXING_SAPLING.get()
        );

        this.tag(MAGIC_PLANTS).addTag(MAGIC_SAPLINGS).add(
                BlockRegistry.SOURCEBERRY_BUSH.get(),
                BlockRegistry.MAGE_BLOOM_CROP.get(),
                BlockRegistry.FROSTAYA_POD.get(),
                BlockRegistry.MENDOSTEEN_POD.get(),
                BlockRegistry.BASTION_POD.get(),
                BlockRegistry.BOMBEGRANTE_POD.get()
        );

        this.tag(BUSHES).add(BlockRegistry.SOURCEBERRY_BUSH.get());

        this.tag(Tags.Blocks.FENCES).add(BlockRegistry.ARCHWOOD_FENCE.get());
        this.tag(Tags.Blocks.FENCES_WOODEN).add(BlockRegistry.ARCHWOOD_FENCE.get());

        this.tag(Tags.Blocks.FENCE_GATES).add(BlockRegistry.ARCHWOOD_FENCE_GATE.get());
        this.tag(Tags.Blocks.FENCE_GATES_WOODEN).add(BlockRegistry.ARCHWOOD_FENCE_GATE.get());

        this.tag(BlockTags.LOGS).add(
                BlockRegistry.VEXING_LOG.get(),
                BlockRegistry.CASCADING_LOG.get(),
                BlockRegistry.FLOURISHING_LOG.get(),
                BlockRegistry.BLAZING_LOG.get(),
                BlockRegistry.STRIPPED_AWLOG_BLUE.get(),
                BlockRegistry.STRIPPED_AWWOOD_BLUE.get(),
                BlockRegistry.STRIPPED_AWLOG_GREEN.get(),
                BlockRegistry.STRIPPED_AWWOOD_GREEN.get(),
                BlockRegistry.STRIPPED_AWLOG_RED.get(),
                BlockRegistry.STRIPPED_AWWOOD_RED.get(),
                BlockRegistry.STRIPPED_AWLOG_PURPLE.get(),
                BlockRegistry.STRIPPED_AWWOOD_PURPLE.get());
        this.tag(BlockTags.LOGS_THAT_BURN).add(BlockRegistry.VEXING_LOG.get(),
                BlockRegistry.CASCADING_LOG.get(),
                BlockRegistry.FLOURISHING_LOG.get(),
                BlockRegistry.BLAZING_LOG.get(),
                BlockRegistry.STRIPPED_AWLOG_BLUE.get(),
                BlockRegistry.STRIPPED_AWWOOD_BLUE.get(),
                BlockRegistry.STRIPPED_AWLOG_GREEN.get(),
                BlockRegistry.STRIPPED_AWWOOD_GREEN.get(),
                BlockRegistry.STRIPPED_AWLOG_RED.get(),
                BlockRegistry.STRIPPED_AWWOOD_RED.get(),
                BlockRegistry.STRIPPED_AWLOG_PURPLE.get(),
                BlockRegistry.STRIPPED_AWWOOD_PURPLE.get());
        this.tag(BlockTags.PLANKS).add(BlockRegistry.ARCHWOOD_PLANK.get());
        this.tag(BlockTags.FENCE_GATES).add(BlockRegistry.ARCHWOOD_FENCE_GATE.get());
        this.tag(BlockTags.FENCES).add(BlockRegistry.ARCHWOOD_FENCE.get());
        this.tag(BlockTags.WOODEN_FENCES).add(BlockRegistry.ARCHWOOD_FENCE.get());
        TagKey<Block> ARCHWOOD_LEAVES = BlockTags.create(new ResourceLocation("minecraft", "leaves/archwood_leaves"));
        this.tag(ARCHWOOD_LEAVES)
                .add(BlockRegistry.VEXING_LEAVES.get(),
                        BlockRegistry.CASCADING_LEAVE.get(),
                        BlockRegistry.BLAZING_LEAVES.get(),
                        BlockRegistry.FLOURISHING_LEAVES.get());
        this.tag(BlockTags.LEAVES).add(BlockRegistry.VEXING_LEAVES.get(),
                BlockRegistry.CASCADING_LEAVE.get(),
                BlockRegistry.BLAZING_LEAVES.get(),
                BlockRegistry.FLOURISHING_LEAVES.get());

        this.tag(BlockTags.BEE_GROWABLES).add(BlockRegistry.MAGE_BLOOM_CROP.get());
        this.tag(BlockTags.BUTTONS).add(BlockRegistry.ARCHWOOD_BUTTON.get());
        this.tag(BlockTags.CROPS).add(BlockRegistry.MAGE_BLOOM_CROP.get());
        this.tag(BlockTags.SLABS).add(BlockRegistry.ARCHWOOD_SLABS.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.ARCHWOOD_STAIRS.get());
        this.tag(BlockTags.TRAPDOORS).add(BlockRegistry.ARCHWOOD_TRAPDOOR.get());
        this.tag(BlockTags.WOODEN_BUTTONS).add(BlockRegistry.ARCHWOOD_BUTTON.get());
        this.tag(BlockTags.WOODEN_DOORS).add(BlockRegistry.ARCHWOOD_DOOR.get());
        this.tag(BlockTags.DOORS).add(BlockRegistry.ARCHWOOD_DOOR.get());
        this.tag(BlockTags.WOODEN_SLABS).add(BlockRegistry.ARCHWOOD_SLABS.get());
        this.tag(BlockTags.WOODEN_STAIRS).add(BlockRegistry.ARCHWOOD_STAIRS.get());
        this.tag(BlockTags.WOODEN_TRAPDOORS).add(BlockRegistry.ARCHWOOD_TRAPDOOR.get());

        this.tag(IGNORE_TILE).add(
                BlockRegistry.INTANGIBLE_AIR.get(),
                BlockRegistry.MAGE_BLOCK.get(),
                BlockRegistry.GOLD_SCONCE_BLOCK.get(),
                BlockRegistry.LIGHT_BLOCK.get(),
                BlockRegistry.T_LIGHT_BLOCK.get(),
                BlockRegistry.GHOST_WEAVE.get(),
                BlockRegistry.SKY_WEAVE.get()
        );

        this.tag(SUMMON_BED).add(
                BlockRegistry.RED_SBED.get(),
                BlockRegistry.GREEN_SBED.get(),
                BlockRegistry.YELLOW_SBED.get(),
                BlockRegistry.BLUE_SBED.get(),
                BlockRegistry.ORANGE_SBED.get(),
                BlockRegistry.PURPLE_SBED.get()
        );
        this.tag(SUMMON_SLEEPABLE).addTag(SUMMON_BED).addTag(BlockTags.BEDS);
        this.tag(BREAK_BLACKLIST);
        this.tag(NO_BREAK_DROP).add(Blocks.TURTLE_EGG);
        this.tag(GRAVITY_BLACKLIST).add(Blocks.BEDROCK, BlockRegistry.MAGE_BLOCK.get()).addTag(RELOCATION_NOT_SUPPORTED);
        this.tag(BREAK_WITH_PICKAXE).add(Blocks.AMETHYST_CLUSTER);
        this.tag(BlockTags.PORTALS).add(BlockRegistry.PORTAL_BLOCK.get());
        this.tag(AUTOPULL_DISABLED).add(BlockRegistry.SCRIBES_BLOCK.get(), BlockRegistry.ALTERATION_TABLE.get());

        this.tag(BLAZING_LOGS).add(BlockRegistry.BLAZING_LOG.get(), BlockRegistry.BLAZING_WOOD.get(), BlockRegistry.STRIPPED_AWLOG_RED.get(), BlockRegistry.STRIPPED_AWWOOD_RED.get());
        this.tag(CASCADING_LOGS).add(BlockRegistry.CASCADING_LOG.get(), BlockRegistry.CASCADING_WOOD.get(), BlockRegistry.STRIPPED_AWLOG_BLUE.get(), BlockRegistry.STRIPPED_AWWOOD_BLUE.get());
        this.tag(FLOURISHING_LOGS).add(BlockRegistry.FLOURISHING_LOG.get(), BlockRegistry.FLOURISHING_WOOD.get(), BlockRegistry.STRIPPED_AWLOG_GREEN.get(), BlockRegistry.STRIPPED_AWWOOD_GREEN.get());
        this.tag(VEXING_LOGS).add(BlockRegistry.VEXING_LOG.get(), BlockRegistry.VEXING_WOOD.get(), BlockRegistry.STRIPPED_AWLOG_PURPLE.get(), BlockRegistry.STRIPPED_AWWOOD_PURPLE.get());

        this.tag(DOWSING_ROD).addTag(BUDDING_BLOCKS);
    }

    public String getName() {
        return "AN tags";
    }
}
