package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An entry in a chapter of the documentation.
 */
public record DocEntry(ResourceLocation id, CopyOnWriteArrayList<SinglePageCtor> pages, ItemStack renderStack, Component entryTitle, int order, Set<DocCategory> categories) implements Comparable<DocEntry> {

    public DocEntry(ResourceLocation id, ItemStack renderStack, Component component) {
        this(id, new CopyOnWriteArrayList<>(), renderStack, component, 100, ConcurrentHashMap.newKeySet());
    }

    public DocEntry(ResourceLocation id, ItemStack renderStack, Component component, int order) {
        this(id, new CopyOnWriteArrayList<>(), renderStack, component, order, ConcurrentHashMap.newKeySet());
    }

    public DocEntry addPage(SinglePageCtor page){
        pages.add(page);
        return this;
    }

    public DocEntry addPages(List<SinglePageCtor> pages){
        this.pages.addAll(pages);
        return this;
    }

    public DocEntry withEntryRelations(List<DocEntry> entries){
        return withRelations(entries.stream().map(DocEntry::id).toList());
    }

    public DocEntry withRelations(DocEntry... entries){
        return withRelations(Arrays.stream(entries).map(DocEntry::id).toList());
    }

    public DocEntry withRelations(List<ResourceLocation> ids){
        if(this.pages.isEmpty()){
            return this;
        }
        if(this.pages.getLast() instanceof RelationEntry.RelationBuilder relationBuilder){
            relationBuilder.entries.addAll(ids);
        }else{
            var builder = new RelationEntry.RelationBuilder();
            builder.entries.addAll(ids);
            this.pages.add(builder);
        }
        return this;
    }

    public DocEntry withRelation(DocEntry entry){
        return withRelation(entry.id);
    }

    public DocEntry withRelation(ResourceLocation id){
        if(this.pages.isEmpty()){
            return this;
        }
        if(this.pages.getLast() instanceof RelationEntry.RelationBuilder relationBuilder){
            relationBuilder.entries.add(id);
        }else{
            var builder = new RelationEntry.RelationBuilder();
            builder.entries.add(id);
            this.pages.add(builder);
        }
        return this;
    }

    @Override
    public int compareTo(@NotNull DocEntry o) {
        return Integer.compare(order, o.order);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocEntry docEntry = (DocEntry) o;
        return Objects.equals(id, docEntry.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
