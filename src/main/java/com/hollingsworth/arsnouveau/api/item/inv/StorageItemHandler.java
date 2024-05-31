package com.hollingsworth.arsnouveau.api.item.inv;

import InteractResult;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import java.util.List;
import java.util.function.Function;

public class StorageItemHandler extends FilterableItemHandler{
    public StorageItemHandler(IItemHandler handler) {
        super(handler);
    }

    public StorageItemHandler(IItemHandler handler, List<Function<ItemStack, ItemScroll.SortPref>> filters) {
        super(handler, filters);
    }

    @Override
    public InteractResult canExtract(ItemStack stack) {
        ItemScroll.SortPref pref = getHighestPreference(stack);
        return new InteractResult(pref, true);
    }
}
