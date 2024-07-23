package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;

public class ImbuementRecipeRegistry extends MultiRecipeRegistry<ImbuementTile, ImbuementRecipe> {
    public static ImbuementRecipeRegistry INSTANCE = new ImbuementRecipeRegistry();
}
