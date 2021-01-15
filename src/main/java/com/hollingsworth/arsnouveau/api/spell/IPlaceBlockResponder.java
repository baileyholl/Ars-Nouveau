package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public interface IPlaceBlockResponder {
    /**
     * Called when an attempt to place a block is made. This is used by the PlaceBlock spell as a way to support automation entities.
     * @return Returns the itemstack that will attempt to be placed.
     */
    @Nonnull ItemStack onPlaceBlock();

    @Nonnull
    default List<IItemHandler> getInventory(){return new ArrayList<>();}

}
