package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.*;
import com.hollingsworth.arsnouveau.api.documentation.builder.DocEntryBuilder;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.registry.*;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.common.datagen.PatchouliProvider;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.*;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.common.perk.EmptyPerk;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class Documentation {

    public static void initOnWorldReload(){
        Level level = ArsNouveau.proxy.getClientWorld();
        RecipeManager manager = level.getRecipeManager();
        Block SOURCESTONE = BlockRegistry.getBlock(LibBlockNames.SOURCESTONE);
        DocCategory MACHINES = DocumentationRegistry.MAGICAL_SYSTEMS;
        DocCategory GETTING_STARTED = DocumentationRegistry.GETTING_STARTED;
        DocCategory EQUIPMENT = DocumentationRegistry.ITEMS_BLOCKS_EQUIPMENT;
        DocCategory AUTOMATION = DocumentationRegistry.MAGICAL_SYSTEMS;
        DocCategory RESOURCES = DocumentationRegistry.CRAFTING;
        DocCategory SOURCE = DocumentationRegistry.SOURCE;
        DocCategory ARMOR = DocumentationRegistry.ITEMS_BLOCKS_EQUIPMENT;
        for(AbstractSpellPart spellPart : GlyphRegistry.getSpellpartMap().values()){
            ItemStack renderStack = spellPart.glyphItem.getDefaultInstance();
            var entry = new DocEntry(spellPart.getRegistryName(), renderStack, Component.literal(spellPart.getLocaleName()));
            entry.addPage(GlyphEntry.create(spellPart));

            var pages = getRecipePages(renderStack, spellPart.getRegistryName());
            entry.addPages(pages);

            DocumentationRegistry.registerEntry(glyphCategory(spellPart.getConfigTier()), entry);
        }


        for (RitualTablet r : RitualRegistry.getRitualItemMap().values()) {
            ItemStack renderStack = r.getDefaultInstance();
            AbstractRitual ritual = r.ritual;

            Component title = Component.translatable("item." + ritual.getRegistryName().getNamespace() + "." + ritual.getRegistryName().getPath());

            var entry = new DocEntry(ritual.getRegistryName(), renderStack,title);

            entry.addPage(TextEntry.create(Component.translatable(ritual.getDescriptionKey()), title, renderStack));

            List<SinglePageCtor> pages = getRecipePages(renderStack, ritual.getRegistryName());
            entry.addPages(pages);

            DocumentationRegistry.registerEntry(DocumentationRegistry.RITUAL_INDEX, entry);
        }

        for (PerkItem perk : PerkRegistry.getPerkItemMap().values()) {
            if(perk.perk instanceof EmptyPerk)
                continue;

            ItemStack renderStack = perk.getDefaultInstance();
            var entry = new DocEntry(perk.perk.getRegistryName(), renderStack, perk.perk.getPerkName());

            entry.addPage(TextEntry.create(Component.translatable(perk.perk.getDescriptionKey()),Component.literal(perk.perk.getName()), renderStack));

            entry.addPages(getRecipePages(renderStack, RegistryHelper.getRegistryName(perk)));


            DocumentationRegistry.registerEntry(DocumentationRegistry.ITEMS_BLOCKS_EQUIPMENT, entry);

        }

        for (AbstractFamiliarHolder r : FamiliarRegistry.getFamiliarHolderMap().values()) {
            ItemStack renderstack = r.getOutputItem();
            var entry = new DocEntry(r.getRegistryName(), renderstack, Component.translatable("entity.ars_nouveau." + r.getRegistryName().getPath()));

            entry.addPage(TextEntry.create(r.getLangDescription(), renderstack.getHoverName(), renderstack));

            DocumentationRegistry.registerEntry(DocumentationRegistry.ITEMS_BLOCKS_EQUIPMENT, entry);
        }

        var dowsingRod = addBasicItem(ItemsRegistry.DOWSING_ROD, EQUIPMENT);

        var imbuementChamber = addPage(new DocEntryBuilder(MACHINES, BlockRegistry.IMBUEMENT_BLOCK)
                .withIntroPage()
                .withCraftingPages()
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_lapis"))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_amethyst"))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_amethyst_block"))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.FIRE_ESSENCE.getRegistryName()))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.EARTH_ESSENCE.getRegistryName()))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.WATER_ESSENCE.getRegistryName()))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.AIR_ESSENCE.getRegistryName()))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.ABJURATION_ESSENCE.getRegistryName()))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.CONJURATION_ESSENCE.getRegistryName()))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.MANIPULATION_ESSENCE.getRegistryName())));

        addPage(new DocEntryBuilder(GETTING_STARTED, "spell_casting")
                .withIcon(ItemsRegistry.NOVICE_SPELLBOOK)
                .withSortNum(1)
                .withLocalizedText()
                .withLocalizedText()
                .withLocalizedText()
                .withCraftingPages(ItemsRegistry.NOVICE_SPELLBOOK));
        addPage(new DocEntryBuilder(GETTING_STARTED, "spell_mana")
                .withSortNum(2)
                .withIcon(ItemsRegistry.NOVICE_SPELLBOOK)
                .withLocalizedText()
                .withLocalizedText());

        addPage(new DocEntryBuilder(GETTING_STARTED, "obtaining_gems")
                .withIcon(BlockRegistry.IMBUEMENT_BLOCK)
                .withSortNum(3)
                .withLocalizedText()
                .withPage(RelationEntry.builder().withEntry(dowsingRod).build()));

//        addPage(new DocEntryBuilder(GETTING_STARTED, "new_glyphs")
//                .withIcon(ItemsRegistry.BLANK_GLYPH)
//                .withSortNum(4)
//                .withLocalizedText()
//                .withPage(RelationEntry.builder().withEntry(MACHINES, "scribes_block").withEntry(GETTING_STARTED, "source")));
//
//        addPage(new DocEntryBuilder(GETTING_STARTED, "source")
//                .withSortNum(5)
//                .withIcon(BlockRegistry.SOURCE_JAR)
//                .withLocalizedText()
//                .withPage(RelationEntry.builder().withEntry(SOURCE, "source_jar").withEntry(SOURCE, "agronomic_sourcelink")));
//
//        addPage(new DocEntryBuilder(GETTING_STARTED, "apparatus_crafting")
//                .withSortNum(6)
//                .withIcon(BlockRegistry.ENCHANTING_APP_BLOCK)
//                .withLocalizedText()
//                .withPage(RelationEntry.builder().withEntry(MACHINES, "enchanting_apparatus")));
//
//        addPage(new DocEntryBuilder(GETTING_STARTED, "better_casting")
//                .withSortNum(7)
//                .withIcon(ItemsRegistry.SORCERER_ROBES)
//                .withLocalizedText()
//                .withPage(RelationEntry.builder().withEntry(MACHINES, "enchanting_apparatus")));
//
//        addPage(new DocEntryBuilder(GETTING_STARTED, "world_generation")
//                        .withSortNum(8)
//                        .withIcon(ItemsRegistry.SOURCE_GEM)
//                        .withLocalizedText()
//                        .withPage(RelationEntry.builder().withEntry(MACHINES, "imbuement_chamber").withEntry(RESOURCES, "archwood").withEntry(RESOURCES, "sourceberry")));
//
//        addPage(new DocEntryBuilder(GETTING_STARTED, "upgrades")
//                .withSortNum(9)
//                .withIcon(ItemsRegistry.ARCHMAGE_SPELLBOOK)
//                .withLocalizedText()
//                .withPage(RelationEntry.builder().withEntry(EQUIPMENT, "spell_books").withEntry(ARMOR, "armor")));
//
//        addPage(new DocEntryBuilder(GETTING_STARTED, "starting_automation")
//                .withSortNum(10)
//                .withIcon(BlockRegistry.BASIC_SPELL_TURRET)
//                .withLocalizedText()
//                .withPage(RelationEntry.builder().withEntry(AUTOMATION, "spell_turret")
//                        .withEntry(AUTOMATION, "spell_prism").withEntry(AUTOMATION, "starbuncle_charm")
//                        .withEntry(AUTOMATION, "wixie_charm")));
//
//        addPage(new DocEntryBuilder(GETTING_STARTED, "trinkets")
//                .withIcon(ItemsRegistry.WARP_SCROLL)
//                .withSortNum(11)
//                .withLocalizedText()
//                .withPage(RelationEntry.builder()
//                        .withEntry(EQUIPMENT, "ring_of_greater_discount")
//                        .withEntry(EQUIPMENT, "jar_of_light")
//                        .withEntry(EQUIPMENT, "void_jar")
//                        .withEntry(EQUIPMENT, "warp_scroll")));



        addBasicItem(ItemsRegistry.AMULET_OF_MANA_BOOST, EQUIPMENT);
        addBasicItem(ItemsRegistry.AMULET_OF_MANA_REGEN, EQUIPMENT);
        addBasicItem(ItemsRegistry.BELT_OF_LEVITATION, EQUIPMENT);
        addBasicItem(ItemsRegistry.BELT_OF_UNSTABLE_GIFTS, EQUIPMENT);
        addBasicItem(ItemsRegistry.JAR_OF_LIGHT, EQUIPMENT);
        var starby = addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.STARBUNCLE_CHARM)
                .withLocalizedText()
                .withCraftingPages(ItemsRegistry.STARBUNCLE_CHARM)
                .withPage(new EntityPage(getRegistryName(ModEntities.STARBUNCLE_TYPE.get()).toString())
                        .withText(getLangPath("starbuncle_charm", 2)))
                .withPage(TextEntry.create(getLangPath("starbuncle_charm", 3), Component.translatable("ars_nouveau.summoning")))
                .withPage(TextEntry.create(getLangPath("starbuncle_charm", 4), Component.translatable("ars_nouveau.item_transport")))
                .withPage(TextEntry.create(getLangPath("starbuncle_charm", 5), Component.translatable("ars_nouveau.filtering")))
                .withPage(TextEntry.create(getLangPath("starbuncle_charm", 6), Component.translatable("ars_nouveau.pathing")))
                .withPage(TextEntry.create(getLangPath("starbuncle_charm", 7), Component.translatable("ars_nouveau.starbuncle_bed")))
                .withPage(TextEntry.create(getLangPath("starbuncle_charm", 8), Component.translatable("ars_nouveau.starbuncle_stacking"))));

        addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.DRYGMY_CHARM)
                .withLocalizedText()
                .withCraftingPages(ItemsRegistry.DRYGMY_CHARM)
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_DRYGMY.get()).toString())
                        .withText(getLangPath("drygmy_charm", 2)))
                .withPage(TextEntry.create(getLangPath("drygmy_charm", 3), Component.translatable("ars_nouveau.summoning")))
                .withPage(TextEntry.create(getLangPath("drygmy_charm", 4), Component.translatable("ars_nouveau.happiness")))
                .withPage(TextEntry.create(getLangPath("drygmy_charm", 5), Component.translatable(("ars_nouveau.production"))))
                .withPage(new RelationsPage().withEntry(MACHINES, "mob_jar")));

        addPage(new DocEntryBuilder(EQUIPMENT, ItemsRegistry.DULL_TRINKET)
                .withPage(CraftingEntry.create(ItemsRegistry.DULL_TRINKET, ItemsRegistry.MUNDANE_BELT))
                .withCraftingPages(ItemsRegistry.RING_OF_POTENTIAL));

        addPage(new DocEntryBuilder(RESOURCES, BlockRegistry.MAGE_BLOOM_CROP)
                .withTextPage("ars_nouveau.page.magebloom_crop")
                .withCraftingPages(BlockRegistry.MAGE_BLOOM_CROP)
                .withPage(CraftingEntry.create(ItemsRegistry.MAGE_FIBER, BlockRegistry.MAGEBLOOM_BLOCK)));

        addPage(new DocEntryBuilder(EQUIPMENT, ItemsRegistry.POTION_FLASK)
                .withLocalizedText()
                .withCraftingPages(ItemsRegistry.POTION_FLASK)
                .withLocalizedText()
                .withCraftingPages(ItemsRegistry.POTION_FLASK_EXTEND_TIME)
                .withLocalizedText()
                .withCraftingPages(ItemsRegistry.POTION_FLASK_AMPLIFY));

        addPage(new DocEntryBuilder(EQUIPMENT, "reactive_enchantment")
                .withIcon(Items.ENCHANTED_BOOK)
                .withLocalizedText()
                .withPage(new EnchantingPage("ars_nouveau:" + EnchantmentRegistry.REACTIVE_ENCHANTMENT.location().getPath() + "_" + 1))
                .withLocalizedText()
                .withPage(new EnchantingPage("ars_nouveau:" + EnchantmentRegistry.REACTIVE_ENCHANTMENT.location().getPath() + "_" + 2))
                .withPage(new EnchantingPage("ars_nouveau:" + EnchantmentRegistry.REACTIVE_ENCHANTMENT.location().getPath() + "_" + 3))
                .withPage(new EnchantingPage("ars_nouveau:" + EnchantmentRegistry.REACTIVE_ENCHANTMENT.location().getPath() + "_" + 4))
                .withLocalizedText()
                .withCraftingPages(ResourceLocation.parse("ars_nouveau:spell_write")));

        addBasicItem(ItemsRegistry.RING_OF_GREATER_DISCOUNT, EQUIPMENT);
        addBasicItem(ItemsRegistry.RING_OF_LESSER_DISCOUNT, EQUIPMENT);

        var turrets = addPage(new DocEntryBuilder(AUTOMATION, BlockRegistry.BASIC_SPELL_TURRET)
                .withLocalizedText()
                .withPage(CraftingEntry.create(BlockRegistry.BASIC_SPELL_TURRET, BlockRegistry.ROTATING_TURRET))
                .withLocalizedText()
                .withCraftingPages(BlockRegistry.ENCHANTED_SPELL_TURRET)
                .withLocalizedText()
                .withCraftingPages(BlockRegistry.TIMER_SPELL_TURRET)
                .withLocalizedText());

        addBasicItem(ItemsRegistry.SUMMONING_FOCUS, EQUIPMENT);
        addBasicItem(ItemsRegistry.VOID_JAR, EQUIPMENT);
        addBasicItem(ItemsRegistry.WAND, EQUIPMENT);
        addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.WHIRLISPRIG_CHARM)
                .withLocalizedText()
                .withCraftingPages(ItemsRegistry.WHIRLISPRIG_CHARM)
                .withPage(new EntityPage(getRegistryName(ModEntities.WHIRLISPRIG_TYPE.get()).toString())
                        .withText(getLangPath("whirlisprig_charm", 2)))
                .withPage(TextEntry.create(getLangPath("whirlisprig_charm", 3), Component.translatable(("ars_nouveau.summoning"))))
                .withPage(TextEntry.create(getLangPath("whirlisprig_charm", 4), Component.translatable(("ars_nouveau.happiness"))))
                .withPage(TextEntry.create(getLangPath("whirlisprig_charm", 5), Component.translatable(("ars_nouveau.important"))))
                .withPage(TextEntry.create(getLangPath("whirlisprig_charm", 6), Component.translatable(("ars_nouveau.production")))));

        var wixie = addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.WIXIE_CHARM)
                .withLocalizedText()
                .withCraftingPages(ItemsRegistry.WIXIE_CHARM)
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_WIXIE_TYPE.get()).toString())
                        .withText(getLangPath("wixie_charm", 2)))
                .withPage(TextEntry.create(getLangPath("wixie_charm", 3), Component.translatable(("ars_nouveau.item_crafting"))))
                .withPage(TextEntry.create(getLangPath("wixie_charm", 4), Component.translatable(("ars_nouveau.item_crafting_setting"))))
                .withPage(TextEntry.create(getLangPath("wixie_charm", 7), Component.translatable(("ars_nouveau.binding_inventories"))))
                .withPage(TextEntry.create(getLangPath("wixie_charm", 5), Component.translatable(("ars_nouveau.potion_crafting"))))
                .withPage(TextEntry.create(getLangPath("wixie_charm", 6))));

        addPage(new DocEntryBuilder(RESOURCES, "archwood")
                        .withIcon(BlockRegistry.BOMBEGRANTE_POD)
                        .withLocalizedText()
                        .withPage(new SpotlightPage(BlockRegistry.BOMBEGRANTE_POD).withText("ars_nouveau.page.bombegrante").linkRecipe(true))
                        .withPage(new SpotlightPage(BlockRegistry.MENDOSTEEN_POD).withText("ars_nouveau.page.mendosteen").linkRecipe(true))
                        .withPage(new SpotlightPage(BlockRegistry.FROSTAYA_POD).withText("ars_nouveau.page.frostaya").linkRecipe(true))
                        .withPage(new SpotlightPage(BlockRegistry.BASTION_POD).withText("ars_nouveau.page.bastion_fruit").linkRecipe(true)));

        addPage(new DocEntryBuilder(RESOURCES, "archwood_forest")
                .withIcon(BlockRegistry.BLAZING_SAPLING)
                .withLocalizedText());


        addPage(new DocEntryBuilder(RESOURCES, "decorative")
                .withIcon(SOURCESTONE)
                .withCraftingPages(SOURCESTONE)
                .withLocalizedText());


        addPage(new DocEntryBuilder(RESOURCES, BlockRegistry.SOURCEBERRY_BUSH)
                .withLocalizedText()
                .withPage(CraftingEntry.create(ItemsRegistry.SOURCE_BERRY_PIE, ItemsRegistry.SOURCE_BERRY_ROLL)));

        addPage(new DocEntryBuilder(RESOURCES, "weald_walker")
                .withIcon(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix( RitualLib.AWAKENING)))
                .withLocalizedText()
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_BLAZING_WEALD.get()).toString()).withText(getLangPath("weald_walker", 2)))
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_CASCADING_WEALD.get()).toString()).withText(getLangPath("weald_walker", 3)))
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_FLOURISHING_WEALD.get()).toString()).withText(getLangPath("weald_walker", 4)))
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_VEXING_WEALD.get()).toString()).withText(getLangPath("weald_walker", 5))));

        addPage(new DocEntryBuilder(RESOURCES, "wilden")
                .withIcon(ItemsRegistry.WILDEN_SPIKE)
                .withLocalizedText()
                .withPage(new EntityPage(getRegistryName(ModEntities.WILDEN_HUNTER.get()).toString()).withScale(0.55f).withText(getLangPath("wilden", 3)))
                .withPage(new EntityPage(getRegistryName(ModEntities.WILDEN_STALKER.get()).toString()).withScale(0.55f).withText(getLangPath("wilden", 4)))
                .withPage(new EntityPage(getRegistryName(ModEntities.WILDEN_GUARDIAN.get()).toString()).withScale(0.55f).withText(getLangPath("wilden", 5)))
                .withPage(new EntityPage(getRegistryName(ModEntities.WILDEN_BOSS.get()).toString()).withScale(0.55f).withText(getLangPath("wilden", 6)))
                .withPage(TextEntry.create(getLangPath("wilden", 7))));

        var denyScroll = addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.DENY_ITEM_SCROLL)
                .withLocalizedText()
                .withPage(CraftingEntry.create(ItemsRegistry.BLANK_PARCHMENT, ItemsRegistry.DENY_ITEM_SCROLL)));

        var mimicScroll = addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.MIMIC_ITEM_SCROLL)
                .withLocalizedText()
                .withPage(getRecipePages(ItemsRegistry.BLANK_PARCHMENT, ItemsRegistry.MIMIC_ITEM_SCROLL)));
        var allowScroll = addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.ALLOW_ITEM_SCROLL)
                .withLocalizedText()
                .withPage(getRecipePages(ItemsRegistry.BLANK_PARCHMENT, ItemsRegistry.ALLOW_ITEM_SCROLL)));

        var dominionWand = addBasicItem(ItemsRegistry.DOMINION_ROD, AUTOMATION);


        var prisms = addBasicItem(BlockRegistry.SPELL_PRISM, AUTOMATION);

        addPage(new DocEntryBuilder(RESOURCES, BlockRegistry.MAGELIGHT_TORCH)
                .withLocalizedText()
                .withPage(getRecipePages(BlockRegistry.GOLD_SCONCE_BLOCK, BlockRegistry.SOURCESTONE_SCONCE_BLOCK))
                .withPage(getRecipePages(BlockRegistry.POLISHED_SCONCE_BLOCK, BlockRegistry.ARCHWOOD_SCONCE_BLOCK))
                .withCraftingPages(BlockRegistry.MAGELIGHT_TORCH));

        addPage(new DocEntryBuilder(EQUIPMENT, "spell_books")
                .withIcon(ItemsRegistry.ARCHMAGE_SPELLBOOK)
                .withLocalizedText()
                .withPage(getRecipePages(RegistryHelper.getRegistryName(ItemsRegistry.NOVICE_SPELLBOOK.asItem()), ArsNouveau.prefix("apprentice_book_upgrade")))
                .withCraftingPages(ArsNouveau.prefix("archmage_book_upgrade")));

        addBasicItem(ItemsRegistry.ENCHANTERS_MIRROR, EQUIPMENT);
        addBasicItem(ItemsRegistry.ENCHANTERS_SHIELD, EQUIPMENT);
        addBasicItem(ItemsRegistry.ENCHANTERS_SWORD, EQUIPMENT);
        addPage(new DocEntryBuilder(EQUIPMENT, ItemsRegistry.SPELL_BOW)
                .withLocalizedText()
                .withCraftingPages(ItemsRegistry.SPELL_BOW)
                .withCraftingPages("ars_nouveau:imbuement_amplify_arrow")
                .withCraftingPages("ars_nouveau:imbuement_pierce_arrow")
                .withCraftingPages("ars_nouveau:imbuement_split_arrow"));
        addPage(new DocEntryBuilder(EQUIPMENT, ItemsRegistry.SPELL_CROSSBOW)
                .withLocalizedText()
                .withCraftingPages(ItemsRegistry.SPELL_CROSSBOW)
                .withCraftingPages("ars_nouveau:imbuement_amplify_arrow")
                .withCraftingPages("ars_nouveau:imbuement_pierce_arrow")
                .withCraftingPages("ars_nouveau:imbuement_split_arrow"));
        addBasicItem(ItemsRegistry.RUNIC_CHALK, EQUIPMENT);

        addPage(new DocEntryBuilder(MACHINES, BlockRegistry.IMBUEMENT_BLOCK)
                        .withLocalizedText()
                        .withCraftingPages(BlockRegistry.IMBUEMENT_BLOCK)
                        .withCraftingPages("ars_nouveau:imbuement_lapis")
                        .withCraftingPages("ars_nouveau:imbuement_amethyst")
                        .withCraftingPages("ars_nouveau:imbuement_amethyst_block")
                        .withCraftingPages("ars_nouveau:imbuement_" + ItemsRegistry.FIRE_ESSENCE.getRegistryName())
                        .withCraftingPages("ars_nouveau:imbuement_" + ItemsRegistry.EARTH_ESSENCE.getRegistryName())
                        .withCraftingPages("ars_nouveau:imbuement_" + ItemsRegistry.WATER_ESSENCE.getRegistryName())
                        .withCraftingPages("ars_nouveau:imbuement_" + ItemsRegistry.AIR_ESSENCE.getRegistryName())
                        .withCraftingPages("ars_nouveau:imbuement_" + ItemsRegistry.ABJURATION_ESSENCE.getRegistryName())
                        .withCraftingPages("ars_nouveau:imbuement_" + ItemsRegistry.CONJURATION_ESSENCE.getRegistryName())
                        .withCraftingPages("ars_nouveau:imbuement_" + ItemsRegistry.MANIPULATION_ESSENCE.getRegistryName()));

        addPage(new DocEntryBuilder(MACHINES, BlockRegistry.ENCHANTING_APP_BLOCK)
                .withLocalizedText()
                .withPage(getRecipePages(BlockRegistry.ARCANE_PEDESTAL, BlockRegistry.ARCANE_PLATFORM))
                .withPage(getRecipePages(BlockRegistry.ENCHANTING_APP_BLOCK, BlockRegistry.ARCANE_CORE_BLOCK)));

        var potionJar = addBasicItem(BlockRegistry.POTION_JAR, MACHINES);
        var melder = addBasicItem(BlockRegistry.POTION_MELDER, MACHINES);
        var diffuser = addBasicItem(BlockRegistry.POTION_DIFFUSER, MACHINES);

        wixie.builder.withPage(new RelationsPage().withEntry(potionJar).withEntry(melder).withEntry(diffuser));
        potionJar.builder.withPage(new RelationsPage().withEntry(melder).withEntry(diffuser).withEntry(wixie));
        melder.builder.withPage(new RelationsPage().withEntry(potionJar).withEntry(diffuser).withEntry(wixie));
        diffuser.builder.withPage(new RelationsPage().withEntry(potionJar).withEntry(melder).withEntry(wixie));

        addBasicItem(BlockRegistry.RITUAL_BLOCK, MACHINES);
        addBasicItem(BlockRegistry.BRAZIER_RELAY, MACHINES);
        addPage(new DocEntryBuilder(MACHINES, BlockRegistry.SCRIBES_BLOCK)
                .withPage(TextEntry.create(getLangPath("scribes_table", 1), Component.translatable("ars_nouveau.glyph_crafting")))
                .withPage(TextEntry.create(getLangPath("scribes_table", 2), Component.translatable("ars_nouveau.scribing")))
                .withPage(getRecipePages(ItemsRegistry.BLANK_PARCHMENT, BlockRegistry.SCRIBES_BLOCK)));

        var portal = addPage(new DocEntryBuilder(MACHINES, "warp_portal")
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
                        .withMapping("0", "ars_nouveau:portal").withText(getLangPath("warp_portal", 4))));
        var scroll = addBasicItem(ItemsRegistry.WARP_SCROLL, EQUIPMENT);

        var stableScroll = addBasicItem(ItemsRegistry.STABLE_WARP_SCROLL, EQUIPMENT);
        stableScroll.builder.withPage(new RelationsPage().withEntry(scroll.relationPath()).withEntry(portal.relationPath()));

        scroll.builder.withPage(new RelationsPage()
                .withEntry(stableScroll.relationPath())
                .withEntry(portal.relationPath()));

        portal.builder.withPage(new RelationsPage().withEntry(scroll.relationPath()).withEntry(stableScroll.relationPath()));

        addBasicItem(BlockRegistry.AGRONOMIC_SOURCELINK, SOURCE);
        addBasicItem(BlockRegistry.ALCHEMICAL_BLOCK, SOURCE);
        addBasicItem(BlockRegistry.MYCELIAL_BLOCK, SOURCE);
        addBasicItem(BlockRegistry.SOURCE_JAR, SOURCE);

        addBasicItem(BlockRegistry.RELAY, SOURCE, new CraftingPage(BlockRegistry.RELAY).withText("ars_nouveau.page2.relay"));

        addBasicItem(BlockRegistry.RELAY_DEPOSIT, SOURCE);
        addBasicItem(BlockRegistry.RELAY_SPLITTER, SOURCE);
        addBasicItem(BlockRegistry.RELAY_WARP, SOURCE);
        addBasicItem(BlockRegistry.RELAY_COLLECTOR, SOURCE);
        addBasicItem(BlockRegistry.VITALIC_BLOCK, SOURCE);
        addPage(new DocEntryBuilder(SOURCE, BlockRegistry.VOLCANIC_BLOCK)
                .withLocalizedText()
                .withPage(TextEntry.create(getLangPath("volcanic_sourcelink", 2)).withTitle("ars_nouveau.active_generation"))
                .withPage(TextEntry.create(getLangPath("volcanic_sourcelink", 3)).withTitle("ars_nouveau.heat"))
                .withCraftingPages(BlockRegistry.VOLCANIC_BLOCK)
                .withPage(TextEntry.create(getLangPath("volcanic_sourcelink", 4))));
        addPage(new DocEntryBuilder(ENCHANTMENTS, "how_to_enchant")
                .withIcon(BlockRegistry.ENCHANTING_APP_BLOCK)
                .withSortNum(-1)
                .withLocalizedText()
                .withLocalizedText()
                .withPage(new RelationsPage().withEntry(MACHINES, "enchanting_apparatus")));
        addPage(new DocEntryBuilder(RITUALS, "performing_rituals")
                .withSortNum(-1)
                .withIcon(BlockRegistry.RITUAL_BLOCK)
                .withLocalizedText()
                .withLocalizedText()
                .withPage(new CraftingPage(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix( RitualLib.SUNRISE))))
                .withPage(new RelationsPage().withEntry(MACHINES, "ritual_brazier")));
        addPage(new DocEntryBuilder(FAMILIARS, "summoning_familiars")
                .withSortNum(-1)
                .withIcon(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix( RitualLib.BINDING)))
                .withLocalizedText()
                .withLocalizedText()
                .withPage(new CraftingPage(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix( RitualLib.BINDING))))
                .withPage(new RelationsPage().withEntry(MACHINES, "ritual_brazier").withEntry(RITUALS, "ritual_binding")));

        addPage(new DocEntryBuilder(MOD_NEWS, "mod_news")
                        .withIcon(ItemsRegistry.SPELL_PARCHMENT)
                        .withPage(new LinkPage("https://discord.gg/y7TMXZu", "ars_nouveau.discord_text", "ars_nouveau.community")));


        addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.AMETHYST_GOLEM_CHARM)
                        .withLocalizedText()
                        .withPage(new EntityPage(getRegistryName(ModEntities.AMETHYST_GOLEM.get()).toString())
                                .withText(getLangPath("amethyst_golem_charm", 2)).withScale(0.75f).withOffset(0.2f))
                        .withPage(TextEntry.create(getLangPath("amethyst_golem_charm", 3)).withTitle("ars_nouveau.summoning"))
                        .withPage(TextEntry.create(getLangPath("amethyst_golem_charm", 4)).withTitle("ars_nouveau.amethyst_farming"))
                        .withPage(TextEntry.create(getLangPath("amethyst_golem_charm", 5)).withTitle("ars_nouveau.amethyst_storage")));
        addBasicItem(ItemsRegistry.ANNOTATED_CODEX, EQUIPMENT);
        var starbyBed = addPage(new DocEntryBuilder(AUTOMATION, BlockRegistry.ORANGE_SBED).withName("ars_nouveau.summon_bed").withLocalizedText("summon_bed")
                        .withPage(new CraftingPage(BlockRegistry.ORANGE_SBED).withRecipe2(BlockRegistry.BLUE_SBED))
                        .withPage(new CraftingPage(BlockRegistry.GREEN_SBED).withRecipe2(BlockRegistry.YELLOW_SBED))
                        .withPage(new CraftingPage(BlockRegistry.RED_SBED).withRecipe2(BlockRegistry.PURPLE_SBED)));
        var scryCaster = addBasicItem(ItemsRegistry.SCRY_CASTER, EQUIPMENT);
        var scryCrystal = addBasicItem(BlockRegistry.SCRYERS_CRYSTAL, MACHINES);
        var oculus = addBasicItem(BlockRegistry.SCRYERS_OCULUS, MACHINES);
        var scryScroll = addBasicItem(ItemsRegistry.SCRYER_SCROLL, MACHINES);

        scryCrystal.builder.withPage(new RelationsPage().withEntry(scryCaster).withEntry(scryScroll).withEntry(oculus));
        scryCaster.builder.withPage(new RelationsPage().withEntry(scryCrystal.relationPath()));
        oculus.builder.withPage(new RelationsPage().withEntry(scryScroll).withEntry(scryCrystal));
        scryScroll.builder.withPage(new RelationsPage().withEntry(scryCaster).withEntry(oculus).withEntry(scryCrystal));
        var starbyShades = addBasicItem(ItemsRegistry.STARBUNCLE_SHADES, AUTOMATION, new CraftingPage(ItemsRegistry.STARBUNCLE_SHADES));
        var wixieHat = addBasicItem(ItemsRegistry.WIXIE_HAT, AUTOMATION);

        addPage(new DocEntryBuilder(MOD_NEWS, "support_mod")
                        .withIcon(ItemsRegistry.STARBUNCLE_CHARM)
                        .withPage(new LinkPage("https://www.patreon.com/arsnouveau", "ars_nouveau.patreon_text", "ars_nouveau.patreon"))
                        .withPage(new EntityPage(ModEntities.LILY.get()).withText("ars_nouveau.lily"))
                        .withPage(new EntityPage(ModEntities.NOOK.get()).withText("ars_nouveau.nook"))
                        .withPage(new LinkPage("https://www.redbubble.com/people/Gootastic/explore?page=1&sortOrder=recent", "ars_nouveau.store_text", "ars_nouveau.store")));


        addPage(new DocEntryBuilder(EQUIPMENT, ItemsRegistry.SHAPERS_FOCUS)
                .withLocalizedText()
                .withCraftingPages(ItemsRegistry.SHAPERS_FOCUS)
                .withPage(TextEntry.create(getLangPath("shapers_focus", 2), Component.translatable("ars_nouveau.shapers_focus.blocks")))
                .withPage(TextEntry.create(getLangPath("shapers_focus", 3), Component.translatable("ars_nouveau.shapers_focus.entities")))
                .withPage(TextEntry.create(getLangPath("shapers_focus", 4), Component.translatable("ars_nouveau.shapers_focus.examples"))));
        addBasicItem(ItemsRegistry.ALCHEMISTS_CROWN, EQUIPMENT);
        addPage(new DocEntryBuilder(EQUIPMENT,  "flask_cannons")
                .withLocalizedText()
                .withIcon(ItemsRegistry.SPLASH_LAUNCHER)
                .withCraftingPages(ItemsRegistry.SPLASH_LAUNCHER)
                .withCraftingPages(ItemsRegistry.LINGERING_LAUNCHER));

        DocEntryBuilder ARMOR_ENTRY = new DocEntryBuilder(EQUIPMENT, "armor")
                .withIcon(ItemsRegistry.SORCERER_ROBES)
                .withLocalizedText()
                .withPage(new CraftingPage(ItemsRegistry.MAGE_FIBER))
                .withPage(TextEntry.create("ars_nouveau.page.threads").withTitle("ars_nouveau.threads"))
                .withPage(new ImagePage().withEntry(ArsNouveau.prefix( "textures/gui/entries/sorcerer_diagram.png"))
                        .withEntry(ArsNouveau.prefix( "textures/gui/entries/arcanist_thread_diagram.png"))
                        .withEntry(ArsNouveau.prefix( "textures/gui/entries/battlemage_diagram.png"))
                        .withBorder().withTitle("ars_nouveau.thread_layout")
                        .withText("ars_nouveau.page.layout_desc"))
                .withCraftingPages(ItemsRegistry.SORCERER_HOOD)
                .withCraftingPages(ItemsRegistry.SORCERER_ROBES)
                .withCraftingPages(ItemsRegistry.SORCERER_LEGGINGS)
                .withCraftingPages(ItemsRegistry.SORCERER_BOOTS)
                .withCraftingPages(ItemsRegistry.ARCANIST_HOOD)
                .withCraftingPages(ItemsRegistry.ARCANIST_ROBES)
                .withCraftingPages(ItemsRegistry.ARCANIST_LEGGINGS)
                .withCraftingPages(ItemsRegistry.ARCANIST_BOOTS)
                .withCraftingPages(ItemsRegistry.BATTLEMAGE_HOOD)
                .withCraftingPages(ItemsRegistry.BATTLEMAGE_ROBES)
                .withCraftingPages(ItemsRegistry.BATTLEMAGE_LEGGINGS)
                .withCraftingPages(ItemsRegistry.BATTLEMAGE_BOOTS)
                .withPage(new RelationsPage().withEntry(ARMOR, "armor_upgrade"));
        addPage(ARMOR_ENTRY.withCategory(ARMOR));

        addPage(new DocEntryBuilder(ARMOR, "armor_upgrading")
                .withLocalizedText()
                .withPage(TextEntry.create(getLangPath("armor_upgrading", 2)).withTitle("ars_nouveau.armor_tiers"))
                .withIcon(ItemsRegistry.ARCANIST_HOOD)
                .withPage(new ApparatusTextPage("ars_nouveau:first_armor_upgrade"))
                .withPage(new ApparatusTextPage("ars_nouveau:second_armor_upgrade"))
                .withPage(new RelationsPage().withEntry(ARMOR, "armor").withEntry(ARMOR, "alteration_table"))
                .withSortNum(1));
        addPage(new DocEntryBuilder(ARMOR, "applying_perks")
                .withLocalizedText()
                .withLocalizedText()
                .withPage(new RelationsPage().withEntry(ARMOR, "alteration_table"))
                .withIcon(ItemsRegistry.BLANK_THREAD)
                .withSortNum(2));

        addPage(new DocEntryBuilder(ARMOR, "alteration_table")
                .withLocalizedText()
                .withPage(new CraftingPage(BlockRegistry.ALTERATION_TABLE).withRecipe2(ItemsRegistry.BLANK_THREAD))
                .withPage(new RelationsPage().withEntry(ARMOR, "armor_upgrade").withEntry(ARMOR, "armor"))
                .withIcon(BlockRegistry.ALTERATION_TABLE)
                .withSortNum(3));

        addPage(new DocEntryBuilder(MACHINES, "mob_jar")
                .withIcon(BlockRegistry.MOB_JAR)
                .withLocalizedText()
                .withPage(TextEntry.create("ars_nouveau.page2.mob_jar").withTitle("ars_nouveau.title.mob_jar"))
                .withPage(new EntityPage(EntityType.VILLAGER).withText("mob_jar.villager"))
                .withPage(new EntityPage(EntityType.PIGLIN).withText("mob_jar.piglin"))
                .withPage(TextEntry.create("mob_jar.allay").withTitle("mob_jar.allay.title"))
                .withPage(new EntityPage(EntityType.ALLAY))
                .withPage(new EntityPage(EntityType.ENDER_DRAGON).withText("mob_jar.ender_dragon"))
                .withPage(new EntityPage(EntityType.SHEEP).withText("mob_jar.sheep"))
                .withPage(new EntityPage(EntityType.CHICKEN).withText("mob_jar.chicken"))
                .withPage(new EntityPage(EntityType.COW).withText("mob_jar.cow"))
                .withPage(new EntityPage(EntityType.MOOSHROOM).withText("mob_jar.mooshroom"))
                .withPage(new EntityPage(EntityType.PUFFERFISH).withText("mob_jar.pufferfish"))
                .withPage(new EntityPage(EntityType.FROG).withText("mob_jar.frog"))
                .withPage(new EntityPage(EntityType.PANDA).withText("mob_jar.panda"))
                .withPage(new EntityPage(ModEntities.ENTITY_DUMMY.get()).withText("mob_jar.dummy"))
                .withPage(new CraftingPage(BlockRegistry.MOB_JAR))
                .withPage(new RelationsPage().withEntry(RITUALS, RitualLib.CONTAINMENT).withEntry(AUTOMATION, "drygmy_charm")));

        var voidPrism = addBasicItem(BlockRegistry.VOID_PRISM, AUTOMATION);

        turrets.builder.withPage(new RelationsPage().withEntry(prisms).withEntry(voidPrism));
        prisms.builder.withPage(new RelationsPage().withEntry(turrets).withEntry(voidPrism));
        voidPrism.builder.withPage(new RelationsPage().withEntry(turrets).withEntry(prisms));
        addPage(new DocEntryBuilder(RESOURCES, "illusion_blocks").withIcon(BlockRegistry.GHOST_WEAVE).withLocalizedText()
                .withCraftingPages(BlockRegistry.MIRROR_WEAVE)
                .withLocalizedText()
                .withCraftingPages(BlockRegistry.FALSE_WEAVE)
                .withLocalizedText()
                .withCraftingPages(BlockRegistry.GHOST_WEAVE)
                .withLocalizedText()
                .withCraftingPages(BlockRegistry.SKY_WEAVE));

        var bookwyrm = addPage(new DocEntryBuilder(MACHINES, ItemsRegistry.BOOKWYRM_CHARM)
                .withLocalizedText()
                .withPage(new EntityPage(getRegistryName(ModEntities.ENTITY_BOOKWYRM_TYPE.get()).toString())
                        .withText(getLangPath("bookwyrm_charm", 2))));

        var storageLectern = addPage(new DocEntryBuilder(MACHINES, BlockRegistry.CRAFTING_LECTERN)
                        .withLocalizedText()
                        .withPage(TextEntry.create(getLangPath("storage", 2), Component.translatable("ars_nouveau.storage")))
                        .withPage(TextEntry.create(getLangPath("storage", 3), Component.translatable("ars_nouveau.storage_tabs")))
                .withCraftingPages(BlockRegistry.CRAFTING_LECTERN));
        bookwyrm.builder.withPage(new RelationsPage().withEntry(storageLectern.relationPath()));

        var displayCase = addPage(new DocEntryBuilder(MACHINES, BlockRegistry.ITEM_DETECTOR)
                .withLocalizedText()
                .withLocalizedText()
                .withCraftingPages(BlockRegistry.ITEM_DETECTOR));
        var repository = addPage(new DocEntryBuilder(MACHINES, BlockRegistry.REPOSITORY)
                .withLocalizedText()
                .withPage(CraftingEntry.create(BlockRegistry.REPOSITORY, BlockRegistry.ARCHWOOD_CHEST)));

        // add scrolls to arrylist
        var scrollRelations = new ArrayList<PatchouliProvider.PatchouliPage>(){
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

        addPage(new DocEntryBuilder(AUTOMATION, BlockRegistry.SPELL_SENSOR).withLocalizedText().withCraftingPages(BlockRegistry.SPELL_SENSOR));
        addPage(new DocEntryBuilder(EQUIPMENT, ItemsRegistry.JUMP_RING).withLocalizedText().withCraftingPages(ItemsRegistry.JUMP_RING));
        addPage(new DocEntryBuilder(AUTOMATION, BlockRegistry.REDSTONE_RELAY).withLocalizedText().withCraftingPages(BlockRegistry.REDSTONE_RELAY));

        addPage(new DocEntryBuilder(AUTOMATION, BlockRegistry.ARCHWOOD_GRATE).withLocalizedText()
                .withPage(CraftingEntry.create(BlockRegistry.ARCHWOOD_GRATE, BlockRegistry.GOLD_GRATE))
                .withPage(CraftingEntry.create(BlockRegistry.SMOOTH_SOURCESTONE_GRATE, BlockRegistry.SOURCESTONE_GRATE)));
        addPage(new DocEntryBuilder(AUTOMATION, BlockRegistry.SOURCE_LAMP).withLocalizedText().withCraftingPages(BlockRegistry.SOURCE_LAMP));

        addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.ALAKARKINOS_CHARM)
                .withLocalizedText()
                .withCraftingPages(ItemsRegistry.ALAKARKINOS_CHARM)
                .withPage(new EntityPage(getRegistryName(ModEntities.ALAKARKINOS_TYPE.get()).toString()).withScale(0.5f)
                        .withText(getLangPath("alakarkinos_charm", 2)))
                .withPage(TextEntry.create(getLangPath("alakarkinos_charm", 3), Component.translatable("ars_nouveau.summoning")))
                .withPage(TextEntry.create(getLangPath("alakarkinos_charm", 4), Component.translatable("ars_nouveau.sifting"))));

        NeoForge.EVENT_BUS.post(new ReloadDocumentationEvent.AddEntries());
        NeoForge.EVENT_BUS.post(new ReloadDocumentationEvent.Post());
    }

    private static DocEntry addPage(DocEntryBuilder builder){
        return DocumentationRegistry.registerEntry(builder.category, builder.build());
    }

    public static DocEntry addBasicItem(ItemLike item, DocCategory category) {
        return addPage(new DocEntryBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withPage(TextEntry.create(Component.translatable("ars_nouveau.page." + getRegistryName(item.asItem()).getPath()))).withCraftingPages(item));
    }

    public static DocEntry addBasicItem(ItemLike item, DocCategory category, ResourceLocation recipeId) {
        return addPage(new DocEntryBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withPage(TextEntry.create(Component.translatable("ars_nouveau.page." + getRegistryName(item.asItem()).getPath()))).withPage(getRecipePages(recipeId)));
    }

    public static List<SinglePageCtor> getRecipePages(ItemLike stack1, ItemLike stack2){
        var key1 = RegistryHelper.getRegistryName(stack1.asItem());
        var key2 = RegistryHelper.getRegistryName(stack2.asItem());
        return getRecipePages(key1, key2);
    }

    public static List<SinglePageCtor> getRecipePages(ResourceLocation key1, ResourceLocation key2){
        Level level = ArsNouveau.proxy.getClientWorld();
        RecipeManager manager = level.getRecipeManager();
        RecipeHolder<CraftingRecipe> recipe1 = manager.byKeyTyped(RecipeType.CRAFTING, key1);
        RecipeHolder<CraftingRecipe> recipe2 = manager.byKeyTyped(RecipeType.CRAFTING, key2);
        List<SinglePageCtor> pages = new ArrayList<>();
        if(recipe1 != null && recipe2 != null){
            pages.add(CraftingEntry.create(recipe1, recipe2));
            return pages;
        }
        pages.addAll(getRecipePages(key1));
        pages.addAll(getRecipePages(key2));
        return pages;
    }

    public static List<SinglePageCtor> getRecipePages(ItemStack stack, ResourceLocation recipeId){
        return getRecipePages(recipeId);
    }

    public static List<SinglePageCtor> getRecipePages(ResourceLocation recipeId){
        Level level = ArsNouveau.proxy.getClientWorld();
        RecipeManager manager = level.getRecipeManager();

        List<SinglePageCtor> pages = new ArrayList<>();

        RecipeHolder<GlyphRecipe> glyphRecipe = manager.byKeyTyped(RecipeRegistry.GLYPH_TYPE.get(), recipeId);

        if(glyphRecipe != null){
            pages.add(GlyphRecipeEntry.create(glyphRecipe));
            return pages;
        }

        RecipeHolder<CraftingRecipe> recipe = manager.byKeyTyped(RecipeType.CRAFTING, recipeId);

        if(recipe != null){
            pages.add(CraftingEntry.create(recipe));
            return pages;
        }

        RecipeHolder<EnchantingApparatusRecipe> apparatusRecipe = manager.byKeyTyped(RecipeRegistry.APPARATUS_TYPE.get(), recipeId);

        if(apparatusRecipe != null){
            pages.add(ApparatusEntry.create(apparatusRecipe));
            return pages;
        }

        RecipeHolder<ImbuementRecipe> imbuementRecipe = manager.byKeyTyped(RecipeRegistry.IMBUEMENT_TYPE.get(), recipeId);
        if(imbuementRecipe != null){
            pages.add(ImbuementRecipeEntry.create(imbuementRecipe));
            return pages;
        }

        return pages;
    }

    public static Component getLangPath(String name, int count) {
        return Component.translatable("ars_nouveau.page" + count + "." + name);
    }


    public static DocCategory glyphCategory(SpellTier tier){
        return switch (tier.value) {
            case 1 -> DocumentationRegistry.GLYPH_TIER_ONE;
            case 2 -> DocumentationRegistry.GLYPH_TIER_TWO;
            case 3, 99 -> DocumentationRegistry.GLYPH_TIER_THREE;
            default -> DocumentationRegistry.GLYPH_TIER_ONE;
        };
    }
}
