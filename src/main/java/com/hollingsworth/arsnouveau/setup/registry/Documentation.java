package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.documentation.DocPlayerData;
import com.hollingsworth.arsnouveau.api.documentation.ReloadDocumentationEvent;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.builder.DocEntryBuilder;
import com.hollingsworth.arsnouveau.api.documentation.entry.*;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.registry.*;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.common.perk.EmptyPerk;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class Documentation {
    static List<DocEntryBuilder> pendingBuilders = new ArrayList<>();

    static Set<DocEntry> entries = new HashSet<>();

    public static void initOnWorldReload(){
        entries = new HashSet<>();
        DocPlayerData.previousScreen = null;
        pendingBuilders = new ArrayList<>();
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
        DocCategory RITUALS = DocumentationRegistry.RITUAL_INDEX;
        DocCategory ENCHANTMENTS = DocumentationRegistry.ITEMS_BLOCKS_EQUIPMENT;
        DocCategory FAMILIARS = DocumentationRegistry.ITEMS_BLOCKS_EQUIPMENT;
        DocCategory MOD_NEWS = DocumentationRegistry.GETTING_STARTED;
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

        var enchantingApparatus = addPage(new DocEntryBuilder(MACHINES, BlockRegistry.ENCHANTING_APP_BLOCK)
                .withIntroPage()
                .withPage(getRecipePages(BlockRegistry.ARCANE_PEDESTAL, BlockRegistry.ARCANE_PLATFORM))
                .withPage(getRecipePages(BlockRegistry.ENCHANTING_APP_BLOCK, BlockRegistry.ARCANE_CORE_BLOCK)));


        var drygmyCharm = addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.DRYGMY_CHARM)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.DRYGMY_CHARM)
                .withPage(EntityEntry.create(ModEntities.ENTITY_DRYGMY.get(), getLangPath("drygmy_charm", 2)))
                .withPage(TextEntry.create(getLangPath("drygmy_charm", 3), Component.translatable("ars_nouveau.summoning")))
                .withPage(TextEntry.create(getLangPath("drygmy_charm", 4), Component.translatable("ars_nouveau.happiness")))
                .withPage(TextEntry.create(getLangPath("drygmy_charm", 5), Component.translatable(("ars_nouveau.production"))))
        );


        var mobJar = addPage(new DocEntryBuilder(MACHINES, "mob_jar")
                .withIcon(BlockRegistry.MOB_JAR)
                .withIntroPage()
                .withPage(TextEntry.create("ars_nouveau.page2.mob_jar", "ars_nouveau.title.mob_jar"))
                .withPage(EntityEntry.create(EntityType.VILLAGER, Component.translatable("mob_jar.villager")))
                .withPage(EntityEntry.create(EntityType.PIGLIN, Component.translatable("mob_jar.piglin")))
                .withPage(TextEntry.create("mob_jar.allay", "mob_jar.allay.title"))
                .withPage(EntityEntry.create(EntityType.ALLAY))
                .withPage(EntityEntry.create(EntityType.ENDER_DRAGON, Component.translatable("mob_jar.ender_dragon")))
                .withPage(EntityEntry.create(EntityType.SHEEP, Component.translatable("mob_jar.sheep")))
                .withPage(EntityEntry.create(EntityType.CHICKEN, Component.translatable("mob_jar.chicken")))
                .withPage(EntityEntry.create(EntityType.COW, Component.translatable("mob_jar.cow")))
                .withPage(EntityEntry.create(EntityType.MOOSHROOM, Component.translatable("mob_jar.mooshroom")))
                .withPage(EntityEntry.create(EntityType.PUFFERFISH, Component.translatable("mob_jar.pufferfish")))
                .withPage(EntityEntry.create(EntityType.FROG, Component.translatable("mob_jar.frog")))
                .withPage(EntityEntry.create(EntityType.PANDA, Component.translatable("mob_jar.panda")))
                .withPage(EntityEntry.create(ModEntities.ENTITY_DUMMY.get(), Component.translatable("mob_jar.dummy")))
                .withCraftingPages(BlockRegistry.MOB_JAR))
                .withRelation(ArsNouveau.prefix(RitualLib.CONTAINMENT))
                .withRelation(drygmyCharm);

        drygmyCharm.withRelation(mobJar);



        var amuletOfManaBoost = addBasicItem(ItemsRegistry.AMULET_OF_MANA_BOOST, EQUIPMENT);
        var amuletOfRegen = addBasicItem(ItemsRegistry.AMULET_OF_MANA_REGEN, EQUIPMENT);
        var beltOfLevitation = addBasicItem(ItemsRegistry.BELT_OF_LEVITATION, EQUIPMENT);
        addBasicItem(ItemsRegistry.BELT_OF_UNSTABLE_GIFTS, EQUIPMENT);
        var jarOfLight = addBasicItem(ItemsRegistry.JAR_OF_LIGHT, EQUIPMENT);
        var starby = addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.STARBUNCLE_CHARM)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.STARBUNCLE_CHARM)
                .withPage(EntityEntry.create(ModEntities.STARBUNCLE_TYPE.get(), getLangPath("starbuncle_charm", 2)))
                .withPage(TextEntry.create(getLangPath("starbuncle_charm", 3), Component.translatable("ars_nouveau.summoning")))
                .withPage(TextEntry.create(getLangPath("starbuncle_charm", 4), Component.translatable("ars_nouveau.item_transport")))
                .withPage(TextEntry.create(getLangPath("starbuncle_charm", 5), Component.translatable("ars_nouveau.filtering")))
                .withPage(TextEntry.create(getLangPath("starbuncle_charm", 6), Component.translatable("ars_nouveau.pathing")))
                .withPage(TextEntry.create(getLangPath("starbuncle_charm", 7), Component.translatable("ars_nouveau.starbuncle_bed")))
                .withPage(TextEntry.create(getLangPath("starbuncle_charm", 8), Component.translatable("ars_nouveau.starbuncle_stacking"))));

        addPage(new DocEntryBuilder(EQUIPMENT, ItemsRegistry.DULL_TRINKET)
                .withPage(CraftingEntry.create(ItemsRegistry.DULL_TRINKET, ItemsRegistry.MUNDANE_BELT))
                .withCraftingPages(ItemsRegistry.RING_OF_POTENTIAL));

        addPage(new DocEntryBuilder(RESOURCES, BlockRegistry.MAGE_BLOOM_CROP)
                .withPage(TextEntry.create(Component.translatable("ars_nouveau.page.magebloom_crop"), BlockRegistry.MAGE_BLOOM_CROP.asItem().getDescription(), BlockRegistry.MAGE_BLOOM_CROP.asItem().getDefaultInstance()))
                .withCraftingPages(BlockRegistry.MAGE_BLOOM_CROP)
                .withPage(CraftingEntry.create(ItemsRegistry.MAGE_FIBER, BlockRegistry.MAGEBLOOM_BLOCK)));

        addPage(new DocEntryBuilder(EQUIPMENT, ItemsRegistry.POTION_FLASK)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.POTION_FLASK)
                .withLocalizedText()
                .withCraftingPages(ItemsRegistry.POTION_FLASK_EXTEND_TIME)
                .withLocalizedText()
                .withCraftingPages(ItemsRegistry.POTION_FLASK_AMPLIFY));

        addPage(new DocEntryBuilder(EQUIPMENT, "reactive_enchantment")
                .withIcon(Items.ENCHANTED_BOOK)
                .withIntroPage()
                .withPage(EnchantmentEntry.create(ArsNouveau.prefix(EnchantmentRegistry.REACTIVE_ENCHANTMENT.location().getPath() + "_" + 1)))
                .withLocalizedText()
                .withPage(EnchantmentEntry.create(ArsNouveau.prefix(EnchantmentRegistry.REACTIVE_ENCHANTMENT.location().getPath() + "_" + 2)))
                .withPage(EnchantmentEntry.create(ArsNouveau.prefix(EnchantmentRegistry.REACTIVE_ENCHANTMENT.location().getPath() + "_" + 3)))
                .withPage(EnchantmentEntry.create(ArsNouveau.prefix(EnchantmentRegistry.REACTIVE_ENCHANTMENT.location().getPath() + "_" + 4)))
                .withLocalizedText()
                .withPage(SpellWriteEntry.create(ResourceLocation.parse("ars_nouveau:spell_write"))));

        var discountRing = addBasicItem(ItemsRegistry.RING_OF_GREATER_DISCOUNT, EQUIPMENT);
        addBasicItem(ItemsRegistry.RING_OF_LESSER_DISCOUNT, EQUIPMENT);

        var turrets = addPage(new DocEntryBuilder(AUTOMATION, BlockRegistry.BASIC_SPELL_TURRET)
                .withIntroPage()
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
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.WHIRLISPRIG_CHARM)
                .withPage(EntityEntry.create(ModEntities.WHIRLISPRIG_TYPE.get(), getLangPath("whirlisprig_charm", 2)))
                .withPage(TextEntry.create(getLangPath("whirlisprig_charm", 3), Component.translatable(("ars_nouveau.summoning"))))
                .withPage(TextEntry.create(getLangPath("whirlisprig_charm", 4), Component.translatable(("ars_nouveau.happiness"))))
                .withPage(TextEntry.create(getLangPath("whirlisprig_charm", 5), Component.translatable(("ars_nouveau.important"))))
                .withPage(TextEntry.create(getLangPath("whirlisprig_charm", 6), Component.translatable(("ars_nouveau.production")))));

        var wixie = addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.WIXIE_CHARM)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.WIXIE_CHARM)
                .withPage(EntityEntry.create(ModEntities.ENTITY_WIXIE_TYPE.get(), getLangPath("wixie_charm", 2), 0.95f, -15))
                .withPage(TextEntry.create(getLangPath("wixie_charm", 3), Component.translatable(("ars_nouveau.item_crafting"))))
                .withPage(TextEntry.create(getLangPath("wixie_charm", 4), Component.translatable(("ars_nouveau.item_crafting_setting"))))
                .withPage(TextEntry.create(getLangPath("wixie_charm", 7), Component.translatable(("ars_nouveau.binding_inventories"))))
                .withPage(TextEntry.create(getLangPath("wixie_charm", 5), Component.translatable(("ars_nouveau.potion_crafting"))))
                .withPage(TextEntry.create(getLangPath("wixie_charm", 6))));

        var archwood = addPage(new DocEntryBuilder(RESOURCES, "archwood")
                        .withIcon(BlockRegistry.BOMBEGRANTE_POD)
                        .withIntroPage()
                        .withPage(TextEntry.create(Component.translatable("ars_nouveau.page.bombegrante"), Component.translatable("block.ars_nouveau.bombegranate_pod"), BlockRegistry.BOMBEGRANTE_POD))
                        .withPage(TextEntry.create(Component.translatable("ars_nouveau.page.mendosteen"), Component.translatable("block.ars_nouveau.mendosteen_pod"),BlockRegistry.MENDOSTEEN_POD))
                        .withPage(TextEntry.create(Component.translatable("ars_nouveau.page.frostaya"), Component.translatable("block.ars_nouveau.frostaya_pod"),BlockRegistry.FROSTAYA_POD))
                        .withPage(TextEntry.create(Component.translatable("ars_nouveau.page.bastion_fruit"), Component.translatable("block.ars_nouveau.bastion_pod"),BlockRegistry.BASTION_POD)));

        var forest = addPage(new DocEntryBuilder(RESOURCES, "archwood_forest")
                .withIcon(BlockRegistry.BLAZING_SAPLING)
                .withIntroPage());


        addPage(new DocEntryBuilder(RESOURCES, "decorative")
                .withIcon(SOURCESTONE)
                .withCraftingPages(SOURCESTONE)
                .withIntroPage());


        var sourceberry = addPage(new DocEntryBuilder(RESOURCES, BlockRegistry.SOURCEBERRY_BUSH)
                .withIntroPage()
                .withPage(CraftingEntry.create(ItemsRegistry.SOURCE_BERRY_PIE, ItemsRegistry.SOURCE_BERRY_ROLL)));

        addPage(new DocEntryBuilder(RESOURCES, "weald_walker")
                .withIcon(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix( RitualLib.AWAKENING)))
                .withIntroPage()
                .withPage(EntityEntry.create(ModEntities.ENTITY_BLAZING_WEALD.get(), getLangPath("weald_walker", 2)))
                .withPage(EntityEntry.create(ModEntities.ENTITY_CASCADING_WEALD.get(), getLangPath("weald_walker", 3)))
                .withPage(EntityEntry.create(ModEntities.ENTITY_FLOURISHING_WEALD.get(),getLangPath("weald_walker", 4)))
                .withPage(EntityEntry.create(ModEntities.ENTITY_VEXING_WEALD.get(), getLangPath("weald_walker", 5))));

        addPage(new DocEntryBuilder(RESOURCES, "wilden")
                .withIcon(ItemsRegistry.WILDEN_SPIKE)
                .withIntroPage()
                .withPage(EntityEntry.create(ModEntities.WILDEN_HUNTER.get(), getLangPath("wilden", 3), 0.55f))
                .withPage(EntityEntry.create(ModEntities.WILDEN_STALKER.get(), getLangPath("wilden", 4), 0.55f))
                .withPage(EntityEntry.create(ModEntities.WILDEN_GUARDIAN.get(), getLangPath("wilden", 5), 0.55f))
                .withPage(EntityEntry.create(ModEntities.WILDEN_BOSS.get(), getLangPath("wilden", 6), 0.55f))
                .withPage(TextEntry.create(getLangPath("wilden", 7))));

        var denyScroll = addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.DENY_ITEM_SCROLL)
                .withIntroPage()
                .withPage(CraftingEntry.create(ItemsRegistry.BLANK_PARCHMENT, ItemsRegistry.DENY_ITEM_SCROLL)));

        var mimicScroll = addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.MIMIC_ITEM_SCROLL)
                .withIntroPage()
                .withPage(getRecipePages(ItemsRegistry.BLANK_PARCHMENT, ItemsRegistry.MIMIC_ITEM_SCROLL)));
        var allowScroll = addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.ALLOW_ITEM_SCROLL)
                .withIntroPage()
                .withPage(getRecipePages(ItemsRegistry.BLANK_PARCHMENT, ItemsRegistry.ALLOW_ITEM_SCROLL)));

        var dominionWand = addBasicItem(ItemsRegistry.DOMINION_ROD, AUTOMATION);


        var prisms = addBasicItem(BlockRegistry.SPELL_PRISM, AUTOMATION);

        addPage(new DocEntryBuilder(RESOURCES, BlockRegistry.MAGELIGHT_TORCH)
                .withIntroPage()
                .withPage(getRecipePages(BlockRegistry.GOLD_SCONCE_BLOCK, BlockRegistry.SOURCESTONE_SCONCE_BLOCK))
                .withPage(getRecipePages(BlockRegistry.POLISHED_SCONCE_BLOCK, BlockRegistry.ARCHWOOD_SCONCE_BLOCK))
                .withCraftingPages(BlockRegistry.MAGELIGHT_TORCH));

        var spellBooks = addPage(new DocEntryBuilder(EQUIPMENT, "spell_books")
                .withIcon(ItemsRegistry.ARCHMAGE_SPELLBOOK)
                .withIntroPage()
                .withPage(getRecipePages(RegistryHelper.getRegistryName(ItemsRegistry.NOVICE_SPELLBOOK.asItem()), ArsNouveau.prefix("apprentice_book_upgrade")))
                .withCraftingPages(ArsNouveau.prefix("archmage_book_upgrade")));

        addBasicItem(ItemsRegistry.ENCHANTERS_MIRROR, EQUIPMENT);
        addBasicItem(ItemsRegistry.ENCHANTERS_SHIELD, EQUIPMENT);
        addBasicItem(ItemsRegistry.ENCHANTERS_SWORD, EQUIPMENT);
        addPage(new DocEntryBuilder(EQUIPMENT, ItemsRegistry.SPELL_BOW)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.SPELL_BOW)
                .withCraftingPages("ars_nouveau:imbuement_amplify_arrow")
                .withCraftingPages("ars_nouveau:imbuement_pierce_arrow")
                .withCraftingPages("ars_nouveau:imbuement_split_arrow"));
        addPage(new DocEntryBuilder(EQUIPMENT, ItemsRegistry.SPELL_CROSSBOW)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.SPELL_CROSSBOW)
                .withCraftingPages("ars_nouveau:imbuement_amplify_arrow")
                .withCraftingPages("ars_nouveau:imbuement_pierce_arrow")
                .withCraftingPages("ars_nouveau:imbuement_split_arrow"));
        addBasicItem(ItemsRegistry.RUNIC_CHALK, EQUIPMENT);


        var potionJar = addBasicItem(BlockRegistry.POTION_JAR, MACHINES);
        var melder = addBasicItem(BlockRegistry.POTION_MELDER, MACHINES);
        var diffuser = addBasicItem(BlockRegistry.POTION_DIFFUSER, MACHINES);

        wixie.withRelation(potionJar).withRelation(melder).withRelation(diffuser);
        potionJar.withRelation(melder).withRelation(diffuser).withRelation(wixie);
        melder.withRelation(potionJar).withRelation(diffuser).withRelation(wixie);
        diffuser.withRelation(potionJar).withRelation(melder).withRelation(wixie);

        var ritualBrazier = addBasicItem(BlockRegistry.RITUAL_BLOCK, MACHINES);
        addBasicItem(BlockRegistry.BRAZIER_RELAY, MACHINES);
        var scribesTable = addPage(new DocEntryBuilder(MACHINES, BlockRegistry.SCRIBES_BLOCK)
                .withPage(TextEntry.create(getLangPath("scribes_table", 1), Component.translatable("ars_nouveau.glyph_crafting"), BlockRegistry.SCRIBES_BLOCK.asItem().getDefaultInstance()))
                .withPage(TextEntry.create(getLangPath("scribes_table", 2), Component.translatable("ars_nouveau.scribing")))
                .withPage(getRecipePages(ItemsRegistry.BLANK_PARCHMENT, BlockRegistry.SCRIBES_BLOCK)));

        var portal = addPage(new DocEntryBuilder(MACHINES, "warp_portal")
                .withIcon(ItemsRegistry.WARP_SCROLL)
                .withIntroPage()
                .withLocalizedText()
//                .withPage(new MultiblockPage(getLangPath("warp_portal", 3), new String[][]{
//                        new String[]{" BB "},
//                        new String[]{"BPPB"},
//                        new String[]{"B0PB"},
//                        new String[]{"BPPB"},
//                        new String[]{" BB "}
//                }).withMapping("B", "ars_nouveau:sourcestone").withMapping("P", "ars_nouveau:portal")
//                        .withMapping("0", "ars_nouveau:portal").withText(getLangPath("warp_portal", 4)))
        );
        var scroll = addBasicItem(ItemsRegistry.WARP_SCROLL, EQUIPMENT);

        var stableScroll = addBasicItem(ItemsRegistry.STABLE_WARP_SCROLL, EQUIPMENT);
        stableScroll.withRelation(scroll).withRelation(portal);

        scroll.withRelation(stableScroll).withRelation(portal);

        portal.withRelation(scroll).withRelation(stableScroll);

        var agronomic = addBasicItem(BlockRegistry.AGRONOMIC_SOURCELINK, SOURCE);
        addBasicItem(BlockRegistry.ALCHEMICAL_BLOCK, SOURCE);
        addBasicItem(BlockRegistry.MYCELIAL_BLOCK, SOURCE);
        var sourceJar = addBasicItem(BlockRegistry.SOURCE_JAR, SOURCE);

        addBasicItem(BlockRegistry.RELAY, SOURCE, CraftingEntry.create(manager.byKeyTyped(RecipeType.CRAFTING, getRegistryName(BlockRegistry.RELAY.get())), Component.translatable("ars_nouveau.page2.relay")));

        addBasicItem(BlockRegistry.RELAY_DEPOSIT, SOURCE);
        addBasicItem(BlockRegistry.RELAY_SPLITTER, SOURCE);
        addBasicItem(BlockRegistry.RELAY_WARP, SOURCE);
        addBasicItem(BlockRegistry.RELAY_COLLECTOR, SOURCE);
        addBasicItem(BlockRegistry.VITALIC_BLOCK, SOURCE);
        addPage(new DocEntryBuilder(SOURCE, BlockRegistry.VOLCANIC_BLOCK)
                .withIntroPage()
                .withPage(TextEntry.create(getLangPath("volcanic_sourcelink", 2), Component.translatable("ars_nouveau.active_generation")))
                .withPage(TextEntry.create(getLangPath("volcanic_sourcelink", 3), Component.translatable("ars_nouveau.heat")))
                .withCraftingPages(BlockRegistry.VOLCANIC_BLOCK)
                .withPage(TextEntry.create(getLangPath("volcanic_sourcelink", 4))));
        addPage(new DocEntryBuilder(ENCHANTMENTS, "how_to_enchant")
                .withIcon(BlockRegistry.ENCHANTING_APP_BLOCK)
                .withSortNum(-1)
                .withIntroPage()
                .withLocalizedText()
        ).withRelation(enchantingApparatus);
        addPage(new DocEntryBuilder(RITUALS, "performing_rituals")
                .withSortNum(-1)
                .withIcon(BlockRegistry.RITUAL_BLOCK)
                .withIntroPage()
                .withLocalizedText()
                .withCraftingPages(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix( RitualLib.SUNRISE))))
                .withRelation(ritualBrazier);
        addPage(new DocEntryBuilder(FAMILIARS, "summoning_familiars")
                .withSortNum(-1)
                .withIcon(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix( RitualLib.BINDING)))
                .withIntroPage()
                .withLocalizedText()
                .withCraftingPages(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix( RitualLib.BINDING))))
                .withRelations(ritualBrazier)
                .withRelation(ArsNouveau.prefix( RitualLib.BINDING));

//        addPage(new DocEntryBuilder(MOD_NEWS, "mod_news")
//                        .withIcon(ItemsRegistry.SPELL_PARCHMENT)
//                        .withPage(new LinkPage("https://discord.gg/y7TMXZu", "ars_nouveau.discord_text", "ars_nouveau.community")));


        addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.AMETHYST_GOLEM_CHARM)
                        .withIntroPage()
                        .withPage(EntityEntry.create(ModEntities.AMETHYST_GOLEM.get(), getLangPath("amethyst_golem_charm", 2), 0.75f))
                        .withPage(TextEntry.create(getLangPath("amethyst_golem_charm", 3), Component.translatable("ars_nouveau.summoning")))
                        .withPage(TextEntry.create(getLangPath("amethyst_golem_charm", 4), Component.translatable("ars_nouveau.amethyst_farming")))
                        .withPage(TextEntry.create(getLangPath("amethyst_golem_charm", 5), Component.translatable("ars_nouveau.amethyst_storage"))));
        addBasicItem(ItemsRegistry.ANNOTATED_CODEX, EQUIPMENT);
        var starbyBed = addPage(new DocEntryBuilder(AUTOMATION, BlockRegistry.ORANGE_SBED)
                .withName("ars_nouveau.summon_bed")
                .withIntroPage("summon_bed")
                        .withPage(getRecipePages(BlockRegistry.ORANGE_SBED, BlockRegistry.BLUE_SBED))
                        .withPage(getRecipePages(BlockRegistry.GREEN_SBED, BlockRegistry.YELLOW_SBED))
                        .withPage(getRecipePages(BlockRegistry.RED_SBED, BlockRegistry.PURPLE_SBED)));
        var scryCaster = addBasicItem(ItemsRegistry.SCRY_CASTER, EQUIPMENT);
        var scryCrystal = addBasicItem(BlockRegistry.SCRYERS_CRYSTAL, MACHINES);
        var oculus = addBasicItem(BlockRegistry.SCRYERS_OCULUS, MACHINES);
        var scryScroll = addBasicItem(ItemsRegistry.SCRYER_SCROLL, MACHINES);

        scryCrystal.withRelation(scryCaster).withRelation(scryScroll).withRelation(oculus);
        scryCaster.withRelation(scryCrystal);
        oculus.withRelation(scryScroll).withRelation(scryCrystal);
        scryScroll.withRelation(scryCaster).withRelation(oculus).withRelation(scryCrystal);
        var starbyShades = addBasicItem(ItemsRegistry.STARBUNCLE_SHADES, AUTOMATION);
        var wixieHat = addBasicItem(ItemsRegistry.WIXIE_HAT, AUTOMATION);

//        addPage(new DocEntryBuilder(MOD_NEWS, "support_mod")
//                        .withIcon(ItemsRegistry.STARBUNCLE_CHARM)
//                        .withPage(new LinkPage("https://www.patreon.com/arsnouveau", "ars_nouveau.patreon_text", "ars_nouveau.patreon"))
//                        .withPage(EntityEntry.create(ModEntities.LILY.get(), Component.translatable("ars_nouveau.lily")))
//                        .withPage(EntityEntry.create(ModEntities.NOOK.get(), Component.translatable("ars_nouveau.nook")))
//                        .withPage(new LinkPage("https://www.redbubble.com/people/Gootastic/explore?page=1&sortOrder=recent", "ars_nouveau.store_text", "ars_nouveau.store")));


        addPage(new DocEntryBuilder(EQUIPMENT, ItemsRegistry.SHAPERS_FOCUS)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.SHAPERS_FOCUS)
                .withPage(TextEntry.create(getLangPath("shapers_focus", 2), Component.translatable("ars_nouveau.shapers_focus.blocks")))
                .withPage(TextEntry.create(getLangPath("shapers_focus", 3), Component.translatable("ars_nouveau.shapers_focus.entities")))
                .withPage(TextEntry.create(getLangPath("shapers_focus", 4), Component.translatable("ars_nouveau.shapers_focus.examples"))));
        addBasicItem(ItemsRegistry.ALCHEMISTS_CROWN, EQUIPMENT);
        addPage(new DocEntryBuilder(EQUIPMENT,  "flask_cannons")
                .withIntroPage()
                .withIcon(ItemsRegistry.SPLASH_LAUNCHER)
                .withCraftingPages(ItemsRegistry.SPLASH_LAUNCHER)
                .withCraftingPages(ItemsRegistry.LINGERING_LAUNCHER));

        DocEntryBuilder ARMOR_ENTRY = new DocEntryBuilder(EQUIPMENT, "armor")
                .withIcon(ItemsRegistry.SORCERER_ROBES)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.MAGE_FIBER)
                .withPage(TextEntry.create("ars_nouveau.page.threads", "ars_nouveau.threads"))
//                .withPage(new ImagePage().withEntry(ArsNouveau.prefix( "textures/gui/entries/sorcerer_diagram.png"))
//                        .withEntry(ArsNouveau.prefix( "textures/gui/entries/arcanist_thread_diagram.png"))
//                        .withEntry(ArsNouveau.prefix( "textures/gui/entries/battlemage_diagram.png"))
//                        .withBorder().withTitle("ars_nouveau.thread_layout")
//                        .withText("ars_nouveau.page.layout_desc"))
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
                .withCraftingPages(ItemsRegistry.BATTLEMAGE_BOOTS);

        var armorEntry = addPage(ARMOR_ENTRY.withCategory(ARMOR));

        var alteraitonTable = addPage(new DocEntryBuilder(ARMOR, "alteration_table")
                .withIntroPage()
                .withPage(getRecipePages(BlockRegistry.ALTERATION_TABLE, ItemsRegistry.BLANK_THREAD))
                .withIcon(BlockRegistry.ALTERATION_TABLE)
                .withSortNum(3))
                .withRelation(armorEntry);

        var armorUpgrade = addPage(new DocEntryBuilder(ARMOR, "armor_upgrading")
                .withIntroPage()
                .withPage(TextEntry.create(getLangPath("armor_upgrading", 2), Component.translatable("ars_nouveau.armor_tiers")))
                .withIcon(ItemsRegistry.ARCANIST_HOOD)
                .withCraftingPages("ars_nouveau:first_armor_upgrade")
                .withCraftingPages("ars_nouveau:second_armor_upgrade")
                .withSortNum(1))
                .withRelations(alteraitonTable, armorEntry);

        armorEntry.withRelation(armorUpgrade);
        armorUpgrade.withRelation(armorEntry);
        alteraitonTable.withRelation(armorUpgrade);

        addPage(new DocEntryBuilder(ARMOR, "applying_perks")
                .withIntroPage()
                .withLocalizedText()
                .withIcon(ItemsRegistry.BLANK_THREAD)
                .withSortNum(2))
                .withRelations(armorEntry, alteraitonTable);

        addPage(new DocEntryBuilder(ARMOR, "alteration_table")
                .withIntroPage()
                .withPage(getRecipePages(BlockRegistry.ALTERATION_TABLE, ItemsRegistry.BLANK_THREAD))
                .withIcon(BlockRegistry.ALTERATION_TABLE)
                .withSortNum(3))
                .withRelations(armorEntry, armorUpgrade);


        var voidPrism = addBasicItem(BlockRegistry.VOID_PRISM, AUTOMATION);

        turrets.withRelation(prisms).withRelation(voidPrism);
        prisms.withRelation(turrets).withRelation(voidPrism);
        voidPrism.withRelation(turrets).withRelation(prisms);
        addPage(new DocEntryBuilder(RESOURCES, "illusion_blocks").withIcon(BlockRegistry.GHOST_WEAVE)
                .withLocalizedText(BlockRegistry.MIRROR_WEAVE)
                .withCraftingPages(BlockRegistry.MIRROR_WEAVE)
                .withLocalizedText(BlockRegistry.FALSE_WEAVE)
                .withCraftingPages(BlockRegistry.FALSE_WEAVE)
                .withLocalizedText(BlockRegistry.GHOST_WEAVE)
                .withCraftingPages(BlockRegistry.GHOST_WEAVE)
                .withLocalizedText(BlockRegistry.SKY_WEAVE)
                .withCraftingPages(BlockRegistry.SKY_WEAVE));

        var bookwyrm = addPage(new DocEntryBuilder(MACHINES, ItemsRegistry.BOOKWYRM_CHARM)
                .withIntroPage()
                .withPage(EntityEntry.create(ModEntities.ENTITY_BOOKWYRM_TYPE.get(), getLangPath("bookwyrm_charm", 2))));

        var storageLectern = addPage(new DocEntryBuilder(MACHINES, BlockRegistry.CRAFTING_LECTERN)
                        .withIntroPage()
                        .withPage(TextEntry.create(getLangPath("storage", 2), Component.translatable("ars_nouveau.storage")))
                        .withPage(TextEntry.create(getLangPath("storage", 3), Component.translatable("ars_nouveau.storage_tabs")))
                .withCraftingPages(BlockRegistry.CRAFTING_LECTERN));
        bookwyrm.withRelation(storageLectern);

        var displayCase = addPage(new DocEntryBuilder(MACHINES, BlockRegistry.ITEM_DETECTOR)
                .withIntroPage()
                .withLocalizedText()
                .withCraftingPages(BlockRegistry.ITEM_DETECTOR));
        var repository = addPage(new DocEntryBuilder(MACHINES, BlockRegistry.REPOSITORY)
                .withIntroPage()
                .withPage(CraftingEntry.create(BlockRegistry.REPOSITORY, BlockRegistry.ARCHWOOD_CHEST)));

        // add scrolls to arrylist
        var scrollRelations = new ArrayList<DocEntry>(){
            {
                add(denyScroll);
                add(mimicScroll);
                add(allowScroll);
            }
        };


        storageLectern.withRelation(bookwyrm)
                .withRelation(displayCase)
                .withRelation(repository);
        repository.withRelations(storageLectern);


        starby.withRelations(scrollRelations.stream().map(DocEntry::id).toList())
                .withRelation(dominionWand)
                .withRelation(storageLectern)
                .withRelation(starbyShades)
                .withRelation(wixieHat)
                .withRelation(starbyBed);
        denyScroll.withRelation(mimicScroll)
                .withRelation(allowScroll)
                .withRelation(dominionWand)
                .withRelation(starby)
                .withRelation(storageLectern)
                .withRelation(displayCase);
        mimicScroll.withRelation(denyScroll)
                .withRelation(allowScroll)
                .withRelation(dominionWand)
                .withRelation(starby)
                .withRelation(storageLectern)
                .withRelation(displayCase);
        allowScroll.withRelation(denyScroll)
                .withRelation(mimicScroll)
                .withRelation(dominionWand)
                .withRelation(starby)
                .withRelation(storageLectern)
                .withRelation(displayCase);

        displayCase.withRelation(bookwyrm).withRelation(storageLectern)
                .withRelation(dominionWand)
                .withEntryRelations(scrollRelations);
        dominionWand.withRelation(storageLectern)
                .withRelation(displayCase)
                .withRelation(starby);

        addBasicItem(BlockRegistry.SPELL_SENSOR, AUTOMATION);
        addBasicItem(ItemsRegistry.JUMP_RING, EQUIPMENT);
        addBasicItem(BlockRegistry.REDSTONE_RELAY, AUTOMATION);

        addPage(new DocEntryBuilder(AUTOMATION, BlockRegistry.ARCHWOOD_GRATE)
                .withIntroPage()
                .withPage(CraftingEntry.create(BlockRegistry.ARCHWOOD_GRATE, BlockRegistry.GOLD_GRATE))
                .withPage(CraftingEntry.create(BlockRegistry.SMOOTH_SOURCESTONE_GRATE, BlockRegistry.SOURCESTONE_GRATE)));
        addBasicItem(BlockRegistry.SOURCE_LAMP, AUTOMATION);

        addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.ALAKARKINOS_CHARM)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.ALAKARKINOS_CHARM)
                .withPage(EntityEntry.create(ModEntities.ALAKARKINOS_TYPE.get(), getLangPath("alakarkinos_charm", 2), 0.5f))
                .withPage(TextEntry.create(getLangPath("alakarkinos_charm", 3), Component.translatable("ars_nouveau.summoning")))
                .withPage(TextEntry.create(getLangPath("alakarkinos_charm", 4), Component.translatable("ars_nouveau.sifting"))));


        addPage(new DocEntryBuilder(GETTING_STARTED, "spell_casting")
                .withIcon(ItemsRegistry.NOVICE_SPELLBOOK)
                .withSortNum(1)
                .withIntroPage()
                .withLocalizedText()
                .withLocalizedText()
                .withCraftingPages(ItemsRegistry.NOVICE_SPELLBOOK));
        addPage(new DocEntryBuilder(GETTING_STARTED, "spell_mana")
                .withSortNum(2)
                .withIcon(ItemsRegistry.NOVICE_SPELLBOOK)
                .withIntroPage()
                .withLocalizedText());

        addPage(new DocEntryBuilder(GETTING_STARTED, "obtaining_gems")
                .withIcon(BlockRegistry.IMBUEMENT_BLOCK)
                .withSortNum(3)
                .withIntroPage())
                .withRelation(dowsingRod);

        addPage(new DocEntryBuilder(GETTING_STARTED, "new_glyphs")
                .withIcon(ItemsRegistry.BLANK_GLYPH)
                .withSortNum(4)
                .withIntroPage())
                .withRelations(scribesTable);

        addPage(new DocEntryBuilder(GETTING_STARTED, "source")
                .withSortNum(5)
                .withIcon(BlockRegistry.SOURCE_JAR)
                .withIntroPage())
                .withRelations(sourceJar, agronomic);

        addPage(new DocEntryBuilder(GETTING_STARTED, "apparatus_crafting")
                .withSortNum(6)
                .withIcon(BlockRegistry.ENCHANTING_APP_BLOCK)
                .withIntroPage())
                .withRelations(enchantingApparatus);

        addPage(new DocEntryBuilder(GETTING_STARTED, "better_casting")
                .withSortNum(7)
                .withIcon(ItemsRegistry.SORCERER_ROBES)
                .withIntroPage())
                .withRelations(enchantingApparatus);

        addPage(new DocEntryBuilder(GETTING_STARTED, "world_generation")
                .withSortNum(8)
                .withIcon(ItemsRegistry.SOURCE_GEM)
                .withIntroPage())
                .withRelations(imbuementChamber, archwood, sourceberry);

        addPage(new DocEntryBuilder(GETTING_STARTED, "upgrades")
                .withSortNum(9)
                .withIcon(ItemsRegistry.ARCHMAGE_SPELLBOOK)
                .withIntroPage())
                .withRelations(spellBooks, armorEntry);

        addPage(new DocEntryBuilder(GETTING_STARTED, "starting_automation")
                .withSortNum(10)
                .withIcon(BlockRegistry.BASIC_SPELL_TURRET)
                .withIntroPage())
                .withRelations(turrets, prisms, starby, wixie);

        addPage(new DocEntryBuilder(GETTING_STARTED, "trinkets")
                .withIcon(ItemsRegistry.WARP_SCROLL)
                .withSortNum(11)
                .withIntroPage())
                .withRelations(jarOfLight, amuletOfRegen, discountRing);

        for(DocEntryBuilder builder : pendingBuilders){
            addPage(builder);
        }
        pendingBuilders = new ArrayList<>();

        NeoForge.EVENT_BUS.post(new ReloadDocumentationEvent.AddEntries());
        NeoForge.EVENT_BUS.post(new ReloadDocumentationEvent.Post());
    }

    private static DocEntry addPage(DocEntryBuilder builder){
        DocEntry entry =  DocumentationRegistry.registerEntry(builder.category, builder.build());
        if(Documentation.entries.contains(entry)){
            throw new IllegalStateException("Entry already exists: " + entry);
        }
        return entry;
    }

    private static DocEntryBuilder buildPage(DocEntryBuilder builder){
        Documentation.pendingBuilders.add(builder);
        return builder;
    }

    public static DocEntryBuilder buildBasicItem(ItemLike item, DocCategory category) {
        var builder = new DocEntryBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withPage(TextEntry.create(Component.translatable("ars_nouveau.page." + getRegistryName(item.asItem()).getPath()))).withCraftingPages(item);
        Documentation.pendingBuilders.add(builder);
        return builder;
    }

    public static DocEntry addBasicItem(ItemLike item, DocCategory category) {
        ItemStack stack = new ItemStack(item);
        return addPage(new DocEntryBuilder(category, stack.getDescriptionId())
                .withIcon(item)
                .withPage(TextEntry.create(Component.translatable("ars_nouveau.page." + getRegistryName(item.asItem()).getPath()), stack.getItem().getDescription(), stack)).withCraftingPages(item));
    }

    public static DocEntry addBasicItem(ItemLike item, DocCategory category, ResourceLocation recipeId) {
        return addPage(new DocEntryBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withPage(TextEntry.create(Component.translatable("ars_nouveau.page." + getRegistryName(item.asItem()).getPath())))
                .withPage(getRecipePages(recipeId)));
    }

    public static DocEntry addBasicItem(ItemLike item, DocCategory category, SinglePageCtor recipePage) {
        Item item1 = item.asItem();
        return addPage(new DocEntryBuilder(category, item1.getDescriptionId())
                .withIcon(item)
                .withPage(TextEntry.create(Component.translatable("ars_nouveau.page." + getRegistryName(item1).getPath()), item1.getDescription(), new ItemStack(item1)))
                .withPage(recipePage));
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
