package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.export.DocExporter;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ApparatusEntry extends PedestalRecipeEntry{
    RecipeHolder<? extends EnchantingApparatusRecipe> apparatusRecipe;

    public ApparatusEntry(RecipeHolder<? extends EnchantingApparatusRecipe> recipe, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.apparatusRecipe = recipe;
        this.title = Component.translatable("block.ars_nouveau.enchanting_apparatus");
        if(recipe != null && recipe.value() != null) {
            this.outputStack = recipe.value().result();
            this.ingredients = recipe.value().pedestalItems();
            this.reagentStack = recipe.value().reagent();
        }
    }

    public static SinglePageCtor create(RecipeHolder<? extends EnchantingApparatusRecipe> recipe){
        return (parent, x, y, width, height) -> new ApparatusEntry(recipe, parent, x, y, width, height);
    }

    @Override
    public void addExportProperties(JsonObject object) {
        super.addExportProperties(object);
        if(apparatusRecipe != null) {
            object.addProperty(DocExporter.RECIPE_PROPERTY, apparatusRecipe.id().toString());
        }
    }
}
