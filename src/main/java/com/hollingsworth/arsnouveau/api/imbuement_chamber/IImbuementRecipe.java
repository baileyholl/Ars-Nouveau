package com.hollingsworth.arsnouveau.api.imbuement_chamber;

import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import net.minecraft.world.item.crafting.Recipe;

public interface IImbuementRecipe extends Recipe<ImbuementTile> {
    int getSourceCost(ImbuementTile imbuementTile);
}
