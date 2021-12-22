package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import net.minecraft.resources.ResourceLocation;

public class SpotlightPage extends AbstractPage{

    public SpotlightPage(String itemString){
        object.addProperty("item", itemString);
    }

    public SpotlightPage withTitle(String title){
        object.addProperty("title", title);
        return this;
    }

    public SpotlightPage linkRecipe(boolean link){
        object.addProperty("link_recipe", link);
        return this;
    }

    public SpotlightPage withText(String text){
        object.addProperty("text", text);
        return this;
    }

    @Override
    public ResourceLocation getType() {
        return new ResourceLocation("patchouli:spotlight");
    }
}
