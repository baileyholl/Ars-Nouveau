package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellTier;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.*;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatchouliProvider implements DataProvider {
    private final DataGenerator generator;
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger();
    public static ResourceLocation AUTOMATION = new ResourceLocation(ArsNouveau.MODID, "automation");
    public static ResourceLocation ENCHANTMENTS = new ResourceLocation(ArsNouveau.MODID, "enchantments");
    public static ResourceLocation EQUIPMENT = new ResourceLocation(ArsNouveau.MODID, "equipment");
    public static ResourceLocation FAMILIARS = new ResourceLocation(ArsNouveau.MODID, "familiars");
    public static ResourceLocation GETTING_STARTED = new ResourceLocation(ArsNouveau.MODID, "getting_started");
    public static ResourceLocation MOD_NEWS = new ResourceLocation(ArsNouveau.MODID, "mod_news");

    public static ResourceLocation MACHINES = new ResourceLocation(ArsNouveau.MODID, "machines");
    public static ResourceLocation RESOURCES = new ResourceLocation(ArsNouveau.MODID, "resources");
    public static ResourceLocation RITUALS = new ResourceLocation(ArsNouveau.MODID, "rituals");
    public static ResourceLocation SOURCE = new ResourceLocation(ArsNouveau.MODID, "source");
    public static ResourceLocation GLYPHS_1 = new ResourceLocation(ArsNouveau.MODID, "glyphs_1");
    public static ResourceLocation GLYPHS_2 = new ResourceLocation(ArsNouveau.MODID, "glyphs_2");
    public static ResourceLocation GLYPHS_3 = new ResourceLocation(ArsNouveau.MODID, "glyphs_3");

    public List<PatchouliPage> pages = new ArrayList<>();

    public PatchouliProvider(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    public void addEntries(){
        for(Enchantment g : enchants){
            addEnchantmentPage(g);
        }
        for(AbstractRitual r : ArsNouveauAPI.getInstance().getRitualMap().values()){
            addRitualPage(r);
        }

        for(AbstractFamiliarHolder r : ArsNouveauAPI.getInstance().getFamiliarHolderMap().values()){
            addFamiliarPage(r);
        }

        for(AbstractSpellPart s : ArsNouveauAPI.getInstance().getSpellpartMap().values()){
            addGlyphPage(s);
        }

        addPage(new PatchouliBuilder(GETTING_STARTED, "spell_casting")
                .withIcon(ItemsRegistry.NOVICE_SPELLBOOK)
                .withSortNum(1)
                .withLocalizedText()
                .withLocalizedText()
                .withLocalizedText()
                .withPage(new CraftingPage(ItemsRegistry.NOVICE_SPELLBOOK)), getPath(GETTING_STARTED, "spell_casting"));
        addPage(new PatchouliBuilder(GETTING_STARTED, "spell_mana")
                .withSortNum(2)
                .withIcon(ItemsRegistry.NOVICE_SPELLBOOK)
                .withLocalizedText()
                .withLocalizedText(), getPath(GETTING_STARTED, "spell_mana"));

        addPage(new PatchouliBuilder(GETTING_STARTED, "obtaining_gems")
                .withIcon(BlockRegistry.IMBUEMENT_BLOCK)
                .withSortNum(3)
                .withLocalizedText()
                .withPage(new RelationsPage().withEntry(GETTING_STARTED, "source")), getPath(GETTING_STARTED, "obtaining_gems"));

        addPage(new PatchouliBuilder(GETTING_STARTED, "new_glyphs")
                .withIcon(ItemsRegistry.BLANK_GLYPH)
                .withSortNum(4)
                .withLocalizedText()
                .withPage(new RelationsPage().withEntry(MACHINES, "glyph_press").withEntry(GETTING_STARTED, "source")), getPath(GETTING_STARTED, "new_glyphs"));

        addPage(new PatchouliBuilder(GETTING_STARTED, "source")
                .withSortNum(5)
                .withIcon(BlockRegistry.SOURCE_JAR)
                .withLocalizedText()
                .withPage(new RelationsPage().withEntry(SOURCE, "source_jar").withEntry(SOURCE, "agronomic_sourcelink")), getPath(GETTING_STARTED, "source"));

        addPage(new PatchouliBuilder(GETTING_STARTED, "apparatus_crafting")
                .withSortNum(6)
                .withIcon(BlockRegistry.ENCHANTING_APP_BLOCK)
                .withLocalizedText()
                .withPage(new RelationsPage().withEntry(MACHINES, "enchanting_apparatus")), getPath(GETTING_STARTED, "apparatus_crafting"));

        addPage(new PatchouliBuilder(GETTING_STARTED, "better_casting")
                .withSortNum(7)
                .withIcon(ItemsRegistry.NOVICE_ROBES)
                .withLocalizedText()
                .withPage(new RelationsPage().withEntry(MACHINES, "enchanting_apparatus")), getPath(GETTING_STARTED, "better_casting"));

        addPage(new PatchouliBuilder(GETTING_STARTED, "world_generation")
                .withSortNum(8)
                .withIcon(ItemsRegistry.SOURCE_GEM)
                .withLocalizedText()
                .withPage(new RelationsPage().withEntry(MACHINES, "imbuement_chamber").withEntry(RESOURCES, "archwood").withEntry(RESOURCES,"sourceberry")),
                getPath(GETTING_STARTED, "world_generation"));

        addPage(new PatchouliBuilder(GETTING_STARTED, "upgrades")
                .withSortNum(9)
                .withIcon(ItemsRegistry.ARCHMAGE_SPELLBOOK)
                .withLocalizedText()
                .withPage(new RelationsPage().withEntry(EQUIPMENT, "spell_books").withEntry(EQUIPMENT, "armor")), getPath(GETTING_STARTED, "upgrades"));

        addPage(new PatchouliBuilder(GETTING_STARTED, "starting_automation")
                .withSortNum(10)
                .withIcon(BlockRegistry.BASIC_SPELL_TURRET)
                .withLocalizedText()
                .withPage(new RelationsPage().withEntry(AUTOMATION, "spell_turret")
                        .withEntry(AUTOMATION, "spell_prism").withEntry(AUTOMATION, "bookwyrm_charm").withEntry(AUTOMATION, "starbuncle_charm")
                        .withEntry(AUTOMATION, "wixie_charm")), getPath(GETTING_STARTED, "starting_automation"));

        addPage(new PatchouliBuilder(GETTING_STARTED, "trinkets")
                .withIcon(ItemsRegistry.WARP_SCROLL)
                .withSortNum(11)
                .withLocalizedText()
                .withPage(new RelationsPage()
                        .withEntry(EQUIPMENT, "ring_of_greater_discount")
                        .withEntry(EQUIPMENT, "jar_of_light")
                        .withEntry(EQUIPMENT,"void_jar")
                        .withEntry(EQUIPMENT, "warp_scroll")), getPath(GETTING_STARTED, "trinkets"));

        addBasicItem(ItemsRegistry.AMULET_OF_MANA_BOOST, EQUIPMENT, new ApparatusPage(ItemsRegistry.AMULET_OF_MANA_BOOST));
        addBasicItem(ItemsRegistry.AMULET_OF_MANA_REGEN, EQUIPMENT,  new ApparatusPage(ItemsRegistry.AMULET_OF_MANA_REGEN));
        addBasicItem(ItemsRegistry.BELT_OF_LEVITATION, EQUIPMENT,  new ApparatusPage(ItemsRegistry.BELT_OF_LEVITATION));
        addBasicItem(ItemsRegistry.BELT_OF_UNSTABLE_GIFTS, EQUIPMENT,  new ApparatusPage(ItemsRegistry.BELT_OF_UNSTABLE_GIFTS));
        addBasicItem(ItemsRegistry.JAR_OF_LIGHT, EQUIPMENT,  new ApparatusPage(ItemsRegistry.JAR_OF_LIGHT));
        addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.STARBUNCLE_CHARM)
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.STARBUNCLE_CHARM))
                .withPage(new EntityPage(ModEntities.STARBUNCLE_TYPE.getRegistryName().toString())
                        .withText(getLangPath("starbuncle_charm", 2)))
                .withPage(new TextPage(getLangPath("starbuncle_charm", 3)).withTitle("ars_nouveau.summoning"))
                .withPage(new TextPage(getLangPath("starbuncle_charm", 4)).withTitle("ars_nouveau.item_transport"))
                .withPage(new TextPage(getLangPath("starbuncle_charm", 5)).withTitle("ars_nouveau.filtering"))
                .withPage(new TextPage(getLangPath("starbuncle_charm", 6)).withTitle("ars_nouveau.pathing")), getPath(AUTOMATION, "starbuncle_charm"));

        addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.DRYGMY_CHARM)
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.DRYGMY_CHARM))
                .withPage(new EntityPage(ModEntities.ENTITY_DRYGMY.getRegistryName().toString())
                        .withText(getLangPath("drygmy_charm", 2)))
                .withPage(new TextPage(getLangPath("drygmy_charm", 3)).withTitle("ars_nouveau.summoning"))
                .withPage(new TextPage(getLangPath("drygmy_charm", 4)).withTitle("ars_nouveau.happiness"))
                .withPage(new TextPage(getLangPath("drygmy_charm", 5)).withTitle("ars_nouveau.production")), getPath(AUTOMATION, "drygmy_charm"));

        addPage(new PatchouliBuilder(EQUIPMENT, ItemsRegistry.DULL_TRINKET)
                .withPage(new CraftingPage(ItemsRegistry.DULL_TRINKET).withRecipe2(ItemsRegistry.MUNDANE_BELT))
                .withPage(new CraftingPage(ItemsRegistry.RING_OF_POTENTIAL)), getPath(EQUIPMENT, "dull_items"));
        addBasicItem(BlockRegistry.MAGE_BLOOM_CROP, RESOURCES, new ApparatusPage(BlockRegistry.MAGE_BLOOM_CROP));

        addPage(new PatchouliBuilder(EQUIPMENT, ItemsRegistry.POTION_FLASK)
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.POTION_FLASK))
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.POTION_FLASK_EXTEND_TIME))
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.POTION_FLASK_AMPLIFY)), getPath(EQUIPMENT, "potion_flask"));

        addPage(new PatchouliBuilder(EQUIPMENT, "reactive_enchantment")
                .withIcon(Items.ENCHANTED_BOOK)
                .withLocalizedText()
                .withPage(new EnchantingPage("ars_nouveau:" + EnchantmentRegistry.REACTIVE_ENCHANTMENT.getRegistryName().getPath() + "_" + 1))
                .withLocalizedText()
                .withPage(new EnchantingPage("ars_nouveau:" + EnchantmentRegistry.REACTIVE_ENCHANTMENT.getRegistryName().getPath() + "_" + 2))
                .withPage(new EnchantingPage("ars_nouveau:" + EnchantmentRegistry.REACTIVE_ENCHANTMENT.getRegistryName().getPath() + "_" + 3))
                .withPage(new EnchantingPage("ars_nouveau:" + EnchantmentRegistry.REACTIVE_ENCHANTMENT.getRegistryName().getPath() + "_" + 4))
                .withLocalizedText()
                .withPage(new ApparatusPage("ars_nouveau:spell_write")), getPath(EQUIPMENT, "reactive_enchantment"));

        addBasicItem(ItemsRegistry.RING_OF_GREATER_DISCOUNT, EQUIPMENT, new ApparatusPage(ItemsRegistry.RING_OF_GREATER_DISCOUNT));
        addBasicItem(ItemsRegistry.RING_OF_LESSER_DISCOUNT, EQUIPMENT, new ApparatusPage(ItemsRegistry.RING_OF_LESSER_DISCOUNT));

        addPage(new PatchouliBuilder(AUTOMATION, BlockRegistry.BASIC_SPELL_TURRET)
                .withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.BASIC_SPELL_TURRET))
                .withLocalizedText()
                .withPage(new ApparatusPage(BlockRegistry.ENCHANTED_SPELL_TURRET))
                .withLocalizedText()
                .withPage(new ApparatusPage(BlockRegistry.TIMER_SPELL_TURRET))
                .withLocalizedText(), getPath(AUTOMATION, "spell_turret"));

        addBasicItem(ItemsRegistry.SUMMONING_FOCUS, EQUIPMENT, new ApparatusPage(ItemsRegistry.SUMMONING_FOCUS));
        addBasicItem(ItemsRegistry.VOID_JAR, EQUIPMENT, new ApparatusPage(ItemsRegistry.VOID_JAR));
        addBasicItem(ItemsRegistry.WAND, EQUIPMENT, new ApparatusPage(ItemsRegistry.WAND));
        addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.WHIRLISPRIG_CHARM)
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.WHIRLISPRIG_CHARM))
                .withPage(new EntityPage(ModEntities.WHIRLISPRIG_TYPE.getRegistryName().toString())
                        .withText(getLangPath("whirlisprig_charm", 2)))
                .withPage(new TextPage(getLangPath("whirlisprig_charm", 3)).withTitle("ars_nouveau.summoning"))
                .withPage(new TextPage(getLangPath("whirlisprig_charm", 4)).withTitle("ars_nouveau.happiness"))
                .withPage(new TextPage(getLangPath("whirlisprig_charm", 5)).withTitle("ars_nouveau.important"))
                .withPage(new TextPage(getLangPath("whirlisprig_charm", 6)).withTitle("ars_nouveau.production")), getPath(AUTOMATION, "whirlisprig_charm"));
        addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.BOOKWYRM_CHARM)
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.BOOKWYRM_CHARM))
                .withPage(new EntityPage(ModEntities.ENTITY_BOOKWYRM_TYPE.getRegistryName().toString())
                        .withText(getLangPath("bookwyrm_charm", 2)))
                .withPage(new TextPage(getLangPath("bookwyrm_charm", 3)))
                .withPage(new TextPage(getLangPath("bookwyrm_charm", 4)))
                .withPage(new TextPage(getLangPath("bookwyrm_charm", 5))), getPath(AUTOMATION, "bookwyrm_charm"));

        addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.WIXIE_CHARM)
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.WIXIE_CHARM))
                .withPage(new EntityPage(ModEntities.ENTITY_WIXIE_TYPE.getRegistryName().toString())
                        .withText(getLangPath("wixie_charm", 2)))
                .withPage(new TextPage(getLangPath("wixie_charm", 3)).withTitle("ars_nouveau.item_crafting"))
                .withPage(new TextPage(getLangPath("wixie_charm", 4)).withTitle("ars_nouveau.potion_crafting"))
                .withPage(new TextPage(getLangPath("wixie_charm", 5))), getPath(AUTOMATION, "wixie_charm"));

        addPage(new PatchouliBuilder(RESOURCES, "archwood")
                .withIcon(BlockRegistry.CASCADING_SAPLING)
                .withLocalizedText(), getPath(RESOURCES, "archwood"));

        addPage(new PatchouliBuilder(RESOURCES, "decorative")
                .withIcon(BlockRegistry.ARCANE_BRICKS)
                .withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.ARCANE_STONE).withRecipe2(BlockRegistry.AS_GOLD_STONE)), getPath(RESOURCES, "decorative"));

        addPage(new PatchouliBuilder(RESOURCES, BlockRegistry.LAVA_LILY)
                .withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.LAVA_LILY)), getPath(RESOURCES, "lava_lily"));

        addPage(new PatchouliBuilder(RESOURCES, BlockRegistry.SOURCEBERRY_BUSH)
                .withLocalizedText()
                .withPage(new CraftingPage(ItemsRegistry.SOURCE_BERRY_PIE).withRecipe2(ItemsRegistry.SOURCE_BERRY_ROLL)), getPath(RESOURCES,  "sourceberry"));

        addPage(new PatchouliBuilder(RESOURCES, "weald_walker")
                .withIcon(ArsNouveauAPI.getInstance().getRitualItemMap().get(RitualLib.AWAKENING))
                .withLocalizedText()
                .withPage(new EntityPage(ModEntities.ENTITY_BLAZING_WEALD.getRegistryName().toString()).withText(getLangPath("weald_walker", 2)))
                .withPage(new EntityPage(ModEntities.ENTITY_CASCADING_WEALD.getRegistryName().toString()).withText(getLangPath("weald_walker", 3)))
                .withPage(new EntityPage(ModEntities.ENTITY_FLOURISHING_WEALD.getRegistryName().toString()).withText(getLangPath("weald_walker", 4)))
                .withPage(new EntityPage(ModEntities.ENTITY_VEXING_WEALD.getRegistryName().toString()).withText(getLangPath("weald_walker", 5))), getPath(RESOURCES, "weald_walker"));

        addPage(new PatchouliBuilder(RESOURCES, "wilden")
                .withIcon(ItemsRegistry.WILDEN_SPIKE)
                .withLocalizedText()
                .withPage(new EntityPage(ModEntities.WILDEN_HUNTER.getRegistryName().toString()).withText(getLangPath("wilden", 3)))
                .withPage(new EntityPage(ModEntities.WILDEN_STALKER.getRegistryName().toString()).withText(getLangPath("wilden", 4)))
                .withPage(new EntityPage(ModEntities.WILDEN_GUARDIAN.getRegistryName().toString()).withText(getLangPath("wilden", 5)))
                .withPage(new EntityPage(ModEntities.WILDEN_BOSS.getRegistryName().toString()))
                .withPage(new TextPage(getLangPath("wilden", 6))), getPath(RESOURCES, "wilden"));

        addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.DENY_ITEM_SCROLL)
                .withLocalizedText()
                .withPage(new CraftingPage(ItemsRegistry.BLANK_PARCHMENT).withRecipe2(ItemsRegistry.DENY_ITEM_SCROLL)), getPath(AUTOMATION, "deny_scroll"));

        addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.MIMIC_ITEM_SCROLL)
                .withLocalizedText()
                .withPage(new CraftingPage(ItemsRegistry.BLANK_PARCHMENT).withRecipe2(ItemsRegistry.MIMIC_ITEM_SCROLL)), getPath(AUTOMATION, "mimic_scroll"));
        addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.ALLOW_ITEM_SCROLL)
                .withLocalizedText()
                .withPage(new CraftingPage(ItemsRegistry.BLANK_PARCHMENT).withRecipe2(ItemsRegistry.ALLOW_ITEM_SCROLL)), getPath(AUTOMATION, "allow_scroll"));

        addBasicItem(ItemsRegistry.DOMINION_ROD, AUTOMATION, new ApparatusPage(ItemsRegistry.DOMINION_ROD));
        addBasicItem(BlockRegistry.SPELL_PRISM, AUTOMATION, new CraftingPage(BlockRegistry.SPELL_PRISM));
        addBasicItem(BlockRegistry.SCONCE_BLOCK, RESOURCES, new CraftingPage(BlockRegistry.SCONCE_BLOCK));

        addPage(new PatchouliBuilder(EQUIPMENT, "armor")
                .withIcon(ItemsRegistry.NOVICE_ROBES)
                .withLocalizedText()
                .withPage(new CraftingPage(ItemsRegistry.MAGE_FIBER).withRecipe2(ItemsRegistry.NOVICE_HOOD))
                .withPage(new CraftingPage(ItemsRegistry.NOVICE_ROBES).withRecipe2(ItemsRegistry.NOVICE_LEGGINGS))
                .withPage(new CraftingPage(ItemsRegistry.NOVICE_BOOTS).withRecipe2(ItemsRegistry.BLAZE_FIBER))
                .withPage(new CraftingPage(ItemsRegistry.APPRENTICE_HOOD).withRecipe2(ItemsRegistry.APPRENTICE_ROBES))
                .withPage(new CraftingPage(ItemsRegistry.APPRENTICE_LEGGINGS).withRecipe2(ItemsRegistry.APPRENTICE_BOOTS))
                .withPage(new CraftingPage(ItemsRegistry.END_FIBER).withRecipe2(ItemsRegistry.ARCHMAGE_HOOD))
                .withPage(new CraftingPage(ItemsRegistry.ARCHMAGE_ROBES).withRecipe2(ItemsRegistry.ARCHMAGE_LEGGINGS))
                .withPage(new CraftingPage(ItemsRegistry.ARCHMAGE_BOOTS)), getPath(EQUIPMENT, "armor"));

        addPage(new PatchouliBuilder(EQUIPMENT, "spell_books")
                .withIcon(ItemsRegistry.ARCHMAGE_SPELLBOOK)
                .withLocalizedText()
                .withPage(new CraftingPage(ItemsRegistry.NOVICE_SPELLBOOK).withRecipe2("ars_nouveau:apprentice_spell_book_upgrade"))
                .withPage(new CraftingPage("ars_nouveau:archmage_spell_book_upgrade")), getPath(EQUIPMENT, "spell_books"));

        addBasicItem(ItemsRegistry.ENCHANTERS_MIRROR, EQUIPMENT, new ApparatusPage(ItemsRegistry.ENCHANTERS_MIRROR));
        addBasicItem(ItemsRegistry.ENCHANTERS_SHIELD, EQUIPMENT, new ApparatusPage(ItemsRegistry.ENCHANTERS_SHIELD));
        addBasicItem(ItemsRegistry.ENCHANTERS_SWORD, EQUIPMENT, new ApparatusPage(ItemsRegistry.ENCHANTERS_SWORD));
        addBasicItem(ItemsRegistry.SPELL_BOW, EQUIPMENT, new ApparatusPage(ItemsRegistry.SPELL_BOW));
        addBasicItem(ItemsRegistry.RUNIC_CHALK, EQUIPMENT, new CraftingPage(ItemsRegistry.RUNIC_CHALK));
        addBasicItem(ItemsRegistry.WARP_SCROLL, EQUIPMENT, new CraftingPage(ItemsRegistry.WARP_SCROLL));
        addBasicItem(BlockRegistry.IMBUEMENT_BLOCK, MACHINES, new CraftingPage(BlockRegistry.IMBUEMENT_BLOCK));
        addPage(new PatchouliBuilder(MACHINES, BlockRegistry.ENCHANTING_APP_BLOCK)
                .withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.ARCANE_PEDESTAL).withRecipe2(BlockRegistry.ARCANE_CORE_BLOCK))
                .withPage(new CraftingPage(BlockRegistry.ENCHANTING_APP_BLOCK)), getPath(MACHINES, "enchanting_apparatus"));
        addPage(new PatchouliBuilder(MACHINES, BlockRegistry.GLYPH_PRESS_BLOCK)
                .withLocalizedText()
                .withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.GLYPH_PRESS_BLOCK).withRecipe2(ItemsRegistry.MAGIC_CLAY))
                .withPage(new CraftingPage(ItemsRegistry.MARVELOUS_CLAY).withRecipe2(ItemsRegistry.MYTHICAL_CLAY)), getPath(MACHINES, "glyph_press"));

        addBasicItem(BlockRegistry.POTION_JAR, MACHINES, new CraftingPage(BlockRegistry.POTION_JAR));

        addPage(new PatchouliBuilder(MACHINES, BlockRegistry.POTION_MELDER)
                .withLocalizedText()
                .withPage(new ApparatusPage(BlockRegistry.POTION_MELDER))
                .withPage(new MultiblockPage(BlockRegistry.POTION_MELDER.getDescriptionId(),new String[][]{
                        new String[]{"   "},
                        new String[]{"C0C"},
                        new String[]{" C "}
                }).withMapping("C", "ars_nouveau:potion_jar").withMapping("0","ars_nouveau:potion_melder")
                        .withText(getLangPath("potion_melder", 2))), getPath(MACHINES, "potion_melder"));

        addBasicItem(BlockRegistry.RITUAL_BLOCK, MACHINES, new CraftingPage(BlockRegistry.RITUAL_BLOCK));
        addPage(new PatchouliBuilder(MACHINES, BlockRegistry.SCRIBES_BLOCK)
                .withLocalizedText()
                .withLocalizedText()
                .withPage(new CraftingPage(ItemsRegistry.SPELL_PARCHMENT).withRecipe2(BlockRegistry.SCRIBES_BLOCK)), getPath(MACHINES, "scribes_block"));

        addPage(new PatchouliBuilder(MACHINES, "warp_portal")
                .withIcon(ItemsRegistry.WARP_SCROLL)
                .withLocalizedText()
                .withLocalizedText()
                .withPage(new MultiblockPage(getLangPath("warp_portal",3),new String[][]{
                        new String[]{" BB "},
                        new String[]{"BPPB"},
                        new String[]{"B0PB"},
                        new String[]{"BPPB"},
                        new String[]{" BB "}
                }).withMapping("B", "ars_nouveau:arcane_bricks").withMapping("P", "ars_nouveau:portal")
                        .withMapping("0", "ars_nouveau:portal").withText(getLangPath("warp_portal", 4))), getPath(MACHINES, "warp_portal"));

        addBasicItem(BlockRegistry.AGRONOMIC_SOURCELINK, SOURCE, new CraftingPage(BlockRegistry.AGRONOMIC_SOURCELINK));
        addBasicItem(BlockRegistry.ALCHEMICAL_BLOCK, SOURCE, new CraftingPage(BlockRegistry.ALCHEMICAL_BLOCK));
        addBasicItem(BlockRegistry.MYCELIAL_BLOCK, SOURCE, new CraftingPage(BlockRegistry.MYCELIAL_BLOCK));
        addBasicItem(BlockRegistry.SOURCE_JAR, SOURCE, new CraftingPage(BlockRegistry.SOURCE_JAR));

        addBasicItem(BlockRegistry.RELAY, SOURCE, new CraftingPage(BlockRegistry.RELAY));

        addBasicItem(BlockRegistry.RELAY_DEPOSIT, SOURCE, new ApparatusPage(BlockRegistry.RELAY_DEPOSIT));
        addBasicItem(BlockRegistry.RELAY_SPLITTER, SOURCE, new ApparatusPage(BlockRegistry.RELAY_SPLITTER));
        addBasicItem(BlockRegistry.RELAY_WARP, SOURCE, new ApparatusPage(BlockRegistry.RELAY_WARP));
        addBasicItem(BlockRegistry.VITALIC_BLOCK, SOURCE, new CraftingPage(BlockRegistry.VITALIC_BLOCK));
        addPage(new PatchouliBuilder(SOURCE, BlockRegistry.VOLCANIC_BLOCK)
                .withLocalizedText()
                .withPage(new TextPage(getLangPath("volcanic_sourcelink", 2)).withTitle("ars_nouveau.active_generation"))
                .withPage(new TextPage(getLangPath("volcanic_sourcelink", 3)).withTitle("ars_nouveau.heat"))
                .withPage(new CraftingPage(BlockRegistry.VOLCANIC_BLOCK).withRecipe2(BlockRegistry.LAVA_LILY))
                .withPage(new TextPage(getLangPath("volcanic_sourcelink", 4))), getPath(SOURCE, "volcanic_sourcelink"));
        addPage(new PatchouliBuilder(ENCHANTMENTS, "how_to_enchant")
                .withIcon(BlockRegistry.ENCHANTING_APP_BLOCK)
                .withSortNum(-1)
                .withLocalizedText()
                .withLocalizedText()
                .withPage(new RelationsPage().withEntry(MACHINES, "enchanting_apparatus")), getPath(ENCHANTMENTS,"how_to_enchant"));
        addPage(new PatchouliBuilder(RITUALS, "performing_rituals")
                .withSortNum(-1)
                .withIcon(BlockRegistry.RITUAL_BLOCK)
                .withLocalizedText()
                .withLocalizedText()
                .withPage(new CraftingPage(ArsNouveauAPI.getInstance().getRitualItemMap().get(RitualLib.SUNRISE)))
                .withPage(new RelationsPage().withEntry(MACHINES, "ritual_brazier")), getPath(RITUALS,"performing_rituals"));
        addPage(new PatchouliBuilder(FAMILIARS, "summoning_familiars")
                .withSortNum(-1)
                .withIcon(ArsNouveauAPI.getInstance().getRitualItemMap().get(RitualLib.BINDING))
                .withLocalizedText()
                .withLocalizedText()
                .withPage(new CraftingPage(ArsNouveauAPI.getInstance().getRitualItemMap().get(RitualLib.BINDING)))
                .withPage(new RelationsPage().withEntry(MACHINES, "ritual_brazier").withEntry(RITUALS, "binding")), getPath(FAMILIARS,"summoning_familiars"));

        addPage(new PatchouliBuilder(MOD_NEWS, "mod_news")
                .withIcon(ItemsRegistry.SPELL_PARCHMENT)
                .withPage(new LinkPage("https://discord.gg/y7TMXZu", "ars_nouveau.discord_text", "ars_nouveau.community"))
                .withPage(new LinkPage("https://www.redbubble.com/people/Gootastic/explore?page=1&sortOrder=recent", "ars_nouveau.store_text", "ars_nouveau.store")), getPath(MOD_NEWS, "mod_news"));

    }

    public String getLangPath(String name, int count){
        return "ars_nouveau.page" + count + "." + name;
    }

    public String getLangPath(String name){
        return "ars_nouveau.page." + name;
    }

    public void addPage(PatchouliBuilder builder, Path path){
        this.pages.add(new PatchouliPage(builder, path));
    }

    public void addBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage){
        PatchouliBuilder builder = new PatchouliBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withPage(new TextPage("ars_nouveau.page." + item.asItem().getRegistryName().getPath()))
                .withPage(recipePage);
        this.pages.add(new PatchouliPage(builder, getPath(category, item.asItem().getRegistryName())));
    }

    public Path getPath(ResourceLocation category, ResourceLocation fileName){
        return this.generator.getOutputFolder().resolve("data/ars_nouveau/patchouli/" + category.getPath() +"/" + fileName.getPath() + ".json");
    }

    public Path getPath(ResourceLocation category, String fileName){
        return this.generator.getOutputFolder().resolve("data/ars_nouveau/patchouli/" + category.getPath() +"/" + fileName + ".json");
    }
    @Override
    public void run(HashCache cache) throws IOException {
        addEntries();
        for(PatchouliPage patchouliPage : pages){
            DataProvider.save(GSON, cache, patchouliPage.builder.build(), patchouliPage.path);
        }
    }

    public static record PatchouliPage(PatchouliBuilder builder, Path path) { }

    public void addGlyphPage(AbstractSpellPart spellPart){
        ResourceLocation category = null;
        if(spellPart.getTier() == ISpellTier.Tier.ONE)
            category = GLYPHS_1;
        else if(spellPart.getTier() == ISpellTier.Tier.TWO)
            category = GLYPHS_2;
        else
            category = GLYPHS_3;
        PatchouliBuilder builder = new PatchouliBuilder(category, spellPart.getName())
                .withName("ars_nouveau.glyph_name." + spellPart.getTag())
                .withIcon(ArsNouveau.MODID + ":" + spellPart.getItemID())
                .withSortNum(spellPart instanceof AbstractCastMethod ? 1 : spellPart instanceof AbstractEffect ? 2 : 3)
                .withPage(new TextPage("ars_nouveau.glyph_desc." + spellPart.tag))
                .withPage(new GlyphPressPage(spellPart));
        this.pages.add(new PatchouliPage(builder, getPath(category, "glyph_" + spellPart.tag)));
    }

    public void addFamiliarPage(AbstractFamiliarHolder familiarHolder){
        PatchouliBuilder builder = new PatchouliBuilder(FAMILIARS, "entity.ars_nouveau." + familiarHolder.getId())
                .withIcon("ars_nouveau:familiar_" + familiarHolder.getId())
                .withTextPage("ars_nouveau.familiar_desc." + familiarHolder.getId())
                .withPage(new EntityPage(new ResourceLocation(ArsNouveau.MODID, familiarHolder.getEntityKey()).toString()));
        this.pages.add(new PatchouliPage(builder, this.generator.getOutputFolder().resolve("data/ars_nouveau/patchouli/familiars/" + familiarHolder.getId() + ".json")));
    }

    public void addRitualPage(AbstractRitual ritual){
        PatchouliBuilder builder = new PatchouliBuilder(RITUALS, "item.ars_nouveau.ritual_" + ritual.getID())
                .withIcon("ars_nouveau:ritual_" + ritual.getID())
                .withTextPage("ars_nouveau.ritual_desc." + ritual.getID())
                .withPage(new CraftingPage("ars_nouveau:ritual_" + ritual.getID()));

        this.pages.add(new PatchouliPage(builder,this.generator.getOutputFolder().resolve("data/ars_nouveau/patchouli/rituals/" + ritual.getID() + ".json")));
    }

    public void addEnchantmentPage(Enchantment enchantment){
        PatchouliBuilder builder = new PatchouliBuilder(ENCHANTMENTS, enchantment.getDescriptionId())
                .withIcon(Items.ENCHANTED_BOOK.getRegistryName().toString());
        for(int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++){
            builder.withPage(new EnchantingPage("ars_nouveau:" + enchantment.getRegistryName().getPath() + "_" + i));
        }
        this.pages.add(new PatchouliPage(builder,  this.generator.getOutputFolder().resolve("data/ars_nouveau/patchouli/enchantments/" +  enchantment.getRegistryName().getPath() + ".json")));
    }

    List<Enchantment> enchants = Arrays.asList(
            Enchantments.AQUA_AFFINITY,
            Enchantments.BANE_OF_ARTHROPODS,
            Enchantments.BLAST_PROTECTION,
            Enchantments.DEPTH_STRIDER,
            Enchantments.BLOCK_EFFICIENCY,
            Enchantments.FALL_PROTECTION,
            Enchantments.FIRE_ASPECT,
            Enchantments.FIRE_PROTECTION,
            Enchantments.FLAMING_ARROWS,
            Enchantments.BLOCK_FORTUNE,
            Enchantments.INFINITY_ARROWS,
            Enchantments.KNOCKBACK,
            Enchantments.MOB_LOOTING,
            Enchantments.MULTISHOT,
            Enchantments.PIERCING,
            Enchantments.POWER_ARROWS,
            Enchantments.PROJECTILE_PROTECTION,
            Enchantments.ALL_DAMAGE_PROTECTION,
            Enchantments.PUNCH_ARROWS,
            Enchantments.QUICK_CHARGE,
            Enchantments.RESPIRATION,
            Enchantments.SHARPNESS,
            Enchantments.SILK_TOUCH,
            Enchantments.SMITE,
            Enchantments.SWEEPING_EDGE,
            Enchantments.THORNS,
            Enchantments.UNBREAKING,
            EnchantmentRegistry.MANA_BOOST_ENCHANTMENT,
            EnchantmentRegistry.MANA_REGEN_ENCHANTMENT
    );

    @Override
    public String getName() {
        return "Patchouli";
    }
}
