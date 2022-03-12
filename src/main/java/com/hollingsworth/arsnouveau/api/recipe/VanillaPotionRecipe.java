package com.hollingsworth.arsnouveau.api.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;

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
