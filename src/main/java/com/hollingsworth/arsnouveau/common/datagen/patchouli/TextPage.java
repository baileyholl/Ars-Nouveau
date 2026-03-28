package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import net.minecraft.resources.Identifier;

public class TextPage extends AbstractPage {

    public TextPage(String text) {
        object.addProperty("text", text);
    }

    public TextPage withTitle(String title) {
        object.addProperty("title", title);
        return this;
    }

    @Override
    public Identifier getType() {
        return Identifier.tryParse("patchouli:text");
    }
}
