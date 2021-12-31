package com.hollingsworth.arsnouveau.client.patchouli.component;

import net.minecraft.world.item.crafting.Ingredient;
import vazkii.patchouli.api.IVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class RotatingItemListComponent extends RotatingItemListComponentBase{
    public List<IVariable> ingredients;

    private transient List<Ingredient> theIngredients = new ArrayList<>();

    @Override
    protected List<Ingredient> makeIngredients() {
        return theIngredients;
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
        theIngredients.clear();
        for (int i = 0; i < ingredients.size(); i++) {
            theIngredients.add(lookup.apply(ingredients.get(i)).as(Ingredient.class));
        }
    }
}
