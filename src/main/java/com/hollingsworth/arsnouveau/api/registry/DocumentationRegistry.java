package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.documentation.DocEntry;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentationRegistry {

    public static final DocCategory GETTING_STARTED = new DocCategory(ArsNouveau.prefix("getting_started"), MethodProjectile.INSTANCE.glyphItem.getDefaultInstance(), 1);

    private static final Map<ResourceLocation, DocCategory> categoryMap = new ConcurrentHashMap<>();

    private static final Map<ResourceLocation, DocEntry> entryMap = new ConcurrentHashMap<>();
    private static final Map<DocCategory, List<DocEntry>> entryCategoryMap = new ConcurrentHashMap<>();

    public static void registerCategory(DocCategory section){
        categoryMap.put(section.id(), section);
    }

    public static void registerEntry(DocCategory category, DocEntry entry){
        entryCategoryMap.computeIfAbsent(category, k -> new ArrayList<>()).add(entry);
    }

    public static DocCategory getCategory(ResourceLocation id){
        return categoryMap.get(id);
    }

    public static Map<ResourceLocation, DocCategory> getCategoryMap(){
        return categoryMap;
    }
}
