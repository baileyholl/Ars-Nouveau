package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class PatchouliBuilder {

    JsonObject object = new JsonObject();;
    JsonArray pages = new JsonArray();;
    int textCounter;
    String name;
    public ResourceLocation category;
    public PatchouliBuilder(ResourceLocation category, String name){
        this.category = category;
        this.withName(name.contains(".") ? name : "ars_nouveau.page." + name);
        this.name = name;
        this.withCategory(category);
    }

    public PatchouliBuilder(ResourceLocation category, ItemLike itemLike){
        this.category = category;
        withName(itemLike.asItem().getDescriptionId());
        this.name = itemLike.asItem().getRegistryName().getPath();
        withIcon(itemLike);
        this.withCategory(category);
    }

    public PatchouliBuilder withName(String path){
        object.addProperty("name", path);
        this.name = path;
        return this;
    }

    public PatchouliBuilder withSortNum(int num){
        object.addProperty("sortnum", num);
        return this;
    }

    public PatchouliBuilder withIcon(String path){
        object.addProperty("icon", path);
        return this;
    }

    public PatchouliBuilder withIcon(ItemLike item){
        object.addProperty("icon", item.asItem().getRegistryName().toString());
        return this;
    }

    private PatchouliBuilder withCategory(ResourceLocation path){
        object.addProperty("category", path.toString());
        return this;
    }

    public PatchouliBuilder withTextPage(String contents){
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:text");
        page.addProperty("text", contents);
        pages.add(page);
        return this;
    }

    public PatchouliBuilder withLocalizedText(String id){
        textCounter++;
        return withTextPage("ars_nouveau.page" + textCounter + "." + id);
    }

    public PatchouliBuilder withLocalizedText(){
        return withLocalizedText(this.name);
    }

    public PatchouliBuilder withEntityPage(ResourceLocation entityType){
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:entity");
        page.addProperty("entity", entityType.toString());
        pages.add(page);
        return this;
    }

    public PatchouliBuilder withEntityTextPage(ResourceLocation entityType, String id){
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:entity");
        page.addProperty("entity", entityType.toString());
        textCounter++;
        page.addProperty("text", "ars_nouveau.page" + textCounter + "." + id);
        pages.add(page);
        return this;
    }

    public PatchouliBuilder withCraftingPage(String path){
        return withRecipePage(new ResourceLocation("patchouli:crafting"), path);
    }

    public PatchouliBuilder withCraftingPage(ItemLike item){
        return withCraftingPage(item.asItem().getRegistryName().toString());
    }

    public PatchouliBuilder withRecipePage(ResourceLocation type, String recipePath){
        JsonObject page = new JsonObject();
        page.addProperty("type", type.toString());
        page.addProperty("recipe", recipePath);
        pages.add(page);
        return this;
    }

    public PatchouliBuilder withRecipePage(ResourceLocation type, ItemLike itemLike){
        return withRecipePage(type, itemLike.asItem().getRegistryName().toString());
    }

    public PatchouliBuilder withRelations(String... entries){
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:relations");
        JsonArray array = new JsonArray();
        for(String s : entries)
            array.add(s);
        page.add("entries", array);
        pages.add(page);
        return this;
    }

    public JsonObject build(){
        this.object.add("pages", pages);
        return this.object;
    }


    public static abstract class RecipeProvider{
        abstract ResourceLocation getType(ItemLike item);

        public ResourceLocation getPath(ItemLike item){
            return item.asItem().getRegistryName();
        }
    }
}
