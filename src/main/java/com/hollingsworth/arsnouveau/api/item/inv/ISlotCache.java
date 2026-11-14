package com.hollingsworth.arsnouveau.api.item.inv;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface ISlotCache {
    IntList getOrCreateSlots(Item item);

    @Nullable
    IntList getIfPresent(Item item);

    void replaceSlotWithItem(Item newItem, int slot);

    ItemStack insertItemStacked(IItemHandler inventory, ItemStack stack, boolean simulate);

    boolean isEmpty(int slot);

    int size();

    int emptyCount();
}
