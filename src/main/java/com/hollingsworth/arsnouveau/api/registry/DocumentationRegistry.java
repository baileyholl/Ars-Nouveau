package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.documentation.DocEntry;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DocumentationRegistry {
    public static final Comparator<DocEntry> GLYPH_PAGE_COMPARATOR = (o1, o2) -> {
        if(o1.renderStack().getItem() instanceof Glyph glyph1 && o2.renderStack().getItem() instanceof Glyph glyph2){
            return CreativeTabRegistry.COMPARE_SPELL_TYPE_NAME.compare(glyph1.spellPart, glyph2.spellPart);
        }
        return o1.compareTo(o2);
    };

    public static final DocCategory GETTING_STARTED = new DocCategory(ArsNouveau.prefix("getting_started"), MethodProjectile.INSTANCE.glyphItem.getDefaultInstance(), 1);
    public static final DocCategory GLYPH_INDEX = new DocCategory(ArsNouveau.prefix("glyph_index"), MethodProjectile.INSTANCE.glyphItem.getDefaultInstance(), 100);
    public static final DocCategory GLYPH_TIER_ONE = new DocCategory(ArsNouveau.prefix("glyphs_tier_one"), MethodProjectile.INSTANCE.glyphItem.getDefaultInstance(), 100).withComparator(GLYPH_PAGE_COMPARATOR);
    public static final DocCategory GLYPH_TIER_TWO = new DocCategory(ArsNouveau.prefix("glyphs_tier_two"), MethodProjectile.INSTANCE.glyphItem.getDefaultInstance(), 200).withComparator(GLYPH_PAGE_COMPARATOR);
    public static final DocCategory GLYPH_TIER_THREE = new DocCategory(ArsNouveau.prefix("glyphs_tier_three"), MethodProjectile.INSTANCE.glyphItem.getDefaultInstance(), 300).withComparator(GLYPH_PAGE_COMPARATOR);

    public static final DocCategory RITUAL_INDEX = new DocCategory(ArsNouveau.prefix("ritual_index"), MethodProjectile.INSTANCE.glyphItem.getDefaultInstance(), 200);
    public static final DocCategory SOURCE = new DocCategory(ArsNouveau.prefix("source"), MethodProjectile.INSTANCE.glyphItem.getDefaultInstance(), 300);
    public static final DocCategory CRAFTING = new DocCategory(ArsNouveau.prefix("crafting"), MethodProjectile.INSTANCE.glyphItem.getDefaultInstance(), 400);
    public static final DocCategory MAGICAL_SYSTEMS = new DocCategory(ArsNouveau.prefix("magical_systems"), MethodProjectile.INSTANCE.glyphItem.getDefaultInstance(), 500);
    public static final DocCategory CREATURE_COMPENDIUM = new DocCategory(ArsNouveau.prefix("creature_compendium"), MethodProjectile.INSTANCE.glyphItem.getDefaultInstance(), 600);
    public static final DocCategory ITEMS_BLOCKS_EQUIPMENT = new DocCategory(ArsNouveau.prefix("items"), MethodProjectile.INSTANCE.glyphItem.getDefaultInstance(), 700);

    private static final Map<ResourceLocation, DocCategory> mainCategoryMap = new ConcurrentHashMap<>();

    private static final Map<ResourceLocation, DocEntry> entryMap = new ConcurrentHashMap<>();
    private static final Map<DocCategory, List<DocEntry>> entryCategoryMap = new ConcurrentHashMap<>();


    static {
        registerMainCategory(GETTING_STARTED);

        GLYPH_INDEX.addSubCategory(GLYPH_TIER_ONE);
        GLYPH_INDEX.addSubCategory(GLYPH_TIER_TWO);
        GLYPH_INDEX.addSubCategory(GLYPH_TIER_THREE);

        registerMainCategory(GLYPH_INDEX);
        registerMainCategory(RITUAL_INDEX);
        registerMainCategory(SOURCE);
        registerMainCategory(CRAFTING);
        registerMainCategory(MAGICAL_SYSTEMS);
        registerMainCategory(CREATURE_COMPENDIUM);
        registerMainCategory(ITEMS_BLOCKS_EQUIPMENT);
    }

    public static void registerMainCategory(DocCategory section){
        mainCategoryMap.put(section.id(), section);
    }

    public static void registerEntry(DocCategory category, DocEntry entry){
        if(!category.subCategories().isEmpty()){
            throw new IllegalArgumentException("Cannot register an entry to a category with subcategories");
        }
        entryMap.put(entry.id(), entry);
        entryCategoryMap.computeIfAbsent(category, k -> new CopyOnWriteArrayList<>()).add(entry);
    }

    public static List<DocEntry> getEntries(DocCategory category){
        List<DocEntry> entries = entryCategoryMap.get(category);
        return entries == null ? new CopyOnWriteArrayList<>() : entries;
    }

    public static DocCategory getCategory(ResourceLocation id){
        return mainCategoryMap.get(id);
    }

    public static Map<ResourceLocation, DocCategory> getMainCategoryMap(){
        return mainCategoryMap;
    }
}
