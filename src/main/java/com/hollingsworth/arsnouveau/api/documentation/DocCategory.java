package com.hollingsworth.arsnouveau.api.documentation;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.documentation.export.DocExporter;
import com.hollingsworth.arsnouveau.api.documentation.export.IJsonExportable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public record DocCategory(ResourceLocation id, ItemStack renderIcon, int order, List<DocCategory> subCategories, Comparator<DocEntry> entryComparator, Set<DocCategory> parents) implements Comparable<DocCategory>, IJsonExportable {

    public DocCategory(ResourceLocation id, ItemStack renderIcon, int order) {
        this(id, renderIcon, order, new CopyOnWriteArrayList<>(), Comparator.comparing(DocEntry::order).thenComparing((entry -> entry.entryTitle().getString())), ConcurrentHashMap.newKeySet());
    }

    public DocCategory withComparator(Comparator<DocEntry> comparator){
        return new DocCategory(id, renderIcon, order, subCategories, comparator, ConcurrentHashMap.newKeySet());
    }

    public void addSubCategory(DocCategory category){
        subCategories.add(category);
        category.parents.add(this);
    }

    public Component getTitle(){
        return Component.translatable(id.getNamespace() + ".section." + id.getPath());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocCategory that = (DocCategory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(@NotNull DocCategory o) {
        return this.order - o.order;
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty(DocExporter.ID_PROPERTY, id.toString());
        object.addProperty(DocExporter.ORDER_PROPERTY, order);
        object.addProperty(DocExporter.TITLE_PROPERTY, getTitle().getString());
        return object;
    }
}
