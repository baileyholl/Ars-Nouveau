package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.ReactiveEnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.SpellWriteRecipe;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.Tags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApparatusRecipeProvider implements DataProvider {
    private final DataGenerator generator;
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger();
    public ApparatusRecipeProvider(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    public List<EnchantingApparatusRecipe> recipes = new ArrayList<>();
    @Override
    public void run(HashCache cache) throws IOException {
        addEntries();
        Path output = this.generator.getOutputFolder();
        for(IEnchantingRecipe g : recipes){
            if(g instanceof EnchantingApparatusRecipe){
                System.out.println(g);
                Path path = getRecipePath(output, ((EnchantingApparatusRecipe) g).getId().getPath());
                DataProvider.save(GSON, cache, ((EnchantingApparatusRecipe) g).asRecipe(), path);

            }
        }

    }

    public ApparatusRecipeBuilder builder(){
        return ApparatusRecipeBuilder.builder();
    }

    public void addEntries(){
        addRecipe(builder().withResult(ItemsRegistry.BELT_OF_LEVITATION)
                .withReagent(ItemsRegistry.MUNDANE_BELT)
                .withPedestalItem(4, Ingredient.of(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(3, Items.FEATHER)
                .withPedestalItem(ItemsRegistry.AIR_ESSENCE)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.JAR_OF_LIGHT)
                .withReagent(Items.GLASS_BOTTLE)
                .withPedestalItem(4, Items.GLOWSTONE)
                .withPedestalItem(2, Items.REDSTONE_LAMP)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.GLASS))
                .build());

        addRecipe(builder()
                .withResult(BlockRegistry.MAGE_BLOOM_CROP)
                .withReagent(Ingredient.of(Tags.Items.SEEDS))
                .withPedestalItem(4, Recipes.SOURCE_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.RING_OF_LESSER_DISCOUNT)
                .withReagent(ItemsRegistry.RING_OF_POTENTIAL)
                .withPedestalItem(4,  Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2,Ingredient.of(Tags.Items.ENDER_PEARLS))
                .withPedestalItem(2,   Recipes.SOURCE_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.RING_OF_GREATER_DISCOUNT)
                .withReagent(ItemsRegistry.RING_OF_LESSER_DISCOUNT)
                .withPedestalItem(4,  Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2,Ingredient.of(Tags.Items.RODS_BLAZE))
                .withPedestalItem(2, Recipes.SOURCE_GEM)
                .build());


        addRecipe(new EnchantingApparatusRecipe(new ItemStack(ItemsRegistry.BELT_OF_UNSTABLE_GIFTS), Ingredient.of(ItemsRegistry.MUNDANE_BELT), Arrays.asList(
                Ingredient.of(Items.SUGAR),
                Ingredient.of(Tags.Items.CROPS_NETHER_WART),
                Ingredient.of(Tags.Items.RODS_BLAZE),
                Ingredient.of(Tags.Items.DUSTS_GLOWSTONE),
                Ingredient.of(Items.FERMENTED_SPIDER_EYE),
                Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                Ingredient.of(Items.BREWING_STAND),
                Ingredient.of(Tags.Items.FEATHERS)
        )));

        addRecipe(builder()
                .withResult(ItemsRegistry.BOOKWYRM_CHARM)
                .withReagent(Items.BOOK)
                .withPedestalItem(4, Ingredient.of(Tags.Items.INGOTS_IRON))
                .withPedestalItem(4, Recipes.SOURCE_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.STARBUNCLE_CHARM)
                .withReagent(ItemsRegistry.STARBUNCLE_SHARD)
                .withPedestalItem(5,Ingredient.of(Tags.Items.NUGGETS_GOLD))
                .withPedestalItem(3, Recipes.SOURCE_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.AMULET_OF_MANA_BOOST)
                .withReagent(ItemsRegistry.DULL_TRINKET)
                .withPedestalItem(3,Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(5, Recipes.SOURCE_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.AMULET_OF_MANA_REGEN)
                .withReagent(ItemsRegistry.DULL_TRINKET)
                .withPedestalItem(2,Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2,Ingredient.of(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(4, Recipes.SOURCE_GEM)
                .build());


        addRecipe(builder()
                .withResult(ItemsRegistry.WHIRLISPRIG_CHARM)
                .withReagent(ItemsRegistry.WHIRLISPRIG_SHARDS)
                .withPedestalItem(Recipes.SOURCE_GEM)
                .withPedestalItem(BlockRegistry.MAGE_BLOOM_CROP)
                .withPedestalItem(ItemsRegistry.MAGE_BLOOM)
                .withPedestalItem(Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem( Items.OAK_SAPLING)
                .withPedestalItem( Items.SPRUCE_SAPLING)
                .withPedestalItem( Items.BIRCH_SAPLING)
                .withPedestalItem(Ingredient.of(Tags.Items.SEEDS_WHEAT))
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.VOID_JAR)
                .withReagent(Items.GLASS_BOTTLE)
                .withPedestalItem(Items.LAVA_BUCKET)
                .withPedestalItem(Items.BUCKET)
                .withPedestalItem(ItemsRegistry.ALLOW_ITEM_SCROLL)
                .withPedestalItem(Ingredient.of(Tags.Items.ENDER_PEARLS))
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.DOMINION_ROD)
                .withReagent(Items.STICK)
                .withPedestalItem(2, Recipes.SOURCE_GEM)
                .withPedestalItem(Ingredient.of(Tags.Items.INGOTS_GOLD))
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.WIXIE_CHARM)
                .withReagent( ItemsRegistry.WIXIE_SHARD)
                .withPedestalItem(Ingredient.of(ItemTags.SAPLINGS))
                .withPedestalItem(Ingredient.of(Tags.Items.GEMS_EMERALD))
                .withPedestalItem(Items.CRAFTING_TABLE)
                .withPedestalItem(Items.BREWING_STAND)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.WAND)
                .withReagent(Recipes.ARCHWOOD_LOG)
                .withPedestalItem(4, Recipes.SOURCE_GEM)
                .withPedestalItem(2, Ingredient.of(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(ItemsRegistry.MANIPULATION_ESSENCE)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.SPELL_BOW)
                .withReagent(Items.BOW)
                .withPedestalItem(Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .withPedestalItem(ItemsRegistry.MANIPULATION_ESSENCE)
                .build());

        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.AMPLIFY_ARROW, 32))
                .withReagent( Ingredient.of(ItemTags.ARROWS))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(ItemsRegistry.AIR_ESSENCE)
                .build());


        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.SPLIT_ARROW, 32))
                .withReagent( Ingredient.of(ItemTags.ARROWS))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(ItemsRegistry.AIR_ESSENCE)
                .build());
        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.PIERCE_ARROW, 32))
                .withReagent( Ingredient.of(ItemTags.ARROWS))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(ItemsRegistry.AIR_ESSENCE)
                .build());

        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.POTION_FLASK))
                .withReagent(Ingredient.of(Items.GLASS_BOTTLE))
                .withPedestalItem(2, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .build());

        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.POTION_FLASK_EXTEND_TIME))
                .withReagent(Ingredient.of(ItemsRegistry.POTION_FLASK))
                .withPedestalItem(8,  ItemsRegistry.MANIPULATION_ESSENCE)
                .build());


        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.POTION_FLASK_AMPLIFY))
                .withReagent(Ingredient.of(ItemsRegistry.POTION_FLASK))
                .withPedestalItem(8,  ItemsRegistry.ABJURATION_ESSENCE)
                .build());

        addRecipe(builder()
                .withResult(new ItemStack(BlockRegistry.POTION_MELDER))
                .withReagent(Ingredient.of(BlockRegistry.POTION_JAR))
                .withPedestalItem(2,  ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2,Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .withPedestalItem(4,Ingredient.of(Tags.Items.RODS_BLAZE))
                .build());


        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(ItemTags.FISHES))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.AQUA_AFFINITY, 1, 5000));

        addRecipe(builder()
                 .withPedestalItem(4, Ingredient.of(Items.BLAZE_POWDER))
                .withPedestalItem(ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(ItemsRegistry.EARTH_ESSENCE)
                .withPedestalItem(ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(ItemsRegistry.WATER_ESSENCE)
                .buildEnchantmentRecipe(EnchantmentRegistry.REACTIVE_ENCHANTMENT, 2, 6000));

        addRecipe(builder()
                .withPedestalItem(4,Ingredient.of(Tags.Items.GEMS_EMERALD))
                .withPedestalItem(1,Ingredient.of(Tags.Items.ENDER_PEARLS))
                .withPedestalItem(ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(ItemsRegistry.CONJURATION_ESSENCE)
                .withPedestalItem(ItemsRegistry.MANIPULATION_ESSENCE)
                .buildEnchantmentRecipe(EnchantmentRegistry.REACTIVE_ENCHANTMENT, 3, 9000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Items.SPIDER_EYE))
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BANE_OF_ARTHROPODS, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(3, Ingredient.of(Items.SPIDER_EYE))
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BANE_OF_ARTHROPODS, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.FERMENTED_SPIDER_EYE))
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BANE_OF_ARTHROPODS, 3, 500));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.FERMENTED_SPIDER_EYE))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BANE_OF_ARTHROPODS, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.FERMENTED_SPIDER_EYE))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BANE_OF_ARTHROPODS, 5, 8000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON))
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(0, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BLAST_PROTECTION, 1, 2000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON))
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BLAST_PROTECTION, 2, 4000));
        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.OBSIDIAN))
                .buildEnchantmentRecipe(Enchantments.BLAST_PROTECTION, 3, 6000));
        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .buildEnchantmentRecipe(Enchantments.BLAST_PROTECTION, 4, 8000));

        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.WATER_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.DEPTH_STRIDER, 1, 3000));
        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.WATER_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Items.PRISMARINE_SHARD))
                .buildEnchantmentRecipe(Enchantments.DEPTH_STRIDER, 2, 6000));
        addRecipe(builder()
                .withPedestalItem(4, ItemsRegistry.WATER_ESSENCE)
                .withPedestalItem(3, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Items.HEART_OF_THE_SEA))
                .buildEnchantmentRecipe(Enchantments.DEPTH_STRIDER, 3, 9000));

        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(Items.SUGAR))
                .withPedestalItem(1,  Ingredient.of(Items.IRON_PICKAXE))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.BLOCK_EFFICIENCY, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .withPedestalItem(1,  Ingredient.of(Items.GOLDEN_PICKAXE))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.BLOCK_EFFICIENCY, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .withPedestalItem(1,  Ingredient.of(Items.GOLDEN_PICKAXE))
                .withPedestalItem(3, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.OBSIDIAN))
                .buildEnchantmentRecipe(Enchantments.BLOCK_EFFICIENCY, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .withPedestalItem(1,  Ingredient.of(Items.DIAMOND_PICKAXE))
                .withPedestalItem(1,  Ingredient.of(Items.IRON_SHOVEL))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.OBSIDIAN))
                .buildEnchantmentRecipe(Enchantments.BLOCK_EFFICIENCY, 4, 6500));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .withPedestalItem(1,  Ingredient.of(Items.DIAMOND_PICKAXE))
                .withPedestalItem(1,  Ingredient.of(Items.DIAMOND_SHOVEL))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BLOCK_EFFICIENCY, 5, 8000));


        addRecipe(builder()
                .withPedestalItem(1,  ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1,  Ingredient.of(Tags.Items.SLIMEBALLS))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.FALL_PROTECTION, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2,   ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1,  Ingredient.of(Tags.Items.SLIMEBALLS))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.FALL_PROTECTION, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(3,   ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1,  Ingredient.of(Tags.Items.SLIMEBALLS))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.FALL_PROTECTION, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(4,    ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.FALL_PROTECTION, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(1,   ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.FIRE_ASPECT, 1, 2000));

        addRecipe(builder()
                .withPedestalItem(2,   ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.FIRE_ASPECT, 2, 4000));

        addRecipe(builder()
                .withPedestalItem(1,  ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(1,  ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.FIRE_PROTECTION, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2,  ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(1,  ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.FIRE_PROTECTION, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(1,  ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(3, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1,  Ingredient.of(Tags.Items.RODS_BLAZE))
                .buildEnchantmentRecipe(Enchantments.FIRE_PROTECTION, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(2,   ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(2,   ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.RODS_BLAZE))
                .buildEnchantmentRecipe(Enchantments.FIRE_PROTECTION, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(3,  ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(3,  ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.RODS_BLAZE))
                .buildEnchantmentRecipe(Enchantments.FIRE_PROTECTION, 5, 8000));

        addRecipe(builder()
                .withPedestalItem(1,  ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(1,  ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, Ingredient.of(Tags.Items.RODS_BLAZE))
                .buildEnchantmentRecipe(Enchantments.FLAMING_ARROWS, 1, 5000));

        addRecipe(builder()
                .withPedestalItem(1,  ItemsRegistry.EARTH_ESSENCE)
                .withPedestalItem(1,  Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(6, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.BLOCK_FORTUNE, 1, 6000));
        addRecipe(builder()
                .withPedestalItem(2,  ItemsRegistry.EARTH_ESSENCE)
                .withPedestalItem(4,  Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.BLOCK_FORTUNE, 2, 8000));
        addRecipe(builder()
                .withPedestalItem(3, ItemsRegistry.EARTH_ESSENCE)
                .withPedestalItem(1,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_DIAMOND))
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.BLOCK_FORTUNE, 3, 9000));

        addRecipe(builder()
                .withPedestalItem(7, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_DIAMOND))
                .buildEnchantmentRecipe(Enchantments.INFINITY_ARROWS, 1, 9000));

        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.KNOCKBACK, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.KNOCKBACK, 2, 4000));

        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.EARTH_ESSENCE)
                .withPedestalItem(1,  Ingredient.of(Tags.Items.GEMS_EMERALD))
                .withPedestalItem(6, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.MOB_LOOTING, 1, 6000));
        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.EARTH_ESSENCE)
                .withPedestalItem(4,  Ingredient.of(Tags.Items.GEMS_EMERALD))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.MOB_LOOTING, 2, 8000));
        addRecipe(builder()
                .withPedestalItem(3, ItemsRegistry.EARTH_ESSENCE)
                .withPedestalItem(1,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_EMERALD))
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.MOB_LOOTING, 3, 9000));

        addRecipe(builder()
                .withPedestalItem(3,ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(5, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.MULTISHOT, 1, 9000));

        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.PIERCING, 1, 2500));
        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.PIERCING, 2, 5000));
        addRecipe(builder()
                .withPedestalItem(3, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.PIERCING, 3, 7500));
        addRecipe(builder()
                .withPedestalItem(4,  ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.PIERCING, 4, 9000));

        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1,  ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.POWER_ARROWS, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1,  ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.POWER_ARROWS, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(3,  Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1,  ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.POWER_ARROWS, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(4,  Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1,  ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.POWER_ARROWS, 4, 6500));
        addRecipe(builder()
                .withPedestalItem(5,  Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1,  ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.POWER_ARROWS, 5, 9000));


        addRecipe(builder()
                .withPedestalItem(1,  ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PROJECTILE_PROTECTION, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2,  ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PROJECTILE_PROTECTION, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(2,  ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(3, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PROJECTILE_PROTECTION, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(2,  ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PROJECTILE_PROTECTION, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(3,  ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(3, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PROJECTILE_PROTECTION, 5, 8000));


        addRecipe(builder()
                .withPedestalItem(1,  ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(1,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.ALL_DAMAGE_PROTECTION, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2,  ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.ALL_DAMAGE_PROTECTION, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(2,  ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(3, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.ALL_DAMAGE_PROTECTION, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(2,  ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.ALL_DAMAGE_PROTECTION, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(3,  ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(3, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_DIAMOND))
                .buildEnchantmentRecipe(Enchantments.ALL_DAMAGE_PROTECTION, 5, 8000));


        addRecipe(builder()
                .withPedestalItem(2,  ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PUNCH_ARROWS, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(3,  ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PUNCH_ARROWS, 2, 4000));

        addRecipe(builder()
                .withPedestalItem(1,  ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1,  ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.QUICK_CHARGE, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(1,  ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2,  ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.QUICK_CHARGE, 2, 4000));
        addRecipe(builder()
                .withPedestalItem(1,  ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2,  ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.QUICK_CHARGE, 3, 6000));


        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.WATER_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.RESPIRATION, 1, 3000));
        addRecipe(builder()
                .withPedestalItem(4, ItemsRegistry.WATER_ESSENCE)
                .withPedestalItem(4, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.RESPIRATION, 2, 6000));
        addRecipe(builder()
                .withPedestalItem(6, ItemsRegistry.WATER_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.RESPIRATION, 3, 9000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_QUARTZ))
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SHARPNESS, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_QUARTZ))
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SHARPNESS, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_QUARTZ))
                .withPedestalItem(1,  Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SHARPNESS, 3, 500));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_QUARTZ))
                .withPedestalItem(2, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SHARPNESS, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_QUARTZ))
                .withPedestalItem(2, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SHARPNESS, 5, 8000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.GEMS_EMERALD))
                .withPedestalItem(2, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SILK_TOUCH, 1, 9000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Items.BONE_BLOCK))
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SMITE, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(3, Ingredient.of(Items.BONE_BLOCK))
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SMITE, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.BONE_BLOCK))
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SMITE, 3, 500));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.BONE_BLOCK))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SMITE, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.BONE_BLOCK))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SMITE, 5, 8000));


        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SWEEPING_EDGE, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SWEEPING_EDGE, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(3, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(3, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SWEEPING_EDGE, 3, 5000));

        addRecipe(builder()
                .withPedestalItem(1, Ingredient.of(ItemsRegistry.WILDEN_SPIKE))
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.THORNS, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(ItemsRegistry.WILDEN_SPIKE))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.THORNS, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(3, Ingredient.of(ItemsRegistry.WILDEN_SPIKE))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(3, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.THORNS, 3, 5000));

        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(1, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.UNBREAKING, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.UNBREAKING, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(3, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.UNBREAKING, 3, 5000));

        addRecipe(builder()
                .withPedestalItem(1, BlockRegistry.SOURCEBERRY_BUSH)
                .withPedestalItem(4, Recipes.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(1, BlockRegistry.SOURCEBERRY_BUSH)
                .withPedestalItem(1, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(4, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(1, BlockRegistry.SOURCEBERRY_BUSH)
                .withPedestalItem(2, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(4, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT, 3, 5000));


        addRecipe(builder()
                .withPedestalItem(2, BlockRegistry.SOURCEBERRY_BUSH)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2, BlockRegistry.SOURCEBERRY_BUSH)
                .withPedestalItem(2,  ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(2, BlockRegistry.SOURCEBERRY_BUSH)
                .withPedestalItem(3,  ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT, 3, 5000));

        addRecipe(builder()
                .withResult(ItemsRegistry.ENCHANTERS_SWORD)
                .withReagent(Items.DIAMOND_SWORD)
                .withPedestalItem(1, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2,Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.ENCHANTERS_SHIELD)
                .withReagent(Items.SHIELD)
                .withPedestalItem(2,Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .withPedestalItem(2, Recipes.SOURCE_GEM_BLOCK)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.DRYGMY_CHARM)
                .withReagent(ItemsRegistry.DRYGMY_SHARD)
                .withPedestalItem(Ingredient.of(ItemTags.FISHES))
                .withPedestalItem(Items.WHEAT)
                .withPedestalItem(Items.APPLE)
                .withPedestalItem(Items.CARROT)
                .withPedestalItem(Ingredient.of(Tags.Items.SEEDS))
                .withPedestalItem(3, Recipes.SOURCE_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.SUMMONING_FOCUS)
                .withReagent(Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(ItemsRegistry.WILDEN_HORN)
                .withPedestalItem(ItemsRegistry.WILDEN_SPIKE)
                .withPedestalItem(ItemsRegistry.WILDEN_WING)
                .withPedestalItem(ItemsRegistry.WILDEN_TRIBUTE)
                .withPedestalItem(Ingredient.of(Tags.Items.INGOTS_GOLD))
                .build());

        addRecipe(builder()
                .withResult(BlockRegistry.RELAY_SPLITTER)
                .withReagent(BlockRegistry.RELAY)
                .withPedestalItem(4,Ingredient.of(Tags.Items.GEMS_QUARTZ))
                .withPedestalItem(4,Ingredient.of(Tags.Items.GEMS_LAPIS))
                .build());

        addRecipe(builder()
                .withResult(BlockRegistry.RELAY_WARP)
                .withReagent(BlockRegistry.RELAY)
                .withPedestalItem(4, Ingredient.of(Tags.Items.ENDER_PEARLS))
                .withPedestalItem(4, Ingredient.of(Items.POPPED_CHORUS_FRUIT))
                .build());

        addRecipe(builder()
                .withResult(BlockRegistry.RELAY_DEPOSIT)
                .withReagent(BlockRegistry.RELAY)
                .withPedestalItem(4,Ingredient.of(Items.HOPPER))
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.ENCHANTERS_MIRROR)
                .withReagent(Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.GLASS))
                .withPedestalItem(2, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, Recipes.ARCHWOOD_LOG)
                .withPedestalItem(2, Ingredient.of(Tags.Items.INGOTS_GOLD))
                .build());

        addRecipe(builder()
                .withResult(BlockRegistry.TIMER_SPELL_TURRET)
                .withReagent(BlockRegistry.BASIC_SPELL_TURRET)
                .withPedestalItem(Items.CLOCK)
                .build());

        addRecipe(builder()
                .withResult(BlockRegistry.ENCHANTED_SPELL_TURRET)
                .withReagent(BlockRegistry.BASIC_SPELL_TURRET)
                .withPedestalItem(Recipes.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.RODS_BLAZE))
                .build());
        List<Ingredient> reactiveIngredients = new ArrayList<>();
        reactiveIngredients.add(Ingredient.of(ItemsRegistry.SPELL_PARCHMENT));
        reactiveIngredients.add(Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS));
        reactiveIngredients.add(Ingredient.of(Recipes.SOURCE_GEM_BLOCK_TAG));
        addRecipe(new ReactiveEnchantmentRecipe(reactiveIngredients, 3000));

        List<Ingredient> spellWriteList = new ArrayList<>();
        spellWriteList.add(Ingredient.of(ItemsRegistry.SPELL_PARCHMENT));
        addRecipe(new SpellWriteRecipe(spellWriteList));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1,Ingredient.of(Tags.Items.ENDER_PEARLS))
                .withPedestalItem(1, Ingredient.of(ItemsRegistry.WILDEN_TRIBUTE))
                .buildEnchantmentRecipe(EnchantmentRegistry.REACTIVE_ENCHANTMENT, 4, 9000));
    }

    public void addRecipe(EnchantingApparatusRecipe recipe){
        recipes.add(recipe);
    }

    private static Path getRecipePath(Path pathIn, Item item) {
        return getRecipePath(pathIn, item.getRegistryName().getPath());
    }

    private static Path getRecipePath(Path pathIn, String str){
        return pathIn.resolve("data/ars_nouveau/recipes/" + str + ".json");
    }
    @Override
    public String getName() {
        return "Apparatus";
    }
}
