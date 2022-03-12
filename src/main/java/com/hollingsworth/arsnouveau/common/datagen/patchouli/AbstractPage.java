package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import com.google.gson.JsonObject;

public abstract class AbstractPage implements IPatchouliPage{
    JsonObject object = new JsonObject();

    @Override
    public JsonObject build() {
        object.addProperty("type", getType().toString());
        return object;
    }
}
