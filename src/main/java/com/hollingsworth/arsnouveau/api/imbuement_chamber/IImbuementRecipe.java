package com.hollingsworth.arsnouveau.api.imbuement_chamber;

import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public interface IImbuementRecipe extends Recipe<ImbuementTile> {
    boolean isMatch(ImbuementTile imbuementTile);

    ItemStack getResult(ImbuementTile imbuementTile);

    int getSourceCost(ImbuementTile imbuementTile);
}
