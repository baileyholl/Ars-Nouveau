package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class RelationsPage extends AbstractPage{

    List<String> entries = new ArrayList<>();

    public RelationsPage(){}

    public RelationsPage withEntry(ResourceLocation category, String fileName){
        entries.add(category.toString() + "/" + fileName);
        return this;
    }

    public RelationsPage withTitle(String title){
        object.addProperty("title", title);
        return this;
    }

    public RelationsPage withText(String text){
        object.addProperty("text", text);
        return this;
    }

    @Override
    public JsonObject build() {
        JsonArray array = new JsonArray();
        for(String s : entries)
            array.add(s);
        object.add("entries", array);
        return super.build();
    }

    @Override
    public ResourceLocation getType() {
        return new ResourceLocation("patchouli:relations");
    }
}
