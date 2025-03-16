package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.registry.*;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.*;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.common.perk.EmptyPerk;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;


//
public class PatchouliProvider extends SimpleDataProvider {

    public static ResourceLocation AUTOMATION = ArsNouveau.prefix("automation");
    public static ResourceLocation ENCHANTMENTS = ArsNouveau.prefix("enchantments");
    public static ResourceLocation EQUIPMENT = ArsNouveau.prefix("equipment");
    public static ResourceLocation FAMILIARS = ArsNouveau.prefix("familiars");
    public static ResourceLocation GETTING_STARTED = ArsNouveau.prefix("getting_started");
    public static ResourceLocation MOD_NEWS = ArsNouveau.prefix("mod_news");

    public static ResourceLocation MACHINES = ArsNouveau.prefix("machines");
    public static ResourceLocation RESOURCES = ArsNouveau.prefix("resources");
    public static ResourceLocation RITUALS = ArsNouveau.prefix("rituals");
    public static ResourceLocation SOURCE = ArsNouveau.prefix("source");
    public static ResourceLocation GLYPHS_1 = ArsNouveau.prefix("glyphs_1");
    public static ResourceLocation GLYPHS_2 = ArsNouveau.prefix("glyphs_2");
    public static ResourceLocation GLYPHS_3 = ArsNouveau.prefix("glyphs_3");
    public static ResourceLocation ARMOR = ArsNouveau.prefix("armor");

    public List<PatchouliPage> pages = new ArrayList<>();

    public CompletableFuture<HolderLookup.Provider> registries;

    public PatchouliProvider(DataGenerator generatorIn, CompletableFuture<HolderLookup.Provider> registries) {
        super(generatorIn);
        this.registries = registries;
    }

    public void addEntries() {
        Block SOURCESTONE = BlockRegistry.getBlock(LibBlockNames.SOURCESTONE);
        for (ResourceKey<Enchantment> g : enchants) {
            addEnchantmentPage(g);
        }
        for (AbstractRitual r : ANRegistries.RITUAL_TYPES) {
            if(r.getRegistryName().getNamespace().equals(ArsNouveau.MODID))
                addRitualPage(r);
        }

        for (AbstractFamiliarHolder r : FamiliarRegistry.getFamiliarHolderMap().values()) {
            if (r.getRegistryName().getNamespace().equals(ArsNouveau.MODID))
                addFamiliarPage(r);
        }

        for (AbstractSpellPart s : ANRegistries.GLYPH_TYPES) {
            if(s.getRegistryName().getNamespace().equals(ArsNouveau.MODID)) {
                addGlyphPage(s);
            }
        }

        for (Map.Entry<ResourceKey<IPerk>, IPerk> entry : PerkRegistry.PERK_TYPES.entrySet()) {
            var perk = entry.getValue();
            if (entry.getKey().location().getNamespace().equals(ArsNouveau.MODID) && !(perk instanceof EmptyPerk))
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
                .withIcon(ItemsRegistry.SORCERER_ROBES)
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
                .withPage(new RelationsPage().withEntry(EQUIPMENT, "spell_books").withEntry(ARMOR, "armor")), getPath(GETTING_STARTED, "upgrades"));

        addPage(new PatchouliBuilder(GETTING_STARTED, "starting_automation")
                .withSortNum(10)
                .withIcon(BlockRegistry.BASIC_SPELL_TURRET)
                .withLocalizedText()
                .withPage(new RelationsPage().withEntry(AUTOMATION, "spell_turret")
                        .withEntry(AUTOMATION, "spell_prism").withEntry(AUTOMATION, "starbuncle_charm")
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
        var starby = addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.STARBUNCLE_CHARM)
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.STARBUNCLE_CHARM))
                .withPage(new EntityPage(getRegistryName(ModEntities.STARBUNCLE_TYPE.get()).toString())
                        .withText(getLangPath("starbuncle_charm", 2)))
                .withPage(new TextPage(getLangPath("starbuncle_charm", 3)).withTitle("ars_nouveau.summoning"))
                .withPage(new TextPage(getLangPath("starbuncle_charm", 4)).withTitle("ars_nouveau.item_transport"))
                .withPage(new TextPage(getLangPath("starbuncle_charm", 5)).withTitle("ars_nouveau.filtering"))
                .withPage(new TextPage(getLangPath("starbuncle_charm", 6)).withTitle("ars_nouveau.pathing"))
                .withPage(new TextPage(getLangPath("starbuncle_charm", 7)).withTitle("ars_nouveau.starbuncle_bed"))
                .withPage(new TextPage(getLangPath("starbuncle_charm", 8)).withTitle("ars_nouveau.starbuncle_stacking")), getPath(AUTOMATION, "starbuncle_charm"));

        addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.DRYGMY_CHARM)
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.DRYGMY_CHARM))
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_DRYGMY.get()).toString())
                        .withText(getLangPath("drygmy_charm", 2)))
                .withPage(new TextPage(getLangPath("drygmy_charm", 3)).withTitle("ars_nouveau.summoning"))
                .withPage(new TextPage(getLangPath("drygmy_charm", 4)).withTitle("ars_nouveau.happiness"))
                .withPage(new TextPage(getLangPath("drygmy_charm", 5)).withTitle("ars_nouveau.production"))
                .withPage(new RelationsPage().withEntry(MACHINES, "mob_jar")), getPath(AUTOMATION, "drygmy_charm"));

        addPage(new PatchouliBuilder(EQUIPMENT, ItemsRegistry.DULL_TRINKET)
                .withPage(new CraftingPage(ItemsRegistry.DULL_TRINKET).withRecipe2(ItemsRegistry.MUNDANE_BELT))
                .withPage(new CraftingPage(ItemsRegistry.RING_OF_POTENTIAL)), getPath(EQUIPMENT, "dull_items"));

        addPage(new PatchouliBuilder(RESOURCES, BlockRegistry.MAGE_BLOOM_CROP)
                .withTextPage("ars_nouveau.page.magebloom_crop")
                .withPage(new ApparatusPage(BlockRegistry.MAGE_BLOOM_CROP))
                .withPage(new CraftingPage(ItemsRegistry.MAGE_FIBER).withRecipe2(BlockRegistry.MAGEBLOOM_BLOCK)), getPath(RESOURCES, "magebloom_crop"));

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
                .withPage(new EnchantingPage("ars_nouveau:" + EnchantmentRegistry.REACTIVE_ENCHANTMENT.location().getPath()))
                .withLocalizedText()
                .withPage(new EnchantingPage("ars_nouveau:" + EnchantmentRegistry.REACTIVE_ENCHANTMENT.location().getPath() + "_" + 2))
                .withPage(new EnchantingPage("ars_nouveau:" + EnchantmentRegistry.REACTIVE_ENCHANTMENT.location().getPath() + "_" + 3))
                .withPage(new EnchantingPage("ars_nouveau:" + EnchantmentRegistry.REACTIVE_ENCHANTMENT.location().getPath() + "_" + 4))
                .withLocalizedText()
                .withPage(new ApparatusTextPage("ars_nouveau:spell_write")), getPath(EQUIPMENT, "reactive_enchantment"));

        addBasicItem(ItemsRegistry.RING_OF_GREATER_DISCOUNT, EQUIPMENT, new ApparatusPage(ItemsRegistry.RING_OF_GREATER_DISCOUNT));
        addBasicItem(ItemsRegistry.RING_OF_LESSER_DISCOUNT, EQUIPMENT, new ApparatusPage(ItemsRegistry.RING_OF_LESSER_DISCOUNT));

        var turrets = addPage(new PatchouliBuilder(AUTOMATION, BlockRegistry.BASIC_SPELL_TURRET)
                .withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.BASIC_SPELL_TURRET).withRecipe2(BlockRegistry.ROTATING_TURRET))
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

        var wixie = addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.WIXIE_CHARM)
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.WIXIE_CHARM))
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_WIXIE_TYPE.get()).toString())
                        .withText(getLangPath("wixie_charm", 2)))
                .withPage(new TextPage(getLangPath("wixie_charm", 3)).withTitle("ars_nouveau.item_crafting"))
                .withPage(new TextPage(getLangPath("wixie_charm", 4)).withTitle("ars_nouveau.item_crafting_setting"))
                .withPage(new TextPage(getLangPath("wixie_charm", 7)).withTitle("ars_nouveau.binding_inventories"))
                .withPage(new TextPage(getLangPath("wixie_charm", 5)).withTitle("ars_nouveau.potion_crafting"))
                .withPage(new TextPage(getLangPath("wixie_charm", 6))), getPath(AUTOMATION, "wixie_charm"));

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


        addPage(new PatchouliBuilder(RESOURCES, BlockRegistry.SOURCEBERRY_BUSH)
                .withLocalizedText()
                .withPage(new CraftingPage(ItemsRegistry.SOURCE_BERRY_PIE).withRecipe2(ItemsRegistry.SOURCE_BERRY_ROLL)), getPath(RESOURCES, "sourceberry"));

        addPage(new PatchouliBuilder(RESOURCES, "weald_walker")
                .withIcon(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix(RitualLib.AWAKENING)))
                .withLocalizedText()
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_BLAZING_WEALD.get()).toString()).withText(getLangPath("weald_walker", 2)))
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_CASCADING_WEALD.get()).toString()).withText(getLangPath("weald_walker", 3)))
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_FLOURISHING_WEALD.get()).toString()).withText(getLangPath("weald_walker", 4)))
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_VEXING_WEALD.get()).toString()).withText(getLangPath("weald_walker", 5))), getPath(RESOURCES, "weald_walker"));

        addPage(new PatchouliBuilder(RESOURCES, "wilden")
                .withIcon(ItemsRegistry.WILDEN_SPIKE)
                .withLocalizedText()
                .withPage(new EntityPage(getRegistryName(ModEntities.WILDEN_HUNTER.get()).toString()).withScale(0.55f).withText(getLangPath("wilden", 3)))
                .withPage(new EntityPage(getRegistryName(ModEntities.WILDEN_STALKER.get()).toString()).withScale(0.55f).withText(getLangPath("wilden", 4)))
                .withPage(new EntityPage(getRegistryName(ModEntities.WILDEN_GUARDIAN.get()).toString()).withScale(0.55f).withText(getLangPath("wilden", 5)))
                .withPage(new EntityPage(getRegistryName(ModEntities.WILDEN_BOSS.get()).toString()).withScale(0.55f).withText(getLangPath("wilden", 6)))
                .withPage(new TextPage(getLangPath("wilden", 7))), getPath(RESOURCES, "wilden"));

        var denyScroll = addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.DENY_ITEM_SCROLL)
                .withLocalizedText()
                .withPage(new CraftingPage(ItemsRegistry.BLANK_PARCHMENT).withRecipe2(ItemsRegistry.DENY_ITEM_SCROLL)), getPath(AUTOMATION, "deny_scroll"));

        var mimicScroll = addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.MIMIC_ITEM_SCROLL)
                .withLocalizedText()
                .withPage(new CraftingPage(ItemsRegistry.BLANK_PARCHMENT).withRecipe2(ItemsRegistry.MIMIC_ITEM_SCROLL)), getPath(AUTOMATION, "mimic_scroll"));
        var allowScroll = addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.ALLOW_ITEM_SCROLL)
                .withLocalizedText()
                .withPage(new CraftingPage(ItemsRegistry.BLANK_PARCHMENT).withRecipe2(ItemsRegistry.ALLOW_ITEM_SCROLL)), getPath(AUTOMATION, "allow_scroll"));

        var dominionWand = addBasicItem(ItemsRegistry.DOMINION_ROD, AUTOMATION, new ApparatusPage(ItemsRegistry.DOMINION_ROD));


        var prisms = addBasicItem(BlockRegistry.SPELL_PRISM, AUTOMATION, new CraftingPage(BlockRegistry.SPELL_PRISM));

        addPage(new PatchouliBuilder(RESOURCES, BlockRegistry.MAGELIGHT_TORCH)
                .withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.GOLD_SCONCE_BLOCK).withRecipe2(BlockRegistry.SOURCESTONE_SCONCE_BLOCK))
                .withPage(new CraftingPage(BlockRegistry.POLISHED_SCONCE_BLOCK).withRecipe2(BlockRegistry.ARCHWOOD_SCONCE_BLOCK))
                .withPage(new CraftingPage(BlockRegistry.MAGELIGHT_TORCH)), getPath(RESOURCES, "magelighting"));

        addPage(new PatchouliBuilder(EQUIPMENT, "spell_books")
                .withIcon(ItemsRegistry.ARCHMAGE_SPELLBOOK)
                .withLocalizedText()
                .withPage(new CraftingPage(ItemsRegistry.NOVICE_SPELLBOOK).withRecipe2("ars_nouveau:apprentice_book_upgrade"))
                .withPage(new CraftingPage("ars_nouveau:archmage_book_upgrade")), getPath(EQUIPMENT, "spell_books"));

        addBasicItem(ItemsRegistry.ENCHANTERS_MIRROR, EQUIPMENT, new ApparatusPage(ItemsRegistry.ENCHANTERS_MIRROR));
        addBasicItem(ItemsRegistry.ENCHANTERS_SHIELD, EQUIPMENT, new ApparatusPage(ItemsRegistry.ENCHANTERS_SHIELD));
        addBasicItem(ItemsRegistry.ENCHANTERS_SWORD, EQUIPMENT, new ApparatusPage(ItemsRegistry.ENCHANTERS_SWORD));
        addPage(new PatchouliBuilder(EQUIPMENT, ItemsRegistry.SPELL_BOW)
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.SPELL_BOW))
                .withPage(new ImbuementPage("ars_nouveau:imbuement_amplify_arrow"))
                .withPage(new ImbuementPage("ars_nouveau:imbuement_pierce_arrow"))
                .withPage(new ImbuementPage("ars_nouveau:imbuement_split_arrow")), getPath(EQUIPMENT, "spell_bow"));
        addPage(new PatchouliBuilder(EQUIPMENT, ItemsRegistry.SPELL_CROSSBOW)
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.SPELL_CROSSBOW))
                .withPage(new ImbuementPage("ars_nouveau:imbuement_amplify_arrow"))
                .withPage(new ImbuementPage("ars_nouveau:imbuement_pierce_arrow"))
                .withPage(new ImbuementPage("ars_nouveau:imbuement_split_arrow")), getPath(EQUIPMENT, "spell_crossbow"));
        addBasicItem(ItemsRegistry.RUNIC_CHALK, EQUIPMENT, new CraftingPage(ItemsRegistry.RUNIC_CHALK));

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
                .withPage(new CraftingPage(BlockRegistry.ARCANE_PEDESTAL).withRecipe2(BlockRegistry.ARCANE_PLATFORM))
                .withPage(new CraftingPage(BlockRegistry.ENCHANTING_APP_BLOCK).withRecipe2(BlockRegistry.ARCANE_CORE_BLOCK)), getPath(MACHINES, "enchanting_apparatus"));

        var potionJar = addBasicItem(BlockRegistry.POTION_JAR, MACHINES, new CraftingPage(BlockRegistry.POTION_JAR));
        var melder = addBasicItem(BlockRegistry.POTION_MELDER, MACHINES, new ApparatusPage(BlockRegistry.POTION_MELDER));
        var diffuser = addBasicItem(BlockRegistry.POTION_DIFFUSER, MACHINES, new ApparatusPage(BlockRegistry.POTION_DIFFUSER));

        wixie.builder.withPage(new RelationsPage().withEntry(potionJar).withEntry(melder).withEntry(diffuser));
        potionJar.builder.withPage(new RelationsPage().withEntry(melder).withEntry(diffuser).withEntry(wixie));
        melder.builder.withPage(new RelationsPage().withEntry(potionJar).withEntry(diffuser).withEntry(wixie));
        diffuser.builder.withPage(new RelationsPage().withEntry(potionJar).withEntry(melder).withEntry(wixie));

        addBasicItem(BlockRegistry.RITUAL_BLOCK, MACHINES, new CraftingPage(BlockRegistry.RITUAL_BLOCK));
        addBasicItem(BlockRegistry.BRAZIER_RELAY, MACHINES, new ApparatusPage(BlockRegistry.BRAZIER_RELAY));
        addPage(new PatchouliBuilder(MACHINES, BlockRegistry.SCRIBES_BLOCK)
                .withPage(new TextPage(getLangPath("scribes_table", 1)).withTitle("ars_nouveau.glyph_crafting"))
                .withPage(new TextPage(getLangPath("scribes_table", 2)).withTitle("ars_nouveau.scribing"))
                .withPage(new CraftingPage(ItemsRegistry.BLANK_PARCHMENT).withRecipe2(BlockRegistry.SCRIBES_BLOCK)), getPath(MACHINES, "scribes_block"));

        var portal = addPage(new PatchouliBuilder(MACHINES, "warp_portal")
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
        var scroll = addBasicItem(ItemsRegistry.WARP_SCROLL, EQUIPMENT, new CraftingPage(ItemsRegistry.WARP_SCROLL));

        var stableScroll = addBasicItem(ItemsRegistry.STABLE_WARP_SCROLL, EQUIPMENT, new ApparatusPage(ItemsRegistry.STABLE_WARP_SCROLL));
        stableScroll.builder.withPage(new RelationsPage().withEntry(scroll.relationPath()).withEntry(portal.relationPath()));

        scroll.builder.withPage(new RelationsPage()
                .withEntry(stableScroll.relationPath())
                .withEntry(portal.relationPath()));

        portal.builder.withPage(new RelationsPage().withEntry(scroll.relationPath()).withEntry(stableScroll.relationPath()));

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
                .withPage(new CraftingPage(BlockRegistry.VOLCANIC_BLOCK))
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
                .withPage(new CraftingPage(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix(RitualLib.SUNRISE))))
                .withPage(new RelationsPage().withEntry(MACHINES, "ritual_brazier")), getPath(RITUALS, "performing_rituals"));
        addPage(new PatchouliBuilder(FAMILIARS, "summoning_familiars")
                .withSortNum(-1)
                .withIcon(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix(RitualLib.BINDING)))
                .withLocalizedText()
                .withLocalizedText()
                .withPage(new CraftingPage(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix(RitualLib.BINDING))))
                .withPage(new RelationsPage().withEntry(MACHINES, "ritual_brazier").withEntry(RITUALS, "ritual_binding")), getPath(FAMILIARS, "summoning_familiars"));

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
        var starbyBed = addPage(new PatchouliBuilder(AUTOMATION, BlockRegistry.ORANGE_SBED).withName("ars_nouveau.summon_bed").withLocalizedText("summon_bed")
                        .withPage(new CraftingPage(BlockRegistry.ORANGE_SBED).withRecipe2(BlockRegistry.BLUE_SBED))
                        .withPage(new CraftingPage(BlockRegistry.GREEN_SBED).withRecipe2(BlockRegistry.YELLOW_SBED))
                        .withPage(new CraftingPage(BlockRegistry.RED_SBED).withRecipe2(BlockRegistry.PURPLE_SBED))
                , getPath(AUTOMATION, "summon_bed"));
        var scryCaster = addBasicItem(ItemsRegistry.SCRY_CASTER, EQUIPMENT, new ApparatusPage(ItemsRegistry.SCRY_CASTER));
        var scryCrystal = addBasicItem(BlockRegistry.SCRYERS_CRYSTAL, MACHINES, new CraftingPage(BlockRegistry.SCRYERS_CRYSTAL));
        var oculus = addBasicItem(BlockRegistry.SCRYERS_OCULUS, MACHINES, new ApparatusPage(BlockRegistry.SCRYERS_OCULUS));
        var scryScroll = addBasicItem(ItemsRegistry.SCRYER_SCROLL, MACHINES, null);

        scryCrystal.builder.withPage(new RelationsPage().withEntry(scryCaster).withEntry(scryScroll).withEntry(oculus));
        scryCaster.builder.withPage(new RelationsPage().withEntry(scryCrystal.relationPath()));
        oculus.builder.withPage(new RelationsPage().withEntry(scryScroll).withEntry(scryCrystal));
        scryScroll.builder.withPage(new RelationsPage().withEntry(scryCaster).withEntry(oculus).withEntry(scryCrystal));
        var starbyShades = addBasicItem(ItemsRegistry.STARBUNCLE_SHADES, AUTOMATION, new CraftingPage(ItemsRegistry.STARBUNCLE_SHADES));
        var wixieHat = addBasicItem(ItemsRegistry.WIXIE_HAT, AUTOMATION, new CraftingPage(ItemsRegistry.WIXIE_HAT));

        addPage(new PatchouliBuilder(MOD_NEWS, "support_mod")
                        .withIcon(ItemsRegistry.STARBUNCLE_CHARM)
                        .withPage(new LinkPage("https://www.patreon.com/arsnouveau", "ars_nouveau.patreon_text", "ars_nouveau.patreon"))
                        .withPage(new EntityPage(ModEntities.LILY.get()).withText("ars_nouveau.lily"))
                        .withPage(new EntityPage(ModEntities.NOOK.get()).withText("ars_nouveau.nook"))
                        .withPage(new LinkPage("https://www.redbubble.com/people/Gootastic/explore?page=1&sortOrder=recent", "ars_nouveau.store_text", "ars_nouveau.store")),
                getPath(MOD_NEWS, "support_mod"));


        addPage(new PatchouliBuilder(EQUIPMENT, ItemsRegistry.SHAPERS_FOCUS)
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.SHAPERS_FOCUS))
                .withPage(new TextPage(getLangPath("shapers_focus", 2)).withTitle("ars_nouveau.shapers_focus.blocks"))
                .withPage(new TextPage(getLangPath("shapers_focus", 3)).withTitle("ars_nouveau.shapers_focus.entities"))
                .withPage(new TextPage(getLangPath("shapers_focus", 4)).withTitle("ars_nouveau.shapers_focus.examples")), getPath(EQUIPMENT, "shapers_focus"));
        addBasicItem(ItemsRegistry.ALCHEMISTS_CROWN, EQUIPMENT, new ApparatusPage(ItemsRegistry.ALCHEMISTS_CROWN));
        addPage(new PatchouliBuilder(EQUIPMENT, "flask_cannons")
                .withLocalizedText()
                .withIcon(ItemsRegistry.SPLASH_LAUNCHER)
                .withPage(new ApparatusPage(ItemsRegistry.SPLASH_LAUNCHER))
                .withPage(new ApparatusPage(ItemsRegistry.LINGERING_LAUNCHER)), getPath(EQUIPMENT, "flask_launcher"));

        PatchouliBuilder ARMOR_ENTRY = new PatchouliBuilder(EQUIPMENT, "armor")
                .withIcon(ItemsRegistry.SORCERER_ROBES)
                .withLocalizedText()
                .withPage(new CraftingPage(ItemsRegistry.MAGE_FIBER))
                .withPage(new TextPage("ars_nouveau.page.threads").withTitle("ars_nouveau.threads"))
                .withPage(new ImagePage().withEntry(ArsNouveau.prefix("textures/gui/entries/sorcerer_diagram.png"))
                        .withEntry(ArsNouveau.prefix("textures/gui/entries/arcanist_thread_diagram.png"))
                        .withEntry(ArsNouveau.prefix("textures/gui/entries/battlemage_diagram.png"))
                        .withBorder().withTitle("ars_nouveau.thread_layout")
                        .withText("ars_nouveau.page.layout_desc"))
                .withPage(new ApparatusPage(ItemsRegistry.SORCERER_HOOD))
                .withPage(new ApparatusPage(ItemsRegistry.SORCERER_ROBES))
                .withPage(new ApparatusPage(ItemsRegistry.SORCERER_LEGGINGS))
                .withPage(new ApparatusPage(ItemsRegistry.SORCERER_BOOTS))
                .withPage(new ApparatusPage(ItemsRegistry.ARCANIST_HOOD))
                .withPage(new ApparatusPage(ItemsRegistry.ARCANIST_ROBES))
                .withPage(new ApparatusPage(ItemsRegistry.ARCANIST_LEGGINGS))
                .withPage(new ApparatusPage(ItemsRegistry.ARCANIST_BOOTS))
                .withPage(new ApparatusPage(ItemsRegistry.BATTLEMAGE_HOOD))
                .withPage(new ApparatusPage(ItemsRegistry.BATTLEMAGE_ROBES))
                .withPage(new ApparatusPage(ItemsRegistry.BATTLEMAGE_LEGGINGS))
                .withPage(new ApparatusPage(ItemsRegistry.BATTLEMAGE_BOOTS))
                .withPage(new RelationsPage().withEntry(ARMOR, "armor_upgrade"));
        addPage(ARMOR_ENTRY.withCategory(ARMOR), getPath(ARMOR, "armor"));

        addPage(new PatchouliBuilder(ARMOR, "armor_upgrading")
                .withLocalizedText()
                .withPage(new TextPage(getLangPath("armor_upgrading", 2)).withTitle("ars_nouveau.armor_tiers"))
                .withIcon(ItemsRegistry.ARCANIST_HOOD)
                .withPage(new ApparatusTextPage("ars_nouveau:first_armor_upgrade"))
                .withPage(new ApparatusTextPage("ars_nouveau:second_armor_upgrade"))
                .withPage(new RelationsPage().withEntry(ARMOR, "armor").withEntry(ARMOR, "alteration_table"))

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

        addPage(new PatchouliBuilder(MACHINES, "mob_jar")
                .withIcon(BlockRegistry.MOB_JAR)
                .withLocalizedText()
                .withPage(new TextPage("ars_nouveau.page2.mob_jar").withTitle("ars_nouveau.title.mob_jar"))
                .withPage(new EntityPage(EntityType.VILLAGER).withText("mob_jar.villager"))
                .withPage(new EntityPage(EntityType.PIGLIN).withText("mob_jar.piglin"))
                .withPage(new TextPage("mob_jar.allay").withTitle("mob_jar.allay.title"))
                .withPage(new EntityPage(EntityType.ALLAY))
                .withPage(new EntityPage(EntityType.ENDER_DRAGON).withText("mob_jar.ender_dragon"))
                .withPage(new EntityPage(EntityType.SHEEP).withText("mob_jar.sheep"))
                .withPage(new EntityPage(EntityType.CHICKEN).withText("mob_jar.chicken"))
                .withPage(new EntityPage(EntityType.ARMADILLO).withText("mob_jar.armadillo"))
                .withPage(new EntityPage(EntityType.SNIFFER).withText("mob_jar.sniffer"))
                .withPage(new EntityPage(EntityType.COW).withText("mob_jar.cow"))
                .withPage(new EntityPage(EntityType.MOOSHROOM).withText("mob_jar.mooshroom"))
                .withPage(new EntityPage(EntityType.PUFFERFISH).withText("mob_jar.pufferfish"))
                .withPage(new EntityPage(EntityType.FROG).withText("mob_jar.frog"))
                .withPage(new EntityPage(EntityType.PANDA).withText("mob_jar.panda"))
                .withPage(new EntityPage(EntityType.CAT).withText("mob_jar.cat"))
                .withPage(new EntityPage(EntityType.BREEZE).withText("mob_jar.breeze"))
                .withPage(new EntityPage(EntityType.SNOW_GOLEM).withText("mob_jar.snow_golem"))
                .withPage(new EntityPage(ModEntities.ENTITY_DUMMY.get()).withText("mob_jar.dummy"))
                .withPage(new CraftingPage(BlockRegistry.MOB_JAR))
                .withPage(new RelationsPage().withEntry(RITUALS, RitualLib.CONTAINMENT).withEntry(AUTOMATION, "drygmy_charm")), getPath(MACHINES, "mob_jar"));

        var voidPrism = addBasicItem(BlockRegistry.VOID_PRISM, AUTOMATION, new CraftingPage(BlockRegistry.VOID_PRISM));

        turrets.builder.withPage(new RelationsPage().withEntry(prisms).withEntry(voidPrism));
        prisms.builder.withPage(new RelationsPage().withEntry(turrets).withEntry(voidPrism));
        voidPrism.builder.withPage(new RelationsPage().withEntry(turrets).withEntry(prisms));
        addPage(new PatchouliBuilder(RESOURCES, "illusion_blocks").withIcon(BlockRegistry.GHOST_WEAVE).withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.MIRROR_WEAVE))
                .withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.FALSE_WEAVE))
                .withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.GHOST_WEAVE)
                ).withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.SKY_WEAVE)), getPath(RESOURCES, "illusion_blocks"));

        var bookwyrm = addPage(new PatchouliBuilder(MACHINES, ItemsRegistry.BOOKWYRM_CHARM)
                .withLocalizedText()
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_BOOKWYRM_TYPE.get()).toString())
                        .withText(getLangPath("bookwyrm_charm", 2))), getPath(MACHINES, "bookwyrm_charm"));

        var storageLectern = addPage(new PatchouliBuilder(MACHINES, BlockRegistry.CRAFTING_LECTERN)
                        .withLocalizedText()
                        .withPage(new TextPage(getLangPath("storage", 2)).withTitle("ars_nouveau.storage"))
                        .withPage(new TextPage(getLangPath("storage", 3)).withTitle("ars_nouveau.storage_tabs"))
                        .withPage(new ApparatusPage(BlockRegistry.CRAFTING_LECTERN))
                , getPath(MACHINES, "storage_lectern"));
        bookwyrm.builder.withPage(new RelationsPage().withEntry(storageLectern.relationPath()));

        var displayCase = addPage(new PatchouliBuilder(MACHINES, BlockRegistry.ITEM_DETECTOR)
                .withLocalizedText()
                .withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.ITEM_DETECTOR)), getPath(MACHINES, "item_detector"));
        var repository = addPage(new PatchouliBuilder(MACHINES, BlockRegistry.REPOSITORY)
                .withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.REPOSITORY).withRecipe2(BlockRegistry.ARCHWOOD_CHEST)), getPath(MACHINES, "repository"));

        // add scrolls to arrylist
        var scrollRelations = new ArrayList<PatchouliPage>() {
            {
                add(denyScroll);
                add(mimicScroll);
                add(allowScroll);
            }
        };


        storageLectern.builder.withPage(new RelationsPage()
                .withEntry(bookwyrm.relationPath())
                .withEntry(displayCase.relationPath())
                .withEntry(repository.relationPath()));
        repository.builder.withPage(new RelationsPage().withEntry(storageLectern.relationPath()));


        starby.builder.withPage(new RelationsPage()
                .withEntries(scrollRelations)
                .withEntry(dominionWand)
                .withEntry(storageLectern)
                .withEntry(starbyShades)
                .withEntry(wixieHat)
                .withEntry(starbyBed));
        denyScroll.builder.withPage(new RelationsPage()
                .withEntry(mimicScroll)
                .withEntry(allowScroll)
                .withEntry(dominionWand)
                .withEntry(starby)
                .withEntry(storageLectern)
                .withEntry(displayCase));
        mimicScroll.builder.withPage(new RelationsPage()
                .withEntry(denyScroll)
                .withEntry(allowScroll)
                .withEntry(dominionWand)
                .withEntry(starby)
                .withEntry(storageLectern)
                .withEntry(displayCase));
        allowScroll.builder.withPage(new RelationsPage()
                .withEntry(denyScroll).withEntry(mimicScroll).withEntry(dominionWand).withEntry(starby).withEntry(storageLectern).withEntry(displayCase));

        displayCase.builder.withPage(new RelationsPage().withEntry(bookwyrm).withEntry(storageLectern).withEntry(dominionWand)
                .withEntries(scrollRelations));
        dominionWand.builder.withPage(new RelationsPage().withEntry(storageLectern).withEntry(displayCase).withEntry(starby));

        addBasicItem(BlockRegistry.SPELL_SENSOR, AUTOMATION, new ApparatusPage(BlockRegistry.SPELL_SENSOR));
        addBasicItem(ItemsRegistry.JUMP_RING, EQUIPMENT, new ApparatusPage(ItemsRegistry.JUMP_RING));
        addBasicItem(BlockRegistry.REDSTONE_RELAY, AUTOMATION, new CraftingPage(BlockRegistry.REDSTONE_RELAY));

        addPage(new PatchouliBuilder(AUTOMATION, BlockRegistry.ARCHWOOD_GRATE).withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.ARCHWOOD_GRATE).withRecipe2(BlockRegistry.GOLD_GRATE))
                .withPage(new CraftingPage(BlockRegistry.SMOOTH_SOURCESTONE_GRATE).withRecipe2(BlockRegistry.SOURCESTONE_GRATE)), getPath(AUTOMATION, "grates"));
        addBasicItem(BlockRegistry.SOURCE_LAMP, AUTOMATION, new CraftingPage(BlockRegistry.SOURCE_LAMP));

        addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.ALAKARKINOS_CHARM)
                .withLocalizedText()
                .withPage(new ApparatusPage(ItemsRegistry.ALAKARKINOS_CHARM))
                .withPage(new EntityPage(getRegistryName(ModEntities.ALAKARKINOS_TYPE.get()).toString()).withScale(0.5f)
                        .withText(getLangPath("alakarkinos_charm", 2)))
                .withPage(new TextPage(getLangPath("alakarkinos_charm", 3)).withTitle("ars_nouveau.summoning"))
                .withPage(new TextPage(getLangPath("alakarkinos_charm", 4)).withTitle("ars_nouveau.sifting")), getPath(AUTOMATION, "alakarkinos"));
    }

    public String getLangPath(String name, int count) {
        return "ars_nouveau.page" + count + "." + name;
    }

    public String getLangPath(String name) {
        return "ars_nouveau.page." + name;
    }

    public PatchouliPage addPage(PatchouliBuilder builder, Path path) {
        return addPage(new PatchouliPage(builder, path));
    }

    public PatchouliPage addPage(PatchouliPage patchouliPage) {
        this.pages.add(patchouliPage);
        return patchouliPage;
    }

    public PatchouliBuilder buildBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage) {
        PatchouliBuilder builder = new PatchouliBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withPage(new TextPage("ars_nouveau.page." + getRegistryName(item.asItem()).getPath()));
        if (recipePage != null) {
            builder.withPage(recipePage);
        }
        return builder;
    }

    public PatchouliPage addBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage) {
        PatchouliBuilder builder = buildBasicItem(item, category, recipePage);
        return addPage(new PatchouliPage(builder, getPath(category, getRegistryName(item.asItem()))));
    }

    public Path getPath(ResourceLocation category, ResourceLocation fileName) {
        return this.output.resolve("assets/ars_nouveau/patchouli_books/worn_notebook/en_us/entries/" + category.getPath() + "/" + fileName.getPath() + ".json");
    }

    public Path getPath(ResourceLocation category, String fileName) {
        return this.output.resolve("assets/ars_nouveau/patchouli_books/worn_notebook/en_us/entries/" + category.getPath() + "/" + fileName + ".json");
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();
        for (PatchouliPage patchouliPage : pages) {
            saveStable(pOutput, patchouliPage.build(), patchouliPage.path);
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

        public String relationPath() {
            String fileName = path.getFileName().toString();
            fileName = FilenameUtils.removeExtension(fileName);
            return builder.category.toString() + "/" + fileName;
        }
    }


    public void addGlyphPage(AbstractSpellPart spellPart) {
        ResourceLocation category = switch (spellPart.defaultTier().value) {
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
        this.pages.add(new PatchouliPage(builder, this.output.resolve("assets/" + familiarHolder.getRegistryName().getNamespace() + "/patchouli_books/worn_notebook/en_us/entries/familiars/" + familiarHolder.getRegistryName().getPath() + ".json")));
    }

    public void addRitualPage(AbstractRitual ritual) {
        PatchouliBuilder builder = new PatchouliBuilder(RITUALS, "item." + ritual.getRegistryName().getNamespace() + "." + ritual.getRegistryName().getPath())
                .withIcon(ritual.getRegistryName().toString())
                .withTextPage(ritual.getDescriptionKey())
                .withPage(new CraftingPage(ritual.getRegistryName().toString()));

        this.pages.add(new PatchouliPage(builder, this.output.resolve("assets/" + ritual.getRegistryName().getNamespace() + "/patchouli_books/worn_notebook/en_us/entries/rituals/" + ritual.getRegistryName().getPath() + ".json")));
    }

    public void addEnchantmentPage(ResourceKey<Enchantment> enchKey) {
        var provider = this.registries.join();
        var enchantmentRegistry = provider.lookupOrThrow(Registries.ENCHANTMENT);
        var maybeEnchant = enchantmentRegistry.get(enchKey);
        if (maybeEnchant.isEmpty()) {
            var arsEnchantmentRegistry = EnchantmentProvider.createLookup();
            maybeEnchant = arsEnchantmentRegistry.lookupOrThrow(Registries.ENCHANTMENT).get(enchKey);
        }
        if (maybeEnchant.isEmpty()) {
            return;
        }
        var enchantment = maybeEnchant.get().value();

        var path = enchKey.location().getPath();
        PatchouliBuilder builder = new PatchouliBuilder(ENCHANTMENTS, path)
                .withName(((TranslatableContents) enchantment.description().getContents()).getKey())
                .withIcon(Items.ENCHANTED_BOOK);

        for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++) {
            builder.withPage(new EnchantingPage("ars_nouveau:" + path + "_" + i));
        }
        addPage(builder, getPath(ENCHANTMENTS, path));
    }

    public void addPerkPage(IPerk perk) {
        PerkItem perkItem = PerkRegistry.PERK_ITEMS.get(perk.getRegistryName());
        PatchouliBuilder builder = new PatchouliBuilder(ARMOR, perkItem)
                .withIcon(perkItem)
                .withTextPage(perk.getDescriptionKey())
                .withPage(new ApparatusPage(perkItem)).withSortNum(99);
        this.pages.add(new PatchouliPage(builder, this.output.resolve("assets/" + perk.getRegistryName().getNamespace() + "/patchouli_books/worn_notebook/en_us/entries/armor/" + perk.getRegistryName().getPath() + ".json")));
    }

    List<ResourceKey<Enchantment>> enchants = Arrays.asList(
            Enchantments.AQUA_AFFINITY,
            Enchantments.BANE_OF_ARTHROPODS,
            Enchantments.BLAST_PROTECTION,
            Enchantments.DEPTH_STRIDER,
            Enchantments.EFFICIENCY,
            Enchantments.FEATHER_FALLING,
            Enchantments.FIRE_ASPECT,
            Enchantments.FIRE_PROTECTION,
            Enchantments.FLAME,
            Enchantments.FORTUNE,
            Enchantments.INFINITY,
            Enchantments.KNOCKBACK,
            Enchantments.LOOTING,
            Enchantments.MULTISHOT,
            Enchantments.PIERCING,
            Enchantments.POWER,
            Enchantments.PROJECTILE_PROTECTION,
            Enchantments.PROTECTION,
            Enchantments.PUNCH,
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
