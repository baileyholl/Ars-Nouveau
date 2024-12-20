package com.hollingsworth.arsnouveau.api.documentation.builder;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.documentation.DocEntry;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.TextEntry;
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
    List<SinglePageCtor> pages = new ArrayList<>();
    int textCounter;
    String textKey;
    int sortNum;
    public DocCategory category;
    ItemStack displayItem;
    String titleKey;
    ResourceLocation entryId;

    public DocEntryBuilder(DocCategory category, String name) {
        this.titleKey = name.contains(".") ? name : "ars_nouveau.page." + name;
        this.textKey = name;
        this.category = category;
        displayItem = ItemStack.EMPTY;
        this.entryId = ArsNouveau.prefix(name);
    }

    public DocEntryBuilder(DocCategory category, ItemLike itemLike) {
        this.category = category;
        this.titleKey = itemLike.asItem().getDescriptionId();
        this.textKey = getRegistryName(itemLike.asItem()).getPath();
        this.displayItem = itemLike.asItem().getDefaultInstance();
        this.entryId = getRegistryName(itemLike.asItem());
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
        textCounter++;
        pages.add(TextEntry.create(Component.translatable("ars_nouveau.page" + textCounter + "." + this.textKey), Component.translatable(titleKey), displayItem));
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

    public DocEntryBuilder withCraftingPages(ResourceLocation resourceLocation){
        this.withPage(Documentation.getRecipePages(resourceLocation));
        return this;
    }

    public DocEntryBuilder withCraftingPages(ItemLike itemLike){
        this.withPage(Documentation.getRecipePages(RegistryHelper.getRegistryName(displayItem.getItem())));
        return this;
    }

    public DocEntryBuilder withCraftingPages(){
        this.withPage(Documentation.getRecipePages(RegistryHelper.getRegistryName(displayItem.getItem())));
        return this;
    }

    public DocEntry build(){
        return new DocEntry(entryId, this.displayItem, Component.translatable(titleKey)).addPages(pages);
    }

}