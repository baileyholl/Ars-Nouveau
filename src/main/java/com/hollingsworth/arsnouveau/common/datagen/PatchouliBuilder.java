package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class PatchouliBuilder {

    JsonObject object;
    JsonArray pages;
    public static ResourceLocation AUTOMATION = new ResourceLocation(ArsNouveau.MODID, "automation");
    public static ResourceLocation ENCHANTMENTS = new ResourceLocation(ArsNouveau.MODID, "enchantments");
    public static ResourceLocation EQUIPMENT = new ResourceLocation(ArsNouveau.MODID, "equipment");
    public static ResourceLocation FAMILIARS = new ResourceLocation(ArsNouveau.MODID, "familiars");
    public static ResourceLocation GETTING_STARTED = new ResourceLocation(ArsNouveau.MODID, "getting_started");

    public static ResourceLocation MACHINES = new ResourceLocation(ArsNouveau.MODID, "machines");
    public static ResourceLocation RESOURCES = new ResourceLocation(ArsNouveau.MODID, "resources");
    public static ResourceLocation RITUALS = new ResourceLocation(ArsNouveau.MODID, "rituals");
    public static ResourceLocation SOURCE = new ResourceLocation(ArsNouveau.MODID, "source");
    public static ResourceLocation GLYPHS_1 = new ResourceLocation(ArsNouveau.MODID, "glyphs_1");
    public static ResourceLocation GLYPHS_2 = new ResourceLocation(ArsNouveau.MODID, "glyphs_2");
    public static ResourceLocation GLYPHS_3 = new ResourceLocation(ArsNouveau.MODID, "glyphs_3");


    public PatchouliBuilder(){
        this.object = new JsonObject();
        this.pages = new JsonArray();
    }

    public PatchouliBuilder withName(String path){
        object.addProperty("name", path);
        return this;
    }

    public PatchouliBuilder withIcon(String path){
        object.addProperty("icon", path);
        return this;
    }

    public PatchouliBuilder withIcon(Item item){
        object.addProperty("icon", item.getRegistryName().toString());
        return this;
    }

    public PatchouliBuilder withCategory(ResourceLocation path){
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

    public PatchouliBuilder withEntityPage(ResourceLocation entityType){
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:entity");
        page.addProperty("entity", entityType.toString());
        pages.add(page);
        return this;
    }

    public PatchouliBuilder withCraftingPage(String path){
        return withRecipePage(new ResourceLocation("patchouli:crafting"), path);
    }

    public PatchouliBuilder withRecipePage(ResourceLocation type, String recipePath){
        JsonObject page = new JsonObject();
        page.addProperty("type", type.toString());
        page.addProperty("recipe", recipePath);
        pages.add(page);
        return this;
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

}
