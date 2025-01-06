package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class SpotlightPage extends AbstractPage {

    public SpotlightPage(String itemString) {
        object.addProperty("item", itemString);
    }

    public SpotlightPage(ItemLike itemLike) {
        this(getRegistryName(itemLike.asItem()).toString());
    }

    public SpotlightPage withTitle(String title) {
        object.addProperty("title", title);
        return this;
    }

    public SpotlightPage linkRecipe(boolean link) {
        object.addProperty("link_recipe", link);
        return this;
    }

    public SpotlightPage withText(String text) {
        object.addProperty("text", text);
        return this;
    }

    @Override
    public ResourceLocation getType() {
        return ResourceLocation.tryParse("patchouli:spotlight");
    }
}
