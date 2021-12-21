package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.nio.file.Path;

public class BlockTagProvider extends BlockTagsProvider {
    public BlockTagProvider(DataGenerator generatorIn, ExistingFileHelper helper) {
        super(generatorIn, ArsNouveau.MODID, helper);
    }

    @Override
    protected void addTags() {
       // super.addTags();
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                BlockRegistry.ARCANE_STONE,
                BlockRegistry.ARCANE_RELAY,
                BlockRegistry.ARCANE_CORE_BLOCK,
                BlockRegistry.ENCHANTING_APP_BLOCK,
                BlockRegistry.GLYPH_PRESS_BLOCK,
                BlockRegistry.ARCANE_PEDESTAL,
                BlockRegistry.CREATIVE_SOURCE_JAR,
                BlockRegistry.RUNE_BLOCK,
                BlockRegistry.IMBUEMENT_BLOCK,
                BlockRegistry.SOURCE_JAR,
                BlockRegistry.ARCANE_RELAY_SPLITTER,
                BlockRegistry.SPELL_TURRET,
                BlockRegistry.VOLCANIC_BLOCK,
                BlockRegistry.LAVA_LILY,
                BlockRegistry.WIXIE_CAULDRON,
                BlockRegistry.SOURCE_GEM_BLOCK,
                BlockRegistry.RITUAL_BLOCK,
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
                BlockRegistry.AB_SMOOTH_CLOVER,
                BlockRegistry.AB_SMOOTH_HERRING,
                BlockRegistry.AB_SMOOTH_MOSAIC,
                BlockRegistry.AB_SMOOTH_ALTERNATING,
                BlockRegistry.AB_SMOOTH_ASHLAR,
                BlockRegistry.AS_GOLD_ALT,
                BlockRegistry.AS_GOLD_ASHLAR,
                BlockRegistry.AS_GOLD_BASKET,
                BlockRegistry.AS_GOLD_CLOVER,
                BlockRegistry.AS_GOLD_HERRING,
                BlockRegistry.AS_GOLD_MOSAIC,
                BlockRegistry.AS_GOLD_STONE,
                BlockRegistry.AS_GOLD_SLAB,
                BlockRegistry.ARCANE_STONE,
                BlockRegistry.AB_BASKET,
                BlockRegistry.AB_HERRING,
                BlockRegistry.AB_MOSAIC,
                BlockRegistry.AB_CLOVER,
                BlockRegistry.AB_SMOOTH_SLAB,
                BlockRegistry.AB_SMOOTH,
                BlockRegistry.AB_ALTERNATE
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
                BlockRegistry.BOOKWYRM_LECTERN,
                BlockRegistry.ARCHWOOD_CHEST

        );
        this.tag(BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "an_decorative")))
                .add(BlockRegistry.AB_SMOOTH_BASKET,
                BlockRegistry.AB_SMOOTH_CLOVER,
                BlockRegistry.AB_SMOOTH_HERRING,
                BlockRegistry.AB_SMOOTH_MOSAIC,
                BlockRegistry.AB_SMOOTH_ALTERNATING,
                BlockRegistry.AB_SMOOTH_ASHLAR,
                BlockRegistry.AS_GOLD_ALT,
                BlockRegistry.AS_GOLD_ASHLAR,
                BlockRegistry.AS_GOLD_BASKET,
                BlockRegistry.AS_GOLD_CLOVER,
                BlockRegistry.AS_GOLD_HERRING,
                BlockRegistry.AS_GOLD_MOSAIC,
                BlockRegistry.AS_GOLD_STONE,
                BlockRegistry.AS_GOLD_SLAB,
                BlockRegistry.ARCANE_STONE,
                BlockRegistry.AB_BASKET,
                BlockRegistry.AB_HERRING,
                BlockRegistry.AB_MOSAIC,
                BlockRegistry.AB_CLOVER,
                BlockRegistry.AB_SMOOTH_SLAB,
                BlockRegistry.AB_SMOOTH,
                BlockRegistry.AB_ALTERNATE,
                BlockRegistry.ARCANE_BRICKS);
        Tag.Named<Block> HARVEST_FOLIAGE = BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "harvest/foliage"));
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
                Blocks.MELON);


        Tag.Named<Block> HARVEST_STEMS = BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "harvest/stems"));
        this.tag(HARVEST_STEMS).addTag(BlockTags.LOGS).add(
                Blocks.MUSHROOM_STEM,
                Blocks.BAMBOO,
                Blocks.SUGAR_CANE,
                Blocks.CACTUS);


        this.tag(BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "harvest/fellable")))
                .addTags(HARVEST_FOLIAGE, HARVEST_STEMS);

        Tag.Named<Block> WHIRLISPRIG_KINDA_LIKES = BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "whirlisprig/kinda_likes"));
        Tag.Named<Block> WHIRLISPRIG_GREATLY_LIKES = BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "whirlisprig/greatly_likes"));
        this.tag(WHIRLISPRIG_GREATLY_LIKES);
        this.tag(WHIRLISPRIG_KINDA_LIKES);

        Tag.Named<Block> MAGIC_PLANTS = BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "magic_plants"));
        Tag.Named<Block> MAGIC_SAPLINGS = BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "magic_saplings"));
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

        this.tag(MAGIC_PLANTS)
                .addTags(MAGIC_SAPLINGS).add(
                        BlockRegistry.SOURCEBERRY_BUSH,
                BlockRegistry.MAGE_BLOOM_CROP
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
        this.tag(BlockTags.LOGS_THAT_BURN).add( BlockRegistry.VEXING_LOG,
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
        this.tag(BlockTags.createOptional(new ResourceLocation("minecraft", "leaves/archwood_leaves")))
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
    }
    protected Path getPath(ResourceLocation p_126514_) {
        return this.generator.getOutputFolder().resolve("data/" + p_126514_.getNamespace() + "/tags/blocks/" + p_126514_.getPath() + ".json");
    }

    public String getName() {
        return "AN tags";
    }
}
