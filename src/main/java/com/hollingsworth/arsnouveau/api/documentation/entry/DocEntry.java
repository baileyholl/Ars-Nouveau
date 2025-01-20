package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageWidget;
import com.hollingsworth.arsnouveau.api.documentation.export.DocExporter;
import com.hollingsworth.arsnouveau.api.documentation.export.IJsonExportable;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.common.util.Log;
import net.minecraft.core.registries.BuiltInRegistries;
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
public record DocEntry(ResourceLocation id, CopyOnWriteArrayList<SinglePageCtor> pages,
                       ItemStack renderStack, Component entryTitle, int order, Set<DocCategory> categories,
                       List<Component> searchTags) implements Comparable<DocEntry>, IJsonExportable {

    public DocEntry(ResourceLocation id, ItemStack renderStack, Component component) {
        this(id, new CopyOnWriteArrayList<>(), renderStack, component, 100, ConcurrentHashMap.newKeySet(), new CopyOnWriteArrayList<>());
    }

    public DocEntry(ResourceLocation id, ItemStack renderStack, Component component, int order) {
        this(id, new CopyOnWriteArrayList<>(), renderStack, component, order, ConcurrentHashMap.newKeySet(), new CopyOnWriteArrayList<>());
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

    public DocEntry withSearchTag(Component component){
        searchTags.add(component);
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

    @Override
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty(DocExporter.ID_PROPERTY, id.toString());
        object.addProperty(DocExporter.ORDER_PROPERTY, order);
        object.addProperty(DocExporter.ICON_PROPERTY, BuiltInRegistries.ITEM.getKey(renderStack.getItem()).toString());
        object.addProperty(DocExporter.TITLE_PROPERTY, entryTitle.getString());
        object.addProperty(DocExporter.CATEGORY_PROPERY, DocumentationRegistry.getCategoryForEntry(this).id().toString());
        if(!ArsNouveau.proxy.isClientSide()){
            return object;
        }
        JsonArray pageJsons = new JsonArray();
        for(SinglePageCtor pageCtor : pages){
            var screen = new BaseDocScreen();
            screen.setMinecraft(ArsNouveau.proxy.getMinecraft());
            SinglePageWidget widget = pageCtor.create(screen, 0, 0, 0, 0);
            JsonObject pageObject = widget.toJson();
            if(pageObject.isEmpty()){
                Log.getLogger().error("Page " + id + " " + pageCtor + " is empty!");
                continue;
            }
            pageJsons.add(pageObject);
        }
        object.add("pages", pageJsons);

        return object;
    }
}
