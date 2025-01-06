package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import net.minecraft.resources.ResourceLocation;

public class TextPage extends AbstractPage {

    public TextPage(String text) {
        object.addProperty("text", text);
    }

    public TextPage withTitle(String title) {
        object.addProperty("title", title);
        return this;
    }

    @Override
    public ResourceLocation getType() {
        return ResourceLocation.tryParse("patchouli:text");
    }
}
