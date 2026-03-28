package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;

public interface IPatchouliPage {


    Identifier getType();

    JsonObject build();

}
