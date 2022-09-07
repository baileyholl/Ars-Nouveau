package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.*;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hollingsworth.arsnouveau.api.RegistryHelper.getRegistryName;

public class PatchouliProvider implements DataProvider {
    protected final DataGenerator generator;
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
    public static ResourceLocation ARMOR = new ResourceLocation(ArsNouveau.MODID, "armor");

    public List<PatchouliPage> pages = new ArrayList<>();

    public PatchouliProvider(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    public void addEntries() {
        Block SOURCESTONE = BlockRegistry.getBlock(LibBlockNames.SOURCESTONE);
        for (Enchantment g : enchants) {
            addEnchantmentPage(g);
        }
        for (AbstractRitual r : ArsNouveauAPI.getInstance().getRitualMap().values()) {
            addRitualPage(r);
        }

        for (AbstractFamiliarHolder r : ArsNouveauAPI.getInstance().getFamiliarHolderMap().values()) {
            addFamiliarPage(r);
        }

        for (AbstractSpellPart s : ArsNouveauAPI.getInstance().getSpellpartMap().values()) {
            addGlyphPage(s);
        }

        for (IPerk perk : ArsNouveauAPI.getInstance().getPerkMap().values()) {
            addPerkPage(perk);
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
                .withPage(new RelationsPage().withEntry(GETTING_STARTED, "source").withEntry(EQUIPMENT, "dowsing_rod")), getPath(GETTING_STARTED, "obtaining_gems"));

        addPage(new PatchouliBuilder(GETTING_STARTED, "new_glyphs")
                .withIcon(ItemsRegistry.BLANK_GLYPH)
                .withSortNum(4)
                .withLocalizedText()
                .withPage(new RelationsPage().withEntry(MACHINES, "scribes_block").withEntry(GETTING_STARTED, "source")), getPath(GETTING_STARTED, "new_glyphs"));

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
                        .withPage(new RelationsPage().withEntry(MACHINES, "imbuement_chamber").withEntry(RESOURCES, "archwood").withEntry(RESOURCES, "sourceberry")),
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
                        .withEntry(EQUIPMENT, "void_jar")
                        .withEntry(EQUIPMENT, "warp_scroll")), getPath(GETTING_STARTED, "trinkets"));

        addBasicItem(ItemsRegistry.AMULET_OF_MANA_BOOST, EQUIPMENT, new ApparatusPage(ItemsRegistry.AMULET_OF_MANA_BOOST));
        addBasicItem(ItemsRegistry.AMULET_OF_MANA_REGEN, EQUIPMENT, new ApparatusPage(ItemsRegistry.AMULET_OF_MANA_REGEN));
        addBasicItem(ItemsRegistry.BELT_OF_LEVITATION, EQUIPMENT, new ApparatusPage(ItemsRegistry.BELT_OF_LEVITATION));
        addBasicItem(ItemsRegistry.BELT_OF_UNSTABLE_GIFTS, EQUIPMENT, new ApparatusPage(ItemsRegistry.BELT_OF_UNSTABLE_GIFTS));
        addBasicItem(ItemsRegistry.JAR_OF_LIGHT, EQUIPMENT, new ApparatusPage(ItemsRegistry.JAR_OF_LIGHT));
        addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.STARBUNCLE_CHARM)
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.STARBUNCLE_CHARM))
                .withPage(new EntityPage(getRegistryName(ModEntities.STARBUNCLE_TYPE.get()).toString())
                        .withText(getLangPath("starbuncle_charm", 2)))
                .withPage(new TextPage(getLangPath("starbuncle_charm", 3)).withTitle("ars_nouveau.summoning"))
                .withPage(new TextPage(getLangPath("starbuncle_charm", 4)).withTitle("ars_nouveau.item_transport"))
                .withPage(new TextPage(getLangPath("starbuncle_charm", 5)).withTitle("ars_nouveau.filtering"))
                .withPage(new TextPage(getLangPath("starbuncle_charm", 6)).withTitle("ars_nouveau.pathing"))
                .withPage(new TextPage(getLangPath("starbuncle_charm", 7)).withTitle("ars_nouveau.starbuncle_bed")), getPath(AUTOMATION, "starbuncle_charm"));

        addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.DRYGMY_CHARM)
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.DRYGMY_CHARM))
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_DRYGMY.get()).toString())
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
                .withPage(new EnchantingPage("ars_nouveau:" + getRegistryName(EnchantmentRegistry.REACTIVE_ENCHANTMENT.get()).getPath() + "_" + 1))
                .withLocalizedText()
                .withPage(new EnchantingPage("ars_nouveau:" + getRegistryName(EnchantmentRegistry.REACTIVE_ENCHANTMENT.get()).getPath() + "_" + 2))
                .withPage(new EnchantingPage("ars_nouveau:" + getRegistryName(EnchantmentRegistry.REACTIVE_ENCHANTMENT.get()).getPath() + "_" + 3))
                .withPage(new EnchantingPage("ars_nouveau:" + getRegistryName(EnchantmentRegistry.REACTIVE_ENCHANTMENT.get()).getPath() + "_" + 4))
                .withLocalizedText()
                .withPage(new ApparatusTextPage("ars_nouveau:spell_write")), getPath(EQUIPMENT, "reactive_enchantment"));

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
                .withPage(new EntityPage(getRegistryName(ModEntities.WHIRLISPRIG_TYPE.get()).toString())
                        .withText(getLangPath("whirlisprig_charm", 2)))
                .withPage(new TextPage(getLangPath("whirlisprig_charm", 3)).withTitle("ars_nouveau.summoning"))
                .withPage(new TextPage(getLangPath("whirlisprig_charm", 4)).withTitle("ars_nouveau.happiness"))
                .withPage(new TextPage(getLangPath("whirlisprig_charm", 5)).withTitle("ars_nouveau.important"))
                .withPage(new TextPage(getLangPath("whirlisprig_charm", 6)).withTitle("ars_nouveau.production")), getPath(AUTOMATION, "whirlisprig_charm"));

        addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.WIXIE_CHARM)
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.WIXIE_CHARM))
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_WIXIE_TYPE.get()).toString())
                        .withText(getLangPath("wixie_charm", 2)))
                .withPage(new TextPage(getLangPath("wixie_charm", 3)).withTitle("ars_nouveau.item_crafting"))
                .withPage(new TextPage(getLangPath("wixie_charm", 4)).withTitle("ars_nouveau.potion_crafting"))
                .withPage(new TextPage(getLangPath("wixie_charm", 5))), getPath(AUTOMATION, "wixie_charm"));

        addPage(new PatchouliBuilder(RESOURCES, "archwood")
                .withIcon(BlockRegistry.BOMBEGRANTE_POD)
                .withLocalizedText()
                .withPage(new SpotlightPage(BlockRegistry.BOMBEGRANTE_POD).withText("ars_nouveau.page.bombegrante").linkRecipe(true))
                        .withPage(new SpotlightPage(BlockRegistry.MENDOSTEEN_POD).withText("ars_nouveau.page.mendosteen").linkRecipe(true))
                        .withPage(new SpotlightPage(BlockRegistry.FROSTAYA_POD).withText("ars_nouveau.page.frostaya").linkRecipe(true))
                        .withPage(new SpotlightPage(BlockRegistry.BASTION_POD).withText("ars_nouveau.page.bastion_fruit").linkRecipe(true))
                , getPath(RESOURCES, "archwood"));

        addPage(new PatchouliBuilder(RESOURCES, "archwood_forest")
                .withIcon(BlockRegistry.BLAZING_SAPLING)
                .withLocalizedText(), getPath(RESOURCES, "archwood_forest"));


        addPage(new PatchouliBuilder(RESOURCES, "decorative")
                .withIcon(SOURCESTONE)
                .withPage(new CraftingPage(SOURCESTONE))
                .withLocalizedText(), getPath(RESOURCES, "decorative"));

        addPage(new PatchouliBuilder(RESOURCES, BlockRegistry.LAVA_LILY)
                .withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.LAVA_LILY)), getPath(RESOURCES, "lava_lily"));

        addPage(new PatchouliBuilder(RESOURCES, BlockRegistry.SOURCEBERRY_BUSH)
                .withLocalizedText()
                .withPage(new CraftingPage(ItemsRegistry.SOURCE_BERRY_PIE).withRecipe2(ItemsRegistry.SOURCE_BERRY_ROLL)), getPath(RESOURCES, "sourceberry"));

        addPage(new PatchouliBuilder(RESOURCES, "weald_walker")
                .withIcon(ArsNouveauAPI.getInstance().getRitualItemMap().get(new ResourceLocation(ArsNouveau.MODID, RitualLib.AWAKENING)))
                .withLocalizedText()
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_BLAZING_WEALD.get()).toString()).withText(getLangPath("weald_walker", 2)))
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_CASCADING_WEALD.get()).toString()).withText(getLangPath("weald_walker", 3)))
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_FLOURISHING_WEALD.get()).toString()).withText(getLangPath("weald_walker", 4)))
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_VEXING_WEALD.get()).toString()).withText(getLangPath("weald_walker", 5))), getPath(RESOURCES, "weald_walker"));

        addPage(new PatchouliBuilder(RESOURCES, "wilden")
                .withIcon(ItemsRegistry.WILDEN_SPIKE)
                .withLocalizedText()
                .withPage(new EntityPage(getRegistryName(ModEntities.WILDEN_HUNTER.get()).toString()).withText(getLangPath("wilden", 3)))
                .withPage(new EntityPage(getRegistryName(ModEntities.WILDEN_STALKER.get()).toString()).withText(getLangPath("wilden", 4)))
                .withPage(new EntityPage(getRegistryName(ModEntities.WILDEN_GUARDIAN.get()).toString()).withText(getLangPath("wilden", 5)))
                .withPage(new EntityPage(getRegistryName(ModEntities.WILDEN_BOSS.get()).toString()).withText(getLangPath("wilden", 6)))
                .withPage(new TextPage(getLangPath("wilden", 7))), getPath(RESOURCES, "wilden"));

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
        addPage(new PatchouliBuilder(MACHINES, BlockRegistry.IMBUEMENT_BLOCK)
                        .withLocalizedText()
                        .withPage(new CraftingPage(BlockRegistry.IMBUEMENT_BLOCK))
                        .withPage(new ImbuementPage("ars_nouveau:imbuement_lapis"))
                        .withPage(new ImbuementPage("ars_nouveau:imbuement_amethyst"))
                        .withPage(new ImbuementPage("ars_nouveau:imbuement_amethyst_block"))
                        .withPage(new ImbuementPage("ars_nouveau:imbuement_" + ItemsRegistry.FIRE_ESSENCE.getRegistryName()))
                        .withPage(new ImbuementPage("ars_nouveau:imbuement_" + ItemsRegistry.EARTH_ESSENCE.getRegistryName()))
                        .withPage(new ImbuementPage("ars_nouveau:imbuement_" + ItemsRegistry.WATER_ESSENCE.getRegistryName()))
                        .withPage(new ImbuementPage("ars_nouveau:imbuement_" + ItemsRegistry.AIR_ESSENCE.getRegistryName()))
                        .withPage(new ImbuementPage("ars_nouveau:imbuement_" + ItemsRegistry.ABJURATION_ESSENCE.getRegistryName()))
                        .withPage(new ImbuementPage("ars_nouveau:imbuement_" + ItemsRegistry.CONJURATION_ESSENCE.getRegistryName()))
                        .withPage(new ImbuementPage("ars_nouveau:imbuement_" + ItemsRegistry.MANIPULATION_ESSENCE.getRegistryName()))
                , getPath(MACHINES, "imbuement_chamber"));

        addPage(new PatchouliBuilder(MACHINES, BlockRegistry.ENCHANTING_APP_BLOCK)
                .withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.ARCANE_PEDESTAL).withRecipe2(BlockRegistry.ARCANE_CORE_BLOCK))
                .withPage(new CraftingPage(BlockRegistry.ENCHANTING_APP_BLOCK)), getPath(MACHINES, "enchanting_apparatus"));

        addBasicItem(BlockRegistry.POTION_JAR, MACHINES, new CraftingPage(BlockRegistry.POTION_JAR));
        addBasicItem(BlockRegistry.POTION_MELDER, MACHINES, new ApparatusPage(BlockRegistry.POTION_MELDER));
        addBasicItem(BlockRegistry.POTION_DIFFUSER, MACHINES, new ApparatusPage(BlockRegistry.POTION_DIFFUSER));
        addBasicItem(BlockRegistry.RITUAL_BLOCK, MACHINES, new CraftingPage(BlockRegistry.RITUAL_BLOCK));
        addPage(new PatchouliBuilder(MACHINES, BlockRegistry.SCRIBES_BLOCK)
                .withPage(new TextPage(getLangPath("scribes_table", 1)).withTitle("ars_nouveau.glyph_crafting"))
                .withPage(new TextPage(getLangPath("scribes_table", 2)).withTitle("ars_nouveau.scribing"))
                .withPage(new CraftingPage(ItemsRegistry.SPELL_PARCHMENT).withRecipe2(BlockRegistry.SCRIBES_BLOCK)), getPath(MACHINES, "scribes_block"));

        addPage(new PatchouliBuilder(MACHINES, "warp_portal")
                .withIcon(ItemsRegistry.WARP_SCROLL)
                .withLocalizedText()
                .withLocalizedText()
                .withPage(new MultiblockPage(getLangPath("warp_portal", 3), new String[][]{
                        new String[]{" BB "},
                        new String[]{"BPPB"},
                        new String[]{"B0PB"},
                        new String[]{"BPPB"},
                        new String[]{" BB "}
                }).withMapping("B", "ars_nouveau:sourcestone").withMapping("P", "ars_nouveau:portal")
                        .withMapping("0", "ars_nouveau:portal").withText(getLangPath("warp_portal", 4))), getPath(MACHINES, "warp_portal"));

        addBasicItem(BlockRegistry.AGRONOMIC_SOURCELINK, SOURCE, new CraftingPage(BlockRegistry.AGRONOMIC_SOURCELINK));
        addBasicItem(BlockRegistry.ALCHEMICAL_BLOCK, SOURCE, new CraftingPage(BlockRegistry.ALCHEMICAL_BLOCK));
        addBasicItem(BlockRegistry.MYCELIAL_BLOCK, SOURCE, new CraftingPage(BlockRegistry.MYCELIAL_BLOCK));
        addBasicItem(BlockRegistry.SOURCE_JAR, SOURCE, new CraftingPage(BlockRegistry.SOURCE_JAR));

        addBasicItem(BlockRegistry.RELAY, SOURCE, new CraftingPage(BlockRegistry.RELAY).withText("ars_nouveau.page2.relay"));

        addBasicItem(BlockRegistry.RELAY_DEPOSIT, SOURCE, new ApparatusPage(BlockRegistry.RELAY_DEPOSIT));
        addBasicItem(BlockRegistry.RELAY_SPLITTER, SOURCE, new ApparatusPage(BlockRegistry.RELAY_SPLITTER));
        addBasicItem(BlockRegistry.RELAY_WARP, SOURCE, new ApparatusPage(BlockRegistry.RELAY_WARP));
        addBasicItem(BlockRegistry.RELAY_COLLECTOR, SOURCE, new ApparatusPage(BlockRegistry.RELAY_COLLECTOR));
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
                .withPage(new RelationsPage().withEntry(MACHINES, "enchanting_apparatus")), getPath(ENCHANTMENTS, "how_to_enchant"));
        addPage(new PatchouliBuilder(RITUALS, "performing_rituals")
                .withSortNum(-1)
                .withIcon(BlockRegistry.RITUAL_BLOCK)
                .withLocalizedText()
                .withLocalizedText()
                .withPage(new CraftingPage(ArsNouveauAPI.getInstance().getRitualItemMap().get(new ResourceLocation(ArsNouveau.MODID, RitualLib.SUNRISE))))
                .withPage(new RelationsPage().withEntry(MACHINES, "ritual_brazier")), getPath(RITUALS, "performing_rituals"));
        addPage(new PatchouliBuilder(FAMILIARS, "summoning_familiars")
                .withSortNum(-1)
                .withIcon(ArsNouveauAPI.getInstance().getRitualItemMap().get(new ResourceLocation(ArsNouveau.MODID, RitualLib.BINDING)))
                .withLocalizedText()
                .withLocalizedText()
                .withPage(new CraftingPage(ArsNouveauAPI.getInstance().getRitualItemMap().get(new ResourceLocation(ArsNouveau.MODID, RitualLib.BINDING))))
                .withPage(new RelationsPage().withEntry(MACHINES, "ritual_brazier").withEntry(RITUALS, "binding")), getPath(FAMILIARS, "summoning_familiars"));

        addPage(new PatchouliBuilder(MOD_NEWS, "mod_news")
                        .withIcon(ItemsRegistry.SPELL_PARCHMENT)
                        .withPage(new LinkPage("https://discord.gg/y7TMXZu", "ars_nouveau.discord_text", "ars_nouveau.community")),
                getPath(MOD_NEWS, "mod_news"));

        addBasicItem(ItemsRegistry.DOWSING_ROD, EQUIPMENT, new CraftingPage(ItemsRegistry.DOWSING_ROD));

        addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.AMETHYST_GOLEM_CHARM)
                        .withLocalizedText()
                        .withPage(new EntityPage(getRegistryName(ModEntities.AMETHYST_GOLEM.get()).toString())
                                .withText(getLangPath("amethyst_golem_charm", 2)).withScale(0.75f).withOffset(0.2f))
                        .withPage(new TextPage(getLangPath("amethyst_golem_charm", 3)).withTitle("ars_nouveau.summoning"))
                        .withPage(new TextPage(getLangPath("amethyst_golem_charm", 4)).withTitle("ars_nouveau.amethyst_farming"))
                        .withPage(new TextPage(getLangPath("amethyst_golem_charm", 5)).withTitle("ars_nouveau.amethyst_storage")),
                getPath(AUTOMATION, "amethyst_golem_charm"));
        addBasicItem(ItemsRegistry.ANNOTATED_CODEX, EQUIPMENT, new CraftingPage(ItemsRegistry.ANNOTATED_CODEX));
        addPage(new PatchouliBuilder(AUTOMATION, BlockRegistry.ORANGE_SBED).withName("ars_nouveau.summon_bed").withLocalizedText("summon_bed")
                        .withPage(new CraftingPage(BlockRegistry.ORANGE_SBED).withRecipe2(BlockRegistry.BLUE_SBED))
                        .withPage(new CraftingPage(BlockRegistry.GREEN_SBED).withRecipe2(BlockRegistry.YELLOW_SBED))
                        .withPage(new CraftingPage(BlockRegistry.RED_SBED).withRecipe2(BlockRegistry.PURPLE_SBED))
                , getPath(AUTOMATION, "summon_bed"));

        addBasicItem(BlockRegistry.SCRYERS_CRYSTAL, MACHINES, new CraftingPage(BlockRegistry.SCRYERS_CRYSTAL));
        addBasicItem(BlockRegistry.SCRYERS_OCULUS, MACHINES, new ApparatusPage(BlockRegistry.SCRYERS_OCULUS));
        addBasicItem(ItemsRegistry.SCRYER_SCROLL, MACHINES, null);
        addBasicItem(ItemsRegistry.STARBUNCLE_SHADES, AUTOMATION, new CraftingPage(ItemsRegistry.STARBUNCLE_SHADES));
        addBasicItem(ItemsRegistry.WIXIE_HAT, AUTOMATION, new CraftingPage(ItemsRegistry.WIXIE_HAT));

        addPage(new PatchouliBuilder(MOD_NEWS, "support_mod")
                        .withIcon(ItemsRegistry.STARBUNCLE_CHARM)
                        .withPage(new LinkPage("https://www.patreon.com/arsnouveau", "ars_nouveau.patreon_text", "ars_nouveau.patreon"))
                        .withPage(new LinkPage("https://www.redbubble.com/people/Gootastic/explore?page=1&sortOrder=recent", "ars_nouveau.store_text", "ars_nouveau.store")),
                getPath(MOD_NEWS, "support_mod"));


        addPage(new PatchouliBuilder(EQUIPMENT, ItemsRegistry.SHAPERS_FOCUS)
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.SHAPERS_FOCUS))
                .withPage(new TextPage(getLangPath("shapers_focus", 2)).withTitle("ars_nouveau.shapers_focus.blocks"))
                .withPage(new TextPage(getLangPath("shapers_focus", 3)).withTitle("ars_nouveau.shapers_focus.entities"))
                .withPage(new TextPage(getLangPath("shapers_focus", 4)).withTitle("ars_nouveau.shapers_focus.examples")), getPath(EQUIPMENT, "shapers_focus"));
        addBasicItem(ItemsRegistry.ALCHEMISTS_CROWN, EQUIPMENT, new ApparatusPage(ItemsRegistry.ALCHEMISTS_CROWN));
        addPage(new PatchouliBuilder(EQUIPMENT,  "flask_cannons")
                .withLocalizedText()
                .withIcon(ItemsRegistry.SPLASH_LAUNCHER)
                .withPage(new ApparatusPage(ItemsRegistry.SPLASH_LAUNCHER))
                .withPage(new ApparatusPage(ItemsRegistry.LINGERING_LAUNCHER)), getPath(EQUIPMENT, "flask_launcher"));

        PatchouliBuilder ARMOR_ENTRY = new PatchouliBuilder(EQUIPMENT, "armor")
                .withIcon(ItemsRegistry.NOVICE_ROBES)
                .withLocalizedText()
                .withPage(new CraftingPage(ItemsRegistry.MAGE_FIBER))
                .withPage(new TextPage("ars_nouveau.page.threads").withTitle("ars_nouveau.threads"))
                .withPage(new ImagePage().withEntry(new ResourceLocation(ArsNouveau.MODID, "textures/gui/entries/sorcerer_diagram.png"))
                        .withEntry(new ResourceLocation(ArsNouveau.MODID, "textures/gui/entries/arcanist_thread_diagram.png"))
                        .withEntry(new ResourceLocation(ArsNouveau.MODID, "textures/gui/entries/battlemage_diagram.png"))
                        .withBorder().withTitle("ars_nouveau.thread_layout")
                        .withText("ars_nouveau.page.layout_desc"))
                .withPage(new CraftingPage(ItemsRegistry.NOVICE_HOOD).withRecipe2(ItemsRegistry.NOVICE_ROBES))
                .withPage(new CraftingPage(ItemsRegistry.NOVICE_LEGGINGS).withRecipe2(ItemsRegistry.NOVICE_BOOTS))
                .withPage(new CraftingPage(ItemsRegistry.APPRENTICE_HOOD).withRecipe2(ItemsRegistry.APPRENTICE_ROBES))
                .withPage(new CraftingPage(ItemsRegistry.APPRENTICE_LEGGINGS).withRecipe2(ItemsRegistry.APPRENTICE_BOOTS))
                .withPage(new CraftingPage(ItemsRegistry.ARCHMAGE_ROBES).withRecipe2(ItemsRegistry.ARCHMAGE_LEGGINGS))
                .withPage(new CraftingPage(ItemsRegistry.ARCHMAGE_BOOTS))
                .withPage(new RelationsPage().withEntry(ARMOR, "armor_upgrade"));
        addPage(ARMOR_ENTRY.withCategory(ARMOR), getPath(ARMOR, "armor"));

        addPage(new PatchouliBuilder(ARMOR, "armor_upgrading")
                .withLocalizedText()
                .withPage(new TextPage(getLangPath("armor_upgrading", 2)).withTitle("ars_nouveau.armor_tiers"))
                .withIcon(ItemsRegistry.APPRENTICE_HOOD)
                .withPage(new ApparatusTextPage("ars_nouveau:upgrade_1"))
                .withPage(new ApparatusTextPage("ars_nouveau:upgrade_2"))
                .withPage(new RelationsPage().withEntry(ARMOR, "armor").withEntry(MACHINES, "alteration_table"))

                .withSortNum(1), getPath(ARMOR, "armor_upgrade"));
        addPage(new PatchouliBuilder(ARMOR, "applying_perks")
                .withLocalizedText()
                .withLocalizedText()
                .withPage(new RelationsPage().withEntry(ARMOR, "alteration_table"))
                .withIcon(ItemsRegistry.BLANK_THREAD)
                .withSortNum(2), getPath(ARMOR, "applying_perks"));

        addPage(new PatchouliBuilder(ARMOR, "alteration_table")
                .withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.ALTERATION_TABLE).withRecipe2(ItemsRegistry.BLANK_THREAD))
                .withPage(new RelationsPage().withEntry(ARMOR, "armor_upgrade").withEntry(ARMOR, "armor"))
                .withIcon(BlockRegistry.ALTERATION_TABLE)
                .withSortNum(3), getPath(ARMOR, "alteration_table"));


    }

    public String getLangPath(String name, int count) {
        return "ars_nouveau.page" + count + "." + name;
    }

    public String getLangPath(String name) {
        return "ars_nouveau.page." + name;
    }

    public void addPage(PatchouliBuilder builder, Path path) {
        this.pages.add(new PatchouliPage(builder, path));
    }

    public void addBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage) {
        PatchouliBuilder builder = new PatchouliBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withPage(new TextPage("ars_nouveau.page." + getRegistryName(item.asItem()).getPath()));
        if (recipePage != null) {
            builder.withPage(recipePage);
        }
        this.pages.add(new PatchouliPage(builder, getPath(category, getRegistryName(item.asItem()))));
    }

    public void addBasicItem(RegistryObject<? extends ItemLike> item, ResourceLocation category, IPatchouliPage recipePage) {
        addBasicItem(item.get(), category, recipePage);
    }

    public Path getPath(ResourceLocation category, ResourceLocation fileName) {
        return this.generator.getOutputFolder().resolve("data/ars_nouveau/patchouli/" + category.getPath() + "/" + fileName.getPath() + ".json");
    }

    public Path getPath(ResourceLocation category, String fileName) {
        return this.generator.getOutputFolder().resolve("data/ars_nouveau/patchouli/" + category.getPath() + "/" + fileName + ".json");
    }

    @Override
    public void run(CachedOutput cache) throws IOException {
        addEntries();
        for (PatchouliPage patchouliPage : pages) {
            DataProvider.saveStable(cache, patchouliPage.build(), patchouliPage.path);
        }
    }

    public record PatchouliPage(PatchouliBuilder builder, Path path) {
        @Override
        public Path path() {
            return path;
        }

        public JsonObject build() {
            return builder.build();
        }
    }

    public void addGlyphPage(AbstractSpellPart spellPart) {
        ResourceLocation category = switch (spellPart.getTier().value) {
            case 1 -> GLYPHS_1;
            case 2 -> GLYPHS_2;
            default -> GLYPHS_3;
        };
        PatchouliBuilder builder = new PatchouliBuilder(category, spellPart.getName())
                .withName("ars_nouveau.glyph_name." + spellPart.getRegistryName().getPath())
                .withIcon(spellPart.getRegistryName().toString())
                .withSortNum(spellPart instanceof AbstractCastMethod ? 1 : spellPart instanceof AbstractEffect ? 2 : 3)
                .withPage(new TextPage("ars_nouveau.glyph_desc." + spellPart.getRegistryName().getPath()))
                .withPage(new GlyphScribePage(spellPart));
        this.pages.add(new PatchouliPage(builder, getPath(category, spellPart.getRegistryName().getPath())));
    }

    public void addFamiliarPage(AbstractFamiliarHolder familiarHolder) {
        PatchouliBuilder builder = new PatchouliBuilder(FAMILIARS, "entity.ars_nouveau." + familiarHolder.getRegistryName().getPath())
                .withIcon("ars_nouveau:" + familiarHolder.getRegistryName().getPath())
                .withTextPage("ars_nouveau.familiar_desc." + familiarHolder.getRegistryName().getPath())
                .withPage(new EntityPage(familiarHolder.getRegistryName().toString()));
        this.pages.add(new PatchouliPage(builder, this.generator.getOutputFolder().resolve("data/ars_nouveau/patchouli/familiars/" + familiarHolder.getRegistryName().getPath() + ".json")));
    }

    public void addRitualPage(AbstractRitual ritual) {
        PatchouliBuilder builder = new PatchouliBuilder(RITUALS, "item." + ritual.getRegistryName().getNamespace() + "." + ritual.getRegistryName().getPath())
                .withIcon(ritual.getRegistryName().toString())
                .withTextPage(ritual.getDescriptionKey())
                .withPage(new CraftingPage(ritual.getRegistryName().toString()));

        this.pages.add(new PatchouliPage(builder, this.generator.getOutputFolder().resolve("data/" + ritual.getRegistryName().getNamespace() + "/patchouli/rituals/" + ritual.getRegistryName().getPath() + ".json")));
    }

    public void addEnchantmentPage(Enchantment enchantment) {
        PatchouliBuilder builder = new PatchouliBuilder(ENCHANTMENTS, enchantment.getDescriptionId())
                .withIcon(getRegistryName(Items.ENCHANTED_BOOK).toString());
        for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++) {
            builder.withPage(new EnchantingPage("ars_nouveau:" + getRegistryName(enchantment).getPath() + "_" + i));
        }
        this.pages.add(new PatchouliPage(builder, this.generator.getOutputFolder().resolve("data/ars_nouveau/patchouli/enchantments/" + getRegistryName(enchantment).getPath() + ".json")));
    }

    public void addPerkPage(IPerk perk){
        PerkItem perkItem = ArsNouveauAPI.getInstance().getPerkItemMap().get(perk.getRegistryName());
        PatchouliBuilder builder = new PatchouliBuilder(ARMOR, perkItem)
                .withIcon(perkItem)
                .withTextPage(perk.getDescriptionKey())
                .withPage(new ApparatusPage(perkItem)).withSortNum(99);
        this.pages.add(new PatchouliPage(builder, this.generator.getOutputFolder().resolve("data/ars_nouveau/patchouli/armor/" + perk.getRegistryName().getPath() + ".json")));
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
            EnchantmentRegistry.MANA_BOOST_ENCHANTMENT.get(),
            EnchantmentRegistry.MANA_REGEN_ENCHANTMENT.get()
    );

    @Override
    public String getName() {
        return "Patchouli";
    }
}
