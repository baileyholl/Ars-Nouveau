package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import net.minecraft.resources.ResourceLocation;

public class LinkPage extends AbstractPage {

    public LinkPage(String url, String linkText, String text) {
        this.object.addProperty("url", url);
        this.object.addProperty("link_text", linkText);
        this.object.addProperty("text", text);
    }

    @Override
    public ResourceLocation getType() {
        return ResourceLocation.tryParse("patchouli:link");
    }
}
