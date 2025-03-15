package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.documentation.DocPlayerData;
import com.hollingsworth.arsnouveau.api.documentation.ReloadDocumentationEvent;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.builder.DocEntryBuilder;
import com.hollingsworth.arsnouveau.api.documentation.entry.*;
import com.hollingsworth.arsnouveau.api.documentation.search.Search;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.registry.*;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.crafting.recipes.*;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.common.perk.EmptyPerk;
import com.hollingsworth.arsnouveau.common.util.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;

import java.util.*;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class Documentation {
    static List<DocEntryBuilder> pendingBuilders = new ArrayList<>();

    static Set<DocEntry> entries = new HashSet<>();

    public static void initOnWorldReload() {
        long startTime = System.nanoTime();
        entries = new HashSet<>();
        Search.connectedSearches = new ArrayList<>();
        DocPlayerData.previousScreen = null;
        pendingBuilders = new ArrayList<>();
        Level level = ArsNouveau.proxy.getClientWorld();
        RecipeManager manager = level.getRecipeManager();
        Block SOURCESTONE = BlockRegistry.getBlock(LibBlockNames.SOURCESTONE);
        DocCategory MACHINES = DocumentationRegistry.CRAFTING;
        DocCategory GETTING_STARTED = DocumentationRegistry.GETTING_STARTED;
        DocCategory SPELL_CASTING = DocumentationRegistry.SPELL_CASTING;
        DocCategory EQUIPMENT = DocumentationRegistry.ITEMS;
        DocCategory AUTOMATION = DocumentationRegistry.CRAFTING;
        DocCategory RESOURCES = DocumentationRegistry.FIELD_GUIDE;
        DocCategory SOURCE = DocumentationRegistry.SOURCE;
        DocCategory ARMOR = DocumentationRegistry.ARMOR;
        DocCategory RITUALS = DocumentationRegistry.RITUAL_INDEX;
        DocCategory ENCHANTMENTS = DocumentationRegistry.ENCHANTING;
        DocCategory FAMILIARS = DocumentationRegistry.FAMILIARS;
        DocCategory MOD_NEWS = DocumentationRegistry.GETTING_STARTED;
        for (AbstractSpellPart spellPart : GlyphRegistry.getSpellpartMap().values()) {
            ItemStack renderStack = spellPart.glyphItem.getDefaultInstance();
            var entry = new DocEntry(spellPart.getRegistryName(), renderStack, Component.literal(spellPart.getLocaleName()));
            entry.addPage(GlyphEntry.create(spellPart));
            entry.withSearchTag(Component.translatable("ars_nouveau.keyword.glyph"));
            for (SpellSchool spellSchool : spellPart.spellSchools) {
                entry.withSearchTag(spellSchool.getTextComponent());
            }

            var pages = getRecipePages(renderStack, spellPart.getRegistryName());
            entry.addPages(pages);

            DocumentationRegistry.registerEntry(glyphCategory(spellPart.getConfigTier()), entry);
        }


        for (RitualTablet r : RitualRegistry.getRitualItemMap().values()) {
            ItemStack renderStack = r.getDefaultInstance();
            AbstractRitual ritual = r.ritual;

            Component title = Component.translatable("item." + ritual.getRegistryName().getNamespace() + "." + ritual.getRegistryName().getPath());

            var entry = new DocEntry(ritual.getRegistryName(), renderStack, title);

            entry.addPage(TextEntry.create(Component.translatable(ritual.getDescriptionKey()), title, renderStack));

            List<SinglePageCtor> pages = getRecipePages(renderStack, ritual.getRegistryName());
            entry.addPages(pages)
                    .withSearchTag(Component.translatable("ars_nouveau.keyword.ritual"));

            DocumentationRegistry.registerEntry(DocumentationRegistry.RITUAL_INDEX, entry);
        }

        for (PerkItem perk : PerkRegistry.getPerkItemMap().values()) {
            if (perk.perk instanceof EmptyPerk)
                continue;

            ItemStack renderStack = perk.getDefaultInstance();
            var entry = new DocEntry(perk.perk.getRegistryName(), renderStack, perk.perk.getPerkName());
            entry.addPage(TextEntry.create(Component.translatable(perk.perk.getDescriptionKey()), Component.literal(perk.perk.getName()), renderStack));
            entry.addPages(getRecipePages(renderStack, RegistryHelper.getRegistryName(perk)));
            entry.withSearchTag(Component.translatable("ars_nouveau.keyword.thread"));
            DocumentationRegistry.registerEntry(DocumentationRegistry.ARMOR, entry);
        }

        for (AbstractFamiliarHolder r : FamiliarRegistry.getFamiliarHolderMap().values()) {
            ItemStack renderstack = r.getOutputItem();
            var entry = new DocEntry(r.getRegistryName(), renderstack, Component.translatable("entity.ars_nouveau." + r.getRegistryName().getPath()));

            entry.addPage(TextEntry.create(r.getLangDescription(), renderstack.getHoverName(), renderstack));
            entry.withSearchTag(Component.translatable("ars_nouveau.keyword.familiar"));

            DocumentationRegistry.registerEntry(DocumentationRegistry.FAMILIARS, entry);
        }

        var dowsingRod = addBasicItem(ItemsRegistry.DOWSING_ROD, EQUIPMENT);

        var imbuementChamber = addPage(new DocEntryBuilder(MACHINES, BlockRegistry.IMBUEMENT_BLOCK)
                .withSortNum(1)
                .withIntroPage()
                .withCraftingPages()
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_lapis"), ItemsRegistry.SOURCE_GEM)
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_amethyst"), ItemsRegistry.SOURCE_GEM)
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_amethyst_block"), BlockRegistry.SOURCE_GEM_BLOCK)
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.FIRE_ESSENCE.getRegistryName()), ItemsRegistry.FIRE_ESSENCE)
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.EARTH_ESSENCE.getRegistryName()), ItemsRegistry.EARTH_ESSENCE)
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.WATER_ESSENCE.getRegistryName()), ItemsRegistry.WATER_ESSENCE)
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.AIR_ESSENCE.getRegistryName()), ItemsRegistry.AIR_ESSENCE)
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.ABJURATION_ESSENCE.getRegistryName()), ItemsRegistry.ABJURATION_ESSENCE)
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.CONJURATION_ESSENCE.getRegistryName()), ItemsRegistry.CONJURATION_ESSENCE)
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.MANIPULATION_ESSENCE.getRegistryName()), ItemsRegistry.MANIPULATION_ESSENCE));

        var enchantingApparatus = addPage(new DocEntryBuilder(MACHINES, BlockRegistry.ENCHANTING_APP_BLOCK)
                .withSortNum(2)
                .withIntroPage()
                .withPage(getRecipePages(BlockRegistry.ARCANE_PEDESTAL, BlockRegistry.ARCANE_PLATFORM))
                .withPage(getRecipePages(BlockRegistry.ENCHANTING_APP_BLOCK, BlockRegistry.ARCANE_CORE_BLOCK))
                .addConnectedSearch(BlockRegistry.ARCANE_CORE_BLOCK));


        var drygmyCharm = addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.DRYGMY_CHARM)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.DRYGMY_CHARM)
                .withPage(EntityEntry.create(ModEntities.ENTITY_DRYGMY.get(), getLangPath("drygmy_charm", 2)))
                .withPage(TextEntry.create(getLangPath("drygmy_charm", 3), Component.translatable("ars_nouveau.summoning")))
                .withPage(TextEntry.create(getLangPath("drygmy_charm", 4), Component.translatable("ars_nouveau.happiness")))
                .withPage(TextEntry.create(getLangPath("drygmy_charm", 5), Component.translatable(("ars_nouveau.production"))))
                .withCraftingPages(ItemsRegistry.GREATER_EXPERIENCE_GEM)
                .addConnectedSearch(ItemsRegistry.DRYGMY_SHARD)
                .addConnectedSearch(ItemsRegistry.EXPERIENCE_GEM)
                .addConnectedSearch(ItemsRegistry.GREATER_EXPERIENCE_GEM)
        );


        var mobJar = addPage(new DocEntryBuilder(MACHINES, "mob_jar")
                .withIcon(BlockRegistry.MOB_JAR)
                .withIntroPage()
                .withPage(TextEntry.create("ars_nouveau.page2.mob_jar", "ars_nouveau.title.mob_jar"))
                .withPage(EntityEntry.create(EntityType.VILLAGER, Component.translatable("mob_jar.villager")))
                .withPage(EntityEntry.create(EntityType.PIGLIN, Component.translatable("mob_jar.piglin")))
                .withPage(TextEntry.create("mob_jar.allay", "mob_jar.allay.title"))
                .withPage(EntityEntry.create(EntityType.ALLAY))
                .withPage(EntityEntry.create(EntityType.ENDER_DRAGON, Component.translatable("mob_jar.ender_dragon"), 1.5f, -30))
                .withPage(EntityEntry.create(EntityType.SHEEP, Component.translatable("mob_jar.sheep")))
                .withPage(EntityEntry.create(EntityType.CHICKEN, Component.translatable("mob_jar.chicken")))
                .withPage(EntityEntry.create(EntityType.ARMADILLO, Component.translatable("mob_jar.armadillo")))
                .withPage(EntityEntry.create(EntityType.SNIFFER, Component.translatable("mob_jar.sniffer")))
                .withPage(EntityEntry.create(EntityType.COW, Component.translatable("mob_jar.cow")))
                .withPage(EntityEntry.create(EntityType.MOOSHROOM, Component.translatable("mob_jar.mooshroom"), 0.7f))
                .withPage(EntityEntry.create(EntityType.PUFFERFISH, Component.translatable("mob_jar.pufferfish")))
                .withPage(EntityEntry.create(EntityType.FROG, Component.translatable("mob_jar.frog")))
                .withPage(EntityEntry.create(EntityType.PANDA, Component.translatable("mob_jar.panda"), 0.7f))
                .withPage(EntityEntry.create(EntityType.CAT, Component.translatable("mob_jar.cat")))
                .withPage(EntityEntry.create(EntityType.BREEZE, Component.translatable("mob_jar.breeze")))
                .withPage(EntityEntry.create(EntityType.SNOW_GOLEM, Component.translatable("mob_jar.snow_golem")))
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
                .withPage(TextEntry.create(getLangPath("starbuncle_charm", 8), Component.translatable("ars_nouveau.starbuncle_stacking")))
                .addConnectedSearch(ItemsRegistry.STARBUNCLE_SHARD));

        addPage(new DocEntryBuilder(EQUIPMENT, ItemsRegistry.DULL_TRINKET)
                .withCraftingPages(ItemsRegistry.DULL_TRINKET, ItemsRegistry.MUNDANE_BELT)
                .withCraftingPages(ItemsRegistry.RING_OF_POTENTIAL));

        var magebloom = addPage(new DocEntryBuilder(RESOURCES, BlockRegistry.MAGE_BLOOM_CROP)
                .withPage(TextEntry.create(Component.translatable("ars_nouveau.page.magebloom_crop"), BlockRegistry.MAGE_BLOOM_CROP.asItem().getDescription(), BlockRegistry.MAGE_BLOOM_CROP.asItem().getDefaultInstance()))
                .withCraftingPages(BlockRegistry.MAGE_BLOOM_CROP)
                .withCraftingPages(ItemsRegistry.MAGE_FIBER, BlockRegistry.MAGEBLOOM_BLOCK));

        addPage(new DocEntryBuilder(EQUIPMENT, ItemsRegistry.POTION_FLASK)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.POTION_FLASK)
                .withLocalizedText(ItemsRegistry.POTION_FLASK_EXTEND_TIME)
                .withCraftingPages(ItemsRegistry.POTION_FLASK_EXTEND_TIME)
                .withLocalizedText(ItemsRegistry.POTION_FLASK_AMPLIFY)
                .withCraftingPages(ItemsRegistry.POTION_FLASK_AMPLIFY));


        RecipeHolder<ReactiveEnchantmentRecipe> enchantmentRecipeRecipeHolder = manager.byKeyTyped(RecipeRegistry.REACTIVE_TYPE.get(), ArsNouveau.prefix(EnchantmentRegistry.REACTIVE_ENCHANTMENT.location().getPath() + "_" + 1));
        var annotatedCodex = addBasicItem(ItemsRegistry.ANNOTATED_CODEX, SPELL_CASTING);
        addPage(new DocEntryBuilder(ENCHANTMENTS, "reactive_enchantment")
                .withIcon(Items.ENCHANTED_BOOK)
                .withSortNum(2)
                .withIntroPage()
                .withPage(EnchantmentEntry.create(enchantmentRecipeRecipeHolder))
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
                .withCraftingPages(BlockRegistry.BASIC_SPELL_TURRET, BlockRegistry.ROTATING_TURRET)
                .withLocalizedText(BlockRegistry.ENCHANTED_SPELL_TURRET)
                .withCraftingPages(BlockRegistry.ENCHANTED_SPELL_TURRET)
                .withLocalizedText(BlockRegistry.TIMER_SPELL_TURRET)
                .withCraftingPages(BlockRegistry.TIMER_SPELL_TURRET)
                .withLocalizedText());

        addBasicItem(ItemsRegistry.SUMMONING_FOCUS, SPELL_CASTING);
        addBasicItem(ItemsRegistry.VOID_JAR, EQUIPMENT);
        addBasicItem(ItemsRegistry.WAND, SPELL_CASTING);
        addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.WHIRLISPRIG_CHARM)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.WHIRLISPRIG_CHARM)
                .withPage(EntityEntry.create(ModEntities.WHIRLISPRIG_TYPE.get(), getLangPath("whirlisprig_charm", 2)))
                .withPage(TextEntry.create(getLangPath("whirlisprig_charm", 3), Component.translatable(("ars_nouveau.summoning"))))
                .withPage(TextEntry.create(getLangPath("whirlisprig_charm", 4), Component.translatable(("ars_nouveau.happiness"))))
                .withPage(TextEntry.create(getLangPath("whirlisprig_charm", 5), Component.translatable(("ars_nouveau.important"))))
                .withPage(TextEntry.create(getLangPath("whirlisprig_charm", 6), Component.translatable(("ars_nouveau.production"))))
                .addConnectedSearch(ItemsRegistry.WHIRLISPRIG_SHARDS));

        var wixie = addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.WIXIE_CHARM)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.WIXIE_CHARM)
                .withPage(EntityEntry.create(ModEntities.ENTITY_WIXIE_TYPE.get(), getLangPath("wixie_charm", 2), 0.95f, -15))
                .withPage(TextEntry.create(getLangPath("wixie_charm", 3), Component.translatable(("ars_nouveau.item_crafting"))))
                .withPage(TextEntry.create(getLangPath("wixie_charm", 4), Component.translatable(("ars_nouveau.item_crafting_setting"))))
                .withPage(TextEntry.create(getLangPath("wixie_charm", 7), Component.translatable(("ars_nouveau.binding_inventories"))))
                .withPage(TextEntry.create(getLangPath("wixie_charm", 5), Component.translatable(("ars_nouveau.potion_crafting"))))
                .withPage(TextEntry.create(getLangPath("wixie_charm", 6)))
                .addConnectedSearch(ItemsRegistry.WIXIE_SHARD));

        var archwood = addPage(new DocEntryBuilder(RESOURCES, "archwood")
                .withIcon(BlockRegistry.CASCADING_SAPLING)
                .withIntroPage()
                .withPage(TextEntry.create(Component.translatable("ars_nouveau.page.bombegrante"), Component.translatable("block.ars_nouveau.bombegranate_pod"), BlockRegistry.BOMBEGRANTE_POD))
                .withCraftingPages(BlockRegistry.BOMBEGRANTE_POD)
                .withPage(Documentation.getForPotionRecipes(BlockRegistry.BOMBEGRANTE_POD.asItem().getDefaultInstance()))
                .withPage(TextEntry.create(Component.translatable("ars_nouveau.page.mendosteen"), Component.translatable("block.ars_nouveau.mendosteen_pod"), BlockRegistry.MENDOSTEEN_POD))
                .withCraftingPages(BlockRegistry.MENDOSTEEN_POD)
                .withPage(Documentation.getForPotionRecipes(BlockRegistry.MENDOSTEEN_POD.asItem().getDefaultInstance()))
                .withPage(TextEntry.create(Component.translatable("ars_nouveau.page.frostaya"), Component.translatable("block.ars_nouveau.frostaya_pod"), BlockRegistry.FROSTAYA_POD))
                .withCraftingPages(BlockRegistry.FROSTAYA_POD)
                .withPage(Documentation.getForPotionRecipes(BlockRegistry.FROSTAYA_POD.asItem().getDefaultInstance()))
                .withPage(TextEntry.create(Component.translatable("ars_nouveau.page.bastion_fruit"), Component.translatable("block.ars_nouveau.bastion_pod"), BlockRegistry.BASTION_POD))
                .withCraftingPages(BlockRegistry.BASTION_POD)
                .withPage(Documentation.getForPotionRecipes(BlockRegistry.BASTION_POD.asItem().getDefaultInstance()))
                .addConnectedSearch(BlockRegistry.BOMBEGRANTE_POD)
                .addConnectedSearch(BlockRegistry.MENDOSTEEN_POD)
                .addConnectedSearch(BlockRegistry.FROSTAYA_POD)
                .addConnectedSearch(BlockRegistry.BASTION_POD));


        addPage(new DocEntryBuilder(RESOURCES, "decorative")
                .withIcon(SOURCESTONE)
                .withCraftingPages(SOURCESTONE)
                .withIntroPage());


        var sourceberry = addPage(new DocEntryBuilder(RESOURCES, BlockRegistry.SOURCEBERRY_BUSH)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.SOURCE_BERRY_PIE, ItemsRegistry.SOURCE_BERRY_ROLL));

        int walkerOffset = -20;
        addPage(new DocEntryBuilder(RESOURCES, "weald_walker")
                .withIcon(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix(RitualLib.AWAKENING)))
                .withIntroPage()
                .withPage(EntityEntry.create(ModEntities.ENTITY_BLAZING_WEALD.get(), getLangPath("weald_walker", 2)))
                .withPage(EntityEntry.create(ModEntities.ENTITY_CASCADING_WEALD.get(), getLangPath("weald_walker", 3)))
                .withPage(EntityEntry.create(ModEntities.ENTITY_FLOURISHING_WEALD.get(), getLangPath("weald_walker", 4)))
                .withPage(EntityEntry.create(ModEntities.ENTITY_VEXING_WEALD.get(), getLangPath("weald_walker", 5))));

        addPage(new DocEntryBuilder(RESOURCES, "wilden")
                .withIcon(ItemsRegistry.WILDEN_SPIKE)
                .withIntroPage()
                .withPage(EntityEntry.create(ModEntities.WILDEN_HUNTER.get(), getLangPath("wilden", 3), 0.55f))
                .withPage(EntityEntry.create(ModEntities.WILDEN_STALKER.get(), getLangPath("wilden", 4), 0.55f))
                .withPage(EntityEntry.create(ModEntities.WILDEN_GUARDIAN.get(), getLangPath("wilden", 5), 0.55f))
                .withPage(EntityEntry.create(ModEntities.WILDEN_BOSS.get(), getLangPath("wilden", 6), 0.55f))
                .withPage(TextEntry.create(getLangPath("wilden", 7)))
                .addConnectedSearch(ItemsRegistry.WILDEN_SPIKE)
                .addConnectedSearch(ItemsRegistry.WILDEN_WING)
                .addConnectedSearch(ItemsRegistry.WILDEN_HORN)
                .addConnectedSearch(ItemsRegistry.WILDEN_TRIBUTE));

        var denyScroll = addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.DENY_ITEM_SCROLL)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.BLANK_PARCHMENT, ItemsRegistry.DENY_ITEM_SCROLL));

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

        var spellBooks = addPage(new DocEntryBuilder(SPELL_CASTING, "spell_books")
                .withSortNum(1)
                .withIcon(ItemsRegistry.ARCHMAGE_SPELLBOOK)
                .withIntroPage()
                .withPage(getRecipePages(RegistryHelper.getRegistryName(ItemsRegistry.NOVICE_SPELLBOOK.asItem()), ArsNouveau.prefix("apprentice_book_upgrade")))
                .withCraftingPages(ArsNouveau.prefix("archmage_book_upgrade"), ItemsRegistry.ARCHMAGE_SPELLBOOK));

        addBasicItem(ItemsRegistry.ENCHANTERS_MIRROR, SPELL_CASTING);
        addBasicItem(ItemsRegistry.ENCHANTERS_SHIELD, SPELL_CASTING);
        addBasicItem(ItemsRegistry.ENCHANTERS_SWORD, SPELL_CASTING);
        addPage(new DocEntryBuilder(SPELL_CASTING, ItemsRegistry.SPELL_BOW)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.SPELL_BOW)
                .withCraftingPages("ars_nouveau:imbuement_amplify_arrow", ItemsRegistry.AMPLIFY_ARROW)
                .withCraftingPages("ars_nouveau:imbuement_pierce_arrow", ItemsRegistry.PIERCE_ARROW)
                .withCraftingPages("ars_nouveau:imbuement_split_arrow", ItemsRegistry.SPLIT_ARROW));
        addPage(new DocEntryBuilder(SPELL_CASTING, ItemsRegistry.SPELL_CROSSBOW)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.SPELL_CROSSBOW)
                .withCraftingPages("ars_nouveau:imbuement_amplify_arrow", ItemsRegistry.AMPLIFY_ARROW)
                .withCraftingPages("ars_nouveau:imbuement_pierce_arrow", ItemsRegistry.PIERCE_ARROW)
                .withCraftingPages("ars_nouveau:imbuement_split_arrow", ItemsRegistry.SPLIT_ARROW));
        addBasicItem(ItemsRegistry.RUNIC_CHALK, SPELL_CASTING);


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
        var alchemical = addBasicItem(BlockRegistry.ALCHEMICAL_BLOCK, SOURCE).withRelation(wixie).withRelation(melder);
        var mycelial = addBasicItem(BlockRegistry.MYCELIAL_BLOCK, SOURCE);

        var relay = addBasicItem(BlockRegistry.RELAY, SOURCE, CraftingEntry.create(manager.byKeyTyped(RecipeType.CRAFTING, getRegistryName(BlockRegistry.RELAY.get())), Component.translatable("ars_nouveau.page2.relay"))).withRelation(dominionWand);

        addBasicItem(BlockRegistry.RELAY_DEPOSIT, SOURCE);
        addBasicItem(BlockRegistry.RELAY_SPLITTER, SOURCE);
        addBasicItem(BlockRegistry.RELAY_WARP, SOURCE);
        addBasicItem(BlockRegistry.RELAY_COLLECTOR, SOURCE);
        addBasicItem(BlockRegistry.VITALIC_BLOCK, SOURCE);
        var volcanic = addPage(new DocEntryBuilder(SOURCE, BlockRegistry.VOLCANIC_BLOCK)
                .withIntroPage()
                .withPage(TextEntry.create(getLangPath("volcanic_sourcelink", 2), Component.translatable("ars_nouveau.active_generation")))
                .withCraftingPages(BlockRegistry.VOLCANIC_BLOCK));
        addPage(new DocEntryBuilder(ENCHANTMENTS, "how_to_enchant")
                .withIcon(BlockRegistry.ENCHANTING_APP_BLOCK)
                .withSortNum(-1)
                .withIntroPage()
                .withLocalizedText()
        ).withRelation(enchantingApparatus);
        var sourceJar = addBasicItem(BlockRegistry.SOURCE_JAR, SOURCE, 0).withRelation(relay)
                .withRelation(agronomic)
                .withRelation(volcanic);

        addPage(new DocEntryBuilder(RITUALS, "performing_rituals")
                .withSortNum(-1)
                .withIcon(BlockRegistry.RITUAL_BLOCK)
                .withIntroPage()
                .withLocalizedText()
                .withCraftingPages(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix(RitualLib.SUNRISE))))
                .withRelation(ritualBrazier);
        addPage(new DocEntryBuilder(FAMILIARS, "summoning_familiars")
                .withSortNum(-1)
                .withIcon(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix(RitualLib.BINDING)))
                .withIntroPage()
                .withLocalizedText()
                .withCraftingPages(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix(RitualLib.BINDING))))
                .withRelations(ritualBrazier)
                .withRelation(ArsNouveau.prefix(RitualLib.BINDING));

//        addPage(new DocEntryBuilder(MOD_NEWS, "mod_news")
//                        .withIcon(ItemsRegistry.SPELL_PARCHMENT)
//                        .withPage(new LinkPage("https://discord.gg/y7TMXZu", "ars_nouveau.discord_text", "ars_nouveau.community")));


        addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.AMETHYST_GOLEM_CHARM)
                .withIntroPage()
                .withPage(EntityEntry.create(ModEntities.AMETHYST_GOLEM.get(), getLangPath("amethyst_golem_charm", 2), 0.75f))
                .withPage(TextEntry.create(getLangPath("amethyst_golem_charm", 3), Component.translatable("ars_nouveau.summoning")))
                .withPage(TextEntry.create(getLangPath("amethyst_golem_charm", 4), Component.translatable("ars_nouveau.amethyst_farming")))
                .withPage(TextEntry.create(getLangPath("amethyst_golem_charm", 5), Component.translatable("ars_nouveau.amethyst_storage"))));

        var starbyBed = addPage(new DocEntryBuilder(AUTOMATION, BlockRegistry.ORANGE_SBED)
                .withName("ars_nouveau.summon_bed")
                .withIntroPage("summon_bed")
                .withPage(getRecipePages(BlockRegistry.ORANGE_SBED, BlockRegistry.BLUE_SBED))
                .withPage(getRecipePages(BlockRegistry.GREEN_SBED, BlockRegistry.YELLOW_SBED))
                .withPage(getRecipePages(BlockRegistry.RED_SBED, BlockRegistry.PURPLE_SBED)));
        var scryCaster = addBasicItem(ItemsRegistry.SCRY_CASTER, SPELL_CASTING);
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


        addPage(new DocEntryBuilder(SPELL_CASTING, ItemsRegistry.SHAPERS_FOCUS)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.SHAPERS_FOCUS)
                .withPage(TextEntry.create(getLangPath("shapers_focus", 2), Component.translatable("ars_nouveau.shapers_focus.blocks")))
                .withPage(TextEntry.create(getLangPath("shapers_focus", 3), Component.translatable("ars_nouveau.shapers_focus.entities")))
                .withPage(TextEntry.create(getLangPath("shapers_focus", 4), Component.translatable("ars_nouveau.shapers_focus.examples"))));
        addBasicItem(ItemsRegistry.ALCHEMISTS_CROWN, EQUIPMENT);
        addPage(new DocEntryBuilder(EQUIPMENT, "flask_cannons")
                .withIcon(ItemsRegistry.SPLASH_LAUNCHER)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.SPLASH_LAUNCHER)
                .withCraftingPages(ItemsRegistry.LINGERING_LAUNCHER));

        DocEntryBuilder ARMOR_ENTRY = new DocEntryBuilder(ARMOR, "armor")
                .withIcon(ItemsRegistry.SORCERER_ROBES)
                .withSortNum(0)
                .withIntroPage()
                .withPage(TextEntry.create("ars_nouveau.page.threads", "ars_nouveau.threads"))
                .withPage(PerkDiagramEntry.create(ItemsRegistry.SORCERER_HOOD, ItemsRegistry.SORCERER_ROBES, ItemsRegistry.SORCERER_LEGGINGS, ItemsRegistry.SORCERER_BOOTS))
                .withPage(PerkDiagramEntry.create(ItemsRegistry.ARCANIST_HOOD, ItemsRegistry.ARCANIST_ROBES, ItemsRegistry.ARCANIST_LEGGINGS, ItemsRegistry.ARCANIST_BOOTS))
                .withPage(PerkDiagramEntry.create(ItemsRegistry.BATTLEMAGE_HOOD, ItemsRegistry.BATTLEMAGE_ROBES, ItemsRegistry.BATTLEMAGE_LEGGINGS, ItemsRegistry.BATTLEMAGE_BOOTS))
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
                .withIcon(BlockRegistry.ALTERATION_TABLE)
                .withIntroPage()
                .withPage(getRecipePages(BlockRegistry.ALTERATION_TABLE, ItemsRegistry.BLANK_THREAD))
                .withSortNum(3))
                .withRelation(armorEntry);

        var armorUpgrade = addPage(new DocEntryBuilder(ARMOR, "armor_upgrading")
                .withIcon(ItemsRegistry.ARCANIST_HOOD)
                .withIntroPage()
                .withPage(TextEntry.create(getLangPath("armor_upgrading", 2), Component.translatable("ars_nouveau.armor_tiers")))
                .withCraftingPages("ars_nouveau:first_armor_upgrade", null)
                .withCraftingPages("ars_nouveau:second_armor_upgrade", null)
                .withSortNum(1))
                .withRelations(alteraitonTable, armorEntry);

        armorEntry.withRelation(armorUpgrade);
        alteraitonTable.withRelation(armorUpgrade);

        addPage(new DocEntryBuilder(ARMOR, "applying_perks")
                .withIcon(ItemsRegistry.BLANK_THREAD)
                .withIntroPage()
                .withLocalizedText()
                .withSortNum(2))
                .withRelations(armorEntry, alteraitonTable);

        addPage(new DocEntryBuilder(ARMOR, "alteration_table")
                .withIcon(BlockRegistry.ALTERATION_TABLE)
                .withIntroPage()
                .withPage(getRecipePages(BlockRegistry.ALTERATION_TABLE, ItemsRegistry.BLANK_THREAD))
                .withSortNum(2))
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
                .withCraftingPages(BlockRegistry.REPOSITORY, BlockRegistry.ARCHWOOD_CHEST));

        // add scrolls to arrylist
        var scrollRelations = new ArrayList<DocEntry>() {
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
                .withCraftingPages(BlockRegistry.ARCHWOOD_GRATE, BlockRegistry.GOLD_GRATE)
                .withCraftingPages(BlockRegistry.SMOOTH_SOURCESTONE_GRATE, BlockRegistry.SOURCESTONE_GRATE));
        addBasicItem(BlockRegistry.SOURCE_LAMP, AUTOMATION);

        addPage(new DocEntryBuilder(AUTOMATION, ItemsRegistry.ALAKARKINOS_CHARM)
                .withIntroPage()
                .withCraftingPages(ItemsRegistry.ALAKARKINOS_CHARM)
                .withPage(EntityEntry.create(ModEntities.ALAKARKINOS_TYPE.get(), getLangPath("alakarkinos_charm", 2), 0.5f, -10))
                .withPage(TextEntry.create(getLangPath("alakarkinos_charm", 3), Component.translatable("ars_nouveau.summoning")))
                .withPage(TextEntry.create(getLangPath("alakarkinos_charm", 4), Component.translatable("ars_nouveau.sifting")))
                .addConnectedSearch(ItemsRegistry.ALAKARKINOS_SHARD));


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
                .withRelation(dowsingRod)
                .withRelation(imbuementChamber);

        addPage(new DocEntryBuilder(GETTING_STARTED, "new_glyphs")
                .withIcon(ItemsRegistry.BLANK_GLYPH)
                .withSortNum(4)
                .withIntroPage())
                .withRelations(scribesTable, annotatedCodex);

        addPage(new DocEntryBuilder(GETTING_STARTED, "source")
                .withSortNum(5)
                .withIcon(BlockRegistry.SOURCE_JAR)
                .withIntroPage())
                .withRelations(sourceJar, agronomic, relay);

        addPage(new DocEntryBuilder(GETTING_STARTED, "apparatus_crafting")
                .withSortNum(6)
                .withIcon(BlockRegistry.ENCHANTING_APP_BLOCK)
                .withIntroPage())
                .withRelations(enchantingApparatus, magebloom);

        addPage(new DocEntryBuilder(GETTING_STARTED, "better_casting")
                .withSortNum(7)
                .withIcon(ItemsRegistry.SORCERER_ROBES)
                .withIntroPage())
                .withRelations(enchantingApparatus, armorEntry, armorUpgrade);

        addPage(new DocEntryBuilder(GETTING_STARTED, "world_generation")
                .withSortNum(8)
                .withIcon(ItemsRegistry.SOURCE_GEM)
                .withIntroPage())
                .withRelations(imbuementChamber, archwood, sourceberry, dowsingRod);

        addPage(new DocEntryBuilder(GETTING_STARTED, "upgrades")
                .withSortNum(9)
                .withIcon(ItemsRegistry.ARCHMAGE_SPELLBOOK)
                .withIntroPage())
                .withRelations(spellBooks, armorEntry);

        addPage(new DocEntryBuilder(GETTING_STARTED, "starting_automation")
                .withSortNum(10)
                .withIcon(BlockRegistry.BASIC_SPELL_TURRET)
                .withIntroPage())
                .withRelations(turrets, prisms, starby, wixie, drygmyCharm);

        addPage(new DocEntryBuilder(GETTING_STARTED, "trinkets")
                .withIcon(ItemsRegistry.WARP_SCROLL)
                .withSortNum(11)
                .withIntroPage())
                .withRelations(jarOfLight, amuletOfRegen, discountRing, beltOfLevitation, stableScroll);

        var enchantmentRecipes = new ArrayList<>(manager.getAllRecipesFor(RecipeRegistry.ENCHANTMENT_TYPE.get()));
        enchantmentRecipes.sort(Comparator.comparingInt(a -> a.value() == null ? -1 : a.value().enchantLevel));
        Map<ResourceKey<Enchantment>, List<RecipeHolder<EnchantmentRecipe>>> enchantmentMap = new HashMap<>();
        for (RecipeHolder<EnchantmentRecipe> recipe : enchantmentRecipes) {
            EnchantmentRecipe recipe1 = recipe.value();
            if (recipe1 == null) {
                continue;
            }
            var key = recipe1.enchantmentKey;
            if (!enchantmentMap.containsKey(key)) {
                enchantmentMap.put(key, new ArrayList<>());
            }
            enchantmentMap.get(key).add(recipe);
        }

        for (var entry : enchantmentMap.entrySet()) {
            var enchantment = entry.getKey();
            var minMax = entry.getValue();
            if (level.holder(enchantment).isEmpty()) break;
            DocEntryBuilder builder = new DocEntryBuilder(ENCHANTMENTS, enchantment.location().getPath())
                    .withIcon(Items.ENCHANTED_BOOK);
            builder.entryId = enchantment.location();
            builder.title = level.holderOrThrow(enchantment).value().description();
            for (RecipeHolder<EnchantmentRecipe> max : minMax) {
                builder.withPage(EnchantmentEntry.create(max));
            }
            addPage(builder);
        }

        for (DocEntryBuilder builder : pendingBuilders) {
            addPage(builder);
        }
        pendingBuilders = new ArrayList<>();

        NeoForge.EVENT_BUS.post(new ReloadDocumentationEvent.AddEntries());
        NeoForge.EVENT_BUS.post(new ReloadDocumentationEvent.Post());

        Search.initSearchIndex();
        long endTime = System.nanoTime();
        Log.getLogger().info("Documentation loaded in {}ms", (endTime - startTime) / 1000000);
    }

    public static DocEntry addPage(DocEntryBuilder builder) {
        DocEntry entry = DocumentationRegistry.registerEntry(builder.category, builder.build());
        if (Documentation.entries.contains(entry)) {
            throw new IllegalStateException("Entry already exists: " + entry);
        }
        return entry;
    }

    public static DocEntryBuilder buildPage(DocEntryBuilder builder) {
        Documentation.pendingBuilders.add(builder);
        return builder;
    }

    public static DocEntryBuilder buildBasicItem(ItemLike item, DocCategory category) {
        Item asItem = item.asItem();
        ResourceLocation registryName = getRegistryName(asItem);
        var builder = new DocEntryBuilder(category, asItem.getDescriptionId())
                .withIcon(asItem)
                .withPage(TextEntry.create(Component.translatable(registryName.getNamespace() + ".page." + registryName.getPath()))).withCraftingPages(item);
        Documentation.pendingBuilders.add(builder);
        return builder;
    }

    public static DocEntry addBasicItem(ItemLike item, DocCategory category) {
        return addBasicItem(item, category, 100);
    }


    public static DocEntry addBasicItem(ItemLike item, DocCategory category, int order) {
        ItemStack stack = new ItemStack(item);
        ResourceLocation registryName = getRegistryName(item.asItem());
        return addPage(new DocEntryBuilder(category, stack.getDescriptionId())
                .withIcon(item)
                .withSortNum(order)
                .withPage(TextEntry.create(Component.translatable(registryName.getNamespace() + ".page." + registryName.getPath()), stack.getItem().getDescription(), stack)).withCraftingPages(item));
    }

    public static DocEntry addBasicItem(ItemLike item, DocCategory category, ResourceLocation recipeId) {
        ResourceLocation registryName = getRegistryName(item.asItem());
        return addPage(new DocEntryBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withPage(TextEntry.create(Component.translatable(registryName.getNamespace() + ".page." + getRegistryName(item.asItem()).getPath())))
                .withPage(getRecipePages(recipeId)));
    }

    public static DocEntry addBasicItem(ItemLike item, DocCategory category, SinglePageCtor recipePage) {
        Item item1 = item.asItem();
        ResourceLocation registryName = getRegistryName(item1);
        return addPage(new DocEntryBuilder(category, item1.getDescriptionId())
                .withIcon(item)
                .withPage(TextEntry.create(Component.translatable(registryName.getNamespace() + ".page." + registryName.getPath()), item1.getDescription(), new ItemStack(item1)))
                .withPage(recipePage));
    }

    public static List<SinglePageCtor> getRecipePages(ItemLike stack1, ItemLike stack2) {
        var key1 = RegistryHelper.getRegistryName(stack1.asItem());
        var key2 = RegistryHelper.getRegistryName(stack2.asItem());
        return getRecipePages(key1, key2);
    }

    public static List<SinglePageCtor> getRecipePages(ResourceLocation key1, ResourceLocation key2) {
        Level level = ArsNouveau.proxy.getClientWorld();
        RecipeManager manager = level.getRecipeManager();
        RecipeHolder<CraftingRecipe> recipe1 = manager.byKeyTyped(RecipeType.CRAFTING, key1);
        RecipeHolder<CraftingRecipe> recipe2 = manager.byKeyTyped(RecipeType.CRAFTING, key2);
        List<SinglePageCtor> pages = new ArrayList<>();
        if (recipe1 != null && recipe2 != null) {
            pages.add(CraftingEntry.create(recipe1, recipe2));
            return pages;
        }
        pages.addAll(getRecipePages(key1));
        pages.addAll(getRecipePages(key2));
        return pages;
    }

    public static List<SinglePageCtor> getRecipePages(ItemStack stack, ResourceLocation recipeId) {
        return getRecipePages(recipeId);
    }

    public static List<SinglePageCtor> getRecipePages(ResourceLocation recipeId) {
        Level level = ArsNouveau.proxy.getClientWorld();
        RecipeManager manager = level.getRecipeManager();

        List<SinglePageCtor> pages = new ArrayList<>();

        RecipeHolder<GlyphRecipe> glyphRecipe = manager.byKeyTyped(RecipeRegistry.GLYPH_TYPE.get(), recipeId);

        if (glyphRecipe != null) {
            pages.add(GlyphRecipeEntry.create(glyphRecipe));
            return pages;
        }

        RecipeHolder<CraftingRecipe> recipe = manager.byKeyTyped(RecipeType.CRAFTING, recipeId);

        if (recipe != null) {
            pages.add(CraftingEntry.create(recipe));
            return pages;
        }

        RecipeHolder<EnchantingApparatusRecipe> apparatusRecipe = manager.byKeyTyped(RecipeRegistry.APPARATUS_TYPE.get(), recipeId);

        if (apparatusRecipe != null) {
            pages.add(ApparatusEntry.create(apparatusRecipe));
            return pages;
        }

        RecipeHolder<ImbuementRecipe> imbuementRecipe = manager.byKeyTyped(RecipeRegistry.IMBUEMENT_TYPE.get(), recipeId);
        if (imbuementRecipe != null) {
            pages.add(ImbuementRecipeEntry.create(imbuementRecipe));
            return pages;
        }
        return pages;
    }

    public static List<SinglePageCtor> getForPotionRecipes(ItemStack stack) {
        List<BrewingRecipe> brewingRecipes = ArsNouveauAPI.getInstance().getAllPotionRecipes(Minecraft.getInstance().level);
        List<SinglePageCtor> pages = new ArrayList<>();
        List<BrewingRecipe> matchingRecipes = new ArrayList<>();
        for (BrewingRecipe recipe : brewingRecipes) {
            if (recipe.isInput(stack) || recipe.isIngredient(stack) || ItemStack.isSameItem(stack, recipe.getOutput())) {
                matchingRecipes.add(recipe);
            }
        }
        BrewingRecipe recipe1 = null;
        for (BrewingRecipe matchingRecipe : matchingRecipes) {
            if (recipe1 == null) {
                recipe1 = matchingRecipe;
            } else {
                pages.add(PotionRecipeEntry.create(recipe1, matchingRecipe));
                recipe1 = null;
            }
        }
        if (recipe1 != null) {
            pages.add(PotionRecipeEntry.create(recipe1, null));
        }
        return pages;
    }

    public static Component getLangPath(String name, int count) {
        return Component.translatable("ars_nouveau.page" + count + "." + name);
    }

    public static Component getLangPath(String namespace, String name, int count) {
        return Component.translatable(namespace + ".page" + count + "." + name);
    }


    public static DocCategory glyphCategory(SpellTier tier) {
        return switch (tier.value) {
            case 1 -> DocumentationRegistry.GLYPH_TIER_ONE;
            case 2 -> DocumentationRegistry.GLYPH_TIER_TWO;
            case 3, 99 -> DocumentationRegistry.GLYPH_TIER_THREE;
            default -> DocumentationRegistry.GLYPH_TIER_ONE;
        };
    }
}
