package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;

public class ImbuementRecipeRegistry extends MultiRecipeRegistry<ImbuementTile, IImbuementRecipe> {
    public static ImbuementRecipeRegistry INSTANCE = new ImbuementRecipeRegistry();
}
