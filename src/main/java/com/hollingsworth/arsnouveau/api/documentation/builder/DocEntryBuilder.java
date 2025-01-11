package com.hollingsworth.arsnouveau.api.documentation.builder;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.documentation.entry.TextEntry;
import com.hollingsworth.arsnouveau.api.documentation.search.ConnectedSearch;
import com.hollingsworth.arsnouveau.api.documentation.search.Search;
import com.hollingsworth.arsnouveau.setup.registry.Documentation;
import com.hollingsworth.arsnouveau.setup.registry.RegistryHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class DocEntryBuilder {
    public List<SinglePageCtor> pages = new ArrayList<>();
    public int textCounter;
    public String textKey;
    public int sortNum = 100;
    public DocCategory category;
    public ItemStack displayItem;
    public String titleKey;
    public ResourceLocation entryId;

    public Component title;
    public List<ConnectedSearch> connectedSearches = new ArrayList<>();

    public DocEntryBuilder(DocCategory category, String name) {
        this(category, name, ArsNouveau.prefix(name));
    }

    public DocEntryBuilder(DocCategory category, String name, ResourceLocation entryId){
        this.titleKey = name.contains(".") ? name : "ars_nouveau.page." + name;
        this.textKey = name;
        this.title = Component.translatable(titleKey);
        this.category = category;
        displayItem = ItemStack.EMPTY;
        this.entryId = entryId;
    }


    public DocEntryBuilder(DocCategory category, ItemLike itemLike) {
        this.category = category;
        this.titleKey = itemLike.asItem().getDescriptionId();
        this.textKey = getRegistryName(itemLike.asItem()).getPath();
        this.displayItem = itemLike.asItem().getDefaultInstance();
        this.entryId = getRegistryName(itemLike.asItem());
        this.title = Component.translatable(titleKey);
    }

    public DocEntryBuilder withName(String path) {
        this.textKey = path;
        this.titleKey = path;
        this.title = Component.translatable(titleKey);
        return this;
    }

    public DocEntryBuilder withSortNum(int num) {
        sortNum = num;
        return this;
    }

    public DocEntryBuilder withPage(SinglePageCtor page) {
        pages.add(page);
        return this;
    }


    public DocEntryBuilder withPage(List<SinglePageCtor> page) {
        pages.addAll(page);
        return this;
    }

    public DocEntryBuilder withIcon(ItemLike item) {
        displayItem = item.asItem().getDefaultInstance();
        return this;
    }

    public DocEntryBuilder withCategory(DocCategory path) {
        category = path;
        return this;
    }

    public DocEntryBuilder withIntroPage() {
        return withIntroPage(this.textKey);
    }

    public DocEntryBuilder withIntroPage(String id) {
        textCounter++;
        pages.add(TextEntry.create(Component.translatable("ars_nouveau.page" + textCounter + "." + id), Component.translatable(titleKey), displayItem));
        return this;
    }

    public DocEntryBuilder withTextPage(String contents) {
        pages.add(TextEntry.create(Component.translatable(contents)));
        return this;
    }

    public DocEntryBuilder withLocalizedText(String id) {
        textCounter++;
        return withTextPage("ars_nouveau.page" + textCounter + "." + id);
    }


    public DocEntryBuilder withLocalizedText() {
        return withLocalizedText(this.textKey);
    }

    public DocEntryBuilder withLocalizedText(ItemLike itemLike){
        textCounter++;
        pages.add(TextEntry.create(Component.translatable("ars_nouveau.page" + textCounter + "." + this.textKey), itemLike.asItem().getDescription(), itemLike.asItem().getDefaultInstance()));
        return this;
    }

    public DocEntryBuilder withCraftingPages(String resourceLocation, ItemLike outputStack){
        return withCraftingPages(ResourceLocation.tryParse(resourceLocation), outputStack);
    }
    public DocEntryBuilder withCraftingPages(ResourceLocation resourceLocation, @Nullable ItemLike outputStack){
        var craftingPages = Documentation.getRecipePages(resourceLocation);
        this.withPage(Documentation.getRecipePages(resourceLocation));
        if(!craftingPages.isEmpty() && outputStack != null && !this.displayItem.is(outputStack.asItem())){
            addConnectedSearch(outputStack.asItem().getDefaultInstance());
        }
        return this;
    }

    public DocEntryBuilder withCraftingPages(ItemLike itemLike){
        List<SinglePageCtor> craftingPages = Documentation.getRecipePages(RegistryHelper.getRegistryName(itemLike.asItem()));
        this.withPage(craftingPages);
        if(!craftingPages.isEmpty() && !this.displayItem.is(itemLike.asItem())){
            ItemStack stack = itemLike.asItem().getDefaultInstance();
            addConnectedSearch(stack);
        }
        return this;
    }

    public DocEntryBuilder withCraftingPages(ItemLike itemLike, ItemLike itemLike2){
        List<SinglePageCtor> craftingPages = Documentation.getRecipePages(itemLike, itemLike2);
        this.withPage(craftingPages);
        if(!craftingPages.isEmpty() && !this.displayItem.is(itemLike.asItem())){
            ItemStack stack = itemLike.asItem().getDefaultInstance();
            addConnectedSearch(stack);
        }
        if(!craftingPages.isEmpty() && !this.displayItem.is(itemLike2.asItem())){
            ItemStack stack = itemLike2.asItem().getDefaultInstance();
            addConnectedSearch(stack);
        }
        return this;
    }

    public DocEntryBuilder addConnectedSearch(ItemStack connectedItem){
        if(this.connectedSearches.stream().anyMatch(cs -> cs.icon().is(connectedItem.getItem())))
            return this;
        this.connectedSearches.add(new ConnectedSearch(entryId, connectedItem.getHoverName(), connectedItem));
        return this;
    }

    public DocEntryBuilder addConnectedSearch(ItemLike itemLike){
        return addConnectedSearch(itemLike.asItem().getDefaultInstance());
    }

    public DocEntryBuilder withCraftingPages(){
        List<SinglePageCtor> pages = Documentation.getRecipePages(RegistryHelper.getRegistryName(displayItem.getItem()));
        this.withPage(pages);
        return this;
    }

    public DocEntry build(){
        DocEntry docEntry = new DocEntry(entryId, displayItem, title, sortNum).addPages(pages);
        for(ConnectedSearch connectedSearch : connectedSearches){
            Search.addConnectedSearch(connectedSearch);
        }
        return docEntry;
    }
}
