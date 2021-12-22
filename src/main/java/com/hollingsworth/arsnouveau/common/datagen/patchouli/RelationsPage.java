package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import com.google.gson.JsonArray;
import net.minecraft.resources.ResourceLocation;

public class RelationsPage extends AbstractPage{

    public RelationsPage(String[] entries){
        JsonArray array = new JsonArray();
        for(String s : entries)
            array.add(s);
        object.add("entries", array);
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
    public ResourceLocation getType() {
        return new ResourceLocation("patchouli:relations");
    }
}
