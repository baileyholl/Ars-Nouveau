package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class CraftingPage extends AbstractPage {

    public CraftingPage(String recipe) {
        object.addProperty("recipe", recipe);
    }

    public CraftingPage(ItemLike itemLike) {
        this(getRegistryName(itemLike.asItem()).toString());
    }

    public CraftingPage withRecipe2(String recipe) {
        object.addProperty("recipe2", recipe);
        return this;
    }

    public CraftingPage withRecipe2(ItemLike recipe) {
        object.addProperty("recipe2", getRegistryName(recipe.asItem()).toString());
        return this;
    }

    public CraftingPage withTitle(String title) {
        object.addProperty("title", title);
        return this;
    }

    public CraftingPage withText(String text) {
        object.addProperty("text", text);
        return this;
    }

    @Override
    public ResourceLocation getType() {
        return ResourceLocation.parse("patchouli:crafting");
    }
}
