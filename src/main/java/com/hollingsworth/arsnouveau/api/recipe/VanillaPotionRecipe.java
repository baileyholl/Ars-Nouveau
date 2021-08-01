package com.hollingsworth.arsnouveau.api.recipe;

import net.minecraft.item.Item;
import net.minecraft.potion.Potion;

public class VanillaPotionRecipe {

    public Potion potionIn;
    public Potion potionOut;
    public Item reagent;
    public VanillaPotionRecipe(Potion potionIn, Item reagent, Potion potionOut){
        this.potionIn = potionIn;
        this.reagent = reagent;
        this.potionOut = potionOut;
    }
}
