package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.lib.LibEntityNames;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectLightning;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.util.Log;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentationRegistry {
    public static final Comparator<DocEntry> GLYPH_PAGE_COMPARATOR = (o1, o2) -> {
        if(o1.renderStack().getItem() instanceof Glyph glyph1 && o2.renderStack().getItem() instanceof Glyph glyph2){
            return CreativeTabRegistry.COMPARE_SPELL_TYPE_NAME.compare(glyph1.spellPart, glyph2.spellPart);
        }
        return o1.compareTo(o2);
    };

    public static final DocCategory GETTING_STARTED = new DocCategory(ArsNouveau.prefix("getting_started"), ItemsRegistry.NOVICE_SPELLBOOK.get().getDefaultInstance(), 1);

    public static final DocCategory GLYPH_INDEX = new DocCategory(ArsNouveau.prefix("glyph_index"), MethodProjectile.INSTANCE.glyphItem.getDefaultInstance(), 100);
    public static final DocCategory GLYPH_TIER_ONE = new DocCategory(ArsNouveau.prefix("glyphs_tier_one"), MethodProjectile.INSTANCE.glyphItem.getDefaultInstance(), 100).withComparator(GLYPH_PAGE_COMPARATOR);
    public static final DocCategory GLYPH_TIER_TWO = new DocCategory(ArsNouveau.prefix("glyphs_tier_two"), AugmentAOE.INSTANCE.glyphItem.getDefaultInstance(), 200).withComparator(GLYPH_PAGE_COMPARATOR);
    public static final DocCategory GLYPH_TIER_THREE = new DocCategory(ArsNouveau.prefix("glyphs_tier_three"), EffectLightning.INSTANCE.glyphItem.getDefaultInstance(), 300).withComparator(GLYPH_PAGE_COMPARATOR);

    public static final DocCategory RITUAL_INDEX = new DocCategory(ArsNouveau.prefix("ritual_index"), BlockRegistry.RITUAL_BLOCK.asItem().getDefaultInstance(), 700);

    public static final DocCategory SOURCE = new DocCategory(ArsNouveau.prefix("source"), BlockRegistry.SOURCE_JAR.asItem().getDefaultInstance(), 300);

    public static final DocCategory CRAFTING = new DocCategory(ArsNouveau.prefix("crafting"), BlockRegistry.ENCHANTING_APP_BLOCK.asItem().getDefaultInstance(), 400);

    public static final DocCategory ENCHANTING = new DocCategory(ArsNouveau.prefix("enchanting"), Items.ENCHANTED_BOOK.getDefaultInstance(), 1000);

    public static final DocCategory FIELD_GUIDE = new DocCategory(ArsNouveau.prefix("field_guide"), BlockRegistry.VEXING_SAPLING.asItem().getDefaultInstance(), 900);

    public static final DocCategory ITEMS_BLOCKS_EQUIPMENT = new DocCategory(ArsNouveau.prefix("items_equipment"), ItemsRegistry.BELT_OF_LEVITATION.asItem().getDefaultInstance(), 500);


    public static final DocCategory SPELL_CASTING = new DocCategory(ArsNouveau.prefix("spell_casting"), ItemsRegistry.ARCHMAGE_SPELLBOOK.asItem().getDefaultInstance(), 100);

    public static final DocCategory ITEMS = new DocCategory(ArsNouveau.prefix("items"), ItemsRegistry.BELT_OF_LEVITATION.asItem().getDefaultInstance(), 200);

    public static final DocCategory ARMOR = new DocCategory(ArsNouveau.prefix("armor"), ItemsRegistry.ARCANIST_HOOD.asItem().getDefaultInstance(), 300);


    public static final DocCategory FAMILIARS = new DocCategory(ArsNouveau.prefix("familiars"), FamiliarRegistry.getFamiliarScriptMap().get(ArsNouveau.prefix(LibEntityNames.FAMILIAR_STARBUNCLE)).asItem().getDefaultInstance(), 800);



    private static final Map<ResourceLocation, DocCategory> mainCategoryMap = new ConcurrentHashMap<>();

    private static final Map<ResourceLocation, DocEntry> entryMap = new ConcurrentHashMap<>();
    private static final Set<DocEntry> allEntries = ConcurrentHashMap.newKeySet();
    private static final Map<DocCategory, Set<DocEntry>> categoryToEntriesMap = new ConcurrentHashMap<>();
    private static final Map<DocEntry, DocCategory> entryToCategoryMap = new ConcurrentHashMap<>();


    static {
        registerMainCategory(GETTING_STARTED);

        GLYPH_INDEX.addSubCategory(GLYPH_TIER_ONE);
        GLYPH_INDEX.addSubCategory(GLYPH_TIER_TWO);
        GLYPH_INDEX.addSubCategory(GLYPH_TIER_THREE);

        registerMainCategory(GLYPH_INDEX);
        registerMainCategory(RITUAL_INDEX);
        registerMainCategory(SOURCE);
        registerMainCategory(CRAFTING);
        registerMainCategory(FIELD_GUIDE);
        ITEMS_BLOCKS_EQUIPMENT.addSubCategory(SPELL_CASTING);
        ITEMS_BLOCKS_EQUIPMENT.addSubCategory(ITEMS);
        ITEMS_BLOCKS_EQUIPMENT.addSubCategory(ARMOR);
        registerMainCategory(FAMILIARS);
        registerMainCategory(ITEMS_BLOCKS_EQUIPMENT);
        registerMainCategory(ENCHANTING);
    }

    public static void registerMainCategory(DocCategory section){
        mainCategoryMap.put(section.id(), section);
    }

    /**
     * Entries are backed by a map, evaluated by the ID of the entry. You may call this
     * as many times as you like to overwrite the entry.
     */
    public static DocEntry registerEntry(DocCategory category, DocEntry entry){
        if(!category.subCategories().isEmpty()){
            Log.getLogger().error("Cannot register an entry to a category with subcategories");
            return entry;
        }
        entryMap.put(entry.id(), entry);
        allEntries.add(entry);
        entryToCategoryMap.put(entry, category);
        var entries = categoryToEntriesMap.computeIfAbsent(category, k -> ConcurrentHashMap.newKeySet());
        entries.remove(entry); // Remove and overwrite in the case of world reloads
        entries.add(entry);
        entry.categories().add(category);
        return entry;
    }

    public static Set<DocEntry> getEntries(){
        return allEntries;
    }

    public static Set<DocEntry> getEntries(DocCategory category){
        Set<DocEntry> entries = categoryToEntriesMap.get(category);
        return entries == null ? ConcurrentHashMap.newKeySet() : entries;
    }

    @Nullable
    public static DocEntry getEntry(ResourceLocation id){
        return entryMap.get(id);
    }

    public static DocCategory getCategory(ResourceLocation id){
        return mainCategoryMap.get(id);
    }

    public static DocCategory getCategoryForEntry(DocEntry entry){
        return entryToCategoryMap.get(entry);
    }

    public static Map<ResourceLocation, DocCategory> getMainCategoryMap(){
        return mainCategoryMap;
    }
}
