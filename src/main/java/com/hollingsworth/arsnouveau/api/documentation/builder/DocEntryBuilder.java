package com.hollingsworth.arsnouveau.api.documentation.builder;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.documentation.entry.TextEntry;
import com.hollingsworth.arsnouveau.setup.registry.Documentation;
import com.hollingsworth.arsnouveau.setup.registry.RegistryHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

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

    public DocEntryBuilder withCraftingPages(String resourceLocation){
        return withCraftingPages(ResourceLocation.tryParse(resourceLocation));
    }
    public DocEntryBuilder withCraftingPages(ResourceLocation resourceLocation){
        this.withPage(Documentation.getRecipePages(resourceLocation));
        return this;
    }

    public DocEntryBuilder withCraftingPages(ItemLike itemLike){
        this.withPage(Documentation.getRecipePages(RegistryHelper.getRegistryName(itemLike.asItem())));
        return this;
    }

    public DocEntryBuilder withCraftingPages(){
        this.withPage(Documentation.getRecipePages(RegistryHelper.getRegistryName(displayItem.getItem())));
        return this;
    }

    public DocEntry build(){
        return new DocEntry(entryId, this.displayItem, title, sortNum).addPages(pages);
    }

}
