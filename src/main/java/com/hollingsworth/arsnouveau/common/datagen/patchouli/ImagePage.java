package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ImagePage extends AbstractPage {
    List<String> images = new ArrayList<>();

    public ImagePage() {
    }

    public ImagePage withEntry(ResourceLocation file) {
        images.add(file.toString());
        return this;
    }

    public ImagePage withTitle(String title) {
        object.addProperty("title", title);
        return this;
    }

    public ImagePage withText(String text) {
        object.addProperty("text", text);
        return this;
    }

    public ImagePage withBorder() {
        object.addProperty("border", true);
        return this;
    }

    @Override
    public JsonObject build() {
        JsonArray array = new JsonArray();
        for (String s : images)
            array.add(s);
        object.add("images", array);
        return super.build();
    }

    @Override
    public ResourceLocation getType() {
        return ResourceLocation.tryParse("patchouli:image");
    }
}
