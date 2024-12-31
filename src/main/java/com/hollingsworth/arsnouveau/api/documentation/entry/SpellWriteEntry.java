package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.common.crafting.recipes.SpellWriteRecipe;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class SpellWriteEntry extends PedestalRecipeEntry{
    RecipeHolder<SpellWriteRecipe> spellWriteRecipe;

    public SpellWriteEntry(RecipeHolder<SpellWriteRecipe> spellWriteRecipe, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.spellWriteRecipe = spellWriteRecipe;
        if(spellWriteRecipe != null) {
            this.ingredients = spellWriteRecipe.value().pedestalItems();
        }
    }

    public static SinglePageCtor create(ResourceLocation id){
        return (parent, x, y, width, height) -> {
            RecipeHolder<SpellWriteRecipe> recipe = parent.recipeManager().byKeyTyped(RecipeRegistry.SPELL_WRITE_TYPE.get(), id);
            return new SpellWriteEntry(recipe, parent, x, y, width, height);
        };
    }
}
