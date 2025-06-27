package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ArmorUpgradeRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ReactiveEnchantmentRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.SpellWriteRecipe;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.perk.*;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class ApparatusRecipeProvider extends SimpleDataProvider {

    public ApparatusRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();
        for (ApparatusRecipeBuilder.RecipeWrapper<? extends EnchantingApparatusRecipe> wrapper : recipes) {
            Path path = getRecipePath(output, wrapper.id().getPath());
            saveStable(pOutput, wrapper.serialize(), path);
        }
    }

    public List<ApparatusRecipeBuilder.RecipeWrapper<? extends EnchantingApparatusRecipe>> recipes = new ArrayList<>();

    public ApparatusRecipeBuilder builder() {
        return ApparatusRecipeBuilder.builder();
    }

    public void addEntries() {
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
                .withPedestalItem(2, Ingredient.of(Tags.Items.GLASS_BLOCKS))
                .build());

        addRecipe(builder()
                .withResult(BlockRegistry.MAGE_BLOOM_CROP)
                .withReagent(Ingredient.of(Tags.Items.SEEDS))
                .withPedestalItem(4, RecipeDatagen.SOURCE_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.RING_OF_LESSER_DISCOUNT)
                .withReagent(ItemsRegistry.RING_OF_POTENTIAL)
                .withPedestalItem(4, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, Ingredient.of(Tags.Items.ENDER_PEARLS))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.RING_OF_GREATER_DISCOUNT)
                .withReagent(ItemsRegistry.RING_OF_LESSER_DISCOUNT)
                .withPedestalItem(4, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, Ingredient.of(Tags.Items.RODS_BLAZE))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.BELT_OF_UNSTABLE_GIFTS)
                .withReagent(ItemsRegistry.MUNDANE_BELT)
                .withPedestalItem(Ingredient.of(Items.SUGAR))
                .withPedestalItem(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .withPedestalItem(Ingredient.of(Tags.Items.RODS_BLAZE))
                .withPedestalItem(Ingredient.of(Tags.Items.DUSTS_GLOWSTONE))
                .withPedestalItem(Ingredient.of(Items.FERMENTED_SPIDER_EYE))
                .withPedestalItem(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .withPedestalItem(Ingredient.of(Items.BREWING_STAND))
                .withPedestalItem(Ingredient.of(Tags.Items.FEATHERS))
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.STARBUNCLE_CHARM)
                .withReagent(ItemsRegistry.STARBUNCLE_SHARD)
                .withPedestalItem(4, Ingredient.of(Tags.Items.INGOTS_GOLD))
                .keepNbtOfReagent(true)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.AMULET_OF_MANA_BOOST)
                .withReagent(ItemsRegistry.DULL_TRINKET)
                .withPedestalItem(3, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(5, RecipeDatagen.SOURCE_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.AMULET_OF_MANA_REGEN)
                .withReagent(ItemsRegistry.DULL_TRINKET)
                .withPedestalItem(2, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, Ingredient.of(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(4, RecipeDatagen.SOURCE_GEM)
                .build());


        addRecipe(builder()
                .withResult(ItemsRegistry.WHIRLISPRIG_CHARM)
                .withReagent(ItemsRegistry.WHIRLISPRIG_SHARDS)
                .withPedestalItem(RecipeDatagen.SOURCE_GEM)
                .withPedestalItem(BlockRegistry.MAGE_BLOOM_CROP)
                .withPedestalItem(ItemsRegistry.MAGE_BLOOM)
                .withPedestalItem(Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(Items.OAK_SAPLING)
                .withPedestalItem(Items.SPRUCE_SAPLING)
                .withPedestalItem(Items.BIRCH_SAPLING)
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
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM)
                .withPedestalItem(Ingredient.of(Tags.Items.INGOTS_GOLD))
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.WIXIE_CHARM)
                .withReagent(ItemsRegistry.WIXIE_SHARD)
                .withPedestalItem(Ingredient.of(ItemTags.SAPLINGS))
                .withPedestalItem(Ingredient.of(Tags.Items.GEMS_EMERALD))
                .withPedestalItem(Items.CRAFTING_TABLE)
                .withPedestalItem(Items.BREWING_STAND)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.WAND)
                .withReagent(RecipeDatagen.ARCHWOOD_LOG)
                .withPedestalItem(4, RecipeDatagen.SOURCE_GEM)
                .withPedestalItem(2, Ingredient.of(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(ItemsRegistry.MANIPULATION_ESSENCE)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.SPELL_BOW)
                .withReagent(Items.BOW)
                .withPedestalItem(RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .withPedestalItem(ItemsRegistry.MANIPULATION_ESSENCE)
                .keepNbtOfReagent(true)
                .build());

        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.POTION_FLASK.get()))
                .withReagent(Ingredient.of(Items.GLASS_BOTTLE))
                .withPedestalItem(2, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .build());

        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.POTION_FLASK_EXTEND_TIME.get()))
                .withReagent(Ingredient.of(ItemsRegistry.POTION_FLASK.get()))
                .withPedestalItem(8, ItemsRegistry.MANIPULATION_ESSENCE)
                .build());


        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.POTION_FLASK_AMPLIFY.get()))
                .withReagent(Ingredient.of(ItemsRegistry.POTION_FLASK.get()))
                .withPedestalItem(8, ItemsRegistry.ABJURATION_ESSENCE)
                .build());

        addRecipe(builder()
                .withResult(new ItemStack(BlockRegistry.POTION_MELDER))
                .withReagent(Ingredient.of(BlockRegistry.POTION_JAR))
                .withPedestalItem(2, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .withPedestalItem(4, Ingredient.of(Tags.Items.RODS_BLAZE))
                .build());


        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(ItemTags.FISHES))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
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
                .withPedestalItem(4, Ingredient.of(Tags.Items.GEMS_EMERALD))
                .withPedestalItem(1, Ingredient.of(Tags.Items.ENDER_PEARLS))
                .withPedestalItem(ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(ItemsRegistry.CONJURATION_ESSENCE)
                .withPedestalItem(ItemsRegistry.MANIPULATION_ESSENCE)
                .buildEnchantmentRecipe(EnchantmentRegistry.REACTIVE_ENCHANTMENT, 3, 9000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Items.SPIDER_EYE))
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BANE_OF_ARTHROPODS, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(3, Ingredient.of(Items.SPIDER_EYE))
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BANE_OF_ARTHROPODS, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.FERMENTED_SPIDER_EYE))
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BANE_OF_ARTHROPODS, 3, 5000));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.FERMENTED_SPIDER_EYE))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BANE_OF_ARTHROPODS, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.FERMENTED_SPIDER_EYE))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BANE_OF_ARTHROPODS, 5, 8000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON))
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(0, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BLAST_PROTECTION, 1, 2000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON))
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BLAST_PROTECTION, 2, 4000));
        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.OBSIDIANS))
                .buildEnchantmentRecipe(Enchantments.BLAST_PROTECTION, 3, 6000));
        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .buildEnchantmentRecipe(Enchantments.BLAST_PROTECTION, 4, 8000));

        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.WATER_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.DEPTH_STRIDER, 1, 3000));
        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.WATER_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Items.PRISMARINE_SHARD))
                .buildEnchantmentRecipe(Enchantments.DEPTH_STRIDER, 2, 6000));
        addRecipe(builder()
                .withPedestalItem(4, ItemsRegistry.WATER_ESSENCE)
                .withPedestalItem(3, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Items.NAUTILUS_SHELL))
                .buildEnchantmentRecipe(Enchantments.DEPTH_STRIDER, 3, 9000));

        addRecipe(builder()
                .withPedestalItem(1, Ingredient.of(Items.SUGAR))
                .withPedestalItem(1, Ingredient.of(Items.IRON_PICKAXE))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.EFFICIENCY, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .withPedestalItem(1, Ingredient.of(Items.GOLDEN_PICKAXE))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.EFFICIENCY, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .withPedestalItem(1, Ingredient.of(Items.GOLDEN_PICKAXE))
                .withPedestalItem(3, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.OBSIDIANS))
                .buildEnchantmentRecipe(Enchantments.EFFICIENCY, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .withPedestalItem(1, Ingredient.of(Items.DIAMOND_PICKAXE))
                .withPedestalItem(1, Ingredient.of(Items.IRON_SHOVEL))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.OBSIDIANS))
                .buildEnchantmentRecipe(Enchantments.EFFICIENCY, 4, 6500));
        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .withPedestalItem(1, Ingredient.of(Items.DIAMOND_PICKAXE))
                .withPedestalItem(1, Ingredient.of(Items.DIAMOND_SHOVEL))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.EFFICIENCY, 5, 8000));


        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, Ingredient.of(Tags.Items.SLIME_BALLS))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.FEATHER_FALLING, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, Ingredient.of(Tags.Items.SLIME_BALLS))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.FEATHER_FALLING, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(3, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, Ingredient.of(Tags.Items.SLIME_BALLS))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.FEATHER_FALLING, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(4, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.FEATHER_FALLING, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.FIRE_ASPECT, 1, 2000));

        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.FIRE_ASPECT, 2, 4000));

        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.FIRE_PROTECTION, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.FIRE_PROTECTION, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(3, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.RODS_BLAZE))
                .buildEnchantmentRecipe(Enchantments.FIRE_PROTECTION, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(2, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.RODS_BLAZE))
                .buildEnchantmentRecipe(Enchantments.FIRE_PROTECTION, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(3, ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(3, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.RODS_BLAZE))
                .buildEnchantmentRecipe(Enchantments.FIRE_PROTECTION, 5, 8000));

        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, Ingredient.of(Tags.Items.RODS_BLAZE))
                .buildEnchantmentRecipe(Enchantments.FLAME, 1, 5000));

        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.EARTH_ESSENCE)
                .withPedestalItem(1, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(6, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.FORTUNE, 1, 6000));
        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.EARTH_ESSENCE)
                .withPedestalItem(4, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.FORTUNE, 2, 8000));
        addRecipe(builder()
                .withPedestalItem(3, ItemsRegistry.EARTH_ESSENCE)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_DIAMOND))
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.FORTUNE, 3, 9000));

        addRecipe(builder()
                .withPedestalItem(7, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_DIAMOND))
                .buildEnchantmentRecipe(Enchantments.INFINITY, 1, 9000));

        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.KNOCKBACK, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.KNOCKBACK, 2, 4000));

        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.EARTH_ESSENCE)
                .withPedestalItem(1, Ingredient.of(Tags.Items.GEMS_EMERALD))
                .withPedestalItem(6, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.LOOTING, 1, 6000));
        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.EARTH_ESSENCE)
                .withPedestalItem(4, Ingredient.of(Tags.Items.GEMS_EMERALD))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.LOOTING, 2, 8000));
        addRecipe(builder()
                .withPedestalItem(3, ItemsRegistry.EARTH_ESSENCE)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_EMERALD))
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.LOOTING, 3, 9000));

        addRecipe(builder()
                .withPedestalItem(3, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(5, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.MULTISHOT, 1, 9000));

        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.PIERCING, 1, 2500));
        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.PIERCING, 2, 5000));
        addRecipe(builder()
                .withPedestalItem(3, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.PIERCING, 3, 7500));
        addRecipe(builder()
                .withPedestalItem(4, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.PIERCING, 4, 9000));

        addRecipe(builder()
                .withPedestalItem(1, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.POWER, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.POWER, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(3, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.POWER, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.POWER, 4, 6500));
        addRecipe(builder()
                .withPedestalItem(5, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.POWER, 5, 9000));


        addRecipe(builder()
                .withPedestalItem(1, Items.ARROW)
                .withPedestalItem(1, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PROJECTILE_PROTECTION, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(1, Items.ARROW)
                .withPedestalItem(2, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PROJECTILE_PROTECTION, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(1, Items.ARROW)
                .withPedestalItem(2, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(3, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PROJECTILE_PROTECTION, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(1, Items.ARROW)
                .withPedestalItem(2, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PROJECTILE_PROTECTION, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(1, Items.ARROW)
                .withPedestalItem(3, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(3, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PROJECTILE_PROTECTION, 5, 8000));


        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PROTECTION, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.PROTECTION, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(3, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.PROTECTION, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.PROTECTION, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(3, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(3, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_DIAMOND))
                .buildEnchantmentRecipe(Enchantments.PROTECTION, 5, 8000));


        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PUNCH, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(3, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PUNCH, 2, 4000));

        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.QUICK_CHARGE, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.QUICK_CHARGE, 2, 4000));
        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(2, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(3, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.QUICK_CHARGE, 3, 6000));


        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.WATER_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.RESPIRATION, 1, 3000));
        addRecipe(builder()
                .withPedestalItem(4, ItemsRegistry.WATER_ESSENCE)
                .withPedestalItem(4, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.RESPIRATION, 2, 6000));
        addRecipe(builder()
                .withPedestalItem(6, ItemsRegistry.WATER_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.RESPIRATION, 3, 9000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Blocks.QUARTZ_BLOCK))
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SHARPNESS, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Blocks.QUARTZ_BLOCK))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SHARPNESS, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Blocks.QUARTZ_BLOCK))
                .withPedestalItem(1, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SHARPNESS, 3, 5000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Blocks.QUARTZ_BLOCK))
                .withPedestalItem(2, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SHARPNESS, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Blocks.QUARTZ_BLOCK))
                .withPedestalItem(2, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SHARPNESS, 5, 8000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.GEMS_EMERALD))
                .withPedestalItem(2, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SILK_TOUCH, 1, 9000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Items.BONE_BLOCK))
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SMITE, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(3, Ingredient.of(Items.BONE_BLOCK))
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SMITE, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.BONE_BLOCK))
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SMITE, 3, 5000));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.BONE_BLOCK))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SMITE, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.BONE_BLOCK))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SMITE, 5, 8000));


        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SWEEPING_EDGE, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SWEEPING_EDGE, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(3, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(3, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SWEEPING_EDGE, 3, 5000));

        addRecipe(builder()
                .withPedestalItem(1, Ingredient.of(ItemsRegistry.WILDEN_SPIKE.get()))
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.THORNS, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(ItemsRegistry.WILDEN_SPIKE.get()))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.THORNS, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(3, Ingredient.of(ItemsRegistry.WILDEN_SPIKE.get()))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(3, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.THORNS, 3, 5000));

        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(1, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.UNBREAKING, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.UNBREAKING, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(1, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(3, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.UNBREAKING, 3, 5000));

        addRecipe(builder()
                .withPedestalItem(1, BlockRegistry.SOURCEBERRY_BUSH)
                .withPedestalItem(4, RecipeDatagen.SOURCE_GEM_BLOCK)
                .buildEnchantmentRecipe(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(1, BlockRegistry.SOURCEBERRY_BUSH)
                .withPedestalItem(1, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(4, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(1, BlockRegistry.SOURCEBERRY_BUSH)
                .withPedestalItem(2, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(4, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT, 3, 5000));


        addRecipe(builder()
                .withPedestalItem(2, BlockRegistry.SOURCEBERRY_BUSH)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2, BlockRegistry.SOURCEBERRY_BUSH)
                .withPedestalItem(2, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(2, BlockRegistry.SOURCEBERRY_BUSH)
                .withPedestalItem(3, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT, 3, 5000));

        addRecipe(builder()
                .withResult(ItemsRegistry.ENCHANTERS_SWORD)
                .withReagent(Items.DIAMOND_SWORD)
                .withPedestalItem(1, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .keepNbtOfReagent(true)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.ENCHANTERS_SHIELD)
                .withReagent(Items.SHIELD)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .keepNbtOfReagent(true)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.DRYGMY_CHARM)
                .withReagent(ItemsRegistry.DRYGMY_SHARD)
                .withPedestalItem(Ingredient.of(ItemTags.FISHES))
                .withPedestalItem(Items.WHEAT)
                .withPedestalItem(Items.APPLE)
                .withPedestalItem(Items.CARROT)
                .withPedestalItem(Ingredient.of(Tags.Items.SEEDS))
                .withPedestalItem(3, RecipeDatagen.SOURCE_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.SUMMONING_FOCUS)
                .withReagent(RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(ItemsRegistry.WILDEN_HORN)
                .withPedestalItem(ItemsRegistry.WILDEN_SPIKE)
                .withPedestalItem(ItemsRegistry.WILDEN_WING)
                .withPedestalItem(ItemsRegistry.WILDEN_TRIBUTE)
                .withPedestalItem(Ingredient.of(Tags.Items.INGOTS_GOLD))
                .build());

        addRecipe(builder()
                .withResult(BlockRegistry.RELAY_SPLITTER)
                .withReagent(BlockRegistry.RELAY)
                .withPedestalItem(4, Ingredient.of(Tags.Items.GEMS_QUARTZ))
                .withPedestalItem(4, Ingredient.of(Tags.Items.GEMS_LAPIS))
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
                .withPedestalItem(4, Ingredient.of(Items.HOPPER))
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.ENCHANTERS_MIRROR)
                .withReagent(RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.GLASS_BLOCKS))
                .withPedestalItem(2, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, RecipeDatagen.ARCHWOOD_LOG)
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
                .withPedestalItem(RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.RODS_BLAZE))
                .build());
        List<Ingredient> reactiveIngredients = new ArrayList<>();
        reactiveIngredients.add(Ingredient.of(ItemsRegistry.SPELL_PARCHMENT.get()));
        reactiveIngredients.add(Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS));
        reactiveIngredients.add(RecipeDatagen.SOURCE_GEM_BLOCK);
        addRecipe(new ApparatusRecipeBuilder.RecipeWrapper<>(ArsNouveau.prefix("reactive_1"),
                new ReactiveEnchantmentRecipe(reactiveIngredients, 3000),
                ReactiveEnchantmentRecipe.CODEC));

        List<Ingredient> spellWriteList = new ArrayList<>();
        spellWriteList.add(Ingredient.of(ItemsRegistry.SPELL_PARCHMENT.get()));
        addRecipe(new ApparatusRecipeBuilder.RecipeWrapper<>(ArsNouveau.prefix("spell_write"), new SpellWriteRecipe(spellWriteList, 3000), SpellWriteRecipe.CODEC));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(1, Ingredient.of(Tags.Items.ENDER_PEARLS))
                .withPedestalItem(1, Ingredient.of(ItemsRegistry.WILDEN_TRIBUTE.get()))
                .buildEnchantmentRecipe(EnchantmentRegistry.REACTIVE_ENCHANTMENT, 4, 9000));

        addRecipe(builder()
                .withResult(BlockRegistry.RELAY_COLLECTOR)
                .withReagent(BlockRegistry.RELAY)
                .withPedestalItem(4, Ingredient.of(Tags.Items.CHESTS))
                .build());

        addRecipe(builder().withResult(BlockRegistry.SCRYERS_OCULUS)
                .withReagent(Items.ENDER_EYE)
                .withPedestalItem(Blocks.OBSERVER)
                .withPedestalItem(Items.SPYGLASS)
                .withPedestalItem(RecipeDatagen.SOURCE_GEM_BLOCK)
                .withPedestalItem(Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .build());

        addRecipe(builder().withResult(ItemsRegistry.SHAPERS_FOCUS)
                .withReagent(ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(1, Ingredient.of(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(Items.PISTON)
                .withPedestalItem(Items.SLIME_BLOCK)
                .withPedestalItem(Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .build());

        addRecipe(builder().withResult(ItemsRegistry.ALCHEMISTS_CROWN).withReagent(Items.GOLDEN_HELMET).withPedestalItem(3, Items.GLASS_BOTTLE).build());
        addRecipe(builder().withResult(BlockRegistry.POTION_DIFFUSER).withReagent(Blocks.CAMPFIRE)
                .withPedestalItem(3, Ingredient.of(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(3, BlockRegistry.ARCHWOOD_PLANK).build());

        addRecipe(builder().withResult(ItemsRegistry.SPLASH_LAUNCHER)
                .withReagent(Items.DISPENSER)
                .withPedestalItem(2, Tags.Items.INGOTS_GOLD)
                .withPedestalItem(2, Tags.Items.RODS_BLAZE)
                .withPedestalItem(4, Tags.Items.GUNPOWDERS).build());
        addRecipe(builder().withResult(ItemsRegistry.LINGERING_LAUNCHER)
                .withReagent(ItemsRegistry.SPLASH_LAUNCHER)
                .withPedestalItem(Items.DRAGON_BREATH)
                .withPedestalItem(2, ItemsRegistry.AIR_ESSENCE)
                .build());

        addRecipe(new ApparatusRecipeBuilder.RecipeWrapper<>(ArsNouveau.prefix("first_armor_upgrade"),
                new ArmorUpgradeRecipe(List.of(Ingredient.of(Tags.Items.RODS_BLAZE), Ingredient.of(Tags.Items.RODS_BLAZE)), 2500, 1),
                ArmorUpgradeRecipe.CODEC));
        addRecipe(new ApparatusRecipeBuilder.RecipeWrapper<>(ArsNouveau.prefix("second_armor_upgrade"),
                new ArmorUpgradeRecipe(List.of(Ingredient.of(Tags.Items.ENDER_PEARLS), Ingredient.of(Tags.Items.ENDER_PEARLS), Ingredient.of(Items.CHORUS_FRUIT)), 5000, 2),
                ArmorUpgradeRecipe.CODEC));


        addRecipe(builder().withResult(getPerkItem(StarbunclePerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(3, ItemsRegistry.STARBUNCLE_SHARD)
                .withPedestalItem(3, Items.SUGAR)
                .withPedestalItem(2, ItemsRegistry.MANIPULATION_ESSENCE)
                .build());

        addRecipe(builder().withResult(getPerkItem(FeatherPerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(4, Tags.Items.FEATHERS)
                .withPedestalItem(2, ItemsRegistry.ABJURATION_ESSENCE)
                .build());

        addRecipe(builder().withResult(getPerkItem(JumpHeightPerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(Items.SPIDER_EYE)
                .withPedestalItem(3, Items.RABBIT_HIDE)
                .withPedestalItem(2, ItemsRegistry.AIR_ESSENCE)
                .build());

        addRecipe(builder().withResult(getPerkItem(SaturationPerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(3, ItemsRegistry.WHIRLISPRIG_SHARDS)
                .withPedestalItem(2, ItemsRegistry.EARTH_ESSENCE)
                .withPedestalItem(Items.GOLDEN_APPLE)
                .build());

        addRecipe(builder().withResult(getPerkItem(DepthsPerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(3, ItemTags.FISHES)
                .withPedestalItem(2, ItemsRegistry.WATER_ESSENCE)
                .withPedestalItem(Items.PUFFERFISH)
                .build());

        addRecipe(builder().withResult(getPerkItem(RepairingPerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(Items.ANVIL)
                .withPedestalItem(2, ItemsRegistry.MANIPULATION_ESSENCE)
                .build());

        addRecipe(builder().withResult(getPerkItem(PotionDurationPerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(3, ItemsRegistry.WIXIE_SHARD)
                .withPedestalItem(2, ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(2, Tags.Items.CROPS_NETHER_WART)
                .withPedestalItem(Items.BLAZE_ROD)
                .build());

        addRecipe(builder().withResult(getPerkItem(LootingPerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(3, ItemsRegistry.DRYGMY_SHARD)
                .withPedestalItem(2, ItemsRegistry.EARTH_ESSENCE)
                .withPedestalItem(Items.RABBIT_FOOT)
                .build());

        addRecipe(builder().withResult(getPerkItem(SpellDamagePerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(ItemsRegistry.MAGE_BLOOM)
                .withPedestalItem(ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(ItemsRegistry.WATER_ESSENCE)
                .withPedestalItem(ItemsRegistry.ABJURATION_ESSENCE)
                .withPedestalItem(ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(ItemsRegistry.EARTH_ESSENCE)
                .withPedestalItem(ItemsRegistry.CONJURATION_ESSENCE)
                .build());

        addRecipe(builder().withResult(getPerkItem(GlidingPerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(2, ItemsRegistry.AIR_ESSENCE)
                .withPedestalItem(Items.ELYTRA)
                .build());

        addRecipe(builder().withResult(getPerkItem(MagicCapacityPerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(3, BlockRegistry.SOURCEBERRY_BUSH)
                .withPedestalItem(3, ItemsRegistry.MAGE_BLOOM)
                .build());

        addRecipe(builder().withResult(getPerkItem(MagicResistPerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(8, ItemsRegistry.MAGE_FIBER)
                .build());


        makeArmor(ItemsRegistry.SORCERER_BOOTS, Items.GOLDEN_BOOTS);
        makeArmor(ItemsRegistry.SORCERER_LEGGINGS, Items.GOLDEN_LEGGINGS);
        makeArmor(ItemsRegistry.SORCERER_ROBES, Items.GOLDEN_CHESTPLATE);
        makeArmor(ItemsRegistry.SORCERER_HOOD, Items.GOLDEN_HELMET);

        makeArmor(ItemsRegistry.ARCANIST_HOOD, Items.IRON_HELMET);
        makeArmor(ItemsRegistry.ARCANIST_ROBES, Items.IRON_CHESTPLATE);
        makeArmor(ItemsRegistry.ARCANIST_LEGGINGS, Items.IRON_LEGGINGS);
        makeArmor(ItemsRegistry.ARCANIST_BOOTS, Items.IRON_BOOTS);

        makeArmor(ItemsRegistry.BATTLEMAGE_BOOTS, Items.DIAMOND_BOOTS);
        makeArmor(ItemsRegistry.BATTLEMAGE_LEGGINGS, Items.DIAMOND_LEGGINGS);
        makeArmor(ItemsRegistry.BATTLEMAGE_ROBES, Items.DIAMOND_CHESTPLATE);
        makeArmor(ItemsRegistry.BATTLEMAGE_HOOD, Items.DIAMOND_HELMET);

        addRecipe(builder().withResult(getPerkItem(IgnitePerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(Items.MAGMA_CREAM)
                .withPedestalItem(ItemsRegistry.FIRE_ESSENCE)
                .withPedestalItem(Items.FIRE_CHARGE)
                .build());

        addRecipe(builder().withResult(getPerkItem(ChillingPerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(Items.BLUE_ICE)
                .withPedestalItem(2, ItemsRegistry.WATER_ESSENCE)
                .withPedestalItem(Items.POWDER_SNOW_BUCKET)
                .build());

        addRecipe(builder().withResult(getPerkItem(VampiricPerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(BlockRegistry.MENDOSTEEN_POD)
                .withPedestalItem(Items.SCULK_CATALYST)
                .withPedestalItem(2, ItemsRegistry.ABJURATION_ESSENCE)
                .build());

        addRecipe(builder().withResult(getPerkItem(TotemPerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(Items.TOTEM_OF_UNDYING)
                .withPedestalItem(Items.PHANTOM_MEMBRANE)
                .withPedestalItem(2, ItemsRegistry.ABJURATION_ESSENCE)
                .build());

        addRecipe(builder().withResult(ItemsRegistry.SPELL_CROSSBOW)
                .keepNbtOfReagent(true)
                .withReagent(Items.CROSSBOW)
                .withPedestalItem(Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .withPedestalItem(ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(RecipeDatagen.SOURCE_GEM_BLOCK).build());

        addRecipe(builder().withResult(BlockRegistry.BRAZIER_RELAY)
                .withReagent(BlockRegistry.RITUAL_BLOCK)
                .withPedestalItem(3, ItemsRegistry.MANIPULATION_ESSENCE).build());

        addRecipe(builder().withResult(ItemsRegistry.STABLE_WARP_SCROLL)
                .withReagent(ItemsRegistry.WARP_SCROLL)
                .withPedestalItem(4, Items.BLAZE_POWDER)
                .withPedestalItem(2, Tags.Items.ENDER_PEARLS)
                .keepNbtOfReagent(true).build());

        addRecipe(builder().withResult(ItemsRegistry.SCRY_CASTER)
                .withReagent(BlockRegistry.SCRYERS_CRYSTAL)
                .withPedestalItem(4, Items.BLAZE_POWDER)
                .withPedestalItem(4, Tags.Items.ENDER_PEARLS)
                .keepNbtOfReagent(true).build());

        addRecipe(builder().withResult(BlockRegistry.CRAFTING_LECTERN)
                .withReagent(Blocks.LECTERN)
                .withPedestalItem(4, Tags.Items.CHESTS).build());

        addRecipe(builder()
                .withResult(ItemsRegistry.WARP_SCROLL, 2)
                .withReagent(ItemsRegistry.WARP_SCROLL)
                .withPedestalItem(ItemsRegistry.WARP_SCROLL)
                .keepNbtOfReagent(true)
                .withSourceCost(1000)
                .withId(ArsNouveau.prefix("warp_scroll_copy"))
                .build());
        addRecipe(builder().withResult(BlockRegistry.SPELL_SENSOR).withReagent(Blocks.SCULK_SENSOR).build());
        addRecipe(builder().withReagent(ItemsRegistry.RING_OF_POTENTIAL).withResult(ItemsRegistry.JUMP_RING)
                .withPedestalItem(3, ItemsRegistry.WILDEN_WING)
                .withPedestalItem(ItemsRegistry.AIR_ESSENCE).build());

        addRecipe(builder().withResult(getPerkItem(ImmolatePerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(3, ItemsRegistry.FIRE_ESSENCE)
                .build());

        addRecipe(builder().withResult(getPerkItem(StepHeightPerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(3, ItemsRegistry.AIR_ESSENCE.get())
                .build());
        addRecipe(builder().withResult(getPerkItem(KnockbackResistPerk.INSTANCE.getRegistryName()))
                .withReagent(ItemsRegistry.BLANK_THREAD)
                .withPedestalItem(3, Ingredient.of(Tags.Items.OBSIDIANS))
                .build());

        addRecipe(builder().withResult(ItemsRegistry.ALAKARKINOS_CHARM).withReagent(ItemsRegistry.ALAKARKINOS_SHARD).withPedestalItem(Items.BRUSH).withPedestalItem(3, Ingredient.of(ItemTags.DECORATED_POT_SHERDS)).build());

        addRecipe(builder().withResult(ItemsRegistry.ENCHANTERS_GAUNTLET)
                .withReagent(Ingredient.of(Tags.Items.LEATHERS))
                .withPedestalItem(1, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .keepNbtOfReagent(true)
                .build());
        addRecipe(builder().withResult(ItemsRegistry.ENCHANTERS_FISHING_ROD)
                .withReagent(Items.FISHING_ROD)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .withPedestalItem(2, RecipeDatagen.SOURCE_GEM_BLOCK)
                .keepNbtOfReagent(true)
                .build());

    }

    public void makeArmor(ItemLike outputItem, ItemLike armorItem) {
        addRecipe(builder().withResult(outputItem)
                .withReagent(armorItem)
                .withPedestalItem(4, ItemsRegistry.MAGE_FIBER)
                .keepNbtOfReagent(true)
                .build());
    }

    public PerkItem getPerkItem(ResourceLocation id) {
        return PerkRegistry.getPerkItemMap().get(id);
    }

    public void addRecipe(ApparatusRecipeBuilder.RecipeWrapper recipe) {
        recipes.add(recipe);
    }

    protected static Path getRecipePath(Path pathIn, Item item) {
        return getRecipePath(pathIn, getRegistryName(item).getPath());
    }

    protected static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/ars_nouveau/recipe/" + str + ".json");
    }

    @Override
    public String getName() {
        return "Apparatus";
    }
}
