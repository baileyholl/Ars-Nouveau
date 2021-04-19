package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ApparatusRecipeProvider implements IDataProvider {
    private final DataGenerator generator;
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger();
    public ApparatusRecipeProvider(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    List<EnchantingApparatusRecipe> recipes = new ArrayList<>();
    @Override
    public void run(DirectoryCache cache) throws IOException {
        addEntries();
        Path output = this.generator.getOutputFolder();
        for(IEnchantingRecipe g : recipes){
            if(g instanceof EnchantingApparatusRecipe){
                System.out.println(g);
                Path path = getRecipePath(output, ((EnchantingApparatusRecipe) g).getId().getPath());
                IDataProvider.save(GSON, cache, ((EnchantingApparatusRecipe) g).asRecipe(), path);
            }
        }
    }

    public ApparatusRecipeBuilder builder(){
        return ApparatusRecipeBuilder.builder();
    }

    public void addEntries(){
//        addRecipe(new EnchantingApparatusRecipe(ItemsRegistry.beltOfLevitation, ItemsRegistry.mundaneBelt, new Item[]
//                {Items.GOLD_INGOT,Items.GOLD_INGOT,Items.GOLD_INGOT, Items.GOLD_INGOT,
//                Items.FEATHER,Items.FEATHER,Items.FEATHER, ArsNouveauAPI.getInstance().getGlyphItem(ModConfig.EffectLaunchID)},  ArsNouveauAPI.PatchouliCategories.equipment.name()));
        addRecipe(builder().withResult(ItemsRegistry.beltOfLevitation)
                .withReagent(ItemsRegistry.mundaneBelt)
                .withPedestalItem(4, Ingredient.of(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(3, Items.FEATHER)
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.EffectLaunchID))
                .withCategory(ArsNouveauAPI.PatchouliCategories.equipment)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.jarOfLight)
                .withReagent(Items.GLASS_BOTTLE)
                .withPedestalItem(4, Items.GLOWSTONE)
                .withPedestalItem(2, Items.REDSTONE_LAMP)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.GLASS))
                .build());

        addRecipe(builder()
                .withResult(BlockRegistry.MANA_BLOOM_CROP)
                .withReagent(Ingredient.of(Tags.Items.SEEDS))
                .withPedestalItem(4, Recipes.MANA_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.ringOfLesserDiscount)
                .withReagent(ItemsRegistry.ringOfPotential)
                .withPedestalItem(4,  Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2,Ingredient.of(Tags.Items.ENDER_PEARLS))
                .withPedestalItem(2,   Recipes.MANA_GEM)
                .withPedestalItem(2,   Ingredient.of(Tags.Items.ENDER_PEARLS))
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.ringOfGreaterDiscount)
                .withReagent(ItemsRegistry.ringOfLesserDiscount)
                .withPedestalItem(4,  Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2,Ingredient.of(Tags.Items.RODS_BLAZE))
                .withPedestalItem(2, Recipes.MANA_GEM)
                .build());


        addRecipe(new EnchantingApparatusRecipe(new ItemStack(ItemsRegistry.beltOfUnstableGifts), Ingredient.of(ItemsRegistry.mundaneBelt), Arrays.asList(
                Ingredient.of(Items.SUGAR),
                Ingredient.of(Tags.Items.CROPS_NETHER_WART),
                Ingredient.of(Tags.Items.RODS_BLAZE),
                Ingredient.of(Tags.Items.DUSTS_GLOWSTONE),
                Ingredient.of(Items.FERMENTED_SPIDER_EYE),
                Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                Ingredient.of(Items.BREWING_STAND),
                Ingredient.of(Tags.Items.FEATHERS)
        ), ArsNouveauAPI.PatchouliCategories.equipment.name()));


        addRecipe(builder()
                .withResult(BlockRegistry.SUMMONING_CRYSTAL)
                .withReagent(Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2,Ingredient.of(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(4, BlockRegistry.ARCANE_STONE)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.whelpCharm)
                .withReagent( Items.EGG)
                .withPedestalItem(4,Ingredient.of(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(3, Recipes.MANA_GEM)
                .withPedestalItem( ItemsRegistry.noviceSpellBook)
                .build());


        addRecipe(builder()
                .withResult(BlockRegistry.ARCANE_RELAY)
                .withReagent(  BlockRegistry.MANA_JAR)
                .withPedestalItem(6,Ingredient.of(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(2, Recipes.MANA_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.carbuncleCharm)
                .withReagent(ItemsRegistry.carbuncleShard)
                .withPedestalItem(5,Ingredient.of(Tags.Items.NUGGETS_GOLD))
                .withPedestalItem(3, Recipes.MANA_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.amuletOfManaBoost)
                .withReagent(ItemsRegistry.dullTrinket)
                .withPedestalItem(3,Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(5, Recipes.MANA_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.amuletOfManaRegen)
                .withReagent(ItemsRegistry.dullTrinket)
                .withPedestalItem(2,Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2,Ingredient.of(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(4, Recipes.MANA_GEM)
                .build());

        addRecipe(builder()
                .withResult(BlockRegistry.ARCANE_RELAY_SPLITTER)
                .withReagent( BlockRegistry.ARCANE_RELAY)
                .withPedestalItem(4,Ingredient.of(Tags.Items.GEMS_QUARTZ))
                .withPedestalItem(4,Ingredient.of(Tags.Items.INGOTS_GOLD))
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.sylphCharm)
                .withReagent(ItemsRegistry.sylphShard)
                .withPedestalItem(Recipes.MANA_GEM)
                .withPedestalItem(BlockRegistry.MANA_BLOOM_CROP)
                .withPedestalItem(ItemsRegistry.manaBloom)
                .withPedestalItem(Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem( Items.OAK_SAPLING)
                .withPedestalItem( Items.SPRUCE_SAPLING)
                .withPedestalItem( Items.BIRCH_SAPLING)
                .withPedestalItem(Ingredient.of(Tags.Items.SEEDS_WHEAT))
                .build());


        addRecipe(builder()
                .withResult(BlockRegistry.SPELL_TURRET)
                .withReagent(Items.DISPENSER)
                .withPedestalItem(Ingredient.of(Tags.Items.RODS_BLAZE))
                .withPedestalItem( ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.EffectRedstoneID))
                .withPedestalItem(Ingredient.of(Tags.Items.STORAGE_BLOCKS_QUARTZ))
                .withPedestalItem(Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .withPedestalItem(Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(3,Ingredient.of(Tags.Items.INGOTS_GOLD))
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
                .withPedestalItem(2, Recipes.MANA_GEM)
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
                .withPedestalItem(4, Recipes.MANA_GEM)
                .withPedestalItem(2, Ingredient.of(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile()))
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAccelerate()))
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.SPELL_BOW)
                .withReagent(Items.BOW)
                .withPedestalItem(Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile()))
                .build());

        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.AMPLIFY_ARROW, 32))
                .withReagent( Ingredient.of(ItemTags.ARROWS))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAmplify()))
                .build());


        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.SPLIT_ARROW, 32))
                .withReagent( Ingredient.of(ItemTags.ARROWS))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentSplit()))
                .build());
        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.PIERCE_ARROW, 32))
                .withReagent( Ingredient.of(ItemTags.ARROWS))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentPierce()))
                .build());

        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.POTION_FLASK))
                .withReagent(Ingredient.of(Items.GLASS_BOTTLE))
                .withPedestalItem(2, ItemsRegistry.blazeFiber)
                .withPedestalItem(Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .build());

        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.POTION_FLASK_EXTEND_TIME))
                .withReagent(Ingredient.of(ItemsRegistry.POTION_FLASK))
                .withPedestalItem(8, ArsNouveauAPI.getInstance().getGlyphItem(new AugmentExtendTime()))
                .build());


        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.POTION_FLASK_AMPLIFY))
                .withReagent(Ingredient.of(ItemsRegistry.POTION_FLASK))
                .withPedestalItem(8, ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAmplify()))
                .build());

        addRecipe(builder()
                .withResult(new ItemStack(BlockRegistry.POTION_MELDER))
                .withReagent(Ingredient.of(BlockRegistry.POTION_JAR))
                .withPedestalItem(2, ArsNouveauAPI.getInstance().getGlyphItem(new AugmentExtract()))
                .withPedestalItem(2,Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .withPedestalItem(4,Ingredient.of(Tags.Items.RODS_BLAZE))
                .build());


        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(ItemTags.FISHES))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.AQUA_AFFINITY, 1, 5000));
        addRecipe(builder()
                 .withPedestalItem(4, Ingredient.of(Tags.Items.RODS_BLAZE))
                .withPedestalItem(1,Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentExtendTime()))
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAOE()))
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentDampen()))
                .buildEnchantmentRecipe(EnchantmentRegistry.REACTIVE_ENCHANTMENT, 2, 6000));

        addRecipe(builder()
                .withPedestalItem(4, ItemsRegistry.mythicalClay)
                .withPedestalItem(1,Ingredient.of(Tags.Items.ENDER_PEARLS))
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentPierce()))
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentExtract()))
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentFortune()))
                .buildEnchantmentRecipe(EnchantmentRegistry.REACTIVE_ENCHANTMENT, 3, 9000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Items.SPIDER_EYE))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BANE_OF_ARTHROPODS, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(3, Ingredient.of(Items.SPIDER_EYE))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BANE_OF_ARTHROPODS, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.FERMENTED_SPIDER_EYE))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BANE_OF_ARTHROPODS, 3, 500));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.FERMENTED_SPIDER_EYE))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BANE_OF_ARTHROPODS, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.FERMENTED_SPIDER_EYE))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BANE_OF_ARTHROPODS, 5, 8000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(0, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BLAST_PROTECTION, 1, 2000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BLAST_PROTECTION, 2, 4000));
        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.OBSIDIAN))
                .buildEnchantmentRecipe(Enchantments.BLAST_PROTECTION, 3, 6000));
        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD))
                .buildEnchantmentRecipe(Enchantments.BLAST_PROTECTION, 4, 8000));

        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectAquatic())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.DEPTH_STRIDER, 1, 3000));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectAquatic())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Items.PRISMARINE_SHARD))
                .buildEnchantmentRecipe(Enchantments.DEPTH_STRIDER, 2, 6000));
        addRecipe(builder()
                .withPedestalItem(4,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectAquatic())))
                .withPedestalItem(3, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Items.HEART_OF_THE_SEA))
                .buildEnchantmentRecipe(Enchantments.DEPTH_STRIDER, 3, 9000));

        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(Items.SUGAR))
                .withPedestalItem(1,  Ingredient.of(Items.IRON_PICKAXE))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.BLOCK_EFFICIENCY, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .withPedestalItem(1,  Ingredient.of(Items.GOLDEN_PICKAXE))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.BLOCK_EFFICIENCY, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .withPedestalItem(1,  Ingredient.of(Items.GOLDEN_PICKAXE))
                .withPedestalItem(3, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.OBSIDIAN))
                .buildEnchantmentRecipe(Enchantments.BLOCK_EFFICIENCY, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .withPedestalItem(1,  Ingredient.of(Items.DIAMOND_PICKAXE))
                .withPedestalItem(1,  Ingredient.of(Items.IRON_SHOVEL))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.OBSIDIAN))
                .buildEnchantmentRecipe(Enchantments.BLOCK_EFFICIENCY, 4, 6500));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .withPedestalItem(1,  Ingredient.of(Items.DIAMOND_PICKAXE))
                .withPedestalItem(1,  Ingredient.of(Items.DIAMOND_SHOVEL))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.BLOCK_EFFICIENCY, 5, 8000));


        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectSlowfall())))
                .withPedestalItem(1,  Ingredient.of(Tags.Items.SLIMEBALLS))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.FALL_PROTECTION, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectSlowfall())))
                .withPedestalItem(1,  Ingredient.of(Tags.Items.SLIMEBALLS))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.FALL_PROTECTION, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(3,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectSlowfall())))
                .withPedestalItem(1,  Ingredient.of(Tags.Items.SLIMEBALLS))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.FALL_PROTECTION, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(4,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectSlowfall())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.FALL_PROTECTION, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectIgnite())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.FIRE_ASPECT, 1, 2000));

        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectIgnite())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.FIRE_ASPECT, 2, 4000));

        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectIgnite())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.FIRE_PROTECTION, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectIgnite())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.FIRE_PROTECTION, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectIgnite())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(3, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1,  Ingredient.of(Tags.Items.RODS_BLAZE))
                .buildEnchantmentRecipe(Enchantments.FIRE_PROTECTION, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectIgnite())))
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.RODS_BLAZE))
                .buildEnchantmentRecipe(Enchantments.FIRE_PROTECTION, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(3,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectIgnite())))
                .withPedestalItem(3,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.RODS_BLAZE))
                .buildEnchantmentRecipe(Enchantments.FIRE_PROTECTION, 5, 8000));

        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectIgnite())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())))
                .withPedestalItem(1, Ingredient.of(Tags.Items.RODS_BLAZE))
                .buildEnchantmentRecipe(Enchantments.FLAMING_ARROWS, 1, 5000));

        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentFortune())))
                .withPedestalItem(1,  Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(6, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.BLOCK_FORTUNE, 1, 6000));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentFortune())))
                .withPedestalItem(4,  Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.BLOCK_FORTUNE, 2, 8000));
        addRecipe(builder()
                .withPedestalItem(3,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentFortune())))
                .withPedestalItem(1,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_DIAMOND))
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.BLOCK_FORTUNE, 3, 9000));

        addRecipe(builder()
                .withPedestalItem(7,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())))
                .withPedestalItem(1,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_DIAMOND))
                .buildEnchantmentRecipe(Enchantments.INFINITY_ARROWS, 1, 9000));

        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodTouch())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectKnockback())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.KNOCKBACK, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodTouch())))
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectKnockback())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.KNOCKBACK, 2, 4000));

        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentFortune())))
                .withPedestalItem(1,  Ingredient.of(Tags.Items.GEMS_EMERALD))
                .withPedestalItem(6, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.MOB_LOOTING, 1, 6000));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentFortune())))
                .withPedestalItem(4,  Ingredient.of(Tags.Items.GEMS_EMERALD))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.MOB_LOOTING, 2, 8000));
        addRecipe(builder()
                .withPedestalItem(3,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentFortune())))
                .withPedestalItem(1,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_EMERALD))
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.MOB_LOOTING, 3, 9000));

        addRecipe(builder()
                .withPedestalItem(3,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentSplit())))
                .withPedestalItem(5, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.MULTISHOT, 1, 9000));

        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentPierce())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.PIERCING, 1, 2500));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentPierce())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.PIERCING, 2, 5000));
        addRecipe(builder()
                .withPedestalItem(3,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentPierce())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.PIERCING, 3, 7500));
        addRecipe(builder()
                .withPedestalItem(4,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentPierce())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.PIERCING, 4, 9000));

        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAmplify())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.POWER_ARROWS, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAmplify())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.POWER_ARROWS, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(3,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAmplify())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.POWER_ARROWS, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(4,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAmplify())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.POWER_ARROWS, 4, 6500));
        addRecipe(builder()
                .withPedestalItem(5,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAmplify())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.POWER_ARROWS, 5, 9000));


        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PROJECTILE_PROTECTION, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PROJECTILE_PROTECTION, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(3, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PROJECTILE_PROTECTION, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())))
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PROJECTILE_PROTECTION, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(3,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())))
                .withPedestalItem(3,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PROJECTILE_PROTECTION, 5, 8000));


        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodTouch())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(1,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.ALL_DAMAGE_PROTECTION, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodTouch())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.ALL_DAMAGE_PROTECTION, 2, 3500));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodTouch())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(3, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.ALL_DAMAGE_PROTECTION, 3, 5000));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodTouch())))
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.ALL_DAMAGE_PROTECTION, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(3,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodTouch())))
                .withPedestalItem(3,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1,  Ingredient.of(Tags.Items.STORAGE_BLOCKS_DIAMOND))
                .buildEnchantmentRecipe(Enchantments.ALL_DAMAGE_PROTECTION, 5, 8000));


        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectKnockback())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PUNCH_ARROWS, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())))
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectKnockback())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.PUNCH_ARROWS, 2, 4000));

        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectHaste())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.QUICK_CHARGE, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())))
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectHaste())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.QUICK_CHARGE, 2, 4000));
        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())))
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectHaste())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.QUICK_CHARGE, 3, 6000));


        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectAquatic())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.RESPIRATION, 1, 3000));
        addRecipe(builder()
                .withPedestalItem(4,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectAquatic())))
                .withPedestalItem(4, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.RESPIRATION, 2, 6000));
        addRecipe(builder()
                .withPedestalItem(6,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectAquatic())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .buildEnchantmentRecipe(Enchantments.RESPIRATION, 3, 9000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_QUARTZ))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SHARPNESS, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_QUARTZ))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SHARPNESS, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_QUARTZ))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAmplify())))
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SHARPNESS, 3, 500));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_QUARTZ))
                .withPedestalItem(2, Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAmplify())))
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SHARPNESS, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_QUARTZ))
                .withPedestalItem(2, Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAmplify())))
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SHARPNESS, 5, 8000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentExtract())))
                .withPedestalItem(2, Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAmplify())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SILK_TOUCH, 1, 9000));

        addRecipe(builder()
                .withPedestalItem(2, Ingredient.of(Items.BONE_BLOCK))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SMITE, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(3, Ingredient.of(Items.BONE_BLOCK))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SMITE, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.BONE_BLOCK))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SMITE, 3, 500));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.BONE_BLOCK))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SMITE, 4, 6500));

        addRecipe(builder()
                .withPedestalItem(4, Ingredient.of(Items.BONE_BLOCK))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SMITE, 5, 8000));


        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAOE())))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SWEEPING_EDGE, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAOE())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SWEEPING_EDGE, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(3,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAOE())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(3, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.SWEEPING_EDGE, 3, 5000));

        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ItemsRegistry.WILDEN_SPIKE))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.THORNS, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(2,  Ingredient.of(ItemsRegistry.WILDEN_SPIKE))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.THORNS, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(3,  Ingredient.of(ItemsRegistry.WILDEN_SPIKE))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(3, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.THORNS, 3, 5000));

        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAmplify())))
                .withPedestalItem(1, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.UNBREAKING, 1, 2000));
        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(2,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAmplify())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(1, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.UNBREAKING, 2, 3500));

        addRecipe(builder()
                .withPedestalItem(1,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new EffectShield())))
                .withPedestalItem(3,  Ingredient.of(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAmplify())))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2, Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .buildEnchantmentRecipe(Enchantments.UNBREAKING, 3, 5000));

    }

    public static List<Ingredient> listOfIngred(Item[] items) {
        return Arrays.stream(items).map(Ingredient::of).collect(Collectors.toList());
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
